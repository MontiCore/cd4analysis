package de.monticore.cddiff;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.cdsyntax2semdiff.DiffHelper;
import de.monticore.od4report._prettyprint.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class TestWitnesses extends CDDiffTestBasis {

  @Test
  public void test5() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/5/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/5/CD2.cd");

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
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(
              CDSemantics.STA_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }
  }

  @Test
  public void test10() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/10/CD1.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/10/CD2.cd");
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

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail();
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    ////    System.out.println(new CD4CodeFullPrettyPrinter(new
    // IndentPrinter()).prettyprint(original1));
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail();
    //      }
    //    }
  }

  @Test
  public void test15() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/15/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/15/CD2.cd");

    DiffHelper diffHelper = new DiffHelper(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(
              CDSemantics.STA_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail();
      }
    }
  }

  @Test
  public void test20() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/20/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/20/CD2.cd");

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
    System.out.println(witnesses.size());
    for (ASTODArtifact od : witnesses) {
      Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(
              CDSemantics.STA_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        System.out.println("Closed World Fail");
        Log.println("Wrong");
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }
  }

  @Test
  public void test25() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/25/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/Performance/25/CD2.cd");

    //    DiffHelper diffHelper = new DiffHelper(compilationUnitNew, compilationUnitOld);
    //    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses) {
    //      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD,
    // compilationUnitNew, compilationUnitOld, od)) {
    //        System.out.println("Closed World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail();
    //      }
    //    }

    // reduction-based
    ASTCDCompilationUnit original1 = compilationUnitNew.deepClone();
    ASTCDCompilationUnit original2 = compilationUnitOld.deepClone();
    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(original1, original2);

    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);

//    System.out.println(new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(original1));
//    System.out.println(new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(original2));
    for (ASTODArtifact od : witnesses2) {
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
        System.out.println("Open World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }
  }
  @Test
  public void testDE() {
    ASTCDCompilationUnit cd1 = parseModel("src/cddifftest/resources/validation/cddiff/DEv2.cd");
    ASTCDCompilationUnit cd2 = parseModel("src/cddifftest/resources/validation/cddiff/DEv1.cd");

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail();
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //      }
    //    }
  }

  @Test
  public void testEA() {
    ASTCDCompilationUnit cd1 = parseModel("src/cddifftest/resources/validation/cddiff/EAv2.cd");
    ASTCDCompilationUnit cd2 = parseModel("src/cddifftest/resources/validation/cddiff/EAv1.cd");

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //      }
    //    }
  }

  @Test
  public void testEMT() {
    ASTCDCompilationUnit cd1 = parseModel("src/cddifftest/resources/validation/cddiff/EMTv1.cd");
    ASTCDCompilationUnit cd2 = parseModel("src/cddifftest/resources/validation/cddiff/EMTv2.cd");

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //      }
    //    }
  }

  @Test
  public void testLibrary1() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/validation/cddiff/LibraryV2.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/cddifftest/resources/validation/cddiff/LibraryV1.cd");

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //      }
    //    }
  }

  @Test
  public void testLibrary2() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/validation/cddiff/LibraryV3.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/cddifftest/resources/validation/cddiff/LibraryV2.cd");

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //      }
    //    }
  }

  @Test
  public void testLibrary3() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/validation/cddiff/LibraryV4.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/cddifftest/resources/validation/cddiff/LibraryV3.cd");

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //      }
    //    }
  }

  @Test
  public void testLibrary4() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/validation/cddiff/LibraryV5.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/cddifftest/resources/validation/cddiff/LibraryV4.cd");

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //      }
    //    }
  }

  @Test
  public void testManagement() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/validation/cd4analysis/ManagementV2.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/cddifftest/resources/validation/cd4analysis/ManagementV1.cd");

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //      }
    //    }
  }

  @Test
  public void testMyCompany() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/validation/cd4analysis/MyCompanyV2.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/cddifftest/resources/validation/cd4analysis/MyCompanyV1.cd");

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //      }
    //    }
  }

  @Test
  public void testMyExample() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/validation/cd4analysis/MyExampleV2.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/cddifftest/resources/validation/cd4analysis/MyExampleV1.cd");

    //    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    //    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses) {
    //      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od))
    // {
    //        System.out.println("Closed World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail();
    //      }
    //    }

    // reduction-based
    ASTCDCompilationUnit original1 = cd1.deepClone();
    ASTCDCompilationUnit original2 = cd2.deepClone();
    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(original1, original2);

    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(true);

    for (ASTODArtifact od : witnesses2) {
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
        System.out.println("Open World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }
  }

  @Test
  public void testMyLife() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/validation/cd4analysis/MyLifeV2.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/cddifftest/resources/validation/cd4analysis/MyLifeV1.cd");

    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    //    for (ASTCDAssociation association : cd1.getCDDefinition().getCDAssociationsList()){
    //      if (getConnectedClasses(association, cd1).a != null){
    //        System.out.println(getConnectedClasses(association, cd1).a.getName() + " " +
    // getConnectedClasses(association, cd1).b.getName());
    //      }
    //      else {
    //        System.out.println("not working");
    //        System.out.println(association.getLeftQualifiedName() + " " +
    // association.getRightQualifiedName());
    //      }
    //    }
    //    System.out.println("2");
    //    for (ASTCDClass astcdClass : cd1.getCDDefinition().getCDClassesList()){
    //      System.out.println(astcdClass.getSymbol().getInternalQualifiedName());
    //      for (ASTCDAssociation association :
    // cd1.getCDDefinition().getCDAssociationsListForType(astcdClass)){
    //        if (getConnectedClasses(association, cd1).a != null){
    //        System.out.println(getConnectedClasses(association, cd1).a.getName() + " " +
    // getConnectedClasses(association, cd1).b.getName());
    //      }
    //      }
    //    }
    //
    //    System.out.println("3");
    //    for (ASTCDClass astcdClass : diffHelper.getHelper().getSrcMap().keySet()) {
    //      for (AssocStruct assocStruct : diffHelper.getHelper().getSrcMap().get(astcdClass)) {
    //        System.out.println(assocStruct.getAssociation().getLeftQualifiedName() + " " +
    // CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()) +  " " +
    // CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()) + " " +
    // assocStruct.getAssociation().getRightQualifiedName());
    //      }
    //    }
    //
    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);
    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od)) {
        System.out.println("Closed World Fail");
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
      }
    }

    // reduction-based
    //    ASTCDCompilationUnit original1 = cd1.deepClone();
    //    ASTCDCompilationUnit original2 = cd2.deepClone();
    //    ReductionTrafo trafo = new ReductionTrafo();
    //    trafo.transform(original1, original2);
    //
    //    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    //    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(false);
    //
    //    for (ASTODArtifact od : witnesses2) {
    //      if (!new OD2CDMatcher()
    //        .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
    //        System.out.println("Open World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //      }
    //    }
  }

  @Test
  public void testTeaching() {
    ASTCDCompilationUnit cd1 =
        parseModel("src/cddifftest/resources/validation/cd4analysis/TeachingV2.cd");
    ASTCDCompilationUnit cd2 =
        parseModel("src/cddifftest/resources/validation/cd4analysis/TeachingV1.cd");

    //    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
    //    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);
    //    for (ASTODArtifact od : witnesses) {
    //      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.STA_CLOSED_WORLD, cd1, cd2, od))
    // {
    //        System.out.println("Closed World Fail");
    //        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
    //        Assert.fail();
    //      }
    //    }

    // reduction-based
    ASTCDCompilationUnit original1 = cd1.deepClone();
    ASTCDCompilationUnit original2 = cd2.deepClone();
    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(original1, original2);

    DiffHelper diffHelper2 = new DiffHelper(original1, original2);
    List<ASTODArtifact> witnesses2 = diffHelper2.generateODs(true);

    System.out.println(new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(original1));
    System.out.println(new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(original2));

//    for (ASTODArtifact od : witnesses2) {
//      if (!new OD2CDMatcher()
//          .checkIfDiffWitness(CDSemantics.STA_OPEN_WORLD, original1, original2, od)) {
//        System.out.println("Open World Fail");
//        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
//        Assert.fail(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
//      }
//    }
  }

  @Test
  public void testEmployees1to2() {
    ASTCDCompilationUnit cd1 =
      parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd");
    ASTCDCompilationUnit cd2 =
      parseModel("src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd");

//    DiffHelper diffHelper = new DiffHelper(cd1, cd2);
//    List<ASTODArtifact> witnesses = diffHelper.generateODs(false);
//    Assert.assertFalse(witnesses.isEmpty());
//    for (ASTODArtifact od : witnesses) {
//      if (!new OD2CDMatcher().checkIfDiffWitness(CDSemantics.SIMPLE_CLOSED_WORLD, cd1, cd2, od)) {
//        System.out.println("Closed World Fail");
//        Log.println(OD4ReportMill.prettyPrint(od, true));
//        Assert.fail();
//      }
//    }
    ASTCDCompilationUnit original1 = cd1.deepClone();
    ASTCDCompilationUnit original2 = cd2.deepClone();
    ReductionTrafo trafo = new ReductionTrafo();
    trafo.transform(original1, original2);
    System.out.println("Emp1");
    System.out.println(new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(original1));
    System.out.println("Emp2");
    System.out.println(new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(original2));
  }
}
