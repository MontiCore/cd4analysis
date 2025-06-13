/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.type;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.cd.CDCompletionContext;
import de.monticore.cdmatcher.BooleanMatchingStrategy;

/** A {@link CDCompletionContext} with additional information when completing a single type. */
public interface TypeCompletionContext extends CDCompletionContext {
  
  /** @return the concrete type that is currently being completed. */
  ASTCDType getConcreteType();
  
  /** @return the reference type that is used to complete the concrete type. */
  ASTCDType getReferenceType();
  
  /**
   * Returns the matching strategy for the attribute incarnations. The strategy returned here is
   * only valid in the current type context!
   *
   * @return the matching strategy for the attribute incarnations.
   */
  BooleanMatchingStrategy<ASTCDAttribute> getAttributeIncStrategy();
  
  /**
   * Returns the matching strategy for the method incarnations. The strategy returned here is only
   * valid in the current type context!
   *
   * @return the matching strategy for the method incarnations.
   */
  BooleanMatchingStrategy<ASTCDMethod> getMethodIncStrategy();
  
}
