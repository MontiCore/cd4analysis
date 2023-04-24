package de.monticore.conformance.inc.type;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import de.monticore.conformance.inc.IncarnationStrategy;
import java.util.Set;
import java.util.stream.Collectors;

public class EqTypeIncStrategy implements IncarnationStrategy<ASTCDType> {
  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  public EqTypeIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.refCD = refCD;
    this.mapping = mapping;
  }

  @Override
  public Set<ASTCDType> getRefElements(ASTCDType concrete) {
    return refCD.getEnclosingScope().resolveCDTypeDownMany(concrete.getName()).stream()
        .map(CDTypeSymbolTOP::getAstNode)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isIncarnation(ASTCDType concrete, ASTCDType ref) {
    return concrete.getName().equals(ref.getName());
  }
}
