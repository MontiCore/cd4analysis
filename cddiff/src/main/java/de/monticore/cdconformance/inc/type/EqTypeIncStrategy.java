package de.monticore.cdconformance.inc.type;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.List;
import java.util.stream.Collectors;

public class EqTypeIncStrategy implements MatchingStrategy<ASTCDType> {
  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  public EqTypeIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.refCD = refCD;
    this.mapping = mapping;
  }

  public List<ASTCDType> getMatchedElements(ASTCDType concrete) {
    return refCD.getEnclosingScope().resolveCDTypeDownMany(concrete.getName()).stream()
        .map(CDTypeSymbolTOP::getAstNode)
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDType concrete, ASTCDType ref) {
    return concrete.getName().equals(ref.getName());
  }
}
