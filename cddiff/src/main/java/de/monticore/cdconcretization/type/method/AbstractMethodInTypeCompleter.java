package de.monticore.cdconcretization.type.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconcretization.util.IChainable;

public class AbstractMethodInTypeCompleter
    implements IMethodInTypeCompleter, IChainable<AbstractMethodInTypeCompleter> {

  private AbstractMethodInTypeCompleter next;

  @Override
  public void completeMethodInType(
      ASTCDType concreteType, ASTCDMethod referenceMethod, TypeCompletionContext context)
      throws CompletionException {
    if (next != null) {
      next.completeMethodInType(concreteType, referenceMethod, context);
    }
  }

  @Override
  public void setNext(AbstractMethodInTypeCompleter next) {
    this.next = next;
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }
}
