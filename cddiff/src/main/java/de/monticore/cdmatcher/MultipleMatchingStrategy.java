/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MultipleMatchingStrategy<T, U> implements MatchingStrategy<T> {
  
  protected double getBestMatchingScore(T srcElem, T tgtElem, Function<T, Set<U>> getSubElems,
      MatchingStrategy<U> matcher) {
    Set<U> srcElemsToMatch = getSubElems.apply(srcElem);
    Set<U> tgtElemsToMatch = getSubElems.apply(tgtElem);
    
    if (srcElemsToMatch.isEmpty()) {
      // If both sets are empty, we can consider them as matched, if only one of them is empty the score is 0.0 which is the result of the empty stream reduction below
      return 1.0;
    }
    
    return srcElemsToMatch.stream().flatMap(src -> tgtElemsToMatch.stream().map(tgt -> new Pair<>(
        src, tgt))).map(entry -> new Triple<>(entry.a, entry.b, matcher.getScore(entry.a, entry.b)))
        .collect(Collectors.toMap(t -> t.a, t -> t.c, Double::max)).values().stream().collect(
            Collectors.averagingDouble(Double::doubleValue));
  }
  
}
