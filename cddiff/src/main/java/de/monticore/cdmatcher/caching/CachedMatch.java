/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.caching;

import org.antlr.v4.runtime.misc.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class CachedMatch<T> {

  private final Map<Pair<T, T>, Double> matchMap = new HashMap<>();

  public Double putMatch(T srcElem, T tgtElem, Double value) {
    return matchMap.put(new Pair<>(srcElem, tgtElem), value);
  }

  public Double getMatch(T srcElem, T tgtElem) {
    return matchMap.get(new Pair<>(srcElem, tgtElem));
  }

  public Map<Pair<T, T>, Double> getMatches() { return matchMap; }

  public void clear() {
    matchMap.clear();
  }

  public static <T> CachedMatch<T> merge(List<CachedMatch<T>> matches, BiFunction<Double, Double, Double> mergeValues) {
    CachedMatch<T> merged = new CachedMatch<>();
    for (CachedMatch<T> cachedMatch : matches) {
      for (Map.Entry<Pair<T, T>, Double> entry : cachedMatch.getMatches().entrySet()) {
        Double current = merged.getMatches().get(entry.getKey());
        if(current != null) {
          merged.getMatches().put(entry.getKey(), mergeValues.apply(current, entry.getValue()));
        } else {
          merged.getMatches().put(entry.getKey(), entry.getValue());
        }
      }
    }
    return merged;
  }

}
