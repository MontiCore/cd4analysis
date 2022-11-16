/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

/**
 * Implements a type merging strategy, where only associations are selected for the result diagram
 * which have been defined in both source diagrams. Although underspecification in each of the
 * diagrams is allowes as long as there are distinct mathc candidates
 */
public class IntersectionAssociationMerger extends AssociationMerger {

  public IntersectionAssociationMerger(
      MergeBlackBoard mergeBlackBoard, AssociationMergeStrategy associationMergeStrategy) {
    super(mergeBlackBoard, associationMergeStrategy);
  }

  @Override
  public void mergeAssociations(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchResult) {
    // TODO - Implement Association Intersection Merger
    throw new UnsupportedOperationException("AssociationIntersectionMerger NOT IMPLEMENTED");
  }
}
