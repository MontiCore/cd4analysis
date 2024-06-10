package de.monticore.cdmatcher;

import java.util.List;

public interface MatchingStrategy<T> {

  /**
   * Returns a list of all elements that match srcElem. The set of elements to check is provided by
   * the constructor of the implementation.
   */
  List<T> getMatchedElements(T srcElem);

  /** @return true iff srcElem and tgtElem match. */
  boolean isMatched(T srcElem, T tgtElem);
}
