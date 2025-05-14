package de.monticore.cdmatcher;

import org.antlr.v4.runtime.misc.MultiMap;

import java.util.List;

public class CachedMultiMatches<T> implements MatchingStrategy<T>{
  protected MultiMap<T,T> matches;

  public CachedMultiMatches(MultiMap<T,T> matches){
    this.matches = matches;
  }

  @Override
  public List<T> getMatchedElements(T srcElem) {
    return matches.get(srcElem);
  }

  @Override
  public boolean isMatched(T srcElem, T tgtElem) {
    return getMatchedElements(srcElem).contains(tgtElem);
  }
}
