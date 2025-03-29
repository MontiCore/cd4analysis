package de.monticore.cdconcretization.typedetails;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.attribute.TypeCompletionContext;

public interface ITypeDetailsCompleter {

  void completeTypeDetails(
      ASTCDType concreteType, ASTCDType referenceType, TypeCompletionContext context)
      throws CompletionException;
}
