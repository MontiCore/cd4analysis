package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public interface ICompletionStrategy {
  ASTCDCompilationUnit complete(ASTCDCompilationUnit rcd, ASTCDCompilationUnit iccd)
      throws CompletionException;
}
