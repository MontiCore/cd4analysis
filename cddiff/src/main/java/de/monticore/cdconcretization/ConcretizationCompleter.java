package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class ConcretizationCompleter implements ICompletionStrategy {

  private final String mapping;

  public ConcretizationCompleter(String mapping) {
    this.mapping = mapping;
  }

  @Override
  public ASTCDCompilationUnit complete(ASTCDCompilationUnit rcd, ASTCDCompilationUnit ccd)
      throws CompletionException {

    DefaultTypeIncCompleter typeCompleter = new DefaultTypeIncCompleter(ccd, rcd, mapping);
    DefaultAssocIncCompleter assocCompleter = new DefaultAssocIncCompleter(ccd, rcd, mapping);

    typeCompleter.completeIncarnations();
    assocCompleter.completeIncarnations();

    ConcretizationHelper.reorderElements(ccd.getCDDefinition());

    return ccd;
  }
}
