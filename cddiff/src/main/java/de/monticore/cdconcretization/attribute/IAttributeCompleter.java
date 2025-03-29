package de.monticore.cdconcretization.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;

/**
 * Completes a concrete type such that it conforms to the reference model regarding the given
 * reference method.
 */
public interface IAttributeCompleter {

  /**
   * Completes the given concrete type such that it conforms to the reference attribute.
   *
   * @param concreteType
   * @param referenceAttribute
   */
  void completeAttribute(
      ASTCDType concreteType, ASTCDAttribute referenceAttribute, TypeCompletionContext context);
}
