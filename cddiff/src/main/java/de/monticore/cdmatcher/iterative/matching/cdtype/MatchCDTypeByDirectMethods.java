package de.monticore.cdmatcher.iterative.matching.cdtype;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.cdmatcher.MultipleMatchingStrategy;
import de.monticore.cdmatcher.caching.StructureCache;

public class MatchCDTypeByDirectMethods extends MultipleMatchingStrategy<ASTCDType, ASTCDMethod> {

  private final StructureCache structureCache;
  private final MatchingStrategy<ASTCDMethod> strategy;

  public MatchCDTypeByDirectMethods(StructureCache structureCache, MatchingStrategy<ASTCDMethod> methodMatchingStrategy) {
    this.structureCache = structureCache;
    this.strategy = methodMatchingStrategy;
  }

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(srcElem, tgtElem, structureCache::getDirectMethods, strategy);
  }
}
