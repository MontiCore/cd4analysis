package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.List;
import java.util.stream.Collectors;

public class EqNameMethodChecker extends AbstractMethodChecker {
  public EqNameMethodChecker(String mapping, MatchingStrategy<ASTCDType> typeMatcher) {
    this.mapping = mapping;
    this.typeMatcher = typeMatcher;
  }

  @Override
  public List<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    return refType.getCDMethodList().stream()
        .filter(method -> isMatched(concrete, method))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
    return ref.getName().equals(concrete.getName());
  }
}
