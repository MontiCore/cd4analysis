package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.iterative.similarity.CDTypeSimilarity;

public class MatchCDTypeByName implements MatchingStrategy<ASTCDType> {

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    CDTypeSimilarity similarity = new CDTypeSimilarity();
    return similarity.computeWeight(srcElem, tgtElem);
  }

}
