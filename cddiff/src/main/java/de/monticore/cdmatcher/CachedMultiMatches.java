package de.monticore.cdmatcher;

import java.util.ArrayList;
import java.util.List;

import de.monticore.cdmatcher.matching.MatchingStrategy;
import org.antlr.v4.runtime.misc.MultiMap;

public class CachedMultiMatches<T> implements MatchingStrategy<T> {
  protected MultiMap<T, T> matches;

  public CachedMultiMatches(MultiMap<T, T> matches) {
    this.matches = new MultiMap<>();
    this.matches.putAll(matches);
  }

  @Override
  public List<T> getMatchedElements(T srcElem) {
    if (!matches.containsKey(srcElem)) {
      matches.put(srcElem, new ArrayList<>());
    }
    return matches.get(srcElem);
  }

  @Override
  public boolean isMatched(T srcElem, T tgtElem) {

    return matches.containsKey(srcElem) && getMatchedElements(srcElem).contains(tgtElem);
  }
}
