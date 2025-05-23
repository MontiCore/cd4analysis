package de.monticore.cdmatcher.matching;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface MatchingStrategy<T> {

  /**
   * @return returns a map of the matched elements together with their score.
   */
  default Map<T, Double> getMatchedElements(T srcElem, Set<T> tgtElems){
    return tgtElems.stream()
      .collect(Collectors.toMap(
        tgtElem -> tgtElem,
        tgtElem -> getScore(srcElem, tgtElem)
      ));
  }

  /**
   * @return returns the score of the match between srcElem and tgtElem, the score is between 0 and 1.
   * */
  double getScore(T srcElem, T tgtElem);
}
