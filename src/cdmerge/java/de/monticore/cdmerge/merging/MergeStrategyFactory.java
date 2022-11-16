/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging;

import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.merging.strategies.AssociationMerger;
import de.monticore.cdmerge.merging.strategies.AttributeMerger;
import de.monticore.cdmerge.merging.strategies.TypeMerger;

/** Configures the Merge Strategies / Algorithms */
public abstract class MergeStrategyFactory {

  protected abstract TypeMerger createTypeMerger(
      MergeBlackBoard blackBoard, AttributeMerger attrMerger);

  protected abstract AttributeMerger createAttributeMerger(MergeBlackBoard blackBoard);

  protected abstract AssociationMerger createAssociationMerger(MergeBlackBoard blackBoard);

  public abstract CDMerger createCDMerger(MergeBlackBoard blackBoard);
}
