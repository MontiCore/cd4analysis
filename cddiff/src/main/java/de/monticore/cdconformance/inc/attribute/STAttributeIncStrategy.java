/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.List;
import java.util.stream.Collectors;

public class STAttributeIncStrategy implements CDAttributeMatchingStrategy {
  
  private final String mapping;
  private ASTCDType referenceType;
  
  public STAttributeIncStrategy(String mapping) {
    this.mapping = mapping;
  }
  
  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    return referenceType.getCDAttributeList().stream().filter(ref -> isMatched(concrete, ref))
        .collect(Collectors.toList());
  }
  
  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    if (!ref.isPresentSymbol() || ref.getEnclosingScope() == null) {
      // If no symbol table information is attached to the reference attribute, we cannot
      // determine whether the concrete attribute matches the reference attribute by stereotype.
      return false;
    }
    if (concrete.getModifier().isPresentStereotype() && concrete.getModifier().getStereotype()
        .contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return ref.getEnclosingScope().resolveFieldMany(refName).contains(ref.getSymbol());
    }
    return false;
  }
  
  @Override
  public void setReferenceType(ASTCDType referenceType) { this.referenceType = referenceType; }
  
}
