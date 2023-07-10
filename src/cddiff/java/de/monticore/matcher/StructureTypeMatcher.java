package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbolTOP;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StructureTypeMatcher implements MatchingStrategy<ASTCDType> {

  private final ASTCDCompilationUnit srcCD;
  private final ASTCDCompilationUnit tgtCD;

  public StructureTypeMatcher(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
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

  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    List<ASTCDAttribute> matchedAttributes = new ArrayList<>();
    if (srcElem.getCDAttributeList().size() >= (0.3 * getAverageForCD(srcCD))
        && tgtElem.getCDAttributeList().size() >= (0.3 * getAverageForCD(tgtCD))) {
      for (ASTCDAttribute srcAttr : srcElem.getCDAttributeList()) {
        for (ASTCDAttribute tgtAttr : tgtElem.getCDAttributeList()) {
          if (srcAttr.getName().equals(tgtAttr.getName())) {
            matchedAttributes.add(srcAttr);
          }
        }
      }
      if (matchedAttributes.size() >= (0.2 * srcElem.getCDAttributeList().size())) {
        return true;
      }
    }
    return false;
  }

  public double getAverageForCD(ASTCDCompilationUnit cd) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (ASTCDClass tgtType : tgtCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDAttribute tgtAttr : tgtType.getCDAttributeList()) {
        attributes.add(tgtAttr);
      }
    }
    return attributes.size() / cd.getCDDefinition().getCDClassesList().size();
  }
}
