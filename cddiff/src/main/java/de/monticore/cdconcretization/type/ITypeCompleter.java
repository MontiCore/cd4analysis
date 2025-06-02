/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.type;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;

/** Completes a concrete type such that it conforms to a given reference type. */
public interface ITypeCompleter {
  
  void completeType(ASTCDType concreteType, ASTCDType referenceType, TypeCompletionContext context)
      throws CompletionException;
  
}
