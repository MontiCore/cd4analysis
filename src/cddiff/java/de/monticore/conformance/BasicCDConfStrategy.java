package de.monticore.conformance;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;

public class BasicCDConfStrategy implements ConformanceStrategy<ASTCDCompilationUnit> {
  protected ASTCDCompilationUnit refCD;
  protected ConformanceStrategy<ASTCDType> typeChecker;
  protected ConformanceStrategy<ASTCDAssociation> assocChecker;

  public BasicCDConfStrategy(
      ASTCDCompilationUnit refCD,
      ConformanceStrategy<ASTCDType> typeChecker,
      ConformanceStrategy<ASTCDAssociation> assocChecker) {
    this.refCD = refCD;
    this.typeChecker = typeChecker;
    this.assocChecker = assocChecker;
  }

  @Override
  public boolean checkConformance(ASTCDCompilationUnit concrete) {
    // todo: check classes, interfaces, enums, associations and attributes
    return false;
  }
}
