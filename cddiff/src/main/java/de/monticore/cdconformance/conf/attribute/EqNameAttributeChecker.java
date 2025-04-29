package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.conf.CDAttributeChecker;
import de.monticore.cdmatcher.MatchingStrategy;

import java.util.List;
import java.util.stream.Collectors;

public class EqNameAttributeChecker extends AbstractAttributeChecker {

  public EqNameAttributeChecker(String mapping, MatchingStrategy<ASTCDType> typeMatcher) {
    super(mapping, typeMatcher);
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    return refType.getCDAttributeList().stream()
        .filter(attr -> isMatched(concrete, attr))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    return ref.getName().equals(concrete.getName());
  }
}
