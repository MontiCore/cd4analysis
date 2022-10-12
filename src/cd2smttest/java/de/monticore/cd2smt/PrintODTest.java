package de.monticore.cd2smt;

import com.microsoft.z3.Context;
import de.monticore.cd2smt.cd2smtGenerator.CD2SMTGenerator;
import de.monticore.cd2smt.context.CDContext;
import de.monticore.cd2smt.smt2odgenerator.SMT2ODGenerator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.od4report.prettyprinter.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class PrintODTest extends CDDiffTestBasis {
  protected final String RELATIVE_MODEL_PATH = "src/cd2smttest/resources/de/monticore/cd2smt";
  protected final String RELATIVE_TARGET_PATH = "target/generated/cd2smt-test";

  protected final OD2CDMatcher matcher = new OD2CDMatcher();

  @BeforeEach
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();

  }

  public void printOD(String CDFileName, String targetNumber) {
    ASTCDCompilationUnit ast = parseModel(Paths.get(RELATIVE_MODEL_PATH, CDFileName).toString());
    CD2SMTGenerator cd2SMTGenerator = new CD2SMTGenerator();
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    CDContext cdContext = cd2SMTGenerator.cd2smt(ast,new Context(cfg));

    SMT2ODGenerator smt2ODGenerator = new SMT2ODGenerator();
   Optional<ASTODArtifact> optOd = smt2ODGenerator.buildOd(cdContext);
   assert optOd.isPresent();

    Path outputFile = Paths.get(RELATIVE_TARGET_PATH, optOd.get().getObjectDiagram().getName() + targetNumber + ".od");
    try {
      FileUtils.writeStringToFile(outputFile.toFile(), new OD4ReportFullPrettyPrinter().prettyprint(optOd.get()), Charset.defaultCharset());
    } catch (Exception e) {
      e.printStackTrace();
      Assertions.fail();
    }
    Assertions.assertTrue(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, optOd.get(), ast));
  }

  @Test
  public void test_EmptyCD() {
    printOD("car0.cd", "0");
  }

  @Test
  public void test_Single_EmptyClass() {
    printOD("car1.cd", "1");
  }

  @Test
  public void test_class_with_attribute() {
    printOD("car2.cd", "2");
  }

  @Test
  public void test_inheritance_of_attribute() {
    printOD("car3.cd", "3");
  }

  @Test
  public void test_Association_one2one() {
    printOD("car4.cd", "4");
  }

  @Test
  public void test_Association_one2atLeastOne() {
    printOD("car5.cd", "5");
  }

  @Test
  public void test_Association_one2optional() {
    printOD("car6.cd", "6");
  }

  @Test
  public void test_Association_optional2optional() {
    printOD("car7.cd", "7");
  }

  @Test
  public void test_Association_optional2AtLeastOne() {
    printOD("car8.cd", "8");
  }

  @Test
  public void test_inheritance_of_association_simple() {
    printOD("car9.cd", "9");
  }

  @Test
  public void test_inheritance_of_association_complete() {
    printOD("car10.cd", "10");
  }
  @Test
  public void test_class_inherit_assoc_of_many_interfaces() {
    printOD( "car14.cd", "14");
  }
  @Test
  public void test_interf_inherit_assoc_of_many_interfaces() {
    printOD( "car15.cd", "15");
  }
  @Test
  public void test_class_inherit_assoc_of_class_and_interf() {
    printOD( "car16.cd", "16");
  }

  @Test
  public void test_inhr_assoc_both_sides() {
    printOD( "car17.cd", "17");
  }

  @Test
  public void test_inhr_assoc_both_sides_complex() {
    printOD( "car18.cd", "18");
  }
  @Test
  public void test_interf_inhr_assoc_and_attribut() {
    printOD( "car19.cd", "19");
  }


  @Test
  public void test_all() {
    printOD("car.cd", "");
  }


}
