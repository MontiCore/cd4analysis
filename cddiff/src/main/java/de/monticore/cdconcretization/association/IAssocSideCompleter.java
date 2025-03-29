package de.monticore.cdconcretization.association;

import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdconcretization.CompletionException;

public interface IAssocSideCompleter {

  void completeAssocSide(ASTCDAssocSide concreteAssocSide, ASTCDAssocSide referenceAssocSide)
      throws CompletionException;
}
