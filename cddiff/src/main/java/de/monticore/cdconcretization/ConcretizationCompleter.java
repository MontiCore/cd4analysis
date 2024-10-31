package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class ConcretizationCompleter implements ICompletionStrategy {

  @Override
  public ASTCDCompilationUnit complete(ASTCDCompilationUnit rcd, ASTCDCompilationUnit iccd)
      throws CompletionException {

    DefaultTypeIncCompleter typeCompleter = new DefaultTypeIncCompleter(iccd, rcd, "ref");
    DefaultAssocIncCompleter assocCompleter = new DefaultAssocIncCompleter(iccd, rcd, "ref");

    typeCompleter.completeIncarnations();
    assocCompleter.completeIncarnations();

    ConcretizationHelper.reorderElements(iccd.getCDDefinition());

    return iccd;
  }
}
