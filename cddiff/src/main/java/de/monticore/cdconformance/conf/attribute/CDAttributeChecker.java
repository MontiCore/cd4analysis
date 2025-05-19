package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.ConformanceStrategy;
import de.monticore.cdconformance.inc.attribute.CDAttributeMatchingStrategy;

public abstract class CDAttributeChecker implements ConformanceStrategy<ASTCDAttribute> {

  private final CDAttributeMatchingStrategy attributeIncStrategy;

  protected CDAttributeChecker(CDAttributeMatchingStrategy attributeIncStrategy) {
    this.attributeIncStrategy = attributeIncStrategy;
  }

  @Override
  public boolean checkConformance(ASTCDAttribute concrete) {
    return attributeIncStrategy.getMatchedElements(concrete).stream()
            .allMatch(ref -> checkConformance(concrete, ref));
  }

  protected abstract boolean checkConformance(ASTCDAttribute concrete, ASTCDAttribute ref);

  public void setReferenceType(ASTCDType refType) {
    attributeIncStrategy.setReferenceType(refType);
  }
}
