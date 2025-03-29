package de.monticore.cdconcretization.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;

public interface IAssociationCompleter {

  void completeAssociation(ASTCDDefinition concreteCD, ASTCDAssociation referenceAssoc);
}
