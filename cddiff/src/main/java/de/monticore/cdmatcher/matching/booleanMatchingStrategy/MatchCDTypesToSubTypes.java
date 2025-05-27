package de.monticore.cdmatcher.matching.booleanMatchingStrategy;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A special type matching strategy that matches the reference type if any strict subtype of a
 * concrete type is an incarnation of the reference type.
 */
public class MatchCDTypesToSubTypes implements ExternalCandidatesMatchingStrategy<ASTCDType> {
  private final BooleanMatchingStrategy<ASTCDType> strategy;
  private final ASTCDCompilationUnit srcCD;
  private final ASTCDCompilationUnit tgtCD;

  public MatchCDTypesToSubTypes(
      BooleanMatchingStrategy<ASTCDType> strategy,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    this.strategy = strategy;
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
  }

  public Set<ASTCDType> getMatchedElements(ASTCDType srcElem) {
    Set<ASTCDType> candidateTypes = new HashSet<>(tgtCD.getCDDefinition().getCDClassesList());
    candidateTypes.addAll(tgtCD.getCDDefinition().getCDInterfacesList());
    candidateTypes.addAll(tgtCD.getCDDefinition().getCDEnumsList());

    return candidateTypes.stream()
      .filter((tgtElem) -> isMatched(srcElem, tgtElem))
      .collect(Collectors.toSet());
  }

  /**
   * A boolean method which checks if a subtype of srcElem matches to tgtCD
   *
   * @param tgtElem element from tgtCD
   * @return true if a subtype of srcElem matches to tgtCD
   */
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return CDDiffUtil.getAllStrictSubTypes(srcElem, srcCD.getCDDefinition()).stream()
        .anyMatch(srcSuper -> strategy.isMatched(srcSuper, tgtElem));
  }

}
