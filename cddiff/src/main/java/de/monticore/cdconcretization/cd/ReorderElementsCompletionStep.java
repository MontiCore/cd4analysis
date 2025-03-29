package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.AbstractCDCompleter;
import de.monticore.cdconcretization.CompletionContext;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.ConcretizationHelper;

public class ReorderElementsCompletionStep extends AbstractCDCompleter {

  @Override
  public void complete(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, CompletionContext context)
      throws CompletionException {
    // 5.2 reorder so we have a consistent output
    ConcretizationHelper.reorderElements(concreteCD.getCDDefinition());
    super.complete(concreteCD, referenceCD, context);
  }
}
