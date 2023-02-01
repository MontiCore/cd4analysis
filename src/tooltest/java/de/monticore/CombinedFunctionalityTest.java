package de.monticore;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiff;
import de.monticore.cddiff.alloycddiff.AlloyCDDiff;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.syntax2semdiff.Syntax2SemDiff;
import de.monticore.cdmerge.CDMerge;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import org.junit.Ignore;
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
    try {
      CD4CodeParser parser = CD4CodeMill.parser();
      Optional<ASTCDCompilationUnit> expected =
          parser.parse(
              "src/test/resources/de"
                  + "/monticore"
                  + "/cd4analysis"
                  + "/examples"
                  + "/industrial_strength_models/MaCoCo.cd");
      Assertions.assertTrue(expected.isPresent());

      // todo: merge parts of MaCoCo
      Set<ASTCDCompilationUnit> mergeSet = new HashSet<>();
      mergeSet.add(expected.get().deepClone());
      mergeSet.add(expected.get().deepClone());

      ASTCDCompilationUnit result = CDMerge.merge(mergeSet, "MergedDomain", new HashSet<>());

      Assertions.assertTrue(
          CDDiff.computeSyntax2SemDiff(
                  result, expected.get(), CDSemantics.MULTI_INSTANCE_CLOSED_WORLD)
              .isEmpty());

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @ParameterizedTest
  @Ignore
  @MethodSource("performanceSet")
  public void testOWDiffPerformance(String file1, String file2) {
    String path = "src/cddifftest/resources/de/monticore/cddiff/Performance/";
    CD4CodeParser parser = CD4CodeMill.parser();
    try {
      Optional<ASTCDCompilationUnit> cd1 = parser.parse(path + file1);
      Optional<ASTCDCompilationUnit> cd2 = parser.parse(path + file2);
      Assertions.assertTrue(cd1.isPresent() && cd2.isPresent());


      // alloy-based
      long startTime_alloy = System.currentTimeMillis(); // start time
      CDDiff.computeAlloySemDiff(cd1.get(),cd2.get(),3,1,
          CDSemantics.MULTI_INSTANCE_OPEN_WORLD);
      long endTime_alloy = System.currentTimeMillis(); // end time

      Log.println("alloy-based: "+ (endTime_alloy - startTime_alloy));


      // reduction-based
      long startTime_reduction = System.currentTimeMillis(); // start time
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1.get(), cd2.get());
      CDDiff.computeSyntax2SemDiff(cd1.get(),cd2.get(),CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
      long endTime_reduction = System.currentTimeMillis(); // end time

      Log.println("reduction-based: "+ (endTime_reduction - startTime_reduction));

    }catch (IOException e){
      Assertions.fail(e.getMessage());
    }
  }

  public static Stream<Arguments> performanceSet() {
    return Stream.of(
        Arguments.of("20A.cd", "20B.cd"),
        Arguments.of("40A.cd", "40B.cd"),
        Arguments.of("60A.cd", "60B.cd"),
        Arguments.of("80A.cd", "80B.cd"),
        Arguments.of("100A.cd", "100B.cd"),
        Arguments.of("120A.cd", "120B.cd"));
  }

}
