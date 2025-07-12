package de.monticore.cddiff.cdscoring;

import de.monticore.cddiff.syndiff.SynDiffTestBasis;
import de.monticore.cdmatcher.similarity.CDEmbeddingSimilarity;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
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
    System.out.println("Iterations: " + iterations + ", Threshold: " + threshold + ", Use Embedding: " + useEmbedding);
    System.out.println("Score for " + srcFile + " and " + tgtFile + ": " + score);
    System.out.println("----------------------------------------");
  }

}
