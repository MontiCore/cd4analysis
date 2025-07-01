/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.booleanMatching;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.caching.StructureCache;

public class MatchCDTypesToSuperType implements BooleanMatchingStrategy<ASTCDType> {
  private final BooleanMatchingStrategy<ASTCDType> typeMatcher;
  private final StructureCache structureCache;

  public MatchCDTypesToSuperType(BooleanMatchingStrategy<ASTCDType> typeMatcher, StructureCache structureCache) {
    this.typeMatcher = typeMatcher;
    this.structureCache = structureCache;
  }

  /**
   * A boolean method which checks if a supertype of srcElem matches to tgtCD
   *
   * @param tgtElem element from tgtCD
   * @return true if a supertype of srcElem matches to tgtCD
   */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return structureCache.getSuperTypes(srcElem).stream().anyMatch(srcSuper -> typeMatcher.isMatched(srcSuper, tgtElem));
  }

}
