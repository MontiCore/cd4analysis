package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Matches classes / interfaces by structure. */
public class MatchCDTypeByStructure implements MatchingStrategy<ASTCDType> {

  private final ASTCDCompilationUnit tgtCD;
  public double threshold = 0.5;

  public MatchCDTypeByStructure(ASTCDCompilationUnit tgtCD) {
    this.tgtCD = tgtCD;
  }

  public MatchCDTypeByStructure(ASTCDCompilationUnit tgtCD, double threshold) {
    this.tgtCD = tgtCD;
    this.threshold = threshold;
  }

  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  public double getThreshold() {
    return threshold;
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
    return result;
  }

  /** CDTypes are matched if there is a sufficient number of matching attributes. */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return new CDTypeSimilarity().computeWeight(srcElem, tgtElem) >= threshold;
  }
}
