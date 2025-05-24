package de.monticore.cdconformance.inc.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.ArrayList;
import java.util.List;

public class CompAttributeIncStrategy implements CDAttributeMatchingStrategy {

  private final List<CDAttributeMatchingStrategy> attributeCheckers = new ArrayList<>();

  public void addIncStrategy(CDAttributeMatchingStrategy checker) {
    attributeCheckers.add(checker);
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    List<ASTCDAttribute> refElements = new ArrayList<>();

    for (CDAttributeMatchingStrategy strategy : attributeCheckers) {
      refElements.addAll(strategy.getMatchedElements(concrete));
      if (!refElements.isEmpty()) {
        return refElements;
      }
    }

    return refElements;
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    return getMatchedElements(concrete).contains(ref);
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    attributeCheckers.forEach(checker -> checker.setReferenceType(refType));
  }
}
