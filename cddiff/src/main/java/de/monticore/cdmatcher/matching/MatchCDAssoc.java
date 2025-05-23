package de.monticore.cdmatcher.matching;

import de.monticore.cdassociation._ast.ASTCDAssociation;

public class MatchCDAssoc implements MatchingStrategy<ASTCDAssociation> {

  @Override
  public double getScore(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    // If right and left types are in CachedMatches use their score plus the name to compute the score
    // Otherwise use the name to compute the score

    return new MatchAssocByName().getScore(srcElem, tgtElem);
  }
}
