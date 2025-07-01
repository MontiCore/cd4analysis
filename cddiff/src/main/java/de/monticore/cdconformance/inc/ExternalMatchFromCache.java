package de.monticore.cdconformance.inc;

import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatch;

import java.util.List;
import java.util.stream.Collectors;

public class ExternalMatchFromCache<T> implements ExternalCandidatesMatchingStrategy<T> {
  private final CachedMatch<T> cachedMatch;
  private final double threshold;

  public ExternalMatchFromCache(CachedMatch<T> cachedMatch, double threshold) {
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

  @Override
  public List<T> getMatchedElements(T srcElem) {
    return cachedMatch.getMatches().entrySet().stream()
      .filter(entry -> entry.getKey().a.equals(srcElem))
      .filter(entry -> entry.getValue() >= threshold)
      .map(entry -> entry.getKey().b)
      .collect(Collectors.toList());
  }
}
