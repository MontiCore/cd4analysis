package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.CDAttributeChecker;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompAttributeChecker extends AbstractAttributeChecker {

  private final List<CDAttributeChecker> attributeCheckers = new ArrayList<>();

  public CompAttributeChecker(
      String mapping, String underspecifiedTypeName, ExternalCandidatesMatchingStrategy<ASTCDType> typeMatcher) {
    super(mapping, underspecifiedTypeName, typeMatcher);
  }

  public void addIncStrategy(CDAttributeChecker checker) {
    attributeCheckers.add(checker);
  }

  @Override
  public Set<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    Set<ASTCDAttribute> refElements = new HashSet<>();

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
