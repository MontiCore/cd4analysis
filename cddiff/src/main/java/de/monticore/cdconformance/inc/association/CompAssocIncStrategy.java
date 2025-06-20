/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import java.util.ArrayList;
import java.util.List;

public class CompAssocIncStrategy implements ExternalCandidatesMatchingStrategy<ASTCDAssociation> {
  
  protected ASTCDCompilationUnit refCD;
  protected String mapping;
  
  List<ExternalCandidatesMatchingStrategy<ASTCDAssociation>> incStrategies = new ArrayList<>();
  
  public CompAssocIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.refCD = refCD;
    this.mapping = mapping;
  }
  
  public void addIncStrategy(ExternalCandidatesMatchingStrategy<ASTCDAssociation> strategy) {
    incStrategies.add(strategy);
  }
  
  @Override
  public List<ASTCDAssociation> getMatchedElements(ASTCDAssociation concrete) {
    List<ASTCDAssociation> refElements = new ArrayList<>();
    
    for (ExternalCandidatesMatchingStrategy<ASTCDAssociation> strategy : incStrategies) {
      refElements.addAll(strategy.getMatchedElements(concrete));
      if (!refElements.isEmpty()) {
        return refElements;
      }
    }
    
    return refElements;
  }
  
  @Override
  public boolean isMatched(ASTCDAssociation concrete, ASTCDAssociation ref) {
    return getMatchedElements(concrete).contains(ref);
  }
  
}
