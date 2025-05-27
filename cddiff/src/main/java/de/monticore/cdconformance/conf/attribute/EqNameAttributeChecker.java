package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.Set;
import java.util.stream.Collectors;

public class EqNameAttributeChecker extends AbstractAttributeChecker {

  public EqNameAttributeChecker(
      String mapping, String underspecifiedTypeName, ExternalCandidatesMatchingStrategy<ASTCDType> typeMatcher) {
    super(mapping, underspecifiedTypeName, typeMatcher);
  }

  @Override
  public Set<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    return referenceType.getCDAttributeList().stream()
        .filter(attr -> isMatched(concrete, attr))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    return ref.getName().equals(concrete.getName());
  }
}
