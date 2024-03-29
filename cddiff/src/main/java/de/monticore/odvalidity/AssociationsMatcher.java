/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Collectors;

public class AssociationsMatcher {
  private final Map<String, Set<ASTCDAssociation>> srcMap;

  private final Map<String, Set<ASTCDAssociation>> targetMap;

  private Set<ASTODNamedObject> objects;

  private Set<ASTODLink> links;

  private ICD4CodeArtifactScope scope;

  private CDSemantics semantics;

  public AssociationsMatcher() {
    srcMap = new HashMap<>();
    targetMap = new HashMap<>();
  }

  /** check if links in od match associations in cd according to semantics */
  public boolean checkAssociations(
      ASTODArtifact od, ASTCDCompilationUnit cd, CDSemantics semantics) {

    this.scope = (ICD4CodeArtifactScope) cd.getEnclosingScope();

    this.semantics = semantics;

    this.objects = new HashSet<>(ODHelper.getAllNamedObjects(od.getObjectDiagram()));

    // we split bidirectional links and make them all left-to-right
    this.links =
        new HashSet<>(
            new NormalizeLinksTrafo()
                .transformLinksToLTR(ODHelper.getAllLinks(od.getObjectDiagram())));

    /* initialize srcMap and targetMap:
    srcMap(object) = { association in cd | object intanceof association.srcType}
    srcMap(object) = { association in cd | object intanceof association.targetType}
     */
    for (ASTODNamedObject object : objects) {
      Set<ASTCDAssociation> srcSet =
          cd.getCDDefinition().getCDAssociationsList().stream()
              .filter(assoc -> isObject4AssocSrc(object, assoc))
              .collect(Collectors.toSet());
      Set<ASTCDAssociation> targetSet =
          cd.getCDDefinition().getCDAssociationsList().stream()
              .filter(assoc -> isObject4AssocTarget(object, assoc))
              .collect(Collectors.toSet());
      srcMap.put(object.getName(), srcSet);
      targetMap.put(object.getName(), targetSet);
    }

    // for closed-world semantics each link in od must be an instance of an association in cd
    if (Semantic.isClosedWorld(semantics)) {
      for (ASTODLink link : links) {
        if (cd.getCDDefinition().getCDAssociationsList().stream()
            .noneMatch(assoc -> matchLinkAgainstAssociation(link, assoc))) {
          Log.println(
              String.format(
                  "[CONFLICT] No association found for link "
                      + link.getLeftReferenceNames()
                      + " -> ("
                      + link.getODLinkRightSide().getRole()
                      + ") "
                      + link.getRightReferenceNames()
                      + "."));
          return false;
        }
      }
    }

    // for each object and each association the cardinality constraints have to hold on both sides
    for (ASTODNamedObject object : objects) {
      if (srcMap.get(object.getName()).stream()
              .anyMatch(assoc -> !checkTargetTypeAndCardinality(object, assoc))
          || targetMap.get(object.getName()).stream()
              .anyMatch(assoc -> !checkSourceCardinality(object, assoc))) {
        return false;
      }
    }

    // Each instance of a bidirectional association is a pair of matching l2r-links
    // i.e. association A <-> B and objects a instanceof A, b instanceof B: a -> b <=> b -> a
    for (ASTCDAssociation assoc :
        cd.getCDDefinition().getCDAssociationsList().stream()
            .filter(assoc -> assoc.getCDAssocDir().isBidirectional())
            .collect(Collectors.toSet())) {
      if (links.stream()
          .filter(link -> matchLinkAgainstAssociation(link, assoc))
          .anyMatch(link -> !findReverseLink(link, assoc))) {
        return false;
      }
    }

    return true;
  }

  /**
   * Check if a link for a bidirectional association has a corresponding reverse-link i.e. for
   * association A <-> B and link a -> b, check if there is link b -> a
   */
  private boolean findReverseLink(ASTODLink link, ASTCDAssociation assoc) {
    for (String leftObject : link.getLeftReferenceNames()) {
      for (String rightObject : link.getRightReferenceNames()) {
        if (links.stream()
            .noneMatch(
                otherLink ->
                    otherLink.getRightReferenceNames().contains(leftObject)
                        && otherLink.getLeftReferenceNames().contains(rightObject)
                        && matchLinkAgainstAssociation(otherLink, assoc))) {
          Log.println(
              String.format(
                  "[CONFLICT] No counterpart found for link %s -> (%s) %s",
                  leftObject, link.getODLinkRightSide().getRole(), rightObject));
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Check if all outgoing links of object have the correct target-type and respect cardinality
   * constraints iff they are an instance of assoc.
   */
  private boolean checkTargetTypeAndCardinality(ASTODNamedObject object, ASTCDAssociation assoc) {

    Set<ASTODLink> outgoingLinks =
        links.stream()
            .filter(link -> link.getLeftReferenceNames().contains(object.getName()))
            .collect(Collectors.toSet());
    ASTCDAssocSide targetSide = getTargetSide4Object(object, assoc);

    if ((assoc.getCDAssocDir().isDefinitiveNavigableLeft()
            == assoc.getCDAssocDir().isDefinitiveNavigableRight())
        && isInstanceOf(object, assoc.getLeftQualifiedName().getQName())
        && isInstanceOf(object, assoc.getRightQualifiedName().getQName())) {
      return checkBidirLoop(object, assoc, outgoingLinks);
    }

    String targetType = targetSide.getMCQualifiedType().getMCQualifiedName().getQName();
    String targetRole = CDDiffUtil.inferRole(targetSide);

    if (object.getODAttributeList().stream().anyMatch(attr -> attr.getName().equals(targetRole))) {
      return false;
    }

    if (outgoingLinks.stream()
        .anyMatch(
            link ->
                link.getODLinkRightSide().getRole().equals(targetRole)
                    && !link.getRightReferenceNames().stream()
                        .allMatch(objName -> isInstanceOf(getObject(objName).get(), targetType)))) {
      Log.println(
          String.format(
              "[TYPE CONFLICT]: %s -> (%s) %s", object.getName(), targetRole, targetType));
      return false;
    }
    if (targetSide.isPresentCDCardinality()) {
      long numberOfTargets = 0;
      for (ASTODLink link : outgoingLinks) {
        if (matchLinkAgainstAssociation(link, assoc)) {
          numberOfTargets += link.getRightReferenceNames().size();
        }
      }
      if (!checkIfObjectNumberIsValid(targetSide.getCDCardinality(), numberOfTargets)) {
        Log.println(
            "[TARGET] "
                + numberOfTargets
                + " of "
                + outgoingLinks.size()
                + " violates "
                + CD4CodeMill.prettyPrint(targetSide, false));
        return false;
      }
    }
    return true;
  }

  private boolean checkBidirLoop(
      ASTODNamedObject object, ASTCDAssociation assoc, Collection<ASTODLink> outgoingLinks) {

    if (object.getODAttributeList().stream()
        .anyMatch(
            attr ->
                (attr.getName().equals(CDDiffUtil.inferRole(assoc.getRight()))
                    || attr.getName().equals(CDDiffUtil.inferRole(assoc.getLeft()))))) {
      return false;
    }

    Set<ASTODLink> rlinks =
        outgoingLinks.stream()
            .filter(
                link ->
                    link.getODLinkRightSide()
                        .getRole()
                        .equals(CDDiffUtil.inferRole(assoc.getRight())))
            .collect(Collectors.toSet());
    if (rlinks.stream()
        .anyMatch(
            link ->
                !link.getRightReferenceNames().stream()
                    .allMatch(
                        objName ->
                            isInstanceOf(
                                getObject(objName).get(),
                                assoc.getRightQualifiedName().getQName())))) {
      return false;
    }
    Set<ASTODLink> llinks =
        outgoingLinks.stream()
            .filter(
                link ->
                    link.getODLinkRightSide()
                        .getRole()
                        .equals(CDDiffUtil.inferRole(assoc.getLeft())))
            .collect(Collectors.toSet());
    if (llinks.stream()
        .anyMatch(
            link ->
                !link.getRightReferenceNames().stream()
                    .allMatch(
                        objName ->
                            isInstanceOf(
                                getObject(objName).get(),
                                assoc.getLeftQualifiedName().getQName())))) {
      return false;
    }
    if (assoc.getRight().isPresentCDCardinality()) {
      long numberOfTargets = 0;
      for (ASTODLink link : rlinks) {
        if (matchLinkAgainstAssociation(link, assoc)) {
          numberOfTargets += link.getRightReferenceNames().size();
        }
      }
      if (!checkIfObjectNumberIsValid(assoc.getRight().getCDCardinality(), numberOfTargets)) {
        Log.println(
            "[RIGHT] "
                + numberOfTargets
                + " violates "
                + CD4CodeMill.prettyPrint(assoc.getRight(), false));
        return false;
      }
    }
    if (assoc.getLeft().isPresentCDCardinality()) {
      long numberOfTargets = 0;
      for (ASTODLink link : llinks) {
        if (matchLinkAgainstAssociation(link, assoc)) {
          numberOfTargets += link.getRightReferenceNames().size();
        }
      }
      if (!checkIfObjectNumberIsValid(assoc.getLeft().getCDCardinality(), numberOfTargets)) {
        Log.println(
            "[LEFT] "
                + numberOfTargets
                + " violates "
                + CD4CodeMill.prettyPrint(assoc.getLeft(), false));
        return false;
      }
    }
    return true;
  }

  /**
   * Check if all incoming links of object that are an instance of assoc respect cardinality
   * constraints.
   */
  private boolean checkSourceCardinality(ASTODNamedObject object, ASTCDAssociation assoc) {

    Set<ASTODLink> incomingLinks =
        links.stream()
            .filter(
                link ->
                    link.getRightReferenceNames().contains(object.getName())
                        && matchLinkAgainstAssociation(link, assoc))
            .collect(Collectors.toSet());

    if ((assoc.getCDAssocDir().isDefinitiveNavigableLeft()
            == assoc.getCDAssocDir().isDefinitiveNavigableRight())
        && isInstanceOf(object, assoc.getLeftQualifiedName().getQName())
        && isInstanceOf(object, assoc.getRightQualifiedName().getQName())) {
      return true;
    }

    ASTCDAssocSide srcSide = getSourceSide4Object(object, assoc);
    if (srcSide.isPresentCDCardinality()) {
      long numberOfTargets = 0;
      for (ASTODLink link : incomingLinks) {
        if (matchLinkAgainstAssociation(link, assoc)) {
          numberOfTargets += link.getLeftReferenceNames().size();
        }
      }
      if (!checkIfObjectNumberIsValid(srcSide.getCDCardinality(), numberOfTargets)) {
        Log.println(
            "[SRC] " + numberOfTargets + " violates " + CD4CodeMill.prettyPrint(srcSide, false));
        return false;
      }
    }
    return true;
  }

  /** return any object with name objname */
  private Optional<ASTODNamedObject> getObject(String objName) {
    return objects.stream().filter(object -> object.getName().equals(objName)).findAny();
  }

  /**
   * Return the target-side of the association; Iff bidirectional, determine side of object and
   * return the other side.
   */
  private ASTCDAssocSide getTargetSide4Object(
      ASTODNamedObject object, ASTCDAssociation association) {

    if (association.getCDAssocDir().isDefinitiveNavigableRight()
        && !association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return association.getRight();
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()
        && !association.getCDAssocDir().isDefinitiveNavigableRight()) {
      return association.getLeft();
    } else if (isInstanceOf(object, association.getLeftQualifiedName().getQName())) {
      return association.getRight();
    }

    return association.getLeft();
  }

  /**
   * Return the source-side of the association; Iff bidirectional, determine side of object and
   * return the other side.
   */
  private ASTCDAssocSide getSourceSide4Object(
      ASTODNamedObject object, ASTCDAssociation association) {

    if (association.getCDAssocDir().isDefinitiveNavigableRight()
        && !association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return association.getLeft();
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()
        && !association.getCDAssocDir().isDefinitiveNavigableRight()) {
      return association.getRight();
    } else if (isInstanceOf(object, association.getLeftQualifiedName().getQName())) {
      return association.getRight();
    }

    return association.getLeft();
  }

  /** Check if object fits type of association-source. */
  private boolean isObject4AssocSrc(ASTODNamedObject object, ASTCDAssociation association) {
    return isObjectInAssociation(
        object,
        association,
        association.getLeftQualifiedName(),
        association.getRightQualifiedName());
  }

  /** Check if object fits type of association-target. */
  private boolean isObject4AssocTarget(ASTODNamedObject object, ASTCDAssociation association) {
    return isObjectInAssociation(
        object,
        association,
        association.getRightQualifiedName(),
        association.getLeftQualifiedName());
  }

  /** Extracted helper-method for isObject4AssocSrc and isObject4AssocTarget. */
  private boolean isObjectInAssociation(
      ASTODNamedObject object,
      ASTCDAssociation association,
      ASTMCQualifiedName nameL2R,
      ASTMCQualifiedName nameR2L) {
    if (association.getCDAssocDir().isDefinitiveNavigableRight()
        && !association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return isInstanceOf(object, nameL2R.getQName());
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()
        && !association.getCDAssocDir().isDefinitiveNavigableRight()) {
      return isInstanceOf(object, nameR2L.getQName());
    } else {
      return isInstanceOf(object, association.getRightQualifiedName().getQName())
          || isInstanceOf(object, association.getLeftQualifiedName().getQName());
    }
  }

  /** Check if object is instance of type. */
  private boolean isInstanceOf(ASTODNamedObject object, String type) {

    // check the intanceof-stereotype iff semantics is STA open-world
    if (semantics.equals(CDSemantics.STA_OPEN_WORLD)) {
      Optional<Set<String>> optSuper = STAObjectMatcher.getSuperSetFromStereotype(object);
      if (optSuper.isPresent()) {
        return optSuper.get().contains(type);
      }
    }
    return CDInheritanceHelper.isSuperOf(type, object.getMCObjectType().printType(), scope);
  }

  /** Checks if link matches association. */
  private boolean matchLinkAgainstAssociation(ASTODLink link, ASTCDAssociation association) {
    String leftRole = CDDiffUtil.inferRole(association.getLeft());
    String leftType = association.getLeftQualifiedName().getQName();
    String rightRole = CDDiffUtil.inferRole(association.getRight());
    String rightType = association.getRightQualifiedName().getQName();

    if (association.getCDAssocDir().isDefinitiveNavigableRight()
        && !association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return matchTypesAndRoles(link, leftType, leftRole, rightRole, rightType);
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()
        && !association.getCDAssocDir().isDefinitiveNavigableRight()) {
      return matchTypesAndRoles(link, rightType, rightRole, leftRole, leftType);
    } else {
      return matchTypesAndRoles(link, leftType, leftRole, rightRole, rightType)
          || matchTypesAndRoles(link, rightType, rightRole, leftRole, leftType);
    }
  }

  /**
   * Check if link-objects are instances of association types && srcRole == link.leftRole &&
   * targetRole == link.rightRole.
   */
  private boolean matchTypesAndRoles(
      ASTODLink link, ASTCDAssociation association, String srcRole, String targetRole) {

    // if left role-name of link is present it should match srcRole
    if (link.getODLinkLeftSide().isPresentRole()
        && !link.getODLinkLeftSide().getRole().equals(srcRole)) {
      return false;
    }

    if (link.getLeftReferenceNames().stream()
        .anyMatch(obj -> !srcMap.get(obj).contains(association))) {
      return false;
    }

    if (link.getRightReferenceNames().stream()
        .anyMatch(obj -> !targetMap.get(obj).contains(association))) {
      return false;
    }

    // right role-name of link should match targetRole
    return link.getODLinkRightSide().isPresentRole()
        && link.getODLinkRightSide().getRole().equals(targetRole);
  }

  /**
   * Check if link-objects are instances of association types && srcRole == link.leftRole &&
   * targetRole == link.rightRole.
   */
  private boolean matchTypesAndRoles(
      ASTODLink link, String srcType, String srcRole, String targetRole, String targetType) {

    if (link.getLeftReferenceNames().stream()
        .anyMatch(obj -> !isInstanceOf(getObject(obj).get(), srcType))) {
      return false;
    }

    /*
    // if left role-name of link is present it should match srcRole
    if (link.getODLinkLeftSide().isPresentRole()
        && !link.getODLinkLeftSide().getRole().equals(srcRole)) {
      return false;
    }
     */

    if (link.getRightReferenceNames().stream()
        .anyMatch(obj -> !isInstanceOf(getObject(obj).get(), targetType))) {
      return false;
    }

    // right role-name of link should match targetRole
    return link.getODLinkRightSide().isPresentRole()
        && link.getODLinkRightSide().getRole().equals(targetRole);
  }

  /** Check if objectNumber is valid according to cardinality card. */
  private boolean checkIfObjectNumberIsValid(ASTCDCardinality card, long objectNumber) {

    if (card.isMult()) {
      return true;
    } else if (card.isAtLeastOne()) {
      if (objectNumber < 1) {
        Log.println("[CONFLICT] Link violates cardinality [+] constraint.");
        return false;
      } else {
        return true;
      }
    } else if (card.isOne()) {
      if (objectNumber != 1) {
        Log.println("[CONFLICT] Link violates cardinality [1] constraint.");
        return false;
      } else {
        return true;
      }
    } else if (card.isOpt()) {
      if (objectNumber > 1) {
        Log.println("[CONFLICT] Link violates cardinality [0..1] constraint.");
        return false;
      } else {
        return true;
      }
    }

    // for possible future extension of available cardinalities
    return ((card.toCardinality().isNoUpperLimit() || objectNumber <= card.getUpperBound())
        && objectNumber >= card.getLowerBound());
  }
}
