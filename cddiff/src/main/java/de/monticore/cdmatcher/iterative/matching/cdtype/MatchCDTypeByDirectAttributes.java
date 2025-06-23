/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.MultipleMatchingStrategy;
import de.monticore.cdmatcher.iterative.matching.caching.StructureCache;

public class MatchCDTypeByDirectAttributes extends
    MultipleMatchingStrategy<ASTCDType, ASTCDAttribute> {
  
  private final StructureCache structureCache;
  private final MatchingStrategy<ASTCDAttribute> strategy;
  
  public MatchCDTypeByDirectAttributes(StructureCache structureCache,
      MatchingStrategy<ASTCDAttribute> strategy) {
    this.structureCache = structureCache;
    this.strategy = strategy;
  }
  
  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(srcElem, tgtElem, structureCache::getAttributes, strategy);
  }
  
}
