package de.monticore.cdmatcher.matching.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.cdmatcher.similarity.CDAttributeSimilarity;

public class MatchCDAttributeByName implements MatchingStrategy<ASTCDAttribute> {

  @Override
  public double getScore(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    CDAttributeSimilarity similarity = new CDAttributeSimilarity();
    return similarity.computeWeight(srcElem, tgtElem);
  }
}
