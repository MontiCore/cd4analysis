package de.monticore.cdmatcher.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.MultipleMatchingStrategy;
import de.monticore.cdmatcher.matching.caching.StructureCache;

public class MatchCDTypeByDirectSubClasses extends MultipleMatchingStrategy<ASTCDType, ASTCDType> {

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(
      srcElem,
      tgtElem,
      StructureCache::getDirectSubTypes,
      new MatchCDTypeFromCache()
    );
  }
}
