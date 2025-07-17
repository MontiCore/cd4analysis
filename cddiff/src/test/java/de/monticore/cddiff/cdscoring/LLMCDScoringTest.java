package de.monticore.cddiff.cdscoring;

import de.monticore.cddiff.syndiff.SynDiffTestBasis;
import de.monticore.cdmatcher.similarity.CDEmbeddingSimilarity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LLMCDScoringTest extends SynDiffTestBasis {

  @BeforeAll
  public static void init() {
    dir = "src/test/resources/de/monticore/cddiff/CDScoring/";
    CDEmbeddingSimilarity.initialize("src/main/resources/crawl-300d-2M-subword.bin");
  }

  @Test
  public void testKineopolis() {
    parseModels("KineopolisLLM.cd", "Kineopolis.cd");

    CDScoring llmCDScoring = new CDScoring(src, tgt);

    System.out.println("Score: " + llmCDScoring.score(5, 0.5, true));
  }

  @Test
  public void testEBike() {
    parseModels("EBikeLLM.cd", "EBike.cd");

    CDScoring llmCDScoring = new CDScoring(src, tgt);

    System.out.println("Score: " + llmCDScoring.score(5, 0.5, true));
  }

  @Test
  public void testBuildingManagement() {
    parseModels("BuildingManagementLLM.cd", "BuildingManagement.cd");

    CDScoring llmCDScoring = new CDScoring(src, tgt);

    System.out.println("Score: " + llmCDScoring.score(5, 0.5, true));
  }
}
