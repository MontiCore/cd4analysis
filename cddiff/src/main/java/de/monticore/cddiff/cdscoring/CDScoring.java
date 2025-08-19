package de.monticore.cddiff.cdscoring;

import com.google.common.math.DoubleMath;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.CDSynDiffMatches;
import de.monticore.cdmatcher.caching.CachedMatch;
import de.monticore.cdmatcher.caching.CachedMatches;
import org.antlr.v4.runtime.misc.Pair;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CDScoring {

  private final ASTCDCompilationUnit srcCD;
  private final ASTCDCompilationUnit tgtCD;
  // score matches that are close to the threshold ([0,9 * threshold, 1.11 * threshold])
  private CachedMatch<ASTCDType> closeTypeMatches = new CachedMatch<>();
  private CachedMatch<ASTCDAssociation> closeAssocMatches = new CachedMatch<>();
  private CachedMatch<ASTCDAttribute> closeAttributeMatches = new CachedMatch<>();

  CDScoring(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
  }

  /**
   * Computes the score for the given source and target CD.
   * The score is based on the matches found between the two CDs.
   *
   * @param iterations  maximum number of iterations for the matching algorithm, if matches converge before that, the algorithm stops
   * @param threshold   threshold for considering a match significant, matches close to the threshold will be saved and can be retrieved via {@link #getCloseTypeMatches()}. If the threshold is greater than 0.9, all matches will be considered close.
   * @param useEmbedding whether to use embedding in the matching process, make sure to initialize the embedding before if useEmbedding is true via {@link de.monticore.cdmatcher.similarity.CDEmbeddingSimilarity#initialize(String)}
   *                     or look at the readme for more information
   * @return the computed score
   */
  //TODO: explain embedding usage in the readme
  public double score(int iterations, double threshold, boolean useEmbedding) {
    CDSynDiffMatches srcToTgtMatcher = new CDSynDiffMatches(srcCD, tgtCD, iterations, threshold, useEmbedding);
    CDSynDiffMatches tgtToSrcMatcher = new CDSynDiffMatches(tgtCD, srcCD, iterations, threshold, useEmbedding);

    CachedMatches srcToTgtScore = srcToTgtMatcher.getScoredMatches();
    CachedMatches tgtToSrcScore = tgtToSrcMatcher.getScoredMatches();

    Pair<CachedMatch<ASTCDType>, CachedMatch<ASTCDType>> combinedTypeMatchesResult = filterAndCombineMatches(
      srcToTgtScore.getTypeMatches(), tgtToSrcScore.getTypeMatches(), threshold
    );
    CachedMatch<ASTCDType> combinedTypeMatches = combinedTypeMatchesResult.a;
    closeTypeMatches = combinedTypeMatchesResult.b;

    Pair<CachedMatch<ASTCDAssociation>, CachedMatch<ASTCDAssociation>> combinedAssocMatchesResult = filterAndCombineMatches(
      srcToTgtScore.getAssocMatches(), tgtToSrcScore.getAssocMatches(), threshold
    );
    CachedMatch<ASTCDAssociation> combinedAssocMatches = combinedAssocMatchesResult.a;
    closeAssocMatches = combinedAssocMatchesResult.b;

    Pair<CachedMatch<ASTCDAttribute>, CachedMatch<ASTCDAttribute>> combinedAttributeMatchesResult = filterAndCombineMatches(
      srcToTgtScore.getAttributeMatches(), tgtToSrcScore.getAttributeMatches(), threshold
    );
    CachedMatch<ASTCDAttribute> combinedAttributeMatches = combinedAttributeMatchesResult.a;
    closeAttributeMatches = combinedAttributeMatchesResult.b;

    // Calculate the scores for all types in the source CD, if a type is not matched, it will be the default value 0.0
    // If two different types are matched to the same type, the average will be lower because both matches were scaled down
    List<Double> typeScores = CDDiffUtil.getAllTypesFromCD(srcCD).stream().map(
      combinedTypeMatches::getMatchesForSource
    ).map(Map::values).map(list -> list.isEmpty() ? 0.0 : DoubleMath.mean(list))
      .collect(Collectors.toList());

    typeScores.addAll(CDDiffUtil.getAllTypesFromCD(tgtCD).stream().map(
      combinedTypeMatches::getMatchesForTarget
    ).map(Map::values).map(list -> list.isEmpty() ? 0.0 : DoubleMath.mean(list)).collect(Collectors.toList()));

    double typeScore = DoubleMath.mean(typeScores);

    return typeScore;
  }

  /**
   * Filters the matches based on a threshold and combines source to target and target to source matches into a single CachedMatch.
   * @param srcToTgt the matches from source to target
   * @param tgtToSrc the matches from target to source
   * @param threshold the threshold to filter matches by should be in the range [0, 1], for values greater than 0.9 close matches will include all matches
   * @return a pair of CachedMatches, the first one contains the filtered matches that are above the threshold, the second one contains the close matches
   * @param <T> the type of the elements in the matches
   */
  private <T> Pair<CachedMatch<T>, CachedMatch<T>> filterAndCombineMatches(CachedMatch<T> srcToTgt, CachedMatch<T> tgtToSrc, double threshold) {
    Pair<CachedMatch<T>, CachedMatch<T>> filteredSrcToTgt = filterMatches(srcToTgt, threshold);
    Pair<CachedMatch<T>, CachedMatch<T>> filteredTgtToSrc = filterMatches(tgtToSrc, threshold);
    CachedMatch<T> closeMatches = de.monticore.cdmatcher.caching.CachedMatch.merge(
      List.of(filteredSrcToTgt.b, filteredTgtToSrc.b), Double::min
    );

    //scale down to disfavor matches only in one direction
    filteredSrcToTgt.a.getMatches().replaceAll((k, v) -> v * 0.5);
    filteredTgtToSrc.a.getMatches().replaceAll((k, v) -> v * 0.5);


    CachedMatch<T> combinedMatches = CachedMatch.merge(List.of(filteredSrcToTgt.a, flipMatches(filteredTgtToSrc.a)), Double::sum);
    return new Pair<>(combinedMatches, closeMatches);
  }

  /**
   * Filters the matches based on a threshold.
   * @param matches the matches to filter
   * @param threshold the threshold to filter matches by should be in the range [0, 1], for values greater than 0.9 close matches will include all matches
   * @return a pair of Cached Matches, the first one contains the filtered matches that are above the threshold, the second one contains the close matches that are in the range [0.9 * threshold, 1.11 * threshold]
   * @param <T> the type of the elements in the matches
   */
  private <T> Pair<CachedMatch<T>, CachedMatch<T>> filterMatches(CachedMatch<T> matches, double threshold) {
    CachedMatch<T> filteredMatches = new CachedMatch<>();
    CachedMatch<T> closeMatches = new CachedMatch<>();
    List<Map.Entry<Pair<T, T>, Double>> matchScores = matches.getMatches().entrySet().stream().sorted(
      Map.Entry.comparingByValue(Comparator.reverseOrder())
    ).collect(Collectors.toList());

    Set<T> matchedMapKeys = new HashSet<>();
    Set<T> matchedMapValues = new HashSet<>();

    for (Map.Entry<Pair<T, T>, Double> entry : matchScores) {
      if (entry.getValue() < (threshold * 0.9)) {
        break;
      }
      if (!matchedMapKeys.contains(entry.getKey().a) && !matchedMapValues.contains(entry.getKey().b)) {
        if( entry.getValue() > threshold) {
          filteredMatches.putMatch(entry.getKey().a, entry.getKey().b, entry.getValue());
        }
        if( entry.getValue() < threshold * 1.11) {
          closeMatches.putMatch(entry.getKey().a, entry.getKey().b, entry.getValue());
        }
        matchedMapKeys.add(entry.getKey().a);
        matchedMapValues.add(entry.getKey().b);
      }
    }
    return new Pair<>(filteredMatches, closeMatches);
  }

  private <T> CachedMatch<T> flipMatches(CachedMatch<T> matches) {
    CachedMatch<T> flippedMatches = new CachedMatch<>();
    for (Map.Entry<Pair<T, T>, Double> entry : matches.getMatches().entrySet()) {
      flippedMatches.putMatch(entry.getKey().b, entry.getKey().a, entry.getValue());
    }
    return flippedMatches;
  }

  /**
   * Returns the cached matches that are close to the threshold.
   * Close matches are defined as those with a score in the range of [0.9 * threshold, 1.11 * threshold].
   * These matches can be used for further analysis or debugging.
   * This is only meaningful if the score method was called before, otherwise this will return an empty CachedMatches.
   *
   * @return CachedMatches containing matches close to the threshold
   */
  public CachedMatch<ASTCDType> getCloseTypeMatches() {
    return closeTypeMatches;
  }

  /**
   * Returns the cached matches that are close to the threshold.
   * Close matches are defined as those with a score in the range of [0.9 * threshold, 1.11 * threshold].
   * These matches can be used for further analysis or debugging.
   * This is only meaningful if the score method was called before, otherwise this will return an empty CachedMatches.
   *
   * @return CachedMatches containing matches close to the threshold
   */
  public CachedMatch<ASTCDAssociation> getCloseAssocMatches() {
    return closeAssocMatches;
  }

  /**
   * Returns the cached matches that are close to the threshold.
   * Close matches are defined as those with a score in the range of [0.9 * threshold, 1.11 * threshold].
   * These matches can be used for further analysis or debugging.
   * This is only meaningful if the score method was called before, otherwise this will return an empty CachedMatches.
   *
   * @return CachedMatches containing matches close to the threshold
   */
  public CachedMatch<ASTCDAttribute> getCloseAttributeMatches() {
    return closeAttributeMatches;
  }
}
