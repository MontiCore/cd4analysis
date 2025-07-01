/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.booleanMatching;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.monticore.cdmatcher.caching.StructureCache;

/**
 * A special type matching strategy that matches the reference type if any strict subtype of a
 * concrete type is an incarnation of the reference type.
 */
public class MatchCDTypesToSubType implements BooleanMatchingStrategy<ASTCDType> {
  private final BooleanMatchingStrategy<ASTCDType> typeMatcher;
  private final StructureCache structureCache;

  public MatchCDTypesToSubType(BooleanMatchingStrategy<ASTCDType> typeMatcher, StructureCache structureCache) {
    this.typeMatcher = typeMatcher;
    this.structureCache = structureCache;
  }

  /**
   * A boolean method which checks if a subtype of srcElem matches to tgtCD
   *
   * @param tgtElem element from tgtCD
   * @return true if a subtype of srcElem matches to tgtCD
   */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return structureCache.getSubTypes(srcElem).stream().anyMatch(srcSuper -> typeMatcher.isMatched(srcSuper, tgtElem));
  }

}
