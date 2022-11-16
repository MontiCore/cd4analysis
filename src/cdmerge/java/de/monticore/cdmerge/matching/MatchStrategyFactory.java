package de.monticore.cdmerge.matching;

import de.monticore.cdmerge.matching.strategies.AssociationMatcher;
import de.monticore.cdmerge.matching.strategies.AttributeMatcher;
import de.monticore.cdmerge.matching.strategies.TypeMatcher;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;

/** Abstract Factory pattern. Use concrete subclasses to assemble own matching strategies. */
public abstract class MatchStrategyFactory {

  protected abstract TypeMatcher createTypeMatcher(MergeBlackBoard blackBoard);

  protected abstract AttributeMatcher createArributeMatcher(MergeBlackBoard blackBoard);

  protected abstract AssociationMatcher createAssociationMatcher(MergeBlackBoard blackBoard);

  public abstract CDMatcher createCDMatcher(MergeBlackBoard blackBoard);
}
