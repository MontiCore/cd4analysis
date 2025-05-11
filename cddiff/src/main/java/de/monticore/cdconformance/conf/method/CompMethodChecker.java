package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.conf.ICDMethodChecker;
import de.monticore.cdconformance.inc.type.TypeIncarnationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompMethodChecker extends AbstractMethodChecker {
  private final List<ICDMethodChecker> methodCheckers = new ArrayList<>();

  public CompMethodChecker(String mapping, Set<CDConfParameter> params, TypeIncarnationHelper typeHelper) {
    super(mapping, params, typeHelper);
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
