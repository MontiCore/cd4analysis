package de.monticore.cdmatcher.matching.cdtype;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cdmatcher.matching.association.MatchCDAssoc;
import de.monticore.cdmatcher.matching.MultipleMatchingStrategy;

public class MatchCDTypeByAssocs extends MultipleMatchingStrategy<ASTCDType, ASTCDAssociation> {

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(
      srcElem,
      tgtElem,
      CDAssociationHelper::getAssociations,
      new MatchCDAssoc()
    );
  }
}
