package de.monticore.cdmatcher;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface BooleanMatchingStrategy<T> extends MatchingStrategy<T> {

  /**
   * @return returns true if the srcElem and tgtElem match, false otherwise.
   */
  boolean isMatched(T srcElem, T tgtElem);

  default List<T> getMatchedElements(T srcElem, Set<T> tgtElems){
    return tgtElems.stream()
        .filter(tgtElem -> isMatched(srcElem, tgtElem))
        .collect(Collectors.toList());
  }

  /**
   * @return the score of the match between srcElem and tgtElem, in this case it is either 0.0 or 1.0.
   */
  @Override
  default double getScore(T srcElem, T tgtElem) {
    return isMatched(srcElem, tgtElem) ? 1.0 : 0.0;
  }
}
