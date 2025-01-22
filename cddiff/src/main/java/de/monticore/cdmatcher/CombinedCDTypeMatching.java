package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.List;

public class CombinedCDTypeMatching extends CD2CDCombinedMatching<ASTCDType> {

  public CombinedCDTypeMatching(
      List<ASTCDType> listToMatch,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD,
      List<MatchingStrategy<ASTCDType>> matcherList) {
    super(listToMatch, srcCD, tgtCD, matcherList);
  }

  public Double computeValueForMatching(ASTCDType srcElem, ASTCDType tgtElem) {
    return new CDTypeSimilarity().computeWeight(srcElem, tgtElem);
  }
}
