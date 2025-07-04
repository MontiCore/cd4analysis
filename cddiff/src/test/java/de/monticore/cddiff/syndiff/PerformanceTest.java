/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syndiff;

import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

public class PerformanceTest extends SynDiffTestBasis {

  @BeforeAll
  public static void init() {
    dir = "src/test/resources/de/monticore/cddiff/syndiff/Performance/";
  }

  public static Stream<Arguments> performanceData() {
    return Stream.of(Arguments.of("5/CD1.cd", "5/CD2.cd", 1), Arguments.of("10/CD1.cd", "10/CD2.cd",
        1), Arguments.of("15/CD1.cd", "15/CD2.cd", 1), Arguments.of("20/CD1.cd", "20/CD2.cd", 1),
        Arguments.of("25/CD1.cd", "25/CD2.cd", 1), Arguments.of("100/CD1.cd", "100/CD2.cd", 2),
        Arguments.of("900/CD1.cd", "900/CD2.cd", 120));
  }

  @ParameterizedTest
  @MethodSource("performanceData")
  public void testPerformance(String srcPath, String tgtPath, int timeoutSec) {
    parseModels(srcPath, tgtPath);

    Duration timeout = Duration.ofSeconds(timeoutSec);
    Assertions.assertTimeout(timeout, () -> new Syn2SemDiff(src, tgt));

  }

}
