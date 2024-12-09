package de.monticore.cdconformance.conf.type;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.CDAttributeChecker;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.conf.ICDMethodChecker;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdmatcher.MatchingStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Collectors;

public class BasicTypeConfStrategy implements ConformanceStrategy<ASTCDType> {
  protected ASTCDCompilationUnit refCD;
  protected ASTCDCompilationUnit conCD;

  protected CDAttributeChecker attributeChecker;

  protected ICDMethodChecker methodChecker;
  protected MatchingStrategy<ASTCDType> typeInc;
  protected MatchingStrategy<ASTCDAssociation> assocInc;

  public BasicTypeConfStrategy(
      ASTCDCompilationUnit conCD,
      ASTCDCompilationUnit refCD,
      CDAttributeChecker attributeChecker,
      ICDMethodChecker methodChecker,
      MatchingStrategy<ASTCDType> typeInc,
      MatchingStrategy<ASTCDAssociation> assocInc) {
    this.refCD = refCD;
    this.conCD = conCD;
    this.attributeChecker = attributeChecker;
    this.methodChecker = methodChecker;
    this.typeInc = typeInc;
    this.assocInc = assocInc;
  }

  @Override
  public boolean checkConformance(ASTCDType concrete) {
    Set<ASTCDType> nonConformingTo =
        typeInc.getMatchedElements(concrete).stream()
            .filter(ref -> !checkConformance(concrete, ref))
            .collect(Collectors.toSet());
    return nonConformingTo.isEmpty();
  }

  public boolean checkConformance(ASTCDType concrete, ASTCDType ref) {

    // an enum must be incarnated as an enum
    if (ref instanceof ASTCDEnum) {
      if (concrete instanceof ASTCDEnum) {
        return checkConformance((ASTCDEnum) concrete, (ASTCDEnum) ref);
      }
      Log.println(
          concrete.getSymbol().getInternalQualifiedName()
              + " (l."
              + concrete.get_SourcePositionStart()
              + ") must be an enumeration-type!");
      return false;
    } else {
      // if ref is not an enum, then concrete should not be an enum, either
      if (concrete instanceof ASTCDEnum) {
        Log.println(
            concrete.getSymbol().getInternalQualifiedName()
                + " (l."
                + concrete.get_SourcePositionStart()
                + ") must not be an enumeration-type!");
        return false;
      }
    }

    // a class must be incarnated as a class
    if (ref instanceof ASTCDClass) {
      if (!(concrete instanceof ASTCDClass)) {
        Log.println(
            concrete.getSymbol().getInternalQualifiedName()
                + " (l."
                + concrete.get_SourcePositionStart()
                + ") must be a class and not an interface!");
        return false;
      }
      // abstract classes must be incarnated as abstract classes
      if (ref.getModifier().isAbstract() && !concrete.getModifier().isAbstract()) {
        Log.println(
            concrete.getSymbol().getInternalQualifiedName()
                + " (l."
                + concrete.get_SourcePositionStart()
                + ") must be abstract!");
        return false;
      }
    }

    // check if all necessary attributes are present
    attributeChecker.setReferenceType(ref);
    attributeChecker.setConcreteType(concrete);
    boolean attributes =
        checkAttributeIncarnation(concrete, ref) && checkAttributeConformance(concrete);

    // check if all necessary methods are present
    methodChecker.setReferenceType(ref);
    methodChecker.setConcreteType(concrete);
    boolean methods = checkMethodIncarnation(concrete, ref) && checkMethodConformance(concrete);

    // check if reference associations are incarnated
    boolean associations = checkAssocIncarnation(concrete, ref);

    // check if all reference super-types are incarnated
    boolean superTypes =
        CDDiffUtil.getAllSuperTypes(ref, refCD.getCDDefinition()).stream()
            .allMatch(
                refSuper ->
                    CDDiffUtil.getAllSuperTypes(concrete, conCD.getCDDefinition()).stream()
                        .anyMatch(
                            conSuper -> typeInc.getMatchedElements(conSuper).contains(refSuper)));
    if (attributes && methods && associations && superTypes) {
      return true;
    }
    System.out.println(
        concrete.getSymbol().getInternalQualifiedName()
            + " is not a valid incarnation of "
            + ref.getSymbol().getInternalQualifiedName());
    if (!attributes) {
      System.out.println("Incarnations of attributes are missing or incorrect!");
    }
    if (!methods) {
      System.out.println("Incarnations of methods are missing or incorrect!");
    }
    if (!associations) {
      System.out.println("Incarnations of associations are missing or incorrect!");
    }
    if (!superTypes) {
      System.out.println("Incarnations of super-types are missing or incorrect!");
    }
    return false;
  }

  /** We check that all reference enum constants are preserved in relative order. */
  public boolean checkConformance(ASTCDEnum concrete, ASTCDEnum ref) {

    List<ASTCDEnumConstant> cConstants = new ArrayList<>(concrete.getCDEnumConstantList());

    for (ASTCDEnumConstant rConstant : ref.getCDEnumConstantList()) {
      Optional<ASTCDEnumConstant> cConstant =
          cConstants.stream().filter(r -> r.getName().equals(rConstant.getName())).findFirst();
      if (cConstant.isEmpty()) {
        System.out.println(
            concrete.getSymbol().getInternalQualifiedName()
                + " is not a valid incarnation of "
                + ref.getSymbol().getInternalQualifiedName());
        System.out.println(
            "Enumeration constant: "
                + rConstant.getName()
                + "is missing or at the wrong position!");
        return false;
      }
      cConstants = cConstants.subList(cConstants.indexOf(cConstant.get()), cConstants.size());
    }

    return true;
  }

  /** check if all attributes of the reference type are incarnated */
  protected boolean checkAttributeIncarnation(ASTCDType con, ASTCDType ref) {
    return checkAttributeIncarnation(
        (new HashSet<>(con.getCDAttributeList())), new HashSet<>(ref.getCDAttributeList()));
  }

  /** check if all methods of the reference type are incarnated */
  protected boolean checkMethodIncarnation(ASTCDType con, ASTCDType ref) {
    return checkMethodIncarnation(
        (new HashSet<>(con.getCDMethodList())), new HashSet<>(ref.getCDMethodList()));
  }

  /** check if all attributes that are incarnations are conformed to the references */
  protected boolean checkAttributeConformance(ASTCDType con) {
    return checkAttributeConformance(new HashSet<>(con.getCDAttributeList()));
  }

  /** check if all methods that are incarnations are conformed to the references */
  protected boolean checkMethodConformance(ASTCDType con) {
    return checkMethodConformance(new HashSet<>(con.getCDMethodList()));
  }

  /** check if all associations of the reference type are incarnated */
  protected boolean checkAssocIncarnation(ASTCDType conType, ASTCDType refType) {
    return checkAssocIncarnation(
        CDDiffUtil.getReferencingAssociations(conType, conCD),
        CDDiffUtil.getReferencingAssociations(refType, refCD));
  }

  protected boolean checkAssocIncarnation(Set<ASTCDAssociation> con, Set<ASTCDAssociation> ref) {
    return ref.stream()
        .allMatch(
            refAssoc ->
                (refAssoc.getModifier().isPresentStereotype()
                        && refAssoc.getModifier().getStereotype().contains("optional"))
                    || con.stream()
                        .anyMatch(
                            cAssoc -> assocInc.getMatchedElements(cAssoc).contains(refAssoc)));
  }

  protected boolean checkAttributeIncarnation(Set<ASTCDAttribute> con, Set<ASTCDAttribute> ref) {
    return ref.stream()
        .allMatch(
            refAttr ->
                (refAttr.getModifier().isPresentStereotype()
                        && refAttr.getModifier().getStereotype().contains("optional"))
                    || con.stream()
                        .anyMatch(conAttr -> attributeChecker.isMatched(conAttr, refAttr)));
  }

  protected boolean checkMethodIncarnation(Set<ASTCDMethod> con, Set<ASTCDMethod> ref) {
    return ref.stream()
        .allMatch(
            refMethod ->
                (refMethod.getModifier().isPresentStereotype()
                        && refMethod.getModifier().getStereotype().contains("optional"))
                    || con.stream()
                        .anyMatch(conMethod -> methodChecker.isMatched(conMethod, refMethod)));
  }

  protected boolean checkAttributeConformance(Set<ASTCDAttribute> concrete) {
    return concrete.stream()
        .allMatch(
            conAttr ->
                attributeChecker.getMatchedElements(conAttr).isEmpty()
                    || attributeChecker.checkConformance(conAttr));
  }

  protected boolean checkMethodConformance(Set<ASTCDMethod> concrete) {
    return concrete.stream()
        .allMatch(
            conMethod ->
                methodChecker.getMatchedElements(conMethod).isEmpty()
                    || methodChecker.checkConformance(conMethod));
  }
}
