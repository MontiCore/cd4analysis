package de.monticore.cdconcretization.cd.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;

/**
 * Completes a CD such that it conforms to the reference CD with respect to a given reference
 * association.
 */
public interface IAssocInCDCompleter {

  void completeAssocInCD(ASTCDDefinition concreteCD, ASTCDAssociation referenceAssoc);
}
