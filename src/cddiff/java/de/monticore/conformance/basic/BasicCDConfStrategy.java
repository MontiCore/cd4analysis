package de.monticore.conformance.basic;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.conformance.ConformanceStrategy;
import de.monticore.conformance.inc.IncarnationStrategy;
import java.util.HashSet;
import java.util.Set;

public class BasicCDConfStrategy implements ConformanceStrategy<ASTCDCompilationUnit> {
  protected ASTCDCompilationUnit refCD;
  protected IncarnationStrategy<ASTCDType> typeInc;
  protected IncarnationStrategy<ASTCDAssociation> assocInc;
  protected ConformanceStrategy<ASTCDType> typeChecker;
  protected ConformanceStrategy<ASTCDAssociation> assocChecker;

  public BasicCDConfStrategy(
      ASTCDCompilationUnit refCD,
      IncarnationStrategy<ASTCDType> typeInc,
      IncarnationStrategy<ASTCDAssociation> assocInc,
      ConformanceStrategy<ASTCDType> typeChecker,
      ConformanceStrategy<ASTCDAssociation> assocChecker) {
    this.refCD = refCD;
    this.typeInc = typeInc;
    this.assocInc = assocInc;
    this.typeChecker = typeChecker;
    this.assocChecker = assocChecker;
  }

  @Override
  public boolean checkConformance(ASTCDCompilationUnit concrete) {
    return checkAssocIncarnation(concrete)
        && checkTypeIncarnation(concrete)
        && checkTypeConformance(concrete)
        && checkAssocConformance(concrete);
  }

  protected boolean checkAssocConformance(ASTCDCompilationUnit concrete) {
    return false;
  }

  protected boolean checkTypeConformance(ASTCDCompilationUnit concrete) {
    boolean classConformance =
        concrete.getCDDefinition().getCDClassesList().stream()
            .allMatch(conClass -> typeChecker.checkConformance(conClass));
    boolean interfaceConformance =
        concrete.getCDDefinition().getCDInterfacesList().stream()
            .allMatch(conInterface -> typeChecker.checkConformance(conInterface));
    boolean enumConformance =
        concrete.getCDDefinition().getCDEnumsList().stream()
            .allMatch(conEnum -> typeChecker.checkConformance(conEnum));
    return classConformance && interfaceConformance && enumConformance;
  }

  protected boolean checkTypeIncarnation(ASTCDCompilationUnit concrete) {
    Set<ASTCDType> refTypes = new HashSet<>(refCD.getCDDefinition().getCDClassesList());
    refTypes.addAll(refCD.getCDDefinition().getCDInterfacesList());
    refTypes.addAll(refCD.getCDDefinition().getCDEnumsList());

    Set<ASTCDType> conTypes = new HashSet<>(concrete.getCDDefinition().getCDClassesList());
    conTypes.addAll(concrete.getCDDefinition().getCDInterfacesList());
    conTypes.addAll(concrete.getCDDefinition().getCDEnumsList());

    return refTypes.stream()
        .allMatch(
            refType -> conTypes.stream().anyMatch(conType -> typeInc.isInstance(conType, refType)));
  }

  protected boolean checkAssocIncarnation(ASTCDCompilationUnit concrete) {
    return refCD.getCDDefinition().getCDAssociationsList().stream()
        .allMatch(
            refAssoc ->
                concrete.getCDDefinition().getCDAssociationsList().stream()
                    .anyMatch(conAssoc -> assocInc.isInstance(conAssoc, refAssoc)));
  }
}
