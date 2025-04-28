package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.CompletionException;

/** Completes a concrete CD such that it conforms to a given reference CD. */
public interface ICDCompleter {

  /**
   * Completes the given concrete CD such that it conforms to a given reference CD.<br>
   * <br>
   * The completion process is influenced by the given {@link CDCompletionContext}. It provides
   * information about the incarnation mapping and other configuration parameters.
   *
   * @param concreteCD the concrete CD to be completed
   * @param referenceCD the reference CD to be used for completion
   * @param context the context of the completion
   *
   * @throws CompletionException if the concrete CD cannot be completed to conform to the reference.
   */
  void complete(
      ASTCDCompilationUnit concreteCD,
      ASTCDCompilationUnit referenceCD,
      CDCompletionContext context)
      throws CompletionException;
}
