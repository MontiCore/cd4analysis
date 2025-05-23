package de.monticore.cdconformance.inc.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.List;
import java.util.stream.Collectors;

public class EqNameAttributeIncStrategy implements CDAttributeMatchingStrategy {

  private ASTCDType referenceType;

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    return referenceType.getCDAttributeList().stream()
        .filter(attr -> isMatched(concrete, attr))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    return ref.getName().equals(concrete.getName());
  }

  @Override
  public void setReferenceType(ASTCDType referenceType) {
    this.referenceType = referenceType;
  }
}
