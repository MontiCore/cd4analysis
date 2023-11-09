package de.monticore.cddiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.od4report._prettyprint.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.params.ParameterizedTest;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import java.util.List;
import java.util.stream.Stream;


public class Syn2SemDiffValidationTest {

  @BeforeEach
  public void init() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  @Test
  public void testEmployees1(){
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");

      Syn2SemDiff syn2semdiff = new Syn2SemDiff(cd1, cd2);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);

      Assertions.assertFalse(witnesses.isEmpty());

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.SIMPLE_CLOSED_WORLD, cd1, cd2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void testEmployees2(){
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");

      Syn2SemDiff syn2semdiff = new Syn2SemDiff(cd1, cd2);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);

      Assertions.assertFalse(witnesses.isEmpty());

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.SIMPLE_CLOSED_WORLD, cd1, cd2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void testOWEmployees(){
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");

      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);
      CDDiffUtil.saveDiffCDs2File(cd1,cd2,"target/generated/syn2semdiff-test/Employees");

      Syn2SemDiff syn2semdiff = new Syn2SemDiff(cd1, cd2);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(true);

      Assertions.assertTrue(witnesses.isEmpty());

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void testOWDigitalTwin1(){
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD("src/cddifftest/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin2.cd");
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD("src/cddifftest/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin1.cd");

      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);
      CDDiffUtil.saveDiffCDs2File(cd1,cd2,"target/generated/syn2semdiff-test/DT2vsDT1");

      Syn2SemDiff syn2semdiff = new Syn2SemDiff(cd1, cd2);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(true);

      Assertions.assertTrue(witnesses.isEmpty());

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void testOWDigitalTwin2(){
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD("src/cddifftest/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin3.cd");
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD("src/cddifftest/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin2.cd");

      ASTCDCompilationUnit original1 = cd1.deepClone();
      ASTCDCompilationUnit original2 = cd2.deepClone();

      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);

      Syn2SemDiff syn2semdiff = new Syn2SemDiff(cd1, cd2);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(true);
      CDDiffUtil.saveDiffCDs2File(cd1,cd2,"target/generated/syn2semdiff-test/DT3vsDT2");

      Assertions.assertFalse(witnesses.isEmpty());

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }
      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
          .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  @ParameterizedTest
  @MethodSource("performanceSet")
  public void testReductionBasedOWDiff(String file1, String file2) {
    String path = "src/cddifftest/resources/validation/Performance/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      ASTCDCompilationUnit original1 = cd1.deepClone();
      ASTCDCompilationUnit original2 = cd2.deepClone();

      // reduction-based
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);

      // print modified CDs
      String dir1 = file1.replaceAll("\\.cd","");
      String dir2 = file2.replaceAll("\\.cd","");
      CDDiffUtil.saveDiffCDs2File(cd1,cd2,"target/generated/syn2semdiff-test/"+dir1 + "vs" + dir2);

      Syn2SemDiff syn2semdiff = new Syn2SemDiff(cd1, cd2);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(true);

      Assertions.assertFalse(witnesses.isEmpty());

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }
      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }


  @ParameterizedTest
  @MethodSource("cddiffSet")
  public void testReductionBasedOWDiff2(String file1, String file2, boolean diff) {
    String path = "src/cddifftest/resources/validation/cddiff/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      ASTCDCompilationUnit original1 = cd1.deepClone();
      ASTCDCompilationUnit original2 = cd2.deepClone();

      // reduction-based
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);

      // print modified CDs
      String dir1 = file1.replaceAll("\\.cd","");
      String dir2 = file2.replaceAll("\\.cd","");
      CDDiffUtil.saveDiffCDs2File(cd1,cd2,"target/generated/syn2semdiff-test/"+dir1 + "vs" + dir2);

      Syn2SemDiff syn2semdiff = new Syn2SemDiff(cd1, cd2);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(true);
      if (diff) {
        Assertions.assertFalse(witnesses.isEmpty());
      }

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }

      for (ASTODArtifact od : witnesses) {
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }


    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }


  @ParameterizedTest
  @MethodSource("cd4analysisSet")
  public void testReductionBasedOWDiff3(String file1, String file2, boolean diff) {
    String path = "src/cddifftest/resources/validation/cd4analysis/";
    try {
      ASTCDCompilationUnit cd1 = CDDiffUtil.loadCD(path + file1);
      ASTCDCompilationUnit cd2 = CDDiffUtil.loadCD(path + file2);

      ASTCDCompilationUnit original1 = cd1.deepClone();
      ASTCDCompilationUnit original2 = cd2.deepClone();

      // reduction-based
      ReductionTrafo trafo = new ReductionTrafo();
      trafo.transform(cd1, cd2);

      // print modified CDs
      String dir1 = file1.replaceAll("\\.cd","");
      String dir2 = file2.replaceAll("\\.cd","");
      CDDiffUtil.saveDiffCDs2File(cd1,cd2,"target/generated/syn2semdiff-test/"+dir1 + "vs" + dir2);

      Syn2SemDiff syn2semdiff = new Syn2SemDiff(cd1, cd2);
      List<ASTODArtifact> witnesses = syn2semdiff.generateODs(true);
      if (diff) {
        Assertions.assertFalse(witnesses.isEmpty());
      }

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }

      for (ASTODArtifact od : witnesses) {
        if (!new OD2CDMatcher()
            .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
          Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
          Assertions.fail();
        }
      }

    } catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }

  protected static Stream<Arguments> performanceSet() {
    return Stream.of(
      Arguments.of("5A.cd", "5B.cd"),
      Arguments.of("10A.cd", "10B.cd"),
      Arguments.of("15A.cd", "15B.cd"),
      Arguments.of("20A.cd", "20B.cd"),
      Arguments.of("25A.cd", "25B.cd"));
  }


  protected static Stream<Arguments> cddiffSet() {
    return Stream.of(
      Arguments.of("DEv2.cd", "DEv1.cd", true),
      Arguments.of("EAv2.cd", "EAv1.cd", true),
      Arguments.of("EMTv1.cd", "EMTv2.cd", true),
      Arguments.of("LibraryV2.cd", "LibraryV1.cd", true),
      Arguments.of("LibraryV3.cd", "LibraryV2.cd", false),
      Arguments.of("LibraryV4.cd", "LibraryV3.cd", true),
      Arguments.of("LibraryV5.cd", "LibraryV4.cd", false));
  }

  protected static Stream<Arguments> cd4analysisSet() {
    return Stream.of(
      Arguments.of("ManagementV2.cd", "ManagementV1.cd", false),
      Arguments.of("MyCompanyV2.cd", "MyCompanyV1.cd", false),
//      Arguments.of("MyExampleV2.cd", "MyExampleV1.cd", false));//Tsveti does a false matching
//      Arguments.of("MyLifeV2.cd", "MyLifeV1.cd", true),//type resolves to present but it is null?
      Arguments.of("TeachingV2.cd", "TeachingV1.cd", true));
  }


}
