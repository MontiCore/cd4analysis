package de.monticore.cdmatcher.matching.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.cdmatcher.similarity.CDAttributeEmbeddingSimilarity;
import de.monticore.cdmatcher.similarity.CDAttributeSimilarity;

public class MatchCDAttributeByName implements MatchingStrategy<ASTCDAttribute> {

  @Override
  public double getScore(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    CDAttributeEmbeddingSimilarity similarity = new CDAttributeEmbeddingSimilarity();
    return similarity.computeWeight(srcElem, tgtElem);
  }
}
