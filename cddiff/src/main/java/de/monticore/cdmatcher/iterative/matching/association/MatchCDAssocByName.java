/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.iterative.similarity.CDAssocSimilarityNormalized;

public class MatchCDAssocByName implements MatchingStrategy<ASTCDAssociation> {
  
  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    CDAssocSimilarityNormalized similarity = new CDAssocSimilarityNormalized();
    return similarity.computeWeight(srcElem, tgtElem);
  }
  
}
