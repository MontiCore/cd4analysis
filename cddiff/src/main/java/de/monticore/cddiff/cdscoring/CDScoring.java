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
import de.monticore.cdmatcher.similarity.CDEmbeddingSimilarity;
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

  CDScoring(ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    CDEmbeddingSimilarity.initialize("src/main/resources/crawl-300d-2M-subword.bin");
  }

  public double score(int iterations, double threshold, boolean useEmbedding) {
    CDSynDiffMatches srcToTgtMatcher = new CDSynDiffMatches(srcCD, tgtCD, iterations, threshold, useEmbedding);
    CDSynDiffMatches tgtToSrcMatcher = new CDSynDiffMatches(tgtCD, srcCD, iterations, threshold, useEmbedding);

    CachedMatches srcToTgtScore = srcToTgtMatcher.getScoredMatches();
    CachedMatches tgtToSrcScore = tgtToSrcMatcher.getScoredMatches();

    CachedMatch<ASTCDType> combinedTypeMatches = filterAndCombineMatches(
      srcToTgtScore.getTypeMatches(), tgtToSrcScore.getTypeMatches(), threshold
    );
    CachedMatch<ASTCDAssociation> combinedAssocMatches = filterAndCombineMatches(
      srcToTgtScore.getAssocMatches(), tgtToSrcScore.getAssocMatches(), threshold
    );
    CachedMatch<ASTCDAttribute> combinedAttributeMatches = filterAndCombineMatches(
      srcToTgtScore.getAttributeMatches(), tgtToSrcScore.getAttributeMatches(), threshold
    );

    // Calculate the scores for all types in the source CD, if a type is not matched, it will be the default value 0.0
    // If two different types are matched to the same type, the average will be lower because both matches were scaled down
    List<Double> typeScores = CDDiffUtil.getAllTypesFromCD(srcCD).stream().map(
      combinedTypeMatches::getMatchesForSource
    ).map(Map::values).map(list -> list.isEmpty() ? 0.0 : DoubleMath.mean(list))
      .collect(Collectors.toList());

    typeScores.addAll(CDDiffUtil.getAllTypesFromCD(tgtCD).stream().map(
      combinedTypeMatches::getMatchesForTarget
    ).map(Map::values).map(DoubleMath::mean).collect(Collectors.toList()));

    double typeScore = DoubleMath.mean(typeScores);

    return typeScore;
  }

  private <T> CachedMatch<T> filterAndCombineMatches(CachedMatch<T> srcToTgt, CachedMatch<T> tgtToSrc, double threshold) {
    CachedMatch<T> filteredSrcToTgt = filterMatches(srcToTgt, threshold);
    CachedMatch<T> filteredTgtToSrc = filterMatches(tgtToSrc, threshold);

    //scale down to disfavor matches only in one direction
    filteredSrcToTgt.getMatches().replaceAll((k, v) -> v * 0.5);
    filteredTgtToSrc.getMatches().replaceAll((k, v) -> v * 0.5);


    return CachedMatch.merge(List.of(filteredSrcToTgt, flipMatches(filteredTgtToSrc)), Double::sum);
  }

  private <T> CachedMatch<T> filterMatches(CachedMatch<T> matches, double threshold) {
    CachedMatch<T> filteredMatches = new CachedMatch<>();
    List<Map.Entry<Pair<T, T>, Double>> matchScores = matches.getMatches().entrySet().stream().sorted(
      Map.Entry.comparingByValue(Comparator.reverseOrder())
    ).collect(Collectors.toList());

    Set<T> matchedMapKeys = new HashSet<>();
    Set<T> matchedMapValues = new HashSet<>();

    for (Map.Entry<Pair<T, T>, Double> entry : matchScores) {
      if (entry.getValue() < threshold) {
        break;
      }
      if (!matchedMapKeys.contains(entry.getKey().a) && !matchedMapValues.contains(entry.getKey().b)) {
        filteredMatches.putMatch(entry.getKey().a, entry.getKey().b, entry.getValue());
        matchedMapKeys.add(entry.getKey().a);
        matchedMapValues.add(entry.getKey().b);
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
}
