/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdconformance.CDConformanceContext;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.inc.CDIncarnationMapping;

public abstract class CDMethodChecker implements ConformanceStrategy<ASTCDMethod> {
  
  protected final CDConformanceContext context;
  protected final CDIncarnationMapping incMapping;
  
  protected CDMethodChecker(CDConformanceContext context) {
    this.context = context;
    this.incMapping = context.getIncarnationMapping();
  }
  
  @Override
  public boolean checkConformance(ASTCDMethod concrete) {
    return incMapping.getReferenceElements(concrete).stream().allMatch(ref -> checkConformance(
        concrete, ref));
  }
  
  protected abstract boolean checkConformance(ASTCDMethod concrete, ASTCDMethod ref);
  
}
