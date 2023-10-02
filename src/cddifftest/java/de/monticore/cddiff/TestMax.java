package de.monticore.cddiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.syndiff.CDTestHelper;
import de.monticore.cddiff.syndiff.OD.DiffHelper;
import de.monticore.cddiff.syndiff.OD.DiffWitnessGenerator;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.CDTypeDiff;
import de.monticore.cddiff.syndiff.imp.TestHelper;
import de.monticore.od4report._prettyprint.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odvalidity.OD2CDMatcher;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getConnectedClasses;
import static org.junit.jupiter.api.Assertions.fail;

public class TestMax extends CDDiffTestBasis {
  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  @Test
  public void test5() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/5A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/5B.cd");

//    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
//    TestHelper testHelper = new TestHelper(diff);
//    testHelper.staDiff();
//    testHelper.deletedAssocs();
//    testHelper.srcExistsTgtNot();
//    testHelper.changedTypes();
//    testHelper.inheritanceDiffs();
//    testHelper.changedAssocs();
//    testHelper.addedConstants();
//    testHelper.addedClasses();
//    testHelper.addedAssocs();

    DiffHelper diffHelper = new DiffHelper(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }
  }

  @Test
  public void test10() {
    ASTCDCompilationUnit cd1 = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/CD1.cd");
    ASTCDCompilationUnit cd2 = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/CD2.cd");
//
//    CDSyntaxDiff diff = new CDSyntaxDiff(cd1, cd2);
//    TestHelper testHelper = new TestHelper(diff);
//    testHelper.staDiff();
//    testHelper.deletedAssocs();
//    testHelper.srcExistsTgtNot();
//    testHelper.changedTypes();
//    testHelper.inheritanceDiffs();
//    testHelper.changedAssocs();
//    testHelper.addedConstants();
//    testHelper.addedClasses();
//    testHelper.addedAssocs();

//    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
//    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);
//
//    for (ASTODArtifact od : witnesses) {
//      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
//        System.out.println("Closed World Fail");
//        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
//        Assertions.fail();
//      }
//    }

    // reduction-based
    ASTCDCompilationUnit original1 = cd1.deepClone();
    ASTCDCompilationUnit original2 = cd2.deepClone();
    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(original1, original2);

    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);

    for (ASTODArtifact od : witnesses2) {
      if (!new OD2CDMatcher()
        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
        System.out.println("Open World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }
}

  @Test
  public void test15() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/15A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/15B.cd");


    DiffHelper diffHelper = new DiffHelper(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }
  }

  @Test
  public void test20() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/20A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/20B.cd");

//    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
//    TestHelper testHelper = new TestHelper(diff);
//    testHelper.staDiff();
//    testHelper.deletedAssocs();
//    testHelper.srcExistsTgtNot();
//    testHelper.changedTypes();
//    testHelper.inheritanceDiffs();
//    testHelper.changedAssocs();
//    testHelper.addedConstants();
//    testHelper.addedClasses();
//    testHelper.addedAssocs();

    DiffHelper diffHelper = new DiffHelper(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }
  }

  @Test
  public void test25() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/25A.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/25B.cd");

//    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
//    TestHelper testHelper = new TestHelper(diff);
//    testHelper.staDiff();
//    testHelper.deletedAssocs();
//    testHelper.srcExistsTgtNot();
//    testHelper.changedTypes();
//    testHelper.inheritanceDiffs();
//    testHelper.changedAssocs();
//    testHelper.addedConstants();
//    testHelper.addedClasses();
//    testHelper.addedAssocs();

    DiffHelper diffHelper = new DiffHelper(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }
  }

  @Test
  public void testDE() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/DEv2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/DEv1.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testEA() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/EAv2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/EAv1.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testEMT() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/EMTv1.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/EMTv2.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testLibrary1() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV1.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testLibrary2() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV3.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV2.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testLibrary3() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV4.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV3.cd");


    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testLibrary4() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV5.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cddiff/LibraryV4.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testManagement() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/ManagementV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/ManagementV1.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testMyCompany() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/MyCompanyV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/MyCompanyV1.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testMyExample() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/MyExampleV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/MyExampleV1.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  //@Test
  //@Disabled
  public void testMyLife() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/MyLifeV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/MyLifeV1.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  @Test
  public void testTeaching() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/validation/cd4analysis/TeachingV2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/validation/cd4analysis/TeachingV1.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff);
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();
  }

  //@Test
  public void testSem2OD(){
    ASTCDCompilationUnit cd1 = parseModel("src/cddifftest/resources/validation/Performance/5A.cd");
    ASTCDCompilationUnit cd2 = parseModel("src/cddifftest/resources/validation/Performance/5B.cd");

    ASTCDCompilationUnit original1 = cd1.deepClone();
    ASTCDCompilationUnit original2 = cd2.deepClone();


    // reduction-based

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

//    for (ASTODArtifact od : witnesses) {
//      Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
//    }

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }

    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(cd1, cd2);
//    for (ASTODArtifact od : witnesses) {
//      if (!new OD2CDMatcher()
//        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
//        System.out.println("Open World Fail");
//        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
//        Assertions.fail();
//      }
//    }
  }
}
