package de.monticore.cdconcretization.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdconcretization.CompletionException;

/**
 * Completes a concrete association side such that it conforms to a given reference association side.
 */
public interface IAssocSideCompleter {

  void completeAssocSide(ASTCDAssocSide concreteAssocSide, ASTCDAssocSide referenceAssocSide)
      throws CompletionException;
}
