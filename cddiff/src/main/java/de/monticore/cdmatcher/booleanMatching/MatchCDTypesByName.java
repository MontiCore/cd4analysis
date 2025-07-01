/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.booleanMatching;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdmatcher.BooleanMatchingStrategy;

public class MatchCDTypesByName implements BooleanMatchingStrategy<ASTCDType> {

  /** Match types iff they have the same name. */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return srcElem.getName().equals(tgtElem.getName());
  }

}
