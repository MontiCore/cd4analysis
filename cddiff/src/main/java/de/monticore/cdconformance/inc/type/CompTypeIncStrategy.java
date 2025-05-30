package de.monticore.cdconformance.inc.type;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.*;
import java.util.stream.Collectors;

public class CompTypeIncStrategy implements ExternalCandidatesMatchingStrategy<ASTCDType> {
  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  List<ExternalCandidatesMatchingStrategy<ASTCDType>> incStrategies = new ArrayList<>();

  public CompTypeIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.refCD = refCD;
    this.mapping = mapping;
  }

  public void addIncStrategy(ExternalCandidatesMatchingStrategy<ASTCDType> strategy) {
    incStrategies.add(strategy);
  }

  @Override
  public Set<ASTCDType> getMatchedElements(ASTCDType concrete) {
    return incStrategies.stream()
      .map(strategy -> strategy.getMatchedElements(concrete))
      .filter((set) -> !set.isEmpty())
      .findFirst()
      .orElseGet(HashSet::new);
  }

  @Override
  public Set<ASTCDType> getMatchedElements(ASTCDType concrete, Set<ASTCDType> refTypes) {
    return getMatchedElements(concrete).stream()
      .filter(refTypes::contains)
      .collect(HashSet::new, HashSet::add, HashSet::addAll);
  }

  @Override
  public Map<ASTCDType, Double> getMatchedElements(ASTCDType concrete, Set<ASTCDType> refTypes, double threshold) {
    if(threshold > 1.0)
      return new HashMap<>();
    return getMatchedElements(concrete).stream()
      .map(element -> Map.entry(element, 1.0))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  // isMatched uses getMatchedElements, to avoid conflicts all default methods have to be overridden
  public boolean isMatched(ASTCDType concrete, ASTCDType ref) {
    return getMatchedElements(concrete).contains(ref);
  }
}
