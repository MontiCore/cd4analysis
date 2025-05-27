package de.monticore.cdmatcher.matching.cdtype;

import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdmatcher.matching.MultipleMatchingStrategy;

public class MatchCDTypeByDirectSubClasses extends MultipleMatchingStrategy<ASTCDType, ASTCDType> {
  ASTCDDefinition ast;

  MatchCDTypeByDirectSubClasses(ASTCDDefinition ast) {
    this.ast = ast;
  }

  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(
      srcElem,
      tgtElem,
      cdType -> CDDiffUtil.getAllStrictSubTypes(cdType, ast), // expensive, check if needed
      new MatchCDTypeFromCache()
    );
  }
}
