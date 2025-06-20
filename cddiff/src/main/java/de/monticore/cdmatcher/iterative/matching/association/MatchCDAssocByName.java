/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.iterative.similarity.CDAssocSimilarity4Iterative;

public class MatchCDAssocByName implements MatchingStrategy<ASTCDAssociation> {
  
  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    CDAssocSimilarity4Iterative similarity = new CDAssocSimilarity4Iterative();
    return similarity.computeWeight(srcElem, tgtElem);
  }
  
}
