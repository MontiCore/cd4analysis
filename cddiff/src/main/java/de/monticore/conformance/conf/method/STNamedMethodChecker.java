package de.monticore.conformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.conformance.conf.MethodChecker;
import java.util.List;
import java.util.stream.Collectors;

public class STNamedMethodChecker implements MethodChecker {
  protected String mapping;
  protected ASTCDType refType;
  protected ASTCDType conType;

  public STNamedMethodChecker(String mapping) {
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
