package de.monticore.matcher;

import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StructureMemberMatcher implements MatchingStrategy<ASTCDAttribute> {

  private final ASTCDType tgtType;

  public StructureMemberMatcher(ASTCDType tgtType) {
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

  @Override
  public boolean isMatched(ASTCDAttribute srcAttr, ASTCDAttribute tgtAttr) {
    if ((srcAttr.getMCType())
      .equals(tgtAttr.getMCType())) {
      return true;
    } else {
      System.out.println("Attributes types do not match!");
    }
    return false;
  }

}
