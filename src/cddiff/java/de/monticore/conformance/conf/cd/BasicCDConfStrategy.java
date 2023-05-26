package de.monticore.conformance.conf.cd;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.conformance.conf.ConformanceStrategy;
import de.monticore.conformance.inc.IncarnationStrategy;
import de.se_rwth.commons.logging.Log;
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
    return checkTypeIncarnation(concrete)
        && checkAssocIncarnation(concrete)
        && checkTypeConformance(concrete)
        && checkAssocConformance(concrete);
  }

  protected boolean checkAssocConformance(ASTCDCompilationUnit concrete) {
    return concrete.getCDDefinition().getCDAssociationsList().stream()
        .allMatch(conAssoc -> assocChecker.checkConformance(conAssoc));
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

    for (ASTCDType refType : refTypes) {
      if (conTypes.stream().noneMatch(conType -> typeInc.isIncarnation(conType, refType))) {
        Log.println(refType.getName() + " has no incarnation!");
        return false;
      }
    }
    return true;
  }

  protected boolean checkAssocIncarnation(ASTCDCompilationUnit concrete) {

    for (ASTCDAssociation refAssoc : refCD.getCDDefinition().getCDAssociationsList()) {
      if (concrete.getCDDefinition().getCDAssociationsList().stream()
          .noneMatch(conAssoc -> assocInc.isIncarnation(conAssoc, refAssoc))) {
        Log.println(CD4CodeMill.prettyPrint(refAssoc, false) + " has no incarnation!");
        return false;
      }
    }
    return true;
  }
}
