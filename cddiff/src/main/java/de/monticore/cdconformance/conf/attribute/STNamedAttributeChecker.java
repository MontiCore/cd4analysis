package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.Set;
import java.util.stream.Collectors;

public class STNamedAttributeChecker extends AbstractAttributeChecker {

  public STNamedAttributeChecker(
      String mapping, String underspecifiedTypeName, ExternalCandidatesMatchingStrategy<ASTCDType> typeMatcher) {
    super(mapping, underspecifiedTypeName, typeMatcher);
  }

  @Override
  public Set<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {

    return referenceType.getCDAttributeList().stream()
        .filter(ref -> isMatched(concrete, ref))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return referenceType.getSpannedScope().resolveFieldMany(refName).contains(ref.getSymbol());
    }
    return false;
  }
}
