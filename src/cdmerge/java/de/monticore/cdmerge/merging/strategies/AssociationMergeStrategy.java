/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import java.util.Optional;

/** Interface for algorithms which merge two associations */
public interface AssociationMergeStrategy {

  /**
   * Merges the two Associations into one association
   *
   * @param association1 - input association 1
   * @param association2 - input association 2
   * @return - the merged association
   */
  Optional<ASTCDAssociation> mergeAssociation(
      ASTCDAssociation association1, ASTCDAssociation association2);
}
