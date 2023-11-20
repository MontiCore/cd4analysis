package de.monticore.conformance.conf.type;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.conformance.conf.AttributeChecker;
import de.monticore.conformance.conf.ConformanceStrategy;
import de.monticore.matcher.MatchingStrategy;
import java.util.HashSet;
import java.util.Set;

public class BasicTypeConfStrategy implements ConformanceStrategy<ASTCDType> {
  protected ASTCDCompilationUnit refCD;
  protected ASTCDCompilationUnit conCD;

  protected AttributeChecker attributeChecker;
  protected MatchingStrategy<ASTCDType> typeInc;
  protected MatchingStrategy<ASTCDAssociation> assocInc;

  public BasicTypeConfStrategy(
      ASTCDCompilationUnit conCD,
      ASTCDCompilationUnit refCD,
      AttributeChecker attributeChecker,
      MatchingStrategy<ASTCDType> typeInc,
      MatchingStrategy<ASTCDAssociation> assocInc) {
    this.refCD = refCD;
    this.conCD = conCD;
    this.attributeChecker = attributeChecker;
    this.typeInc = typeInc;
    this.assocInc = assocInc;
  }

  @Override
  public boolean checkConformance(ASTCDType concrete) {
    return typeInc.getMatchedElements(concrete).stream()
        .allMatch(ref -> checkConformance(concrete, ref));
  }

  public boolean checkConformance(ASTCDType concrete, ASTCDType ref) {

    // an enum must be incarnated as an enum
    if (ref instanceof ASTCDEnum) {
      if (concrete instanceof ASTCDEnum) {
        return checkConformance((ASTCDEnum) concrete, (ASTCDEnum) ref);
      }
      return false;
    } else {
      // if ref is not an enum, then concrete should not be an enum, either
      if (concrete instanceof ASTCDEnum) {
        return false;
      }
    }

    // a class must be incarnated as a class
    if (ref instanceof ASTCDClass) {
      if (!(concrete instanceof ASTCDClass)) {
        return false;
      }
      // abstract classes must be incarnated as abstract classes
      if (ref.getModifier().isAbstract() && !concrete.getModifier().isAbstract()) {
        return false;
      }
    }

    // check if all necessary attributes are present
    attributeChecker.setReferenceType(ref);
    attributeChecker.setConcreteType(concrete);
    boolean attributes =
        checkAttributeIncarnation(concrete, ref) && checkAttributeConformance(concrete);

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
    if (attributes && associations && superTypes) {
      return true;
    }
    System.out.println(
        CD4CodeMill.prettyPrint(concrete, false)
            + " does not conform to "
            + CD4CodeMill.prettyPrint(ref, false));
    if (!attributes) {
      System.out.println("Attributes do not match!");
    }
    if (!associations) {
      System.out.println("Associations do not match!");
    }
    if (!superTypes) {
      System.out.println("Super-types do not match!");
    }
    return false;
  }

  public boolean checkConformance(ASTCDEnum concrete, ASTCDEnum ref) {
    return concrete.getCDEnumConstantList().stream()
        .allMatch(conConst -> ref.getCDEnumConstantList().stream().anyMatch(conConst::deepEquals));
  }

  /** check if all attributes of the reference type are incarnated */
  protected boolean checkAttributeIncarnation(ASTCDType con, ASTCDType ref) {
    return checkIncarnationAt(
        (new HashSet<>(con.getCDAttributeList())), new HashSet<>(ref.getCDAttributeList()));
  }

  /** check if all attributes that are incarnations are conformed to the references */
  protected boolean checkAttributeConformance(ASTCDType con) {
    return checkConformanceAt(new HashSet<>(con.getCDAttributeList()));
  }

  /** check if all associations of the reference type are incarnated */
  protected boolean checkAssocIncarnation(ASTCDType conType, ASTCDType refType) {
    return checkIncarnationAs(
        CDDiffUtil.getReferencingAssociations(conType, conCD),
        CDDiffUtil.getReferencingAssociations(refType, refCD));
  }

  protected boolean checkIncarnationAs(Set<ASTCDAssociation> con, Set<ASTCDAssociation> ref) {
    return ref.stream()
        .allMatch(
            refAssoc ->
                con.stream()
                    .anyMatch(cAssoc -> assocInc.getMatchedElements(cAssoc).contains(refAssoc)));
  }

  protected boolean checkIncarnationAt(Set<ASTCDAttribute> con, Set<ASTCDAttribute> ref) {
    return ref.stream()
        .allMatch(
            refAttr ->
                con.stream().anyMatch(conAttr -> attributeChecker.isMatched(conAttr, refAttr)));
  }

  protected boolean checkConformanceAt(Set<ASTCDAttribute> concrete) {
    return concrete.stream()
        .allMatch(
            conAttr ->
                attributeChecker.getMatchedElements(conAttr).isEmpty()
                    || attributeChecker.checkConformance(conAttr));
  }
}
