package de.monticore.cdmatcher.matching.caching;

import org.antlr.v4.runtime.misc.Pair;

import java.util.HashMap;
import java.util.Map;

public class CachedMatch<T> {
  private final Map<Pair<T, T>, Double> matchMap = new HashMap<>();

  public Double putMatch(T srcElem, T tgtElem, Double value) {
    return matchMap.put(new Pair<>(srcElem, tgtElem), value);
  }

  public Double getMatch(T srcElem, T tgtElem) {
    return matchMap.get(new Pair<>(srcElem, tgtElem));
  }

  public Map<Pair<T, T>, Double> getMatches() {
    return matchMap;
  }

  public void clear() {
    matchMap.clear();
  }
}
