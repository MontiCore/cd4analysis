package de.monticore.conformance.inc;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class STTypeIncStrategy implements IncarnationStrategy<ASTCDType> {

  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  public STTypeIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.refCD = refCD;
    this.mapping = mapping;
  }

  @Override
  public Set<ASTCDType> getRefElements(ASTCDType concrete) {
    Set<ASTCDType> refTypes = new HashSet<>();
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      refTypes.addAll(
          refCD.getEnclosingScope().resolveCDTypeDownMany(refName).stream()
              .map(CDTypeSymbolTOP::getAstNode)
              .collect(Collectors.toSet()));
    }
    return refTypes;
  }

  @Override
  public boolean isIncarnation(ASTCDType concrete, ASTCDType ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return refCD.getEnclosingScope().resolveCDTypeDownMany(refName).contains(ref.getSymbol());
    }
    return false;
  }
}
