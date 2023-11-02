package de.monticore.cddiff.syndiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.cdsyntax2semdiff.DiffHelper;
import de.monticore.cddiff.cdsyntax2semdiff.Syn2SemDiffHelper;
import de.monticore.cddiff.syndiff.semdiff.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.semdiff.CDTypeDiff;
import de.monticore.cddiff.syndiff.semdiff.SyntaxDiffBuilder;
import de.monticore.cddiff.syndiff.semdiff.TestHelper;
import java.io.IOException;
import java.util.*;

import de.monticore.od4report._prettyprint.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.prettyprint.IndentPrinter;
import org.junit.Assert;
import org.junit.Test;

public class SyntaxDiffTest extends CDDiffTestBasis {
  @Test
  public void ini() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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

  // Test for Bad overlapping
  // @Test
  public void testCD1() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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

  // @Test
  public void testCD2() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD2.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD2.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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
  public void testCD3() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD3.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD3.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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
  public void testCD4() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD4.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD4.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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
  public void testCD5() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD5.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD5.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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
  public void testCD6() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD6.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD6.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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
  public void testCD7() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD7.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD7.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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
  public void testCD8() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD8.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD8.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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
  public void test11() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD11.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD12.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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
  public void test21() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD21.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD22.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
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
  public void test31() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD31.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD32.cd");

    //CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    //TestHelper testHelper = new TestHelper(syntaxDiff, syntaxDiff.getHelper());
    /*System.out.println("Start");
    testHelper.staDiff();
    testHelper.deletedAssocs();
    testHelper.srcExistsTgtNot();
    testHelper.changedTypes();
    testHelper.inheritanceDiffs();
    testHelper.changedAssocs();
    testHelper.addedConstants();
    testHelper.addedClasses();
    testHelper.addedAssocs();*/

    SyntaxDiffBuilder sb = new SyntaxDiffBuilder(compilationUnitNew, compilationUnitOld);
    System.out.println(sb.printOnlyDeleted());


  }

  @Test
  public void testSimpleSem() {
    ASTCDCompilationUnit compilationUnitNew =
      parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/SS1.cd");
    ASTCDCompilationUnit compilationUnitOld =
      parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/SS2.cd");

        DiffHelper diffHelper = new DiffHelper(compilationUnitNew, compilationUnitOld);
        List<ASTODArtifact> witnesses = diffHelper.generateODs(false);
        for (ASTODArtifact witness : witnesses) {
          System.out.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(witness));
        }
  }
  /*--------------------------------------------------------------------*/
  // Syntax Diff Tests

  public static final String dir =
      "src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;

  @Test
  public void testSyntax1() {
    parseModels("Source1.cd", "Target1.cd");

    SyntaxDiffBuilder sb = new SyntaxDiffBuilder(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Test
  public void testSyntax2() {
    parseModels("Source2.cd", "Target2.cd");

    SyntaxDiffBuilder sb = new SyntaxDiffBuilder(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Test
  public void testSyntax3() {
    parseModels("TechStoreV2.cd", "TechStoreV1.cd");
    SyntaxDiffBuilder sb = new SyntaxDiffBuilder(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Test
  public void testSyntax4() {
    parseModels("TechStoreV9.cd", "TechStoreV10.cd");
    SyntaxDiffBuilder sb = new SyntaxDiffBuilder(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Test
  public void testSyntax5() {
    parseModels("TechStoreV11.cd", "TechStoreV12.cd");
    SyntaxDiffBuilder sb = new SyntaxDiffBuilder(src, tgt);
    System.out.println(sb.printDiff());
  }

  public void parseModels(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> src =
          CD4CodeMill.parser().parseCDCompilationUnit(dir + concrete);
      Optional<ASTCDCompilationUnit> tgt = CD4CodeMill.parser().parseCDCompilationUnit(dir + ref);
      if (src.isPresent() && tgt.isPresent()) {
        CD4CodeMill.scopesGenitorDelegator().createFromAST(src.get());
        CD4CodeMill.scopesGenitorDelegator().createFromAST(tgt.get());
        src.get().accept(new CD4CodeSymbolTableCompleter(src.get()).getTraverser());
        tgt.get().accept(new CD4CodeSymbolTableCompleter(tgt.get()).getTraverser());
        this.tgt = tgt.get();
        this.src = src.get();
      } else {
        Assert.fail("Could not parse CDs.");
      }

    } catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }
}
