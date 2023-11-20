package de.monticore.matcher;

import java.util.List;

public interface MatchingStrategy<T> {
  // Set with the matched elements in which we have the matched element, the concrete class diagram
  // and the reference class diagram
  List<T> getMatchedElements(T srcElem);

  // We check if the combinations of elements are a match, or not
  boolean isMatched(T srcElem, T tgtElem);
}
