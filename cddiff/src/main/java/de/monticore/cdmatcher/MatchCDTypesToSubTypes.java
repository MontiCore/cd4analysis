package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;

public class MatchCDTypesToSubTypes extends MatchCDTypeInHierarchy {

  public MatchCDTypesToSubTypes(
      MatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    super(typeMatcher, srcCD, tgtCD);
  }

  /**
   * A boolean method which checks if a subtype of srcElem matches to tgtCD
   *
   * @param tgtElem element from tgtCD
   * @return true if a subtype of srcElem matches to tgtCD
   */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return CDDiffUtil.getAllStrictSubTypes(srcElem, srcCD.getCDDefinition()).stream()
        .anyMatch(srcSuper -> typeMatcher.isMatched(srcSuper, tgtElem));
  }
}
