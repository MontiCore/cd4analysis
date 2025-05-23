package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.MatchingStrategy;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/** Matches types iff they have the same qualified name. */
public class MatchCDTypesByQName2Set implements MatchingStrategy<ASTCDType> {

  protected LinkedHashSet<ASTCDType> tgtSet;

  public MatchCDTypesByQName2Set(Collection<ASTCDType> tgtSet) {
    this.tgtSet = new LinkedHashSet<>(tgtSet);
  }

  @Override
  public List<ASTCDType> getMatchedElements(ASTCDType srcElem) {
    return tgtSet.stream().filter(type -> isMatched(srcElem, type)).collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return srcElem
        .getSymbol()
        .getInternalQualifiedName()
        .equals(tgtElem.getSymbol().getInternalQualifiedName());
  }
}
