package de.monticore.cdmatcher.matching;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdmatcher.similarity.CDAttributeSimilarity;

public class MatchAttributeByName implements MatchingStrategy<ASTCDAttribute> {

  @Override
  public double getScore(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    CDAttributeSimilarity similarity = new CDAttributeSimilarity();
    return similarity.computeWeight(srcElem, tgtElem);
  }
}
