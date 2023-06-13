package de.monticore.conformance.inc.type;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.matcher.MatchingStrategy;

import java.util.ArrayList;
import java.util.List;

public class CompTypeIncStrategy implements MatchingStrategy<ASTCDType> {
  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  protected EqTypeIncStrategy eqTypeIncStrategy;
  protected STTypeIncStrategy stTypeIncStrategy;

  public CompTypeIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.eqTypeIncStrategy = new EqTypeIncStrategy(refCD, mapping);
    this.stTypeIncStrategy = new STTypeIncStrategy(refCD, mapping);
    this.refCD = refCD;
    this.mapping = mapping;
  }

  @Override
  public List<ASTCDType> getMatchedElements(ASTCDType concrete) {
    List<ASTCDType> refElements = new ArrayList<>(stTypeIncStrategy.getMatchedElements(concrete));
    if (refElements.isEmpty()) {
      refElements.addAll(eqTypeIncStrategy.getMatchedElements(concrete));
    }
    return refElements;
  }

  @Override
  public boolean isMatched(ASTCDType concrete, ASTCDType ref) {
    return getMatchedElements(concrete).contains(ref);
  }
}
