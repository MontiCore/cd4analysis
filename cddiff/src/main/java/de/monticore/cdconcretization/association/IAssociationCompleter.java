/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdconcretization.CompletionException;

public interface IAssociationCompleter {
  
  /**
   * Completes the details of the given concrete association based on the reference association.
   * <br>
   * Because of the concrete textual syntax of CD4A, associations have a left and right side.
   * However, these have no semantic meaning. The associations <code>A -> B</code> and <code>B <- A
   * </code> are semantically equivalent. Therefore, we additionally pass the match direction as a
   * parameter.
   *
   * @param concreteAssoc
   * @param referenceAssoc
   * @param concreteAssoc the concrete association to be completed
   * @param referenceAssoc the reference association to be used for completion
   * @throws CompletionException
   */
  void completeAssociation(ASTCDAssociation concreteAssoc, ASTCDAssociation referenceAssoc,
      AssocMatchDirection matchDirection) throws CompletionException;
  
}
