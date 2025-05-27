package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.ICDMethodChecker;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompMethodChecker extends AbstractMethodChecker {
  private final List<ICDMethodChecker> methodCheckers = new ArrayList<>();

  public CompMethodChecker(
      String mapping, String underspecifiedTypeName, ExternalCandidatesMatchingStrategy<ASTCDType> typeMatcher) {
    super(mapping, underspecifiedTypeName, typeMatcher);
  }

  public void addIncStrategy(ICDMethodChecker checker) {
    methodCheckers.add(checker);
  }

  @Override
  public Set<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    return methodCheckers.stream()
      .map(strategy -> strategy.getMatchedElements(concrete))
      .collect(HashSet::new, Set::addAll, Set::addAll);
  }

  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
    return methodCheckers.stream().anyMatch(strategy -> strategy.isMatched(concrete,ref));
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.refType = refType;
    methodCheckers.forEach(checker -> checker.setReferenceType(refType));
  }

  @Override
  public void setConcreteType(ASTCDType conType) {
    this.conType = conType;
    methodCheckers.forEach(checker -> checker.setConcreteType(conType));
  }
}
