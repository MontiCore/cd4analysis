package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class CompAssocIncStrategy implements ExternalCandidatesMatchingStrategy<ASTCDAssociation> {

  List<ExternalCandidatesMatchingStrategy<ASTCDAssociation>> incStrategies = new ArrayList<>();

  public void addIncStrategy(ExternalCandidatesMatchingStrategy<ASTCDAssociation> strategy) {
    incStrategies.add(strategy);
  }

  @Override
  public Set<ASTCDAssociation> getMatchedElements(ASTCDAssociation concrete) {
    return incStrategies.stream()
      .map(strategy -> strategy.getMatchedElements(concrete))
      .filter((set) -> !set.isEmpty())
      .findFirst()
      .orElseGet(HashSet::new);
  }

  @Override
  public Set<ASTCDAssociation> getMatchedElements(ASTCDAssociation concrete, Set<ASTCDAssociation> refTypes) {
    return getMatchedElements(concrete).stream()
      .filter(refTypes::contains)
      .collect(HashSet::new, HashSet::add, HashSet::addAll);
  }

  @Override
  public Map<ASTCDAssociation, Double> getMatchedElements(ASTCDAssociation concrete, Set<ASTCDAssociation> refTypes, double threshold) {
    if(threshold > 1.0)
      return new HashMap<>();
    return getMatchedElements(concrete).stream()
      .map(element -> Map.entry(element, 1.0))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  // isMatched uses getMatchedElements, to avoid conflicts all default methods have to be overridden
  public boolean isMatched(ASTCDAssociation concrete, ASTCDAssociation ref) {
    return getMatchedElements(concrete).contains(ref);
  }
}
