package de.monticore.matcher;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StructureTypeMatcher implements MatchingStrategy<ASTCDType> {

  private final ASTCDCompilationUnit tgtCD;
  public double threshold = 0.5;

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
    List<ASTCDType> result = new ArrayList<>();

    result.addAll(
        tgtCD.getCDDefinition().getCDClassesList().stream()
            .filter(type -> isMatched(srcElem, type))
            .collect(Collectors.toList()));
    result.addAll(
        tgtCD.getCDDefinition().getCDInterfacesList().stream()
            .filter(type -> isMatched(srcElem, type))
            .collect(Collectors.toList()));

    return result;
  }

  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    List<ASTCDAttribute> srcAttr = new ArrayList<>(srcElem.getCDAttributeList());
    List<ASTCDAttribute> tgtAttr = new ArrayList<>(tgtElem.getCDAttributeList());

    List<ASTCDAttribute> tgtAttrDeletedAttr = new ArrayList<>(tgtAttr);
    List<ASTCDAttribute> similarities = new ArrayList<>();

    for (ASTCDAttribute x : srcAttr) {
      for (ASTCDAttribute y : tgtAttr) {
        if (x.getName().equals(y.getName())) {
          tgtAttrDeletedAttr.remove(y);
          similarities.add(x);
        }
      }
    }

    List<ASTCDAttribute> allAttributes = new ArrayList<>(srcAttr);
    allAttributes.addAll(tgtAttrDeletedAttr);

    // Jaccard Index
    double weight = (double) similarities.size() / allAttributes.size();

    // Chosen threshold
    return weight >= threshold;
  }
}
