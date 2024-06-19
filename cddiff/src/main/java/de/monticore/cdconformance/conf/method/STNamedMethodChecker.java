package de.monticore.cdconformance.conf.method;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import java.util.List;
import java.util.stream.Collectors;

public class STNamedMethodChecker extends AbstractMethodChecker {
  public STNamedMethodChecker(String mapping, MatchingStrategy<ASTCDType> typeMatcher) {
    this.mapping = mapping;
    this.typeMatcher = typeMatcher;
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
}
