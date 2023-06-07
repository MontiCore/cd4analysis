package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import de.monticore.cddiff.CDDiffUtil;
import de.se_rwth.commons.logging.Log;
import java.util.Set;
import java.util.stream.Collectors;

public class SuperTypeMatcher implements MatchingStrategy<ASTCDType> {

  protected MatchingStrategy<ASTCDType> typeMatcher;

  public SuperTypeMatcher(MatchingStrategy<ASTCDType> typeMatcher) {
    this.typeMatcher = typeMatcher;
  }

  @Override
  public Set<ASTCDType> getMatchedElements(
      ASTCDType srcElem, ASTCDCompilationUnit tgtCD, ASTCDCompilationUnit srcCD) {
    return tgtCD.getEnclosingScope().resolveCDTypeDownMany(srcElem.getName()).stream()
        .map(CDTypeSymbolTOP::getAstNode)
        .collect(Collectors.toSet());
  }

  /**
   * A boolean method which checks the source class of the srcCD is a sub class pf the srcClass of
   * the tgtCD and if the associations are the same
   *
   * @param srcElem element from srcCD
   * @param tgtElem element from tgtCD
   * @param srcCD target CD which has been improved
   * @param tgtCD source CD which has not been improved
   * @return true if the source class of the tgtCD is a sub class of the tgt of the srcCD and if the
   *     associations are the same
   */
  @Override
  public boolean isMatched(
      ASTCDType srcElem,
      ASTCDType tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    if (checkSuperClass(srcElem, tgtElem, srcCD, tgtCD)) {
      return true;
    } else {
      Log.error("There is a problem with isMatched() in MatchAssocSubSuperTyp!");
    }
    return false;
  }

  /**
   * A boolean method which checks if srcClass from tgtCD is a Super Class of srcClass from srcCd
   *
   * @param tgtElem element from tgtCD
   * @return true if srcClass from tgtCD is a Super Class of srcClass from srcCd
   */
  public boolean checkSuperClass(
      ASTCDType srcElem,
      ASTCDType tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {

    boolean superType =
        CDDiffUtil.getAllSuperTypes(tgtElem, tgtCD.getCDDefinition()).stream()
            .anyMatch(tgtSuper -> typeMatcher.isMatched(srcElem, tgtSuper, srcCD, tgtCD));

    if (superType) {
      return true;
    } else {
      Log.error("There is a problem with checkSuperClass()!");
    }
    return false;
  }
}
