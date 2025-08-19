package de.monticore.cddiff.cdscoring;

import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syndiff.SynDiffTestBasis;
import de.monticore.cdmatcher.caching.CachedMatch;
import de.monticore.cdmatcher.similarity.CDEmbeddingSimilarity;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CDScoringTest extends SynDiffTestBasis {

  @BeforeAll
  public static void init() {
    dir = "src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/";
    CDEmbeddingSimilarity.initialize("src/main/resources/crawl-300d-2M-subword.bin");
  }

  @Test
  public void testCDScoring() {
    parseModels("MaCoCo_v1.cd", "MaCoCo_v2.cd");
    CDEmbeddingSimilarity.initialize("src/main/resources/crawl-300d-2M-subword.bin");

    CDScoring cdScoring = new CDScoring(src, tgt);

    System.out.println("Score: " + cdScoring.score(5, 0.5, true));
  }

  @Test
  public void testCDScoringTechStore() {
    List<String> fileNames = IntStream.range(1, 13).mapToObj(i -> "TechStoreV" + i + ".cd").collect(Collectors.toList());

    fileNames.stream().flatMap(f1 -> fileNames.stream().map(f2 -> new Pair<>(f1, f2))).forEach(pair -> {
      score(pair.a, pair.b, 5, 0.5, false);
      score(pair.a, pair.b, 5, 0.5, true);
    });
  }


  private void score(String srcFile, String tgtFile, int iterations, double threshold, boolean useEmbedding) {
    try {
      parseModels(srcFile, tgtFile);
      System.out.println("Syntax correct for " + srcFile + " and " + tgtFile);
    } catch (Exception e) {
      System.out.println("Syntax not correct for " + srcFile + " and " + tgtFile + ": " + e.getMessage());
      return;
    }

    CDScoring cdScoring = new CDScoring(src, tgt);
    double score = cdScoring.score(iterations, threshold, useEmbedding);
    CachedMatch<ASTCDType> closeMatches = cdScoring.getCloseTypeMatches();
    System.out.println("Iterations: " + iterations + ", Threshold: " + threshold + ", Use Embedding: " + useEmbedding);
    System.out.println("Score for " + srcFile + " and " + tgtFile + ": " + String.format("%.4f", score));
    if (!closeMatches.getMatches().isEmpty()) {
      System.out.println(printCloseMatches(closeMatches, threshold));
    }
    System.out.println("----------------------------------------");
  }

  private static String printCloseMatches(CachedMatch<ASTCDType> closeMatches, double threshold) {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<Pair<ASTCDType, ASTCDType>, Double> entry : closeMatches.getMatches().entrySet()) {
      Pair<ASTCDType, ASTCDType> pair = entry.getKey();
      double score = entry.getValue();
      builder.append("Close match: ")
        .append(pair.a.getName())
        .append(" <-> ")
        .append(pair.b.getName())
        .append(" (score: ")
        .append(String.format("%.4f", score));
      if (score >= threshold) {
        builder.append(" - match included)");
      } else {
        builder.append(" - match excluded)");
      }
      builder.append("\n");
    }
    return builder.toString();
  }

}
