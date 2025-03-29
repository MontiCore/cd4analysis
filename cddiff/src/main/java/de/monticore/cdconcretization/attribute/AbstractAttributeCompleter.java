package de.monticore.cdconcretization.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.util.AbstractChainable;

public abstract class AbstractAttributeCompleter
    extends AbstractChainable<AbstractAttributeCompleter> implements IAttributeCompleter {

  @Override
  public void completeAttribute(
      ASTCDType concreteType, ASTCDAttribute referenceAttribute, TypeCompletionContext context) {
    next(concreteType, referenceAttribute, context);
  }

  /**
   * Calls the next completer in the chain if it is present.
   *
   * @param concreteType
   * @param referenceAttribute
   */
  protected void next(
      ASTCDType concreteType, ASTCDAttribute referenceAttribute, TypeCompletionContext context) {
    if (hasNext()) {
      next.completeAttribute(concreteType, referenceAttribute, context);
    }
  }
}
