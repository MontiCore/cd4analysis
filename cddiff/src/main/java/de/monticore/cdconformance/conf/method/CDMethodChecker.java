package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.inc.method.CDMethodMatchingStrategy;

public abstract class CDMethodChecker implements ConformanceStrategy<ASTCDMethod> {

  protected final CDMethodMatchingStrategy methodIncStrategy;

  protected CDMethodChecker(CDMethodMatchingStrategy methodIncStrategy) {
    this.methodIncStrategy = methodIncStrategy;
  }

  @Override
  public boolean checkConformance(ASTCDMethod concrete) {
    return methodIncStrategy.getMatchedElements(concrete).stream()
            .allMatch(ref -> checkConformance(concrete, ref));
  }

  protected abstract boolean checkConformance(ASTCDMethod concrete, ASTCDMethod ref);

  public void setReferenceType(ASTCDType refType) {
    methodIncStrategy.setReferenceType(refType);
  }
}
