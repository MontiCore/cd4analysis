package de.monticore.conformance.inc.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.matcher.MatchingStrategy;

import java.util.ArrayList;
import java.util.List;

public class CompAssocIncStrategy implements MatchingStrategy<ASTCDAssociation> {
  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  protected EqNameAssocIncStrategy eqNameAssocIncStrategy;
  protected STNamedAssocIncStrategy stNamedAssocIncStrategy;

  public CompAssocIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.eqNameAssocIncStrategy = new EqNameAssocIncStrategy(refCD, mapping);
    this.stNamedAssocIncStrategy = new STNamedAssocIncStrategy(refCD, mapping);
    this.refCD = refCD;
    this.mapping = mapping;
  }

  @Override
  public List<ASTCDAssociation> getMatchedElements(ASTCDAssociation concrete) {
    List<ASTCDAssociation> refElements =
        new ArrayList<>(stNamedAssocIncStrategy.getMatchedElements(concrete));
    if (refElements.isEmpty()) {
      refElements.addAll(eqNameAssocIncStrategy.getMatchedElements(concrete));
    }
    return refElements;
  }

  @Override
  public boolean isMatched(ASTCDAssociation concrete, ASTCDAssociation ref) {
    return getMatchedElements(concrete).contains(ref);
  }
}
