package de.monticore.cdconformance.inc.type;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.ArrayList;
import java.util.List;

public class CompTypeIncStrategy implements MatchingStrategy<ASTCDType> {
  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  List<MatchingStrategy<ASTCDType>> incStrategies = new ArrayList<>();

  public CompTypeIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.refCD = refCD;
    this.mapping = mapping;
  }

  public void addIncStrategy(MatchingStrategy<ASTCDType> strategy) {
    incStrategies.add(strategy);
  }

  @Override
  public List<ASTCDType> getMatchedElements(ASTCDType concrete) {
    List<ASTCDType> refElements = new ArrayList<>();

    for (MatchingStrategy<ASTCDType> strategy : incStrategies) {
      refElements.addAll(strategy.getMatchedElements(concrete));
      if (!refElements.isEmpty()) {
        return refElements;
      }
    }

    return refElements;
  }

  @Override
  public boolean isMatched(ASTCDType concrete, ASTCDType ref) {
    return getMatchedElements(concrete).contains(ref);
  }
}
