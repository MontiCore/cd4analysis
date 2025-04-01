package de.monticore.cdconcretization.type.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconcretization.util.IChainable;

public abstract class AbstractTypeAttributeCompleter implements ITypeAttributeCompleter,
        IChainable<AbstractTypeAttributeCompleter> {

  private AbstractTypeAttributeCompleter next;

  @Override
  public void completeTypeForAttribute(
      ASTCDType concreteType, ASTCDAttribute referenceAttribute, TypeCompletionContext context)
      throws CompletionException {
    next(concreteType, referenceAttribute, context);
  }

  /**
   * Calls the next completer in the chain if it is present.
   *
   * @param concreteType
   * @param referenceAttribute
   */
  protected void next(
      ASTCDType concreteType, ASTCDAttribute referenceAttribute, TypeCompletionContext context)
      throws CompletionException {
    if (hasNext()) {
      next.completeTypeForAttribute(concreteType, referenceAttribute, context);
    }
  }

  @Override
  public void setNext(AbstractTypeAttributeCompleter next) {
    this.next = next;
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }
}
