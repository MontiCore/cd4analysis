package de.monticore.cdconcretization.type;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;

/**
 * Completes the concrete CD such that it conforms to the reference CD regarding the given reference
 * type.<br>
 * Note: This ignores member details like attributes and methods. They are completed in a separate
 * step because we first have to complete inheritance relations.
 */
public interface ITypeCompleter {
  /**
   * Completes the given concrete CD such that it conforms to the reference type.
   *
   * @param concreteCD
   * @param referenceType
   */
  void completeType(ASTCDDefinition concreteCD, ASTCDType referenceType);
}
