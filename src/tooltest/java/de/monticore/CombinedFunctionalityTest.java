package de.monticore;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiff;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cdmerge.CDMerge;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CombinedFunctionalityTest {

  @BeforeEach
  public void init() {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  @Test
  public void testMaCoCo() {
    String base_path =
        "src/test/resources/de/monticore/cd4analysis/examples/industrial_strength_models/";
    try {
      CD4CodeParser parser = CD4CodeMill.parser();

      Set<ASTCDCompilationUnit> mergeSet =
          Arrays.stream(new File(base_path + "MaCoCoMerge/").listFiles())
              .map(
                  file -> {
                    try {
                      return parser.parse(file.getAbsolutePath());
                    } catch (IOException e) {
                      fail();
                      return Optional.<ASTCDCompilationUnit>empty();
                    }
                  })
              .peek(optional -> assertTrue(optional.isPresent()))
              .map(Optional::get)
              .collect(Collectors.toUnmodifiableSet());

      ASTCDCompilationUnit merged = CDMerge.merge(mergeSet, "MergedDomain", new HashSet<>());

      Optional<ASTCDCompilationUnit> expected = parser.parse(base_path + "MaCoCo.cd");
      Assertions.assertTrue(expected.isPresent());

      CD4CodeMill.scopesGenitorDelegator().createFromAST(expected.get());
      CD4CodeMill.scopesGenitorDelegator().createFromAST(merged);

      Assertions.assertTrue(
          CDDiff.computeSyntax2SemDiff(
                  merged, expected.get(), CDSemantics.MULTI_INSTANCE_CLOSED_WORLD)
              .isEmpty());

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Disabled
  @ParameterizedTest
  @MethodSource("performanceSet")
  public void testOWDiffPerformance(String file1, String file2) {
    String path = "src/tooltest/resources/Performance/";
    CD4CodeParser parser = CD4CodeMill.parser();
    try {
      Optional<ASTCDCompilationUnit> cd1 = parser.parse(path + file1);
      Optional<ASTCDCompilationUnit> cd2 = parser.parse(path + file2);
      Assertions.assertTrue(cd1.isPresent() && cd2.isPresent());

      int diffsize = 10;

      // alloy-based
      long startTime_alloy = System.currentTimeMillis(); // start time
      CDDiff.computeAlloySemDiff(
          cd1.get(), cd2.get(), diffsize, 1, CDSemantics.MULTI_INSTANCE_OPEN_WORLD);
      long endTime_alloy = System.currentTimeMillis(); // end time

      Log.println("alloy-based: " + (endTime_alloy - startTime_alloy));

      // reduction-based
      long startTime_reduction = System.currentTimeMillis(); // start time
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1.get(), cd2.get());
      CDDiff.computeAlloySemDiff(
          cd1.get(), cd2.get(), diffsize, 1, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
      long endTime_reduction = System.currentTimeMillis(); // end time

      Log.println("reduction-based: " + (endTime_reduction - startTime_reduction));

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  public static Stream<Arguments> performanceSet() {
    return Stream.of(
        Arguments.of("5A.cd", "5B.cd"),
        Arguments.of("10A.cd", "10B.cd"),
        Arguments.of("15A.cd", "15B.cd"),
        Arguments.of("20A.cd", "20B.cd"),
        Arguments.of("25A.cd", "25B.cd"));
  }
}
