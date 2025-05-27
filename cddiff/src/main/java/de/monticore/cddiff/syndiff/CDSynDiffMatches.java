package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdmatcher.matching.CachedMatch;
import de.monticore.cdmatcher.matching.MatchingStrategy;
import de.monticore.cdmatcher.matching.cdtype.*;
import org.antlr.v4.runtime.misc.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class should be used to construct a matching of respectively types and associations between
 * the srcCD and the tgtCD for the SynDiff and Syn2SemDiff. Recomputing of matches should be
 * avoided.
 */
public class CDSynDiffMatches {
  protected Map<ASTCDType, ASTCDType> typeMatches;
  protected Map<ASTCDAssociation, ASTCDAssociation> assocMatches;
  protected Map<ASTCDAttribute, ASTCDAttribute> attributeMatches;

  /**
   * The constructor call computes all matches of types and associations between srcCD and tgtCD.
   *
   * @param matchStructure determines whether structural similarities are used to determine type
   *     matches
   */
  public CDSynDiffMatches(
      ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, boolean matchStructure, int matchingIterations, double threshold) {

    de.monticore.cdmatcher.matching.CachedMatches.clear();

    // Compute types of srcCD and tgtCD without using the traverser
    Set<ASTCDType> srcTypes = CDDiffUtil.getAllTypesFromCD(srcCD);
    Set<ASTCDType> tgtTypes = CDDiffUtil.getAllTypesFromCD(tgtCD);

    Set<MatchingStrategy<ASTCDType>> matchingStrategies = new HashSet<>((Set.of(
      new MatchCDTypeByName(),
      new MatchCDTypeByAssocs(),
      new MatchCDTypeByAttribute()
    )));
    if(matchStructure) {
      new MatchCDTypeByDirectSuperClasses();
    }

    MatchCDType matcher = new MatchCDType(matchingStrategies);

    for(int i = 0; i < matchingIterations; i++) {
      for (ASTCDType srcType : srcTypes) {
        for (ASTCDType tgtType : tgtTypes) {
          matcher.getScore(srcType, tgtType);
        }
      }
    }

    // compute a matching of types by name
    typeMatches = computeMatching(de.monticore.cdmatcher.matching.CachedMatches.getTypeMatches(), threshold);
    assocMatches = computeMatching(de.monticore.cdmatcher.matching.CachedMatches.getAssocMatches(), threshold);
    attributeMatches = computeMatching(de.monticore.cdmatcher.matching.CachedMatches.getAttributeMatches(), threshold);
  }

  /** computes a matching based on a MatchingStrategy */
  public static <T> Map<T, T> computeMatching(CachedMatch<T> matches, double threshold) {
    Map<T, T> matching = new LinkedHashMap<>();
    List<Map.Entry<Pair<T, T>, Double>> matchScores = matches.getMatches().entrySet().stream().sorted(
      Map.Entry.comparingByValue(Comparator.reverseOrder())
    ).collect(Collectors.toList());
    Iterator<Map.Entry<Pair<T, T>, Double>> iterator = matchScores.iterator();

    while (iterator.hasNext()) {
      Map.Entry<Pair<T, T>, Double> entry = iterator.next();
      if(entry.getValue() < threshold) {
        break;
      }
      Pair<T, T> pair = entry.getKey();
      if (!matching.containsKey(pair.a)) {
        matching.put(pair.a, pair.b);
      } else {
        iterator.remove();
      }
    }
    return matching;
  }

  public Map<ASTCDType, ASTCDType> getTypeMatches() {
    return typeMatches;
  }

  public Map<ASTCDAssociation, ASTCDAssociation> getAssocMatches() {
    return assocMatches;
  }

  public Map<ASTCDAttribute, ASTCDAttribute> getAttributeMatches() {
    return attributeMatches;
  }
}
