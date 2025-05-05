package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.CompletionException;

/** Completes a concrete CD such that it conforms to a given reference CD. */
public interface ICDCompleter {
  void complete(
      ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD,
      CDCompletionContext context)
      throws CompletionException;
}
