package de.monticore.cd2smt;

import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.od4report.prettyprinter.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.*;
import de.se_rwth.artifacts.lang.matcher.OD2CDMatcher;
import de.se_rwth.commons.logging.Log;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
//import org.sosy_lab.common.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class SMTDemoTest {
  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
  }

  @Test
  public void testCd2od() {
    // Given
    Path model = Paths.get("src/cd2smttest/resources/de.monticore.cd2smt/car.cd");
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTCDCompilationUnit> optAutomaton = Optional.empty();
    try {
      optAutomaton = parser.parse(model.toString());
    } catch (IOException e) {
      fail();
    }//
    assertTrue(optAutomaton.isPresent());
    ASTCDDefinition cd = optAutomaton.get().getCDDefinition();

    //When

    System.out.println("---------------Test------------------------------------------");
    CD2ODGenerator obj = new CD2ODGenerator();
    Optional<ASTODArtifact> myOd = obj.cd2od(cd);
    assertTrue(myOd.isPresent());

    OD2CDMatcher matcher = new OD2CDMatcher();
    //assertTrue( matcher.checkODConsistency(cd,myOd.get().getObjectDiagram())) ;

    Path outputFile = Paths.get("target/generated/cd2smt-test",
        myOd.get().getObjectDiagram().getName() + ".od");

    try {
      // Write results into a file
      FileUtils.writeStringToFile(outputFile.toFile(), new OD4ReportFullPrettyPrinter().prettyprint(myOd.get()), Charset.defaultCharset());
    } catch (Exception e){
      e.printStackTrace();
      Assert.fail();
    }


    System.out.println("___________________Test_________________________________________");


  }




}
