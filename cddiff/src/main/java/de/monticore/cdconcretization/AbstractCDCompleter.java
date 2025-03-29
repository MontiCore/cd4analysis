package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.util.AbstractChainable;

public abstract class AbstractCDCompleter extends AbstractChainable<AbstractCDCompleter>
    implements ICDCompleter {
  @Override
  public void complete(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD)
      throws CompletionException {
    next(concreteCD, referenceCD);
  }

  /**
   * Calls the next completer in the chain if it is present.
   *
   * @param concreteCD
   * @param referenceCD
   */
  protected void next(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD)
      throws CompletionException {
    if (hasNext()) {
      next.complete(concreteCD, referenceCD);
    }
  }
}
