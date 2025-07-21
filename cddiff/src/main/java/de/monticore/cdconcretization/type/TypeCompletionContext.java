/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.type;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.cd.CDCompletionContext;

/** A {@link CDCompletionContext} with additional information when completing a single type. */
public interface TypeCompletionContext extends CDCompletionContext {
  
  /** @return the concrete type that is currently being completed. */
  ASTCDType getConcreteType();
  
  /** @return the reference type that is used to complete the concrete type. */
  ASTCDType getReferenceType();
  
}
