package de.monticore.cdmatcher.iterative.matching.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.iterative.similarity.CDAssocSimilarity;

public class MatchCDAssocByName implements MatchingStrategy<ASTCDAssociation> {

  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    CDAssocSimilarity similarity = new CDAssocSimilarity();
    return similarity.computeWeight(srcElem, tgtElem);
  }
}
