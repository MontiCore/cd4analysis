/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.cdconformance.inc.method.CDMethodMatchingStrategy;

public abstract class CDMethodChecker implements ConformanceStrategy<ASTCDMethod> {
  
  protected final CDIncarnationMapping incMapping;
  protected final CDMethodMatchingStrategy methodIncStrategy;
  
  protected CDMethodChecker(CDIncarnationMapping incMapping) {
    this.incMapping = incMapping;
    this.methodIncStrategy = incMapping.getMethodIncStrategy();
  }
  
  @Override
  public boolean checkConformance(ASTCDMethod concrete) {
    return incMapping.getMethodIncStrategy().getMatchedElements(concrete).stream().allMatch(
        ref -> checkConformance(concrete, ref));
  }
  
  protected abstract boolean checkConformance(ASTCDMethod concrete, ASTCDMethod ref);
  
  public void setReferenceType(ASTCDType refType) {
    methodIncStrategy.setReferenceType(refType);
  }
  
}
