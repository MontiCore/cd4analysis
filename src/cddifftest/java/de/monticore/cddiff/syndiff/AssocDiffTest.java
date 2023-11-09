package de.monticore.cddiff.syndiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.syn2semdiff.Syn2SemDiff;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AssocDiffTest extends CDDiffTestBasis {

  @Test
  public void testCD5() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/AssocDeletedMerging/CD51.cd");
    ASTCDCompilationUnit compilationUnitOld =

        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/AssocDeletedMerging/CD52.cd");

    Syn2SemDiff syn2semdiff = new Syn2SemDiff(compilationUnitNew, compilationUnitOld);
    List<ASTODArtifact> witnesses = syn2semdiff.generateODs(false);

    //todo: fix me
    //Assertions.assertTrue(witnesses.isEmpty());
  }


  /*--------------------------------------------------------------------*/
  // Syntax Diff Tests

  public static final String dir =
      "src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;

  @Test
  public void testAssoc1() {
    parseModels("Source1.cd", "Target1.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt);
    // System.out.println(synDiff.printDiff());
  }

  @Test
  public void testAssoc2() {
    parseModels("Source2.cd", "Target2.cd");

    CDSyntaxDiff associationDiff = new CDSyntaxDiff(src, tgt);
    // System.out.println(associationDiff.printDiff());
    // System.out.println(associationDiff.getBaseDiff());
    // System.out.println(associationDiff.getMatchedAssocs());
  }

  @Test
  public void testAssoc3() {
    parseModels("Source3.cd", "Target3.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    // System.out.println(syntaxDiff.printSrcCD());
    // System.out.println(syntaxDiff.printTgtCD());
    // System.out.println(syntaxDiff.getBaseDiff());
  }

  @Test
  public void testAssoc4() {
    parseModels("Source4.cd", "Target4.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    System.out.println(syntaxDiff.getBaseDiff());
  }

  @Test
  public void testAssoc5() {
    parseModels("Source5.cd", "Target5.cd");
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    System.out.println(syntaxDiff.getMatchedClasses());
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
