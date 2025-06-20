/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.inc.CDIncarnationMapping;
import de.monticore.cdconformance.inc.attribute.CDAttributeMatchingStrategy;

public abstract class CDAttributeChecker implements ConformanceStrategy<ASTCDAttribute> {
  
  protected final CDIncarnationMapping incMapping;
  protected final CDAttributeMatchingStrategy attributeIncStrategy;
  
  protected CDAttributeChecker(CDIncarnationMapping incMapping) {
    this.incMapping = incMapping;
    this.attributeIncStrategy = incMapping.getAttributeIncStrategy();
  }
  
  @Override
  public boolean checkConformance(ASTCDAttribute concrete) {
    return attributeIncStrategy.getMatchedElements(concrete).stream().allMatch(
        ref -> checkConformance(concrete, ref));
  }
  
  protected abstract boolean checkConformance(ASTCDAttribute concrete, ASTCDAttribute ref);
  
  public void setReferenceType(ASTCDType refType) {
    attributeIncStrategy.setReferenceType(refType);
  }
  
}
