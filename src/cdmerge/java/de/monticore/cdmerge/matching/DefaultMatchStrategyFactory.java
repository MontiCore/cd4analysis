package de.monticore.cdmerge.matching;

import de.monticore.cdmerge.matching.strategies.*;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

/** Concrete Factory that assembles default matching strategies. */
public class DefaultMatchStrategyFactory extends MatchStrategyFactory {

  @Override
  protected TypeMatcher createTypeMatcher(MergeBlackBoard blackBoard) {
    return new DefaultTypeMatcher(blackBoard);
  }

  @Override
  protected AttributeMatcher createArributeMatcher(MergeBlackBoard blackBoard) {
    return new DefaultAttributeMatcher(blackBoard);
  }

  @Override
  protected AssociationMatcher createAssociationMatcher(MergeBlackBoard blackBoard) {
    return new DefaultAssociationMatcher(blackBoard);
  }

  @Override
  public CDMatcher createCDMatcher(MergeBlackBoard blackBoard) {
    TypeMatcher typeMatcher = createTypeMatcher(blackBoard);
    AttributeMatcher attributeMatcher = createArributeMatcher(blackBoard);
    AssociationMatcher associationMatcher = createAssociationMatcher(blackBoard);
    return new DefaultCDMatcher(blackBoard, typeMatcher, attributeMatcher, associationMatcher);
  }
}
