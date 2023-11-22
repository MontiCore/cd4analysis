package de.monticore.conformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.conformance.conf.MethodChecker;
import java.util.ArrayList;
import java.util.List;

public class CompMethodChecker implements MethodChecker {
  protected String mapping;
  protected ASTCDType conType;

  protected ASTCDType refType;

  List<MethodChecker> methodCheckers = new ArrayList<>();

  public CompMethodChecker(String mapping) {
    this.mapping = mapping;
  }

  public void addIncStrategy(MethodChecker checker) {
    methodCheckers.add(checker);
  }

  @Override
  public List<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    List<ASTCDMethod> refElements = new ArrayList<>();

    for (MethodChecker checker : methodCheckers) {
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
  public ASTCDType getReferenceType() {
    return refType;
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.refType = refType;
    methodCheckers.forEach(checker -> checker.setReferenceType(refType));
  }

  @Override
  public ASTCDType getConcreteType() {
    return conType;
  }

  @Override
  public void setConcreteType(ASTCDType conType) {
    this.conType = conType;
    methodCheckers.forEach(checker -> checker.setConcreteType(conType));
  }
}
