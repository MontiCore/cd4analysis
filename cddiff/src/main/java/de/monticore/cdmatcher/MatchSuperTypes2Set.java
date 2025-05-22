package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MatchSuperTypes2Set implements MatchingStrategy<ASTCDType> {

  protected MatchingStrategy<ASTCDType> typeMatcher;
  protected LinkedHashSet<ASTCDType> tgtSet;

  public MatchSuperTypes2Set(
      MatchingStrategy<ASTCDType> typeMatcher, Collection<ASTCDType> tgtSet) {
    this.typeMatcher = typeMatcher;
    this.tgtSet = new LinkedHashSet<>(tgtSet);
  }

  @Override
  public List<ASTCDType> getMatchedElements(ASTCDType srcElem) {
    return tgtSet.stream()
        .filter(tgtElem -> isMatched(srcElem, tgtElem))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return CDDiffUtil.getAllSuperTypes(srcElem).stream()
        .anyMatch(srcSuper -> typeMatcher.isMatched(srcSuper, tgtElem));
  }
}
