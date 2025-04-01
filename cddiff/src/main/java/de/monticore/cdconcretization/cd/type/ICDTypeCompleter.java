package de.monticore.cdconcretization.cd.type;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.cd.CDCompletionContext;

/**
 * Completes the concrete CD such that it conforms to the reference CD with respect to the given
 * reference type.<br>
 * Note: This ignores member details like attributes and methods. They are completed in a separate
 * step because we first have to complete inheritance relations.
 */
public interface ICDTypeCompleter {
  /**
   * Completes the given concrete CD such that it conforms with respect to the reference type.
   *
   * @param concreteCD
   * @param referenceType
   */
  void completeCDForType(
      ASTCDDefinition concreteCD, ASTCDType referenceType, CDCompletionContext context)
      throws CompletionException;
}
