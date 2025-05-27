package de.monticore.cdmatcher.matching.cdtype;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cdmatcher.matching.MultipleMatchingStrategy;

public class MatchCDTypeByDirectSuperClasses extends MultipleMatchingStrategy<ASTCDType, ASTCDType> {


  @Override
  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    return getBestMatchingScore(
      srcElem,
      tgtElem,
      cdType -> CDInheritanceHelper.getDirectSuperClasses(cdType, (ICD4CodeArtifactScope) cdType.getEnclosingScope()),
      new MatchCDTypeFromCache()
    );
  }

}
