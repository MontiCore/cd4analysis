package de.monticore.cdconformance.inc;

import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;

import java.util.List;
import java.util.Set;

public class ExternalMatchingStrategyWrapper<T> implements ExternalCandidatesMatchingStrategy<T> {

  private final BooleanMatchingStrategy<T> wrappedStrategy;
  private final Set<T> candidates;

  public ExternalMatchingStrategyWrapper(BooleanMatchingStrategy<T> wrappedStrategy, Set<T> candidates) {
    this.wrappedStrategy = wrappedStrategy;
    this.candidates = candidates;
  }

  @Override
  public boolean isMatched(T srcElem, T tgtElem) {
    return wrappedStrategy.isMatched(srcElem, tgtElem);
  }


  @Override
  public List<T> getMatchedElements(T srcElem) {
    return wrappedStrategy.getMatchedElements(srcElem, candidates);
  }
}
