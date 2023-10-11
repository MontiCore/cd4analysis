package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import java.util.ArrayList;
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
    List<ASTCDType> result = new ArrayList<>();

    result.addAll(
        tgtCD.getCDDefinition().getCDClassesList().stream()
            .filter(type -> isMatched(srcElem, type))
            .collect(Collectors.toList()));
    result.addAll(
        tgtCD.getCDDefinition().getCDInterfacesList().stream()
            .filter(type -> isMatched(srcElem, type))
            .collect(Collectors.toList()));
    result.addAll(
        tgtCD.getCDDefinition().getCDEnumsList().stream()
            .filter(type -> isMatched(srcElem, type))
            .collect(Collectors.toList()));

    return result;
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
    if (checkSuperClass(srcElem, tgtElem, srcCD)) {
      return true;
    }
    return false;
  }

  /**
   * A boolean method which checks if srcClass from tgtCD is a Super Class of srcClass from srcCd
   *
   * @param tgtElem element from tgtCD
   * @return true if srcClass from tgtCD is a Super Class of srcClass from srcCd
   */
  public boolean checkSuperClass(ASTCDType srcElem, ASTCDType tgtElem, ASTCDCompilationUnit srcCD) {

    boolean superType =
        CDDiffUtil.getAllSuperTypes(srcElem, srcCD.getCDDefinition()).stream()
            .anyMatch(srcSuper -> typeMatcher.isMatched(srcSuper, tgtElem));

    if (superType) {
      return true;
    }
    return false;
  }
}
