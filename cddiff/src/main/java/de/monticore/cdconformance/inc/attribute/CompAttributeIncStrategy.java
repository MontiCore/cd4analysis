/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.ArrayList;
import java.util.List;

public class CompAttributeIncStrategy implements CDAttributeMatchingStrategy {
  
  private final List<CDAttributeMatchingStrategy> incStrategies = new ArrayList<>();
  
  public void addIncStrategy(CDAttributeMatchingStrategy checker) {
    incStrategies.add(checker);
  }
  
  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    List<ASTCDAttribute> refElements = new ArrayList<>();
    
    for (CDAttributeMatchingStrategy strategy : incStrategies) {
      refElements.addAll(strategy.getMatchedElements(concrete));
      if (!refElements.isEmpty()) {
        return refElements;
      }
    }
    
    return refElements;
  }
  
  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    return incStrategies.stream().anyMatch(strategy -> strategy.isMatched(concrete, ref));
  }
  
  @Override
  public void setReferenceType(ASTCDType refType) {
    incStrategies.forEach(checker -> checker.setReferenceType(refType));
  }
  
}
