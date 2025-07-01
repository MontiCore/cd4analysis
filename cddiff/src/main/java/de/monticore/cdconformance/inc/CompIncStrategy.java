package de.monticore.cdconformance.inc;

import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompIncStrategy<T> implements ExternalCandidatesMatchingStrategy<T> {

  List<ExternalCandidatesMatchingStrategy<T>> incStrategies = new ArrayList<>();
  Set<T> refElements;

  public CompIncStrategy(Set<T> refElements) {
    this.refElements = refElements;
  }

  public void addIncStrategy(ExternalCandidatesMatchingStrategy<T> strategy) {
    incStrategies.add(strategy);
  }

  public void addIncStrategy(BooleanMatchingStrategy<T> strategy) {
    incStrategies.add(new ExternalMatchingStrategyWrapper<>(strategy, refElements));
  }

  @Override
  public List<T> getMatchedElements(T concrete) {
    List<T> refElements = new ArrayList<>();

    for (ExternalCandidatesMatchingStrategy<T> strategy : incStrategies) {
      refElements.addAll(strategy.getMatchedElements(concrete));
      if (!refElements.isEmpty()) {
        return refElements;
      }
    }

    return refElements;
  }

  @Override
  public List<T> getMatchedElements(T concrete, Set<T> refElems) {
    CompIncStrategy<T> strategy = new CompIncStrategy<>(refElems);
    for (ExternalCandidatesMatchingStrategy<T> incStrategy : incStrategies) {
      strategy.addIncStrategy(incStrategy);
    }
    return strategy.getMatchedElements(concrete);
  }

  @Override
  public boolean isMatched(T concrete, T ref) {
    return getMatchedElements(concrete).contains(ref);
  }

}
