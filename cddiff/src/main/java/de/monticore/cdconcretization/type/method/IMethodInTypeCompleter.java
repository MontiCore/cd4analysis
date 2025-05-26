package de.monticore.cdconcretization.type.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.type.TypeCompletionContext;

/**
 * Completes a concrete type such that it conforms to the reference model with respect to the given
 * reference method.
 */
public interface IMethodInTypeCompleter {

  /**
   * Completes the given concrete type such that it conforms with respect to the reference method.
   *
   * @param concreteType
   * @param referenceMethod
   */
  void completeMethodInType(
      ASTCDType concreteType, ASTCDMethod referenceMethod, TypeCompletionContext context)
      throws CompletionException;
}
