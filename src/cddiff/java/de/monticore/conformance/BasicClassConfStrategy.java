package de.monticore.conformance;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;

public class BasicClassConfStrategy implements ConformanceStrategy<ASTCDClass> {
  protected ASTCDCompilationUnit refCD;
  protected ASTCDCompilationUnit conCD;
  protected IncarnationStrategy<ASTCDType> typeInc;
  protected IncarnationStrategy<ASTCDAssociation> assocInc;

  public BasicClassConfStrategy(
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
  public boolean checkConformance(ASTCDClass concrete) {
    return typeInc.getRefElements(concrete).stream()
        .allMatch(ref -> checkConformance(concrete, ref));
  }

  public boolean checkConformance(ASTCDClass concrete, ASTCDType reference) {
    // todo: check modifier, attributes, associations, interfaces, etc...
    if (reference.getModifier().isAbstract() && !concrete.getModifier().isAbstract()) {
      return false;
    }
    boolean attributes =
        reference.getCDAttributeList().stream()
            .allMatch(
                rAttr ->
                    concrete.getCDAttributeList().stream()
                        .anyMatch(cAttr -> cAttr.deepEquals(rAttr)));
    boolean associations =
        refCD.getCDDefinition().getCDAssociationsList().stream()
            .filter(
                rAssoc ->
                    reference
                            .getSymbol()
                            .getInternalQualifiedName()
                            .contains(rAssoc.getLeftQualifiedName().getQName())
                        || reference
                            .getSymbol()
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
    boolean superTypes =
        CDDiffUtil.getAllSuperTypes(reference, refCD.getCDDefinition()).stream()
            .allMatch(
                refSuper ->
                    CDDiffUtil.getAllSuperTypes(concrete, conCD.getCDDefinition()).stream()
                        .anyMatch(conSuper -> typeInc.getRefElements(conSuper).contains(refSuper)));
    return attributes && associations && superTypes;
  }
}
