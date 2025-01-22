package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.ICDMethodChecker;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.ArrayList;
import java.util.List;

public class CompMethodChecker extends AbstractMethodChecker {
  List<ICDMethodChecker> methodCheckers = new ArrayList<>();

  public CompMethodChecker(String mapping, MatchingStrategy<ASTCDType> typeMatcher) {
    this.mapping = mapping;
    this.typeMatcher = typeMatcher;
  }

  public void addIncStrategy(ICDMethodChecker checker) {
    methodCheckers.add(checker);
  }

  @Override
  public List<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    List<ASTCDMethod> refElements = new ArrayList<>();

    for (ICDMethodChecker checker : methodCheckers) {
      refElements.addAll(checker.getMatchedElements(concrete));
      if (!refElements.isEmpty()) {
        return refElements;
      }
    }

    return refElements;
  }

  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
    return getMatchedElements(concrete).contains(ref);
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
