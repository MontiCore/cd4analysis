package de.monticore.conformance.basic.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.conformance.AttributeChecker;
import java.util.Set;
import java.util.stream.Collectors;

public class EqNameAttributeChecker implements AttributeChecker {
  protected ASTCDType refType;
  protected ASTCDType conType;
  protected String mapping;

  public EqNameAttributeChecker(String mapping) {
    this.mapping = mapping;
  }

  @Override
  public Set<ASTCDAttribute> getRefElements(ASTCDAttribute concrete) {
    return refType.getCDAttributeList().stream()
        .filter(attr -> isIncarnation(concrete, attr))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isIncarnation(ASTCDAttribute concrete, ASTCDAttribute ref) {
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
