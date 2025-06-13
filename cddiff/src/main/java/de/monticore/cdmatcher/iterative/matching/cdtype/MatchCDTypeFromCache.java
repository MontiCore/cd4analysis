package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.iterative.matching.caching.CachedMatches;

public class MatchCDTypeFromCache implements MatchingStrategy<ASTCDType> {

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    Double cachedScore = CachedMatches.getMatch(srcElem, tgtElem);

    return cachedScore != null ? cachedScore : new MatchCDTypeByName().getScore(srcElem, tgtElem);
  }
}
