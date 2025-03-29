package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

/** Completes a concrete CD such that it conforms to a given reference CD. */
public interface ICDCompleter {
  void complete(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, CompletionContext context)
      throws CompletionException;
}
