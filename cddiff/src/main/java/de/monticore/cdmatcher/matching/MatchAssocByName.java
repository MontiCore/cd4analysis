package de.monticore.cdmatcher.matching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdmatcher.similarity.CDAssocSimilarity;

public class MatchAssocByName implements MatchingStrategy<ASTCDAssociation> {

  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    CDAssocSimilarity similarity = new CDAssocSimilarity();
    return similarity.computeWeight(srcElem, tgtElem);
  }
}
