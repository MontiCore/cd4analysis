package de.monticore.cdcoconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class CoconcretizationMerge implements IMergeStrategy {

  @Override
  public ASTCDCompilationUnit merge(ASTCDCompilationUnit rcd, ASTCDCompilationUnit iccd) {
    ASTCDCompilationUnit cccd = iccd.deepClone();

    // Identify and copy missing types
    // use identifyAndAddMissingTypeIncarnations

    // complete type incarnations
    // use method completeTypeIncarnations

    // identify and copy missing associations

    // handle conflicts

    return cccd;
  }
}
