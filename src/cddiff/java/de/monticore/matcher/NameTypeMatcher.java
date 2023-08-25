package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import java.util.List;
import java.util.stream.Collectors;

public class NameTypeMatcher implements MatchingStrategy<ASTCDType> {

  private final ASTCDCompilationUnit tgtCD;

  public NameTypeMatcher(ASTCDCompilationUnit tgtCD) {
    this.tgtCD = tgtCD;
  }

  /**
   * A set for the matched elements which can be per definition modified
   *
   * @return all elements which have been matched
   */
  @Override
  public List<ASTCDType> getMatchedElements(ASTCDType srcElem) {
    return tgtCD.getEnclosingScope().resolveCDTypeDownMany(srcElem.getName()).stream()
        .map(CDTypeSymbolTOP::getAstNode)
        .collect(Collectors.toList());
  }

  /**
   * A boolean method which gives if the name of the tgterence type from tgtCD is matched with the
   * name of the srccrete type from srcCD
   *
   * @param srcElem The tgterence type which we pick from the tgterence class diagram
   * @param tgtElem The srccrete type which we pick from the srccrete class diagram
   * @return true if both types have the same name
   */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    if (srcElem.getName().equals(tgtElem.getName())) {
      return true;
    }
    return false;
  }
}
