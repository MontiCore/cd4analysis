package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class ConcretizationCompleter implements ICompletionStrategy {

  @Override
  public ASTCDCompilationUnit complete(ASTCDCompilationUnit rcd, ASTCDCompilationUnit ccd)
      throws CompletionException {

    DefaultTypeIncCompleter typeCompleter = new DefaultTypeIncCompleter(ccd, rcd, "ref");
    DefaultAssocIncCompleter assocCompleter = new DefaultAssocIncCompleter(ccd, rcd, "ref");

    typeCompleter.completeIncarnations();
    assocCompleter.completeIncarnations();

    ConcretizationHelper.reorderElements(ccd.getCDDefinition());

    return ccd;
  }
}
