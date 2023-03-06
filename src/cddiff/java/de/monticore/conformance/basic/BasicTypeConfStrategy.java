package de.monticore.conformance.basic;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.conformance.ConformanceStrategy;
import de.monticore.conformance.inc.IncarnationStrategy;

public class BasicTypeConfStrategy implements ConformanceStrategy<ASTCDType> {
  protected ASTCDCompilationUnit refCD;
  protected ASTCDCompilationUnit conCD;
  protected IncarnationStrategy<ASTCDType> typeInc;
  protected IncarnationStrategy<ASTCDAssociation> assocInc;

  public BasicTypeConfStrategy(
      ASTCDCompilationUnit refCD,
      ASTCDCompilationUnit conCD,
      IncarnationStrategy<ASTCDType> typeInc,
      IncarnationStrategy<ASTCDAssociation> assocInc) {
    this.refCD = refCD;
    this.conCD = conCD;
    this.typeInc = typeInc;
    this.assocInc = assocInc;
  }

  @Override
  public boolean checkConformance(ASTCDType concrete) {
    return typeInc.getRefElements(concrete).stream()
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
    boolean attributes =
        ref.getCDAttributeList().stream()
            .allMatch(
                rAttr ->
                    concrete.getCDAttributeList().stream()
                        .anyMatch(cAttr -> cAttr.deepEquals(rAttr)));

    // check if reference associations are incarnated
    boolean associations =
        refCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(
                rAssoc ->
                    ref.getSymbol()
                            .getInternalQualifiedName()
                            .contains(rAssoc.getLeftQualifiedName().getQName())
                        || ref.getSymbol()
                            .getInternalQualifiedName()
                            .contains(rAssoc.getRightQualifiedName().getQName()))
            .allMatch(
                rAssoc ->
                    conCD.getCDDefinition().getCDAssociationsList().stream()
                        .filter(
                            cAssoc ->
                                concrete
                                        .getSymbol()
                                        .getInternalQualifiedName()
                                        .contains(cAssoc.getLeftQualifiedName().getQName())
                                    || concrete
                                        .getSymbol()
                                        .getInternalQualifiedName()
                                        .contains(cAssoc.getRightQualifiedName().getQName()))
                        .anyMatch(cAssoc -> assocInc.getRefElements(cAssoc).contains(rAssoc)));

    // check if all reference super-types are incarnated
    boolean superTypes =
        CDDiffUtil.getAllSuperTypes(ref, refCD.getCDDefinition()).stream()
            .allMatch(
                refSuper ->
                    CDDiffUtil.getAllSuperTypes(concrete, conCD.getCDDefinition()).stream()
                        .anyMatch(conSuper -> typeInc.getRefElements(conSuper).contains(refSuper)));
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
}
