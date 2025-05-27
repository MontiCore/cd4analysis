package de.monticore.cdmatcher.matching.booleanMatchingStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.misc.MultiMap;

public class CachedMultiMatches<T> implements ExternalCandidatesMatchingStrategy<T> {
  protected MultiMap<T, T> matches;

  public CachedMultiMatches(MultiMap<T, T> matches) {
    this.matches = new MultiMap<>();
    this.matches.putAll(matches);
  }

  @Override
  public Set<T> getMatchedElements(T srcElem) {
    if (!matches.containsKey(srcElem)) {
      matches.put(srcElem, new ArrayList<>());
    }
    return new HashSet<>(matches.get(srcElem));
  }

  @Override
  public boolean isMatched(T srcElem, T tgtElem) {

    return matches.containsKey(srcElem) && getMatchedElements(srcElem).contains(tgtElem);
  }
}
