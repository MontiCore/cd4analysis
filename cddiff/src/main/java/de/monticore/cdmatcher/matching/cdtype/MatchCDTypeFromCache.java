package de.monticore.cdmatcher.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.caching.CachedMatches;
import de.monticore.cdmatcher.matching.MatchingStrategy;

public class MatchCDTypeFromCache implements MatchingStrategy<ASTCDType> {

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    Double cachedScore = CachedMatches.getMatch(srcElem, tgtElem);

    return cachedScore != null ? cachedScore : new MatchCDTypeByName().getScore(srcElem, tgtElem);
  }
}
