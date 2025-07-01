/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.booleanMatching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdmatcher.BooleanMatchingStrategy;

public class MatchCDAssocsByName implements BooleanMatchingStrategy<ASTCDAssociation> {

  /** Match two associations iff their names are present and equal. */
  @Override
  public boolean isMatched(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    if (tgtElem.isPresentName() && srcElem.isPresentName()) {
      return tgtElem.getName().equals(srcElem.getName());
    }
    return false;
  }

}
