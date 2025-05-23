package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.cdmatcher.similarity.CDTypeSimilarity;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A special type matching strategy that matches the reference type if any strict subtype of a
 * concrete type is an incarnation of the reference type.
 */
public class MatchCDTypesToSubTypes extends MatchCDTypeInHierarchy {

  public MatchCDTypesToSubTypes(
      MatchingStrategy<ASTCDType> typeMatcher,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    super(typeMatcher, srcCD, tgtCD);
  }

  /**
   * A boolean method which checks if a subtype of srcElem matches to tgtCD
   *
   * @param tgtElem element from tgtCD
   * @return true if a subtype of srcElem matches to tgtCD
   */
  @Override
  public boolean isMatched(ASTCDType srcElem, ASTCDType tgtElem) {
    return CDDiffUtil.getAllStrictSubTypes(srcElem, srcCD.getCDDefinition()).stream()
        .anyMatch(srcSuper -> typeMatcher.isMatched(srcSuper, tgtElem));
  }

  public double getScore(ASTCDType srcElem, ASTCDType tgtElem) {
    Set<ASTCDType> srcStrictSubTypes =
        CDDiffUtil.getAllStrictSubTypes(srcElem, srcCD.getCDDefinition());
    Set<ASTCDType> tgtStrictSubTypes =
        CDDiffUtil.getAllStrictSubTypes(tgtElem, tgtCD.getCDDefinition());
    CDTypeSimilarity similarity = new CDTypeSimilarity();

    return srcStrictSubTypes.stream()
      .flatMap(srcSuper -> tgtStrictSubTypes.stream().map(tgtSuper -> new Pair<>(srcSuper, tgtSuper)))
      .map(entry -> new Triple<>(entry.a, entry.b, similarity.computeWeight(entry.a, entry.b)))
      .collect(Collectors.toMap(
        t -> t.a,
        t -> t.c,
        Double::max
      )).values().stream().collect(Collectors.averagingDouble(Double::doubleValue));
  }
}
