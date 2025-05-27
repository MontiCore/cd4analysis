package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompAssocIncStrategy implements ExternalCandidatesMatchingStrategy<ASTCDAssociation> {

  List<ExternalCandidatesMatchingStrategy<ASTCDAssociation>> incStrategies = new ArrayList<>();

  public void addIncStrategy(ExternalCandidatesMatchingStrategy<ASTCDAssociation> strategy) {
    incStrategies.add(strategy);
  }

  @Override
  public Set<ASTCDAssociation> getMatchedElements(ASTCDAssociation concrete) {
    return incStrategies.stream()
      .map(strategy -> strategy.getMatchedElements(concrete))
      .collect(HashSet::new, Set::addAll, Set::addAll);
  }

  @Override
  public boolean isMatched(ASTCDAssociation concrete, ASTCDAssociation ref) {
    return incStrategies.stream().anyMatch(strategy -> strategy.isMatched(concrete, ref));
  }
}
