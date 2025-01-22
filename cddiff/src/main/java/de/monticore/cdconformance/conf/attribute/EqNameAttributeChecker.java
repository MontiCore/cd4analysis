package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.CDAttributeChecker;
import java.util.List;
import java.util.stream.Collectors;

public class EqNameAttributeChecker implements CDAttributeChecker {
  protected ASTCDType refType;
  protected ASTCDType conType;
  protected String mapping;

  public EqNameAttributeChecker(String mapping) {
    this.mapping = mapping;
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    return refType.getCDAttributeList().stream()
        .filter(attr -> isMatched(concrete, attr))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    return ref.getName().equals(concrete.getName());
  }

  @Override
  public ASTCDType getReferenceType() {
    return refType;
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.refType = refType;
  }

  @Override
  public ASTCDType getConcreteType() {
    return conType;
  }

  @Override
  public void setConcreteType(ASTCDType conType) {
    this.conType = conType;
  }
}
