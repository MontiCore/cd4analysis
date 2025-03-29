package de.monticore.cdconcretization.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdconcretization.CompletionException;

public interface IAssociationDetailsCompleter {

  /**
   * Completes the details of the given concrete association based on the reference association.
   * <br>
   * Because of the concrete textual syntax of CD4A, associations have a left and right side.
   * However, these have no semantic meaning. The associations <code>A -> B</code> and <code>B <- A
   * </code> are semantically equivalent. Therefore, input to this method is always normalized such
   * that the left/right sides of the concrete association are the same as the left/right side of
   * the reference association. TODO discuss this decision
   *
   * <p>OR we just pass the match direction here and maybe refactor later to keep the interfaces
   * more clean.
   *
   * @param concreteAssoc
   * @param referenceAssoc
   * @throws CompletionException
   */
  void completeAssociationDetails(
      ASTCDAssociation concreteAssoc,
      ASTCDAssociation referenceAssoc,
      AssocMatchDirection matchDirection)
      throws CompletionException;
}
