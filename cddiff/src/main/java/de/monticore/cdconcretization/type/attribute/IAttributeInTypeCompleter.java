/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.type.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.type.TypeCompletionContext;

/**
 * Completes a concrete type such that it conforms to the reference model with respect to the given
 * reference method.
 */
public interface IAttributeInTypeCompleter {
  
  /**
   * Completes the given concrete type such that it conforms with respect to the reference
   * attribute.
   *
   * @param concreteType
   * @param referenceAttribute
   */
  void completeAttributeInType(ASTCDType concreteType, ASTCDAttribute referenceAttribute,
      TypeCompletionContext context) throws CompletionException;
  
}
