/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.ReductionTrafo;

public class RemoveRedundanciesCompletionStep extends AbstractCDCompleter {
  
  @Override
  public void complete(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD,
      CDCompletionContext context) throws CompletionException {
    // 5.1 remove redundancies that may have been introduced by inheritance
    CDDiffUtil.refreshSymbolTable(concreteCD);
    ReductionTrafo.removeRedundantAttributes(concreteCD);
    super.complete(concreteCD, referenceCD, context);
  }
  
}
