package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.CDAttributeChecker;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import java.util.ArrayList;
import java.util.List;

public class CompAttributeChecker extends AbstractAttributeChecker {

  private final List<CDAttributeChecker> attributeCheckers = new ArrayList<>();

  public CompAttributeChecker(
      String mapping, String underspecifiedTypeName, MatchingStrategy<ASTCDType> typeMatcher) {
    super(mapping, underspecifiedTypeName, typeMatcher);
  }

  public void addIncStrategy(CDAttributeChecker checker) {
    attributeCheckers.add(checker);
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    List<ASTCDAttribute> refElements = new ArrayList<>();

    for (CDAttributeChecker checker : attributeCheckers) {
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
  public void setReferenceType(ASTCDType refType) {
    this.referenceType = refType;
    attributeCheckers.forEach(checker -> checker.setReferenceType(refType));
  }

  @Override
  public void setConcreteType(ASTCDType conType) {
    this.concreteType = conType;
    attributeCheckers.forEach(checker -> checker.setConcreteType(conType));
  }
}
