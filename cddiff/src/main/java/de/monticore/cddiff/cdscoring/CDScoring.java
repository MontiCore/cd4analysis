package de.monticore.cddiff.cdscoring;

import com.google.common.math.DoubleMath;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.CDSynDiffMatches;
import de.monticore.cdmatcher.caching.CachedMatch;
import de.monticore.cdmatcher.caching.CachedMatches;
import org.antlr.v4.runtime.misc.Pair;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CDScoring {

  private final ASTCDCompilationUnit srcCD;
  private final ASTCDCompilationUnit tgtCD;
  // score matches that are close to the threshold ([0,9 * threshold, 1.11 * threshold]), only used with greedy matching
  private CachedMatch<ASTCDType> closeTypeMatches = new CachedMatch<>();

  CDScoring(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
  }

  /**
   * scores the two CDs using the given parameters, embedding and a greedy matching approach.
   * This is equivalent to calling {@link #score(int, double, boolean, boolean)} with useEmbedding set to true and useOptimalMatching set to false.
   */
  public double score(int iterations, double threshold) {
    return score(iterations, threshold, true, false);
  }

  /**
   * scores the two CDs using the given parameters and a greedy matching approach.
   * This is equivalent to calling {@link #score(int, double, boolean, boolean)} with useOptimalMatching set to false.
   */
  public double score(int iterations, double threshold, boolean useEmbedding) {
    return score(iterations, threshold, useEmbedding, false);
  }

  /**
   * Computes the score for the given source and target CD.
   * The score is based on the matches found between the two CDs.
   *
   * @param iterations   maximum number of iterations for the matching algorithm, if matches converge before that, the algorithm stops
   * @param threshold    threshold for considering a match significant.
   *                    If @link useOptimalMatching is false, matches close to the threshold will be saved and can be retrieved via {@link #getCloseTypeMatches()}.
   *                    A threshold is greater than 0.9, means all matches will be considered close in this case.
   * @param useEmbedding whether to use embedding in the matching process, make sure to initialize the embedding before if useEmbedding is true via {@link de.monticore.cdmatcher.similarity.CDEmbeddingSimilarity#initialize(String)}
   *                     or look at the readme for more information
   * @param useOptimalMatching whether to use the Kuhn-Munkres algorithm to find the optimal matching instead of a greedy approach
   *                           The greedy approach saves close matches that can be used for further analysis.
   *                           The Kuhn-Munkres algorithm does not provide close matches but finds the optimal matching.
   * @return the computed score
   */
  public double score(int iterations, double threshold, boolean useEmbedding, boolean useOptimalMatching) {
    CDSynDiffMatches srcToTgtMatcher = new CDSynDiffMatches(srcCD, tgtCD, iterations, threshold, useEmbedding);
    CDSynDiffMatches tgtToSrcMatcher = new CDSynDiffMatches(tgtCD, srcCD, iterations, threshold, useEmbedding);

    CachedMatches srcToTgtScore = srcToTgtMatcher.getScoredMatches();
    CachedMatches tgtToSrcScore = tgtToSrcMatcher.getScoredMatches();

    CachedMatch<ASTCDType> combinedTypeMatches;
    Set<ASTCDType> srcTypes = CDDiffUtil.getAllTypesFromCD(srcCD);
    Set<ASTCDType> tgtTypes = CDDiffUtil.getAllTypesFromCD(tgtCD);

    if (useOptimalMatching) {
      combinedTypeMatches = filterMatchesKuhnMunkres(
        srcToTgtScore.getTypeMatches(),
        tgtToSrcScore.getTypeMatches(),
        threshold,
        new HashSet<>(srcTypes),
        new HashSet<>(tgtTypes),
        ASTCDClass::new
      );
    } else {
      Pair<CachedMatch<ASTCDType>, CachedMatch<ASTCDType>> combinedTypeMatchesResult = filterAndCombineMatches(
        srcToTgtScore.getTypeMatches(), tgtToSrcScore.getTypeMatches(), threshold
      );
      combinedTypeMatches = combinedTypeMatchesResult.a;
      closeTypeMatches = combinedTypeMatchesResult.b;
    }

    // Calculate the scores for all types in the source CD, if a type is not matched, it will be the default value 0.0
    // If two different types are matched to the same type, the average will be smaller because both matches were scaled down
    List<Double> typeScores = srcTypes.stream().map(
        combinedTypeMatches::getMatchesForSource
      ).map(Map::values).map(list -> list.isEmpty() ? 0.0 : DoubleMath.mean(list))
      .collect(Collectors.toList());

    typeScores.addAll(tgtTypes.stream().map(
      combinedTypeMatches::getMatchesForTarget
    ).map(Map::values).map(list -> list.isEmpty() ? 0.0 : DoubleMath.mean(list)).collect(Collectors.toList()));

    return DoubleMath.mean(typeScores);
  }

  /**
   * Filters the matches based on a threshold and combines source to target and target to source matches into a single CachedMatch.
   *
   * @param srcToTgt  the matches from source to target
   * @param tgtToSrc  the matches from target to source
   * @param threshold the threshold to filter matches by should be in the range [0, 1], for values greater than 0.9 close matches will include all matches
   * @param <T>       the type of the elements in the matches
   * @return a pair of CachedMatches, the first one contains the filtered matches that are above the threshold, the second one contains the close matches
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
   * Filters the matches using the Kuhn-Munkres algorithm to find the optimal matching above a given threshold
   * This method does not return close matches as they cannot be determined with the Kuhn-Munkres algorithm the same way as with greedy matching.
   * For a matching that does return close matches but is not optimal, use {@link #filterAndCombineMatches(CachedMatch, CachedMatch, double)}
   *
   * @param srcToTgt    the matches from source to target
   * @param tgtToSrc    the matches from target to source
   * @param threshold   the threshold to filter matches by should be in the range [0, 1]
   * @param srcElements all elements of type T in the source CD
   * @param tgtElements all elements of type T in the target CD
   * @param <T>         the type of the elements in the matches
   * @return a CachedMatch containing the filtered matches
   */
  private <T> CachedMatch<T> filterMatchesKuhnMunkres(CachedMatch<T> srcToTgt, CachedMatch<T> tgtToSrc, double threshold, Set<T> srcElements, Set<T> tgtElements, Supplier<T> paddingSupplier) {
    CachedMatch<T> filteredSrcToTgt = filterMatchesKuhnMunkres(srcToTgt, threshold, srcElements, tgtElements, paddingSupplier);
    CachedMatch<T> filteredTgtToSrc = filterMatchesKuhnMunkres(tgtToSrc, threshold, tgtElements, srcElements, paddingSupplier);

    //scale down to disfavor matches only in one direction
    filteredSrcToTgt.getMatches().replaceAll((k, v) -> v * 0.5);
    filteredTgtToSrc.getMatches().replaceAll((k, v) -> v * 0.5);

    return CachedMatch.merge(List.of(filteredSrcToTgt, flipMatches(filteredTgtToSrc)), Double::sum);

  }

  /**
   * Filters the matches based on a threshold.
   *
   * @param matches   the matches to filter
   * @param threshold the threshold to filter matches by should be in the range [0, 1], for values greater than 0.9 close matches will include all matches
   * @param <T>       the type of the elements in the matches
   * @return a pair of Cached Matches, the first one contains the filtered matches that are above the threshold, the second one contains the close matches that are in the range [0.9 * threshold, 1.11 * threshold]
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
        if (entry.getValue() > threshold) {
          filteredMatches.putMatch(entry.getKey().a, entry.getKey().b, entry.getValue());
        }
        if (entry.getValue() < threshold * 1.11) {
          closeMatches.putMatch(entry.getKey().a, entry.getKey().b, entry.getValue());
        }
        matchedMapKeys.add(entry.getKey().a);
        matchedMapValues.add(entry.getKey().b);
      }
    }
    return new Pair<>(filteredMatches, closeMatches);
  }

  /**
   * Filters the matches using the Kuhn-Munkres algorithm to find the optimal matching above a given threshold.
   *
   * @param matches         the matches to filter
   * @param threshold       the threshold to filter matches by
   * @param firstPartition  the source partition of the bipartite graph
   * @param secondPartition the target partition of the bipartite graph
   * @param <T>             the type of the elements in the matches
   * @return a CachedMatch containing the filtered matches
   */
  private <T> CachedMatch<T> filterMatchesKuhnMunkres(CachedMatch<T> matches, double threshold, Set<T> firstPartition, Set<T> secondPartition, Supplier<T> paddingSupplier) {
    Graph<T, DefaultWeightedEdge> matchingGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

    List<T> paddingClasses = IntStream.range(0,
        Math.abs(firstPartition.size() - secondPartition.size()))
      .mapToObj(i -> paddingSupplier.get())
      .collect(Collectors.toList());

    if(firstPartition.size() < secondPartition.size()) {
      firstPartition.addAll(paddingClasses);
    } else { // if size is equal paddingClasses is empty and nothing is added
      secondPartition.addAll(paddingClasses);
    }

    firstPartition.forEach(matchingGraph::addVertex);
    secondPartition.forEach(matchingGraph::addVertex);

    for (T src : firstPartition) {
      for (T tgt : secondPartition) {
        DefaultWeightedEdge edge = matchingGraph.addEdge(src, tgt);
        Double matchScore = matches.getMatch(src, tgt);
        if (matchScore == null || matchScore < threshold) {
          matchingGraph.setEdgeWeight(edge, 2000000.0); // large weight to avoid matching
        } else {
          matchingGraph.setEdgeWeight(edge, invertSimilarity(matchScore));
        }
      }
    }

    MatchingAlgorithm.Matching<T, DefaultWeightedEdge> matching =
      new KuhnMunkresMinimalWeightBipartitePerfectMatching<>(matchingGraph, firstPartition, secondPartition).getMatching();

    CachedMatch<T> filteredMatches = new CachedMatch<>();
    for (DefaultWeightedEdge edge : matching.getEdges()) {
      T src = matchingGraph.getEdgeSource(edge);
      T tgt = matchingGraph.getEdgeTarget(edge);
      double weight = matchingGraph.getEdgeWeight(edge);
      if (weight <= 1.0) {
        filteredMatches.putMatch(src, tgt, invertSimilarity(weight));
      }
    }
    return filteredMatches;
  }

  private <T> CachedMatch<T> flipMatches(CachedMatch<T> matches) {
    CachedMatch<T> flippedMatches = new CachedMatch<>();
    for (Map.Entry<Pair<T, T>, Double> entry : matches.getMatches().entrySet()) {
      flippedMatches.putMatch(entry.getKey().b, entry.getKey().a, entry.getValue());
    }
    return flippedMatches;
  }

  private double invertSimilarity(double value) {
    return 1.0 - value;
  }


  /**
   * Returns the cached matches that are close to the threshold.
   * Close matches are defined as those with a score in the range of [0.9 * threshold, 1.11 * threshold].
   * These matches can be used for further analysis or debugging.
   * This is only meaningful if the score method was called before, otherwise this will return an empty CachedMatches.
   * Close matches are also not available if the score method was called with useOptimalMatching set to true.
   * @return CachedMatches containing matches close to the threshold
   */
  public CachedMatch<ASTCDType> getCloseTypeMatches() {
    return closeTypeMatches;
  }

}
