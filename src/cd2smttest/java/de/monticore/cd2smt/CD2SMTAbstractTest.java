package de.monticore.cd2smt;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.microsoft.z3.Context;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis.trafo.CD4AnalysisAfterParseTrafo;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.Arguments;

public class CD2SMTAbstractTest {
  protected final String RELATIVE_MODEL_PATH = "src/cd2smttest/resources/de/monticore/cd2smt";
  protected final String RELATIVE_TARGET_PATH = "target/generated/cd2smt-test";
  protected Context ctx;

  public void printOD(ASTODArtifact od, String dir) {
    Path outputFile =
        Paths.get(RELATIVE_TARGET_PATH + "/" + dir, od.getObjectDiagram().getName() + ".od");
    try {
      FileUtils.writeStringToFile(
          outputFile.toFile(), OD4ReportMill.prettyPrint(od, true), Charset.defaultCharset());
    } catch (Exception e) {
      e.printStackTrace();
      Assertions.fail();
    }
  }

  protected ASTCDCompilationUnit parseModel(String modelFile) {
    Path model = Paths.get(Paths.get(RELATIVE_MODEL_PATH, modelFile).toString());
    CD4AnalysisParser parser = CD4AnalysisMill.parser();
    Optional<ASTCDCompilationUnit> optAutomaton;
    try {
      optAutomaton = parser.parse(model.toString());
      assertTrue(optAutomaton.isPresent());
      (new CD4AnalysisAfterParseTrafo()).transform(optAutomaton.get());
      return optAutomaton.get();
    } catch (Exception e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + modelFile + ": " + e.getMessage());
    }

    return null;
  }

  public static Stream<Arguments> modelTarget() {
    return Stream.of(
        Arguments.of("car1.cd"),
        Arguments.of("car2.cd"),
        Arguments.of("car3.cd"),
        Arguments.of("car4.cd"),
        Arguments.of("car5.cd"),
        Arguments.of("car6.cd"),
        Arguments.of("car7.cd"),
        Arguments.of("car8.cd"),
        Arguments.of("car9.cd"),
        Arguments.of("car10.cd"),
        Arguments.of("car11.cd"),
        Arguments.of("car12.cd"),
        Arguments.of("car14.cd"),
        Arguments.of("car15.cd"),
        Arguments.of("car16.cd"),
        Arguments.of("car17.cd"),
        Arguments.of("car18.cd"),
        Arguments.of("car19.cd"),
        Arguments.of("car20.cd"),
        Arguments.of("car21.cd"),
        Arguments.of("car.cd"));
  }

  public static Stream<Arguments> modelTargetSS() {
    return Stream.of(
        Arguments.of("car1.cd"),
        Arguments.of("car2.cd"),
        //  Arguments.of("car3.cd"),         // to much time
        Arguments.of("car4.cd"),
        Arguments.of("car5.cd"),
        Arguments.of("car6.cd"),
        Arguments.of("car7.cd"),
        Arguments.of("car8.cd"),
        // Arguments.of("car9.cd"),         // too much time
        // Arguments.of("car10.cd"),       // don't terminate
        Arguments.of("car11.cd"),
        Arguments.of("car12.cd"),
        // Arguments.of("car14.cd"),       // don't terminate
        // Arguments.of("car15.cd"),       // don't terminate
        // Arguments.of("car16.cd"),      // don't terminate
        // Arguments.of("car17.cd"),       // don't terminate
        // Arguments.of("car18.cd"),       // don't terminate
        // Arguments.of("car19.cd"),       // don't terminate
        Arguments.of("car20.cd"), // don't terminate
        Arguments.of("car21.cd")); // don't terminate
    // Arguments.of("car.cd"));         // don't terminate
  }
}
