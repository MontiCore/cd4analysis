package de.monticore.cdmatcher.matching;

import de.monticore.cdbasis._ast.ASTCDType;

public class MatchCDType implements MatchingStrategy<ASTCDType>{

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    double nameMatch = new MatchCDTypeByName().getScore(srcElem, tgtElem);
    double attributeMatch = new MatchAttributeByName().getScore(srcElem, tgtElem);
    double assocMatch = new MatchAssocByName().getScore(srcElem, tgtElem);
    // match superclass
    // match subclass



  }
}
