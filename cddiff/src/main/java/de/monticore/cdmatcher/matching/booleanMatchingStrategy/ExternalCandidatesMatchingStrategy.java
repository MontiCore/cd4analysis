package de.monticore.cdmatcher.matching.booleanMatchingStrategy;

import java.util.Set;

public interface ExternalCandidatesMatchingStrategy<T> extends BooleanMatchingStrategy<T> {

  /**
   * @return returns a set of elements that match the srcElem. The candidates must be provided externally, i.e. in the constructor.
   * * To preserve the validity of other methods make sure that isMatched(T srcElem, T tgtElem) and getScore(T srcElem, T tgtElem) do not use getMatchedElements(T srcElem) internally.
   * * Alternatively make sure to overwrite all default methods.
   */
  Set<T> getMatchedElements(T srcElem);
}
