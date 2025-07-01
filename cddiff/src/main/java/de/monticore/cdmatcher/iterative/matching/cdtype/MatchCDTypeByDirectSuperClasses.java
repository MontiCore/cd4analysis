/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MultipleMatchingStrategy;
import de.monticore.cdmatcher.caching.CachedMatches;
import de.monticore.cdmatcher.caching.StructureCache;

public class MatchCDTypeByDirectSuperClasses extends
    MultipleMatchingStrategy<ASTCDType, ASTCDType> {

  public CachedMatches cachedMatches;
  public StructureCache structureCache;

  public MatchCDTypeByDirectSuperClasses(CachedMatches cachedMatches,
      StructureCache structureCache) {
    this.cachedMatches = cachedMatches;
    this.structureCache = structureCache;
  }

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(srcElem, tgtElem, structureCache::getDirectSuperTypes,
        new MatchCDTypeFromCache(cachedMatches));
  }

}
