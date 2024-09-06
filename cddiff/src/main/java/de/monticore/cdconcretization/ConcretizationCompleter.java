package de.monticore.cdconcretization;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class ConcretizationCompleter implements IMergeStrategy {

  @Override
  public ASTCDCompilationUnit merge(ASTCDCompilationUnit rcd, ASTCDCompilationUnit iccd)
      throws CompletionException {
    ASTCDCompilationUnit cccd = iccd.deepClone();

    DefaultTypeIncCompleter typeCompleter = new DefaultTypeIncCompleter(cccd, rcd, "ref");
    DefaultAssocIncCompleter assocCompleter = new DefaultAssocIncCompleter(cccd, rcd, "ref");

    typeCompleter.completeIncarnations();
    assocCompleter.completeIncarnations();

    return cccd;
  }
}
