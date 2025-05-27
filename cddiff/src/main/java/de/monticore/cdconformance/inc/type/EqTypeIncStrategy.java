package de.monticore.cdconformance.inc.type;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.Set;
import java.util.stream.Collectors;

public class EqTypeIncStrategy implements ExternalCandidatesMatchingStrategy<ASTCDType> {
  protected ASTCDCompilationUnit refCD;

  public EqTypeIncStrategy(ASTCDCompilationUnit refCD) {
    this.refCD = refCD;
  }

  public Set<ASTCDType> getMatchedElements(ASTCDType concrete) {
    return refCD.getEnclosingScope().resolveCDTypeDownMany(concrete.getName()).stream()
        .map(CDTypeSymbolTOP::getAstNode)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isMatched(ASTCDType concrete, ASTCDType ref) {
    return concrete.getName().equals(ref.getName());
  }
}
