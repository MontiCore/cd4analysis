package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.util.AbstractChainable;

public abstract class AbstractCDCompleter extends AbstractChainable<AbstractCDCompleter>
    implements ICDCompleter {
  @Override
  public void complete(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, CompletionContext context)
      throws CompletionException {
    if (hasNext()) {
      next.complete(concreteCD, referenceCD, context);
    }
  }
}
