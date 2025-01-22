package de.monticore.cdconformance.inc.type;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class STTypeIncStrategy implements MatchingStrategy<ASTCDType> {

  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  public STTypeIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.refCD = refCD;
    this.mapping = mapping;
  }

  @Override
  public List<ASTCDType> getMatchedElements(ASTCDType concrete) {
    List<ASTCDType> refTypes = new ArrayList<>();
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      refTypes.addAll(
          refCD.getEnclosingScope().resolveCDTypeDownMany(refName).stream()
              .map(CDTypeSymbolTOP::getAstNode)
              .collect(Collectors.toList()));
    }
    return refTypes;
  }

  @Override
  public boolean isMatched(ASTCDType concrete, ASTCDType ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return refCD.getEnclosingScope().resolveCDTypeDownMany(refName).contains(ref.getSymbol());
    }
    return false;
  }
}
