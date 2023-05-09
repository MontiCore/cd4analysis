package de.monticore.conformance.basic.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.conformance.AttributeChecker;
import java.util.Set;
import java.util.stream.Collectors;

public class STNamedAttributeChecker implements AttributeChecker {
  protected String mapping;
  protected ASTCDType refType;
  protected ASTCDType conType;

  public STNamedAttributeChecker(String mapping) {
    this.mapping = mapping;
  }

  @Override
  public Set<ASTCDAttribute> getRefElements(ASTCDAttribute concrete) {

    return refType.getCDAttributeList().stream()
        .filter(ref -> isIncarnation(concrete, ref))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isIncarnation(ASTCDAttribute concrete, ASTCDAttribute ref) {
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
