/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.iterative.matching.caching.CachedMatches;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDAttributeByNameAndType implements MatchingStrategy<ASTCDAttribute> {
  
  private final CachedMatches cachedMatches;
  private final MatchingStrategy<ASTCDAttribute> nameMatchingStrategy;
  
  public MatchCDAttributeByNameAndType(CachedMatches cachedMatches,
      MatchingStrategy<ASTCDAttribute> nameMatchingStrategy) {
    this.cachedMatches = cachedMatches;
    this.nameMatchingStrategy = nameMatchingStrategy;
  }
  
  @Override
  public double getScore(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    ASTCDType srcAttributeClassType = CDAttributeHelper.resolveClass(srcElem);
    ASTCDType tgtAttributeClassType = CDAttributeHelper.resolveClass(tgtElem);
    
    Double attributeClassType = cachedMatches.getMatch(srcAttributeClassType,
        tgtAttributeClassType);
    
    double score = nameMatchingStrategy.getScore(srcElem, tgtElem);
    
    if (attributeClassType != null) {
      score = mean(score, attributeClassType);
    }
    
    cachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }
  
}
