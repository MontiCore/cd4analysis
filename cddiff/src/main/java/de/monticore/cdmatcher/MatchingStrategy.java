/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface MatchingStrategy<T> {
  
  /**
   * @return returns a map of the matched elements together with their score.
   */
  default Map<T, Double> getMatchedElements(T srcElem, Set<T> tgtElems, double threshold) {
    return tgtElems.stream().map(tgtElem -> Map.entry(tgtElem, getScore(srcElem, tgtElem))).filter(
        entry -> entry.getValue() >= threshold).collect(Collectors.toMap(Map.Entry::getKey,
            Map.Entry::getValue));
  }
  
  /**
   * @return returns the score of the match between srcElem and tgtElem, the score is between 0 and
   * 1.
   */
  double getScore(T srcElem, T tgtElem);
  
}
