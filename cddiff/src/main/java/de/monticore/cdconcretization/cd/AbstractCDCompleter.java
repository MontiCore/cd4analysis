/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.cd;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.util.IChainable;

public abstract class AbstractCDCompleter implements ICDCompleter, IChainable<AbstractCDCompleter> {
  
  private AbstractCDCompleter next;
  
  @Override
  public void complete(ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD,
      CDCompletionContext context) throws CompletionException {
    if (hasNext()) {
      next.complete(concreteCD, referenceCD, context);
    }
  }
  
  @Override
  public void setNext(AbstractCDCompleter next) { this.next = next; }
  
  @Override
  public boolean hasNext() {
    return next != null;
  }
  
}
