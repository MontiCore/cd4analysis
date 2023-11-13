package de.monticore.cddiff.syndiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import de.monticore.od4report._prettyprint.OD4ReportFullPrettyPrinter;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odvalidity.OD2CDMatcher;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class SyntaxDiffTest extends CDDiffTestBasis {

  @Test
  public void test11() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel(
            "src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/MoveAttributes/CD11.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel(
            "src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/MoveAttributes/CD12.cd");

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);

    Assertions.assertTrue(witnesses.isEmpty());

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(
              CDSemantics.SIMPLE_CLOSED_WORLD, compilationUnitOld, compilationUnitNew, od)) {
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }
  }

  @Test
  public void test21() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel(
            "src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/MoveAttributes/CD12.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel(
            "src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/MoveAttributes/CD11.cd");

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);

    Assertions.assertTrue(witnesses.isEmpty());

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(
              CDSemantics.SIMPLE_CLOSED_WORLD, compilationUnitOld, compilationUnitNew, od)) {
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }
  }

  @Test
  public void test31() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel(
            "src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/AddedDeletedAssocs/CD31.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel(
            "src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/AddedDeletedAssocs/CD32.cd");

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);

    Assertions.assertFalse(witnesses.isEmpty());

    for (ASTODArtifact od : witnesses) {
      if (!new OD2CDMatcher()
          .checkIfDiffWitness(
              CDSemantics.SIMPLE_CLOSED_WORLD, compilationUnitNew, compilationUnitOld, od)) {
        Log.println(new OD4ReportFullPrettyPrinter(new IndentPrinter()).prettyprint(od));
        Assertions.fail();
      }
    }
  }

  @Test
  public void testSimpleSem() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/SS1.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/SS2.cd");

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);
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
