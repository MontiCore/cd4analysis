package de.monticore.conformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.conformance.conf.AttributeChecker;
import java.util.ArrayList;
import java.util.List;

public class CompAttributeChecker implements AttributeChecker {
  protected String mapping;
  protected ASTCDType conType;

  protected ASTCDType refType;

  List<AttributeChecker> attributeCheckers = new ArrayList<>();

  public CompAttributeChecker(String mapping) {
    this.mapping = mapping;
  }

  public void addIncStrategy(AttributeChecker checker) {
    attributeCheckers.add(checker);
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    List<ASTCDAttribute> refElements = new ArrayList<>();

    for (AttributeChecker checker : attributeCheckers) {
      refElements.addAll(checker.getMatchedElements(concrete));
      if (!refElements.isEmpty()) {
        return refElements;
      }
    }

    return refElements;
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    return getMatchedElements(concrete).contains(ref);
  }

  @Override
  public ASTCDType getReferenceType() {
    return refType;
  }

  @Override
  public void setReferenceType(ASTCDType refType) {
    this.refType = refType;
    attributeCheckers.forEach(checker -> checker.setReferenceType(refType));
  }

  @Override
  public ASTCDType getConcreteType() {
    return conType;
  }

  @Override
  public void setConcreteType(ASTCDType conType) {
    this.conType = conType;
    attributeCheckers.forEach(checker -> checker.setConcreteType(conType));
  }
}
