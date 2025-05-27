package de.monticore.cdmatcher.matching.booleanMatchingStrategy;

import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.misc.MultiMap;

public class CachedMatches<T> extends CachedMultiMatches<T> {

  public CachedMatches(Map<T, T> matches) {
    super(new MultiMap<>());
    matches.forEach((k, v) -> this.matches.put(k, List.of(v)));
  }
}
