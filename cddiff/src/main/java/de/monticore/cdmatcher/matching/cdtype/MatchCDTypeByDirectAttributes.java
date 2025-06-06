package de.monticore.cdmatcher.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.matching.attribute.MatchCDAttribute;
import de.monticore.cdmatcher.matching.MultipleMatchingStrategy;
import de.monticore.cdmatcher.matching.caching.StructureCache;

public class MatchCDTypeByDirectAttributes extends MultipleMatchingStrategy<ASTCDType, ASTCDAttribute> {

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(
        srcElem,
        tgtElem,
      StructureCache::getAttributes,
      new MatchCDAttribute()
    );
  }
}
