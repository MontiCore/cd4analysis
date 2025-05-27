package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.Set;
import java.util.stream.Collectors;

public class EqNameMethodChecker extends AbstractMethodChecker {
  public EqNameMethodChecker(
      String mapping, String underspecifiedTypeName, ExternalCandidatesMatchingStrategy<ASTCDType> typeMatcher) {
    super(mapping, underspecifiedTypeName, typeMatcher);
  }

  @Override
  public Set<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    return refType.getCDMethodList().stream()
        .filter(method -> isMatched(concrete, method))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
    return ref.getName().equals(concrete.getName());
  }
}
