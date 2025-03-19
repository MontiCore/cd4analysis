package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;

public class MatchCDTypesToSuperTypes extends MatchCDTypeInHierarchy {

  public MatchCDTypesToSuperTypes(
      MatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    super(typeMatcher, srcCD, tgtCD);
  }

  /**
   * A boolean method which checks if the source class of the srcCD is a subclass of the srcClass of
   * the tgtCD and if the associations are the same
   *
   * @param srcElem element from srcCD
   * @param tgtElem element from tgtCD
   * @return true if the source class of the tgtCD is a subclass of the tgt of the srcCD and if the
   *     associations are the same
   */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return checkSuperType(srcElem, tgtElem, srcCD);
  }

  /**
   * A boolean method which checks if srcClass from tgtCD is a Super Class of srcClass from srcCd
   *
   * @param tgtElem element from tgtCD
   * @return true if srcClass from tgtCD is a Super Class of srcClass from srcCd
   */
  public boolean checkSuperType(ASTCDType srcElem, ASTCDType tgtElem, ASTCDCompilationUnit srcCD) {
    return CDDiffUtil.getAllSuperTypes(srcElem, srcCD.getCDDefinition()).stream()
        .anyMatch(srcSuper -> typeMatcher.isMatched(srcSuper, tgtElem));
  }
}
