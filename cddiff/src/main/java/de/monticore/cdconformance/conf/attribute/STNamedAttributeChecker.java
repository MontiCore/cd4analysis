package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.CDAttributeChecker;
import java.util.List;
import java.util.stream.Collectors;

public class STNamedAttributeChecker implements CDAttributeChecker {
  protected String mapping;
  protected ASTCDType refType;
  protected ASTCDType conType;

  public STNamedAttributeChecker(String mapping) {
    this.mapping = mapping;
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {

    return refType.getCDAttributeList().stream()
        .filter(ref -> isMatched(concrete, ref))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return ref.getName().equals(refName);
    }
    return false;
  }

  @Override
  public ASTCDType getReferenceType() {
    return this.refType;
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
