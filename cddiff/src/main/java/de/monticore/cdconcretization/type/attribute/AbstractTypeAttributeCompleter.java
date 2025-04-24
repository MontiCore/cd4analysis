package de.monticore.cdconcretization.type.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.type.TypeCompletionContext;
import de.monticore.cdconcretization.util.AbstractChainable;

public abstract class AbstractTypeAttributeCompleter
    extends AbstractChainable<AbstractTypeAttributeCompleter> implements ITypeAttributeCompleter {

  @Override
  public void completeTypeForAttribute(
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
      next.completeTypeForAttribute(concreteType, referenceAttribute, context);
    }
  }
}
