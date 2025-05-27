package de.monticore.cdconformance.inc.type;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
      .collect(HashSet::new, Set::addAll, Set::addAll);
  }

  @Override
  public boolean isMatched(ASTCDType concrete, ASTCDType ref) {
    return incStrategies.stream().anyMatch(strategy -> strategy.isMatched(concrete, ref));
  }
}
