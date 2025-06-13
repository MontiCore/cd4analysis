package de.monticore.cdmatcher.iterative.matching.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.iterative.similarity.CDAttributeSimilarity;

public class MatchCDAttributeByName implements MatchingStrategy<ASTCDAttribute> {

  @Override
  public double getScore(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    CDAttributeSimilarity similarity = new CDAttributeSimilarity();
    return similarity.computeWeight(srcElem, tgtElem);
  }
}
