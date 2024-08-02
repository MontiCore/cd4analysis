/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;

/** Determines if two associations match */
public interface AssociationMatcher {

  boolean match(ASTCDAssociation association1, ASTCDAssociation association2);

  ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> findMatchingAssociations()
      throws MergingException;

  boolean rolesMatch(
      ASTCDAssociation association1, ASTCDAssociation association2, int numRolesToMatch);

  boolean nameMatch(ASTCDAssociation assoc1, ASTCDAssociation assoc2, boolean strict);

  boolean cardinalitiesMatch(ASTCDAssociation association1, ASTCDAssociation association2);

  boolean navigationDirectionMatch(ASTCDAssociation association1, ASTCDAssociation association2);

  boolean qualifierMatch(ASTCDAssociation association1, ASTCDAssociation association2);
}
