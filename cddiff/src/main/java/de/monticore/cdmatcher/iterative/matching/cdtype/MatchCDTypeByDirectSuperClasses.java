package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MultipleMatchingStrategy;
import de.monticore.cdmatcher.iterative.matching.caching.StructureCache;

public class MatchCDTypeByDirectSuperClasses extends MultipleMatchingStrategy<ASTCDType, ASTCDType> {


  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(
      srcElem,
      tgtElem,
      StructureCache::getDirectSuperTypes,
      new MatchCDTypeFromCache()
    );
  }

}
