package de.monticore.cdmatcher.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.ow2cw.CDAttributeHelper;
import de.monticore.cdmatcher.matching.attribute.MatchCDAttribute;
import de.monticore.cdmatcher.matching.MultipleMatchingStrategy;

public class MatchCDTypeByAttribute extends MultipleMatchingStrategy<ASTCDType, ASTCDAttribute> {

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(
        srcElem,
        tgtElem,
      CDAttributeHelper::getAttributes,
      new MatchCDAttribute()
    );
  }
}
