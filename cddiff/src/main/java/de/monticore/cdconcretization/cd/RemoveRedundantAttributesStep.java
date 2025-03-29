package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.AbstractCDCompleter;
import de.monticore.cdconcretization.CompletionContext;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.ReductionTrafo;

public class RemoveRedundantAttributesStep extends AbstractCDCompleter {

  @Override
  public void complete(
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD, CompletionContext context)
      throws CompletionException {
    // 5.1 remove redundancies that may have been introduced by inheritance
    CDDiffUtil.refreshSymbolTable(concreteCD);
    ReductionTrafo.removeRedundantAttributes(concreteCD);
    super.complete(concreteCD, referenceCD, context);
  }
}
