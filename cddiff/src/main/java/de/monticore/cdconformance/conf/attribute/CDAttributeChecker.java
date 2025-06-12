/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.inc.CDIncarnationMapping;

public abstract class CDAttributeChecker implements ConformanceStrategy<ASTCDAttribute> {
  
  protected final CDIncarnationMapping incMapping;
  
  protected CDAttributeChecker(CDIncarnationMapping incMapping) {
    this.incMapping = incMapping;
  }
  
  @Override
  public boolean checkConformance(ASTCDAttribute concrete) {
    return incMapping.getReferenceElements(concrete).stream().allMatch(ref -> checkConformance(
        concrete, ref));
  }
  
  protected abstract boolean checkConformance(ASTCDAttribute concrete, ASTCDAttribute ref);
  
}
