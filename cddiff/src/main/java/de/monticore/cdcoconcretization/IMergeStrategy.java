package de.monticore.cdcoconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public interface IMergeStrategy {
  ASTCDCompilationUnit merge(ASTCDCompilationUnit rcd, ASTCDCompilationUnit iccd);
}
