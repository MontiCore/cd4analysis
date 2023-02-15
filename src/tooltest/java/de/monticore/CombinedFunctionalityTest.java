package de.monticore;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiff;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cdmerge.CDMerge;
import de.monticore.cdmerge.config.MergeParameter;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  protected static ASTCDCompilationUnit parseCDModel(String cdFilePath) {
    CD4CodeParser cdParser = CD4CodeMill.parser();
    final Optional<ASTCDCompilationUnit> optCdAST;
    try {
      optCdAST = cdParser.parse(cdFilePath);
    } catch (IOException e) {
      fail();
      throw new RuntimeException(e);
    }
    assert (optCdAST.isPresent());
    return optCdAST.get();
  }

  @Disabled
  @Test
  public void testMaCoCo() {
    String base_path =
        "src/test/resources/de/monticore/cd4analysis/examples/industrial_strength_models/";

    Set<ASTCDCompilationUnit> mergeSet =
        Arrays.stream(new File(base_path + "MaCoCoMerge/").listFiles())
            .map(f -> parseCDModel(f.getAbsolutePath()))
            .collect(Collectors.toCollection(LinkedHashSet::new));

    Set<MergeParameter> paramSet = new HashSet<>();
    paramSet.add(MergeParameter.LOG_TO_CONSOLE);

    ASTCDCompilationUnit merged = CDMerge.merge(mergeSet, "MergedDomain", paramSet);
    assertNotEquals(null, merged);

    ASTCDCompilationUnit expected =
        parseCDModel(Path.of(base_path, "MaCoCo.cd").toAbsolutePath().toString());

      assertEquals(new ArrayList<>(), CDDiff.computeSyntax2SemDiff(
        merged, expected, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD));

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
