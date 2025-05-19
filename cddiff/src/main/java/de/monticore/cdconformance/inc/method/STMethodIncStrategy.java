package de.monticore.cdconformance.inc.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;

import java.util.List;
import java.util.stream.Collectors;

public class STMethodIncStrategy implements CDMethodMatchingStrategy {

  private final String mapping;
  private ASTCDType refType;

  public STMethodIncStrategy(String mapping) {
    this.mapping = mapping;
  }

  @Override
  public List<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    return refType.getCDMethodList().stream()
        .filter(ref -> isMatched(concrete, ref))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return refType.getSpannedScope().resolveMethodMany(refName).contains(ref.getSymbol());
    }
    return false;
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.refType = refType;
  }
}
