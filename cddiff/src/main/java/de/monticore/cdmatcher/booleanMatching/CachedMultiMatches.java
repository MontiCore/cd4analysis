/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.booleanMatching;

import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import org.antlr.v4.runtime.misc.MultiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CachedMultiMatches<T> implements ExternalCandidatesMatchingStrategy<T> {

  protected MultiMap<T, T> matches;

  public CachedMultiMatches(MultiMap<T, T> matches) {
    this.matches = new MultiMap<>();
    this.matches.putAll(matches);
  }

  public CachedMultiMatches(Map<T, T> matches) {
    this(new MultiMap<>());
    matches.forEach((k, v) -> this.matches.put(k, List.of(v)));
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
