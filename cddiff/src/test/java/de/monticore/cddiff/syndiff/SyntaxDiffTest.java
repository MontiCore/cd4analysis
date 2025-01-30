package de.monticore.cddiff.syndiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

public class SyntaxDiffTest extends CDDiffTestBasis {

  /*--------------------------------------------------------------------*/
  // Syntax Diff Tests

  public static final String dir = "src/test/resources/de/monticore/cddiff/syndiff/SyntaxDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;

  @Test
  public void testDTs() {
    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/test/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin3.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/test/resources/de/monticore/cddiff/DigitalTwins/DigitalTwin1.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld, List.of());
    Assert.assertEquals(4, synDiff.getAddedClasses().size());
    Assert.assertEquals(2, synDiff.getAddedAssocs().size());
  }

  @Test
  public void testSyntax1() {
    parseModels("Source1.cd", "Target1.cd");

    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Test
  public void testSyntax2() {
    parseModels("Source2.cd", "Target2.cd");

    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Test
  public void testSyntax3() {
    parseModels("TechStoreV2.cd", "TechStoreV1.cd");
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Test
  public void testSyntax4() {
    parseModels("TechStoreV9.cd", "TechStoreV10.cd");
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Test
  public void testSyntax5() {
    parseModels("TechStoreV11.cd", "TechStoreV12.cd");
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(src, tgt);
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
