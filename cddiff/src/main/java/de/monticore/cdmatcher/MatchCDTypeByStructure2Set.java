package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.cdmatcher.similarity.CDTypeSimilarity;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MatchCDTypeByStructure2Set implements MatchingStrategy<ASTCDType> {

  protected LinkedHashSet<ASTCDType> tgtSet;
  protected double threshold = 0.5;
  protected final CDTypeSimilarity typeSimilarity = new CDTypeSimilarity();

  public MatchCDTypeByStructure2Set(Collection<ASTCDType> tgtSet, double threshold) {
    this.tgtSet = new LinkedHashSet<>(tgtSet);
    this.threshold = threshold;
  }

  public MatchCDTypeByStructure2Set(Collection<ASTCDType> tgtSet) {
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
    return typeSimilarity.computeWeight(srcElem, tgtElem) >= threshold;
  }
}
