package de.monticore.conformance;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class STTypeIncStrategy implements IncarnationStrategy<ASTCDType> {

  protected ASTCDCompilationUnit refCD;
  protected Set<String> mappings;

  public STTypeIncStrategy(ASTCDCompilationUnit refCD, Set<String> mappings) {
    this.refCD = refCD;
  }

  @Override
  public Set<ASTCDType> getRefElements(ASTCDType concrete) {
    Set<ASTCDType> refTypes = new HashSet<>();
    if (concrete.getModifier().isPresentStereotype()) {
      Set<String> refNames =
          mappings.stream()
              .filter(mapping -> concrete.getModifier().getStereotype().contains(mapping))
              .map(mapping -> concrete.getModifier().getStereotype().getValue(mapping))
              .collect(Collectors.toSet());
      refNames.forEach(
          name ->
              refTypes.addAll(
                  refCD.getEnclosingScope().resolveCDTypeDownMany(name).stream()
                      .map(CDTypeSymbolTOP::getAstNode)
                      .collect(Collectors.toSet())));
    }
    return refTypes;
  }
}
