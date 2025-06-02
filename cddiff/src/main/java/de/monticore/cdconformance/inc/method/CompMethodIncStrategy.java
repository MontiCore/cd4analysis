/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.ArrayList;
import java.util.List;

public class CompMethodIncStrategy implements CDMethodMatchingStrategy {
  
  private final List<CDMethodMatchingStrategy> incStrategies = new ArrayList<>();
  
  public void addIncStrategy(CDMethodMatchingStrategy strategy) {
    incStrategies.add(strategy);
  }
  
  @Override
  public List<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    List<ASTCDMethod> refElements = new ArrayList<>();
    
    for (CDMethodMatchingStrategy strategy : incStrategies) {
      refElements.addAll(strategy.getMatchedElements(concrete));
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
    incStrategies.forEach(checker -> checker.setReferenceType(refType));
  }
  
}
