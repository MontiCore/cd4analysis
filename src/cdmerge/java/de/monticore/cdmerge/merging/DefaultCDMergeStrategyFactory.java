/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging;

import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.merging.strategies.*;

/**
 * Configures the Merge Strategies / Algorithms
 */
public class DefaultCDMergeStrategyFactory extends MergeStrategyFactory {

  @Override
  protected TypeMerger createTypeMerger(MergeBlackBoard blackBoard, AttributeMerger attrMerger) {
    TypeMergeStrategy typeMerger = new DefaultTypeMergeStrategy(blackBoard, attrMerger);
    return new DefaultTypeMerger(blackBoard, typeMerger);

  }

  @Override
  protected AttributeMerger createAttributeMerger(MergeBlackBoard blackBoard) {

    return new DefaultAtributeMerger(blackBoard);
  }

  @Override
  protected AssociationMerger createAssociationMerger(MergeBlackBoard blackBoard) {
    AssociationMergeStrategy associationMergeStrategy = new DefaultAssociationMergeStrategy(
        blackBoard);

    return new DefaultAssociationMerger(blackBoard, associationMergeStrategy);
  }

  @Override
  public CDMerger createCDMerger(MergeBlackBoard blackBoard) {
    AttributeMerger attributeMerger = createAttributeMerger(blackBoard);
    TypeMerger typeMerger = createTypeMerger(blackBoard, attributeMerger);
    AssociationMerger associationMerger = createAssociationMerger(blackBoard);
    return new DefaultCDMerger(blackBoard, typeMerger, associationMerger);
  }

}
