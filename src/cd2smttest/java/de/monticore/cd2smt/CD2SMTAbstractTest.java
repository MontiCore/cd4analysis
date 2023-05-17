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

  public void printOD(ASTODArtifact od, String targetNumber) {
    Path outputFile =
        Paths.get(RELATIVE_TARGET_PATH, od.getObjectDiagram().getName() + targetNumber + ".od");
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

  public static Stream<Arguments> modelTargetDS() {
    return Stream.of(
        Arguments.of("car1.cd", "1"),
        Arguments.of("car2.cd", "2"),
        Arguments.of("car3.cd", "3"),
        Arguments.of("car4.cd", "4"),
        Arguments.of("car5.cd", "5"),
        Arguments.of("car6.cd", "6"),
        Arguments.of("car7.cd", "7"),
        Arguments.of("car8.cd", "8"),
        Arguments.of("car9.cd", "9"),
        Arguments.of("car10.cd", "10"),
        Arguments.of("car11.cd", "11"),
        Arguments.of("car12.cd", "12"),
        Arguments.of("car14.cd", "14"),
        Arguments.of("car15.cd", "15"),
        Arguments.of("car16.cd", "116"),
        Arguments.of("car17.cd", "17"),
        Arguments.of("car18.cd", "18"),
        Arguments.of("car19.cd", "19"),
        Arguments.of("car20.cd", "20"),
        Arguments.of("car21.cd", "21"),
        Arguments.of("car.cd", ""));
  }

  public static Stream<Arguments> modelTargetSS() {
    return Stream.of(
        Arguments.of("car1.cd", "1"),
        Arguments.of("car2.cd", "2"),
        //  Arguments.of("car3.cd", "3"),         // to much time
        Arguments.of("car4.cd", "4"),
        Arguments.of("car5.cd", "5"),
        Arguments.of("car6.cd", "6"),
        Arguments.of("car7.cd", "7"),
        Arguments.of("car8.cd", "8"),
        // Arguments.of("car9.cd", "9"),         // too much time
        // Arguments.of("car10.cd", "10"),       // don't terminate
        Arguments.of("car11.cd", "11"),
        Arguments.of("car12.cd", "12"));
    // Arguments.of("car14.cd", "14"),       // don't terminate
    // Arguments.of("car15.cd", "15"),       // don't terminate
    // Arguments.of("car16.cd", "116"),      // don't terminate
    // Arguments.of("car17.cd", "17"),       // don't terminate
    // Arguments.of("car18.cd", "18"),       // don't terminate
    // Arguments.of("car19.cd", "19"),       // don't terminate
    // Arguments.of("car20.cd", "20"),      // don't terminate
    // Arguments.of("car21.cd", "21"),      // don't terminate
    // Arguments.of("car.cd", ""));         // don't terminate
  }
}
