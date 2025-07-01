package de.monticore.cdmatcher.booleanMatching;

import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatch;

public class BooleanMatchFromCache<T> implements BooleanMatchingStrategy<T> {

  private final CachedMatch<T> cachedMatch;
  private final double threshold;

  public BooleanMatchFromCache(CachedMatch<T> cachedMatch, double threshold) {
    this.cachedMatch = cachedMatch;
    this.threshold = threshold;
  }

  @Override
  public boolean isMatched(T srcElem, T tgtElem) {
    Double matchValue = cachedMatch.getMatch(srcElem, tgtElem);
    if (matchValue == null) {
      return false;
    }
    return matchValue >= threshold;
  }

}
