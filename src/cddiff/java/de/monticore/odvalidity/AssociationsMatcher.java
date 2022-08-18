package de.monticore.odvalidity;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdassociation.prettyprint.CDAssociationFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.cd2alloy.CD2AlloyQNameHelper;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODName;
import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odlink._ast.ASTODLinkLeftSide;
import de.monticore.odlink._ast.ASTODLinkRightSide;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

public class AssociationsMatcher {

  MCBasicTypesFullPrettyPrinter pp;

  private final static String INSTANCE_OF_STEREOTYPE = "instanceof";

  private boolean openWorldDiffFound;

  private Map<String, Set<ASTCDAssociation>> srcMap;

  private Map<String, Set<ASTCDAssociation>> targetMap;

  private Set<ASTODNamedObject> objects;

  private Set<ASTODLink> links;

  private ASTCDCompilationUnit cd;

  private ICD4CodeArtifactScope scope;

  private CDSemantics semantics;

  public AssociationsMatcher(MCBasicTypesFullPrettyPrinter prettyPrinter) {
    this.pp = prettyPrinter;
    srcMap = new HashMap<>();
    targetMap = new HashMap<>();
  }

  //TODO: consider qualifiers, compare super/sub types when making diff
  public boolean checkAssociations(ASTODArtifact od, ASTCDCompilationUnit cd,
      CDSemantics semantic) {

    this.cd = cd;
    this.scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);

    this.objects = new HashSet<>(ODHelper.getAllNamedObjects(od.getObjectDiagram()));
    this.links = new HashSet<>(
        new NormalizeLinksTrafo().transformLinksToLTR(ODHelper.getAllLinks(od.getObjectDiagram())));

    for (ASTODNamedObject object : objects) {
      Set<ASTCDAssociation> srcSet = cd.getCDDefinition()
          .getCDAssociationsList()
          .stream()
          .filter(assoc -> isObject4AssocSrc(object, assoc))
          .collect(Collectors.toSet());
      Set<ASTCDAssociation> targetSet = cd.getCDDefinition()
          .getCDAssociationsList()
          .stream()
          .filter(assoc -> isObject4AssocTarget(object, assoc))
          .collect(Collectors.toSet());
      srcMap.put(object.getName(),srcSet);
      targetMap.put(object.getName(),srcSet);
    }

    if (Semantic.isClosedWorld(semantic)) {
      for (ASTODLink link : links) {
        if (cd.getCDDefinition()
            .getCDAssociationsList()
            .stream()
            .noneMatch(assoc -> matchLinkAgainstAssociation(link, assoc))) {
          return false;
        }
      }
    }

    //TODO: implement checkInstances(association)
    assert false;
    return true;
  }

  private boolean isObject4AssocSrc(ASTODNamedObject object, ASTCDAssociation association) {
    return isObjectInAssociation(object, association, association.getLeftQualifiedName(),
        association.getRightQualifiedName());
  }

  private boolean isObject4AssocTarget(ASTODNamedObject object, ASTCDAssociation association) {
    return isObjectInAssociation(object, association, association.getRightQualifiedName(),
        association.getLeftQualifiedName());
  }

  private boolean isObjectInAssociation(ASTODNamedObject object, ASTCDAssociation association,
      ASTMCQualifiedName nameL2R, ASTMCQualifiedName nameR2L) {
    if (association.getCDAssocDir().isDefinitiveNavigableRight() && !association.getCDAssocDir()
        .isDefinitiveNavigableLeft()) {
      return isInstanceOf(object, nameL2R.getQName());
    }
    else if (association.getCDAssocDir().isDefinitiveNavigableLeft() && !association.getCDAssocDir()
        .isDefinitiveNavigableRight()) {
      return isInstanceOf(object, nameR2L.getQName());
    }
    else {
      return isInstanceOf(object, association.getRightQualifiedName().getQName()) || isInstanceOf(
          object, association.getLeftQualifiedName().getQName());
    }
  }

  private boolean isInstanceOf(ASTODNamedObject object, String qName) {

    if (semantics.equals(CDSemantics.MULTI_INSTANCE_OPEN_WORLD)){
      Optional<Set<String>> optSuper = MultiInstanceMatcher.getSuperSetFromStereotype(object);
      if (optSuper.isPresent()){
        return optSuper.get().contains(qName);
      }
    }
    return CDInheritanceHelper.isSuperOf(qName,object.getMCObjectType().printType(pp),scope);
  }

  /**
   * Checks if link matches association.
   */
  private boolean matchLinkAgainstAssociation(ASTODLink link, ASTCDAssociation association) {

    String leftType = association.getLeftQualifiedName().getQName();
    String rightType = association.getRightQualifiedName().getQName();

    String leftRole;
    String rightRole;

    if (association.getLeft().isPresentCDRole()) {
      leftRole = association.getLeft().getCDRole().getName();
    }
    else {
      leftRole = CD2AlloyQNameHelper.processQName2RoleName(leftType);
    }

    if (association.getRight().isPresentCDRole()) {
      rightRole = association.getRight().getCDRole().getName();
    }
    else {
      rightRole = CD2AlloyQNameHelper.processQName2RoleName(rightType);
    }

    if (association.getCDAssocDir().isDefinitiveNavigableRight() && !association.getCDAssocDir()
        .isDefinitiveNavigableLeft()) {
      return matchTypesAndRoles(link, association, leftRole, rightRole);
    }
    else if (association.getCDAssocDir().isDefinitiveNavigableLeft() && !association.getCDAssocDir()
        .isDefinitiveNavigableRight()) {
      return matchTypesAndRoles(link, association, rightRole, leftRole);
    }
    else {
      return matchTypesAndRoles(link, association, leftRole, rightRole)
          || matchTypesAndRoles(link, association, rightRole, leftRole);
    }
  }

  private boolean matchTypesAndRoles(ASTODLink link, ASTCDAssociation association, String srcRole,
      String targetRole) {

    // if left role-name of link is present it should match srcRole
    if (link.getODLinkLeftSide().isPresentRole() && !link.getODLinkLeftSide().getRole().equals(srcRole)){
      return false;
    }

    if (link.getLeftReferenceNames().stream().anyMatch(obj -> !srcMap.get(obj).contains(association))){
      return false;
    }

    if (link.getRightReferenceNames().stream().anyMatch(obj -> !targetMap.get(obj).contains(association))){
      return false;
    }

    // right role-name of link should match targetRole
    return link.getODLinkLeftSide().isPresentRole() && link.getODLinkRightSide().getRole().equals(targetRole);
  }




  /**
   * Matches the given object with the class name given. Returns true iff the objects type is the
   * given class or a subclass of it. For open world semantics the object has to have the
   * <<instanceof>> stereotype set for any additional super types. Otherwise the type match might
   * fail for added types.
   *
   * @param cd       cd compilation unit to check in
   * @param object   the object which should be matched
   * @param cdType   the class name that should be matched
   * @param semantic the used semantic to determine diffs strategy
   */
  private boolean isOAndCTypeMatch(ASTCDCompilationUnit cd, ASTODNamedObject object, String cdType,
      CDSemantics semantic) {

    Log.debug(
        "Do type check for association with type " + cdType + " and object " + object.getName(),
        "AssociationMatcherLog");
    String odType = pp.prettyprint(object.getMCObjectType());

    CD4CodeMill.reset();
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();

    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    Set<String> objectSuperTypes = MultiInstanceMatcher.getSuperSet(odType, scope);

    //if present, make use of the instanceOf stereotype
    if (Semantic.isOpenWorld(semantic)) {
      ASTModifier modifier = object.getModifier();
      if (modifier.isPresentStereotype()) {
        if (modifier.getStereotype().contains(INSTANCE_OF_STEREOTYPE)) {
          String instanceStereotype = modifier.getStereotype().getValue(INSTANCE_OF_STEREOTYPE);
          List<String> instances = Arrays.stream(instanceStereotype.split(","))
              .collect(Collectors.toList());
          instances.addAll(objectSuperTypes);
          //in the open world case, with existing "instanceOf" stereotype, we know that the
          // objects type has to inherit from the given class type. Otherwise we can only tell
          // for types present in closed world scenario
          if (instances.stream().anyMatch(sup -> sup.equals(cdType))) {
            return true;
          }
          else {
            openWorldDiffFound = true;
            return false;
          }
        }
      }
    }

    //Without the stereotype the types have to be checked the usual way and open world diffs
    // can't be determined here
    if (objectSuperTypes.stream().anyMatch(sup -> sup.equals(cdType))) {
      return true;
    }
    else {
      Log.println("[CONFLICT] Object of type: " + odType + " did not match class type: " + cdType
          + " or any" + " Subtypes of it.");
      return false;
    }
  }

  private String getAssociationTypeBySide(ASTCDAssociation association, boolean left) {
    if (left) {
      return pp.prettyprint(association.getLeft().getMCQualifiedType());
    }
    else {
      return pp.prettyprint((association.getRight().getMCQualifiedType()));
    }
  }

  private boolean checkCardinality(ASTODLink link, ASTCDAssociation assoc) {

    List<String> left = link.getLeftReferenceNames();
    List<String> right = link.getRightReferenceNames();

    boolean leftOK = true;
    boolean rightOK = true;

    boolean lTR = assoc.getCDAssocDir().isDefinitiveNavigableRight() && !assoc.getCDAssocDir()
        .isDefinitiveNavigableLeft();
    boolean rTL = assoc.getCDAssocDir().isDefinitiveNavigableLeft() && !assoc.getCDAssocDir()
        .isDefinitiveNavigableRight();

    //Those lists are the combined references of all links matching the same association
    long leftNo = left.stream().distinct().count();
    long rightNo = right.stream().distinct().count();

    if (assoc.getLeft().isPresentCDCardinality()) {
      ASTCDCardinality leftCard = assoc.getLeft().getCDCardinality();
      if (lTR) {
        leftOK = compareNumberWithCardinality(leftCard, leftNo);
      }
      else if (rTL) {
        leftOK = compareNumberWithCardinality(leftCard, rightNo);
      }
      else {
        //undirected or bidirectional so check for role names, both roles have to be present
        if (assoc.getLeft().isPresentCDRole() && assoc.getRight().isPresentCDRole()) {
          //the link represents the right to left side of the bidirectional association
          if (assoc.getLeft().getCDRole().getName().equals(link.getODLinkRightSide().getRole())) {
            leftOK = compareNumberWithCardinality(leftCard, rightNo);
          }
        }
        else {
          Log.error("Bidirectional Associations have to have roles in both directions.");
        }
      }
    }

    if (assoc.getRight().isPresentCDCardinality()) {
      ASTCDCardinality rightCard = assoc.getRight().getCDCardinality();
      if (lTR) {
        rightOK = compareNumberWithCardinality(rightCard, rightNo);
      }
      else if (rTL) {
        rightOK = compareNumberWithCardinality(rightCard, leftNo);
      }
      else {
        //undirected or bidirectional so check for role names, both roles have to be present
        if (assoc.getLeft().isPresentCDRole() && assoc.getRight().isPresentCDRole()) {
          //the link represents the left to right side of the bidirectional association
          if (assoc.getRight().getCDRole().getName().equals(link.getODLinkRightSide().getRole())) {
            rightOK = compareNumberWithCardinality(rightCard, rightNo);
          }
        }
        else {
          Log.error("Bidirectional Associations have to have roles in both directions.");
        }
      }
    }

    return leftOK && rightOK;
  }

  /**
   * Sets the associations' role to the type of the side. The affected association side is
   * determined by the isLeftSide flag.
   *
   * @param association an association that should have its role name changed
   * @param isLeftSide  pass in true, if the left association role is supposed to be changed.
   * @return TODO
   */
  private ASTCDAssocSide setAssociationTypeAsRoleName(ASTCDAssociation association,
      boolean isLeftSide) {
    if (isLeftSide) {
      ASTCDAssocLeftSide left = association.getLeft();
      left.setCDRole(
          new ASTCDRoleBuilder().setName(getAssociationTypeBySide(association, true).toLowerCase())
              .build());
      return left;
    }
    else {
      ASTCDAssocRightSide right = association.getRight();
      right.setCDRole(
          new ASTCDRoleBuilder().setName(getAssociationTypeBySide(association, false).toLowerCase())
              .build());
      return right;
    }
  }

  private boolean compareNumberWithCardinality(ASTCDCardinality card, long elements) {

    //is *
    if (card.isMult()) {
      return true;
    }
    else if (card.isAtLeastOne()) {
      if (elements < 1) {
        Log.println("[CONFLICT] Link violates cardinality [+] constraint.");
        return false;
      }
      else {
        return true;
      }
    }
    else if (card.isOne()) {
      if (elements != 1) {
        Log.println("[CONFLICT] Link violates cardinality [1] constraint.");
        return false;
      }
    }
    else if (card.isOpt()) {
      if (elements > 1) {
        Log.println("[CONFLICT] Link violates cardinality [1] constraint.");
        return false;
      }
    }

    return ((card.toCardinality().isNoUpperLimit() || elements <= card.getUpperBound())
        && elements >= card.getLowerBound());

  }

  /**
   * Checks if an association is required due to its cardinalities. Eg. A[+] -> B[+] would have to
   * be represented by a link, since both sides have to reference at least one object.
   *
   * @return true if the association is required.
   */
  private boolean isRequiredDueToCardinality(ASTCDAssociation assoc) {
    //only if both sides have at least one cardinality, they are required
    return hasCardinalityAtLeastGreaterZero(assoc.getRight()) && hasCardinalityAtLeastGreaterZero(
        assoc.getLeft());
  }

  /**
   * Helper method to check if there has to be at least one object linked to fit this association.
   */
  private boolean hasCardinalityAtLeastGreaterZero(ASTCDAssocSide side) {
    if (side.isPresentCDCardinality()) {
      ASTCDCardinality card = side.getCDCardinality();
      return card.isAtLeastOne() || card.isOne();
    }
    else {
      return false;
    }
  }

}