/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;

public class MatchCDTypeFromCache implements MatchingStrategy<ASTCDType> {

  public CachedMatches cachedMatches;

  public MatchCDTypeFromCache(CachedMatches cachedMatches) {
    this.cachedMatches = cachedMatches;
  }

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    Double cachedScore = cachedMatches.getMatch(srcElem, tgtElem);

    return cachedScore != null ? cachedScore : new MatchCDTypeByName().getScore(srcElem, tgtElem);
  }

}
