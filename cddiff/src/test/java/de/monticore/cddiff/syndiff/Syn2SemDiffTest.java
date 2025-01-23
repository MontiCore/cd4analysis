package de.monticore.cddiff.syndiff;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import de.monticore.od4report._prettyprint.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class Syn2SemDiffTest extends CDDiffTestBasis {

  @Test
  public void testCD5() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syndiff/AssocDiff/AssocDeletedMerging/CD51.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syndiff/AssocDiff/AssocDeletedMerging/CD52.cd");

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);

    Assert.assertTrue(witnesses.isEmpty());
  }

  @Test
  public void test11() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/MoveAttributes/CD11.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/MoveAttributes/CD12.cd");

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);

    Assert.assertTrue(witnesses.isEmpty());
  }

  @Test
  public void test21() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/MoveAttributes/CD12.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/MoveAttributes/CD11.cd");

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);

    Assert.assertTrue(witnesses.isEmpty());
  }

  @Test
  public void test31() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/AddedDeletedAssocs/CD31.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/AddedDeletedAssocs/CD32.cd");

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);

    Assert.assertFalse(witnesses.isEmpty());

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(
              CDSemantics.SIMPLE_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail();
      }
    }
  }

  @Test
  public void testSimpleSem() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/SS1.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/SS2.cd");

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);
    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(
              CDSemantics.SIMPLE_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail();
      }
    }
  }

  @Test
  public void testDT23() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/test/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin2.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/test/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin3.cd");
    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);
    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(
              CDSemantics.SIMPLE_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail();
      }
    }
  }

  @Test
  public void testDT32() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/test/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin3.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/test/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin2.cd");
    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);
    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(
              CDSemantics.SIMPLE_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assert.fail();
      }
    }
  }
}
