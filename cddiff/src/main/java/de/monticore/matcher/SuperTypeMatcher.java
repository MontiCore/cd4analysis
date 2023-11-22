package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import de.monticore.cddiff.CDDiffUtil;
import java.util.List;
import java.util.stream.Collectors;

public class SuperTypeMatcher implements MatchingStrategy<ASTCDType> {

  protected MatchingStrategy<ASTCDType> typeMatcher;
  protected final ASTCDCompilationUnit srcCD;
  protected final ASTCDCompilationUnit tgtCD;

  public SuperTypeMatcher(
      MatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    this.typeMatcher = typeMatcher;
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
  }

  @Override
  public List<ASTCDType> getMatchedElements(ASTCDType srcElem) {
    return tgtCD.getEnclosingScope().resolveCDTypeDownMany(srcElem.getName()).stream()
        .map(CDTypeSymbolTOP::getAstNode)
        .collect(Collectors.toList());
  }

  /**
   * A boolean method which checks the source class of the srcCD is a sub class pf the srcClass of
   * the tgtCD and if the associations are the same
   *
   * @param srcElem element from srcCD
   * @param tgtElem element from tgtCD
   * @return true if the source class of the tgtCD is a sub class of the tgt of the srcCD and if the
   *     associations are the same
   */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return checkSuperClass(srcElem, tgtElem, tgtCD);
  }

  /**
   * A boolean method which checks if srcClass from tgtCD is a Super Class of srcClass from srcCd
   *
   * @param tgtElem element from tgtCD
   * @return true if srcClass from tgtCD is a Super Class of srcClass from srcCd
   */
  public boolean checkSuperClass(ASTCDType srcElem, ASTCDType tgtElem, ASTCDCompilationUnit tgtCD) {
    return CDDiffUtil.getAllSuperTypes(tgtElem, tgtCD.getCDDefinition()).stream()
        .anyMatch(tgtSuper -> typeMatcher.isMatched(srcElem, tgtSuper));
  }
}
