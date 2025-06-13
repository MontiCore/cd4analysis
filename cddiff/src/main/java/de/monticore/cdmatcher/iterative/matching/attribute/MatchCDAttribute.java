/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.iterative.matching.caching.CachedMatches;

import static com.google.common.math.DoubleMath.mean;

public class MatchCDAttribute implements MatchingStrategy<ASTCDAttribute> {
  
  @Override
  public double getScore(ASTCDAttribute srcElem, ASTCDAttribute tgtElem) {
    ASTCDType srcAttributeClassType = CDAttributeHelper.resolveClass(srcElem);
    ASTCDType tgtAttributeClassType = CDAttributeHelper.resolveClass(tgtElem);
    
    Double attributeClassType = CachedMatches.getMatch(srcAttributeClassType,
        tgtAttributeClassType);
    
    double score = new MatchCDAttributeByName().getScore(srcElem, tgtElem);
    
    if (attributeClassType != null) {
      score = mean(score, attributeClassType);
    }
    
    CachedMatches.putMatch(srcElem, tgtElem, score);
    return score;
  }
  
}
