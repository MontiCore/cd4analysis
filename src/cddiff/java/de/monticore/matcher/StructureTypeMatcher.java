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

  private final ASTCDCompilationUnit tgtCD;

  public StructureTypeMatcher(ASTCDCompilationUnit tgtCD) {
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
    if (tgtElem.getCDAttributeList().size() >= (0.3 * getAverageForCD(tgtCD))) {
      for (ASTCDAttribute srcAttr : srcElem.getCDAttributeList()) {
        for (ASTCDAttribute tgtAttr : tgtElem.getCDAttributeList()) {
          if (srcAttr.getName().equals(tgtAttr.getName())) {
            matchedAttributes.add(srcAttr);
          }
        }
      }
        return matchedAttributes.size() >= (0.2 * srcElem.getCDAttributeList().size());
    }
    return false;
  }

  public double getAverageForCD(ASTCDCompilationUnit cd) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for (ASTCDClass tgtType : tgtCD.getCDDefinition().getCDClassesList()) {
      attributes.addAll(tgtType.getCDAttributeList());
    }
    return (double) attributes.size() / cd.getCDDefinition().getCDClassesList().size();
  }
}
