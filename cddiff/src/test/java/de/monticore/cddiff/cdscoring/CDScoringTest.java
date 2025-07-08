package de.monticore.cddiff.cdscoring;

import de.monticore.cddiff.syndiff.SynDiffTestBasis;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CDScoringTest extends SynDiffTestBasis {

  @BeforeAll
  public static void init() {
    dir = "src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/";
  }

  @Test
  public void testCDScoring() {
    parseModels("Source1.cd", "TechStoreV10.cd");

    CDScoring cdScoring = new CDScoring(src, tgt);

    System.out.println("Score: " + cdScoring.score(5, 0.5, true));
  }

}
