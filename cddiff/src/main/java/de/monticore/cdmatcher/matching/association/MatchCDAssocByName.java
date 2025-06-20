package de.monticore.cdmatcher.matching.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.cdmatcher.similarity.CDAssocEmbeddingSimilarity;
import de.monticore.cdmatcher.similarity.CDAssocSimilarity;

public class MatchCDAssocByName implements MatchingStrategy<ASTCDAssociation> {

  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    CDAssocEmbeddingSimilarity similarity = new CDAssocEmbeddingSimilarity();
    return similarity.computeWeight(srcElem, tgtElem);
  }
}
