/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

/**
 * Implements a type merging strategy, where only types are selected for the result diagram which
 * have been defined in both source diagrams
 */
public class IntersectionTypeMerger extends TypeMerger {

  public IntersectionTypeMerger(MergeBlackBoard mergeBlackBoard,
      TypeMergeStrategy typeMergeStrategy) {
    super(mergeBlackBoard, typeMergeStrategy);
  }

  @Override
  public void mergeTypes(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchResult) {
    // TODO - Implement Type Intersection Merger
    throw new UnsupportedOperationException("TypeIntersectionMerger NOT IMPLEMENTED");
  }

}
