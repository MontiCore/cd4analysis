package de.monticore.cdmatcher.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.cdmatcher.similarity.CDTypeEmbeddingWithAttributesSimilarity;
import de.monticore.cdmatcher.similarity.CDTypeSimilarity;

public class MatchCDTypeByName implements MatchingStrategy<ASTCDType> {

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    CDTypeEmbeddingWithAttributesSimilarity similarity = new CDTypeEmbeddingWithAttributesSimilarity();
    return similarity.computeWeight(srcElem, tgtElem);
  }

}
