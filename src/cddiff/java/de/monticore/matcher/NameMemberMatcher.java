package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;

import java.util.List;
import java.util.stream.Collectors;

public class NameMemberMatcher implements MatchingStrategy<ASTCDAttribute> {

  private final ASTCDType tgtType;

  public NameMemberMatcher(ASTCDType tgtType) {
    this.tgtType = tgtType;
  }

  /**
   * A set for the matched elements which can be per definition modified
   *
   * @return all elements which have been matched
   */
  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute attribute) {
    return tgtType.getCDAttributeList().stream()
      .filter(attr -> isMatched(attribute, attr))
      .collect(Collectors.toList());
  }

  /**
   * A boolean method which gives if the name of a member from srcCD is matched with the
   * name of a member from tgtCD
   *
   * @param srcAttr
   * @param tgtAttr
   * @return true if both types have the same name
   */
  @Override
  public boolean isMatched(ASTCDAttribute srcAttr, ASTCDAttribute tgtAttr) {
    if ((srcAttr.getName())
        .equals(tgtAttr.getName())) {
      return true;
    } else {
      System.out.println("Attributes names do not match!");
    }
    return false;
  }
}
