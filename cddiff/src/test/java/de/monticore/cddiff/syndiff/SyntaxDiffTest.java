package de.monticore.cddiff.syndiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import de.monticore.cddiff.CDDiffUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


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
    assertEquals(4, synDiff.getAddedClasses().size());
    assertEquals(2, synDiff.getAddedAssocs().size());
  }

  @Test
  public void testSyntax1() {
    parseModels("Source1.cd", "Target1.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    //SyntaxDiffPrinter sb = new SyntaxDiffPrinter(synDiff);
    //System.out.println(sb.printDiff());

    // check added / deleted classes
    assertEquals(2, synDiff.getAddedClasses().size());
    assertEquals(2, synDiff.getDeletedClasses().size());

    // check added / deleted enums
    assertEquals(1, synDiff.getAddedEnums().size());
    assertEquals(1, synDiff.getDeletedEnums().size());

    // check changed types
    assertEquals(4, synDiff.getChangedTypes().size());

    // check associations
    assertEquals(2, synDiff.getChangedAssocs().size());
    assertEquals(2, synDiff.getAddedAssocs().size());
    assertEquals(2, synDiff.getDeletedAssocs().size());
  }

  @Disabled
  @Test
  public void testSyntax2() {
    parseModels("Source2.cd", "Target2.cd");
    //todo: add appropriate asserts
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Disabled
  @Test
  public void testSyntax3() {
    parseModels("TechStoreV2.cd", "TechStoreV1.cd");
    //todo: add appropriate asserts
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Disabled
  @Test
  public void testSyntax4() {
    parseModels("TechStoreV9.cd", "TechStoreV10.cd");
    //todo: add appropriate asserts
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Disabled
  @Test
  public void testSyntax5() {
    parseModels("TechStoreV11.cd", "TechStoreV12.cd");
    //todo: add appropriate asserts
    SyntaxDiffPrinter sb = new SyntaxDiffPrinter(src, tgt);
    System.out.println(sb.printDiff());
  }

  @Test
  public void testMaCoCo(){
    CDDiffUtil.setUseJavaTypes(true);
    parseModels("MaCoCo_v1.cd", "MaCoCo_v2.cd");

    CDSyntaxDiff synDiff = new CDSyntaxDiff(src, tgt, List.of());
    //SyntaxDiffPrinter sb = new SyntaxDiffPrinter(synDiff);
    //System.out.println(sb.printDiff());
    assertEquals(1, synDiff.getAddedClasses().size());
    assertEquals(1, synDiff.getAddedEnums().size());
    assertEquals(1, synDiff.getAddedAssocs().size());

    synDiff = new CDSyntaxDiff(tgt, src, List.of());
    //sb = new SyntaxDiffPrinter(synDiff);
    //System.out.println(sb.printDiff());
    assertEquals(1, synDiff.getDeletedClasses().size());
    assertEquals(1, synDiff.getDeletedEnums().size());
    assertEquals(1, synDiff.getDeletedAssocs().size());
  }

  public void parseModels(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> src =
          CD4CodeMill.parser().parseCDCompilationUnit(dir + concrete);
      Optional<ASTCDCompilationUnit> tgt = CD4CodeMill.parser().parseCDCompilationUnit(dir + ref);
      if (src.isPresent() && tgt.isPresent()) {
        CDDiffUtil.refreshSymbolTable(src.get());
        CDDiffUtil.refreshSymbolTable(tgt.get());
        this.tgt = tgt.get();
        this.src = src.get();
      } else {
        fail("Could not parse CDs.");
      }

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
