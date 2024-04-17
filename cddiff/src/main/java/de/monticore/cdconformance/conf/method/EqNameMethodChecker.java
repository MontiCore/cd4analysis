package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.MethodChecker;
import java.util.List;
import java.util.stream.Collectors;

public class EqNameMethodChecker implements MethodChecker {
  protected ASTCDType refType;
  protected ASTCDType conType;
  protected String mapping;

  public EqNameMethodChecker(String mapping) {
    this.mapping = mapping;
  }

  @Override
  public List<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {
    return refType.getCDMethodList().stream()
        .filter(method -> isMatched(concrete, method))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
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
