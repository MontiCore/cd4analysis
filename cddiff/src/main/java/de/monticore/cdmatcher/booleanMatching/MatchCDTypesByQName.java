/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.booleanMatching;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.BooleanMatchingStrategy;

/** Matches types iff they have the same qualified name. */
public class MatchCDTypesByQName implements BooleanMatchingStrategy<ASTCDType> {

  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return srcElem.getSymbol().getInternalQualifiedName().equals(tgtElem.getSymbol()
        .getInternalQualifiedName());
  }

}
