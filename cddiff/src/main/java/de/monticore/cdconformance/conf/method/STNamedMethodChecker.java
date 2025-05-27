package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;
import java.util.Set;
import java.util.stream.Collectors;

public class STNamedMethodChecker extends AbstractMethodChecker {
  public STNamedMethodChecker(
      String mapping, String underspecifiedTypeName, ExternalCandidatesMatchingStrategy<ASTCDType> typeMatcher) {
    super(mapping, underspecifiedTypeName, typeMatcher);
  }

  @Override
  public Set<ASTCDMethod> getMatchedElements(ASTCDMethod concrete) {

    return refType.getCDMethodList().stream()
        .filter(ref -> isMatched(concrete, ref))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isMatched(ASTCDMethod concrete, ASTCDMethod ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return refType.getSpannedScope().resolveMethodMany(refName).contains(ref.getSymbol());
    }
    return false;
  }
}
