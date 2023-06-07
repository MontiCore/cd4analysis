package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import java.util.Set;
import java.util.stream.Collectors;

public class NameTypeMatcher implements MatchingStrategy<ASTCDType> {
  /**
   * A set for the matched elements which can be per definition modified
   *
   * @return all elements which have been matched
   */
  @Override
  public Set<ASTCDType> getMatchedElements(
      ASTCDType srcElem, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    return tgtCD.getEnclosingScope().resolveCDTypeDownMany(srcElem.getName()).stream()
        .map(CDTypeSymbolTOP::getAstNode)
        .collect(Collectors.toSet());
  }

  /**
   * A boolean method which gives if the name of the tgterence type from tgtCD is matched with the
   * name of the srccrete type from srcCD
   *
   * @param srcElem The tgterence type which we pick from the tgterence class diagram
   * @param tgtElem The srccrete type which we pick from the srccrete class diagram
   * @param srcCD The srccrete class diagram is the new (tgtined) class diagram
   * @param tgtCD The tgterence class diagram is the old class diagram
   * @return true if both types have the same name
   */
  @Override
  public boolean isMatched(
      ASTCDType srcElem,
      ASTCDType tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    if ((srcElem.getSymbol().getInternalQualifiedName())
        .equals(tgtElem.getSymbol().getInternalQualifiedName())) {
      return true;
    } else {
      System.out.println("Types names do not match!");
    }
    return false;
  }
}
