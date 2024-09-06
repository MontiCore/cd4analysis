package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public interface IMergeStrategy {
  ASTCDCompilationUnit merge(ASTCDCompilationUnit rcd, ASTCDCompilationUnit iccd)
      throws CompletionException;
}
