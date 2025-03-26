package de.monticore.cdconcretization.typedetails;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;

public interface ITypeDetailsCompleter {

  void completeTypeDetails(ASTCDType concreteType, ASTCDType referenceType)
      throws CompletionException;
}
