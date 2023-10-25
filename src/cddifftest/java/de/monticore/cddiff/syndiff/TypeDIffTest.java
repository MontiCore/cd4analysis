package de.monticore.cddiff.syndiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.semdiff.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.semdiff.CDTypeDiff;
import de.monticore.cddiff.syndiff.semdiff.TestHelper;
import de.monticore.prettyprint.IndentPrinter;

import java.io.IOException;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

public class TypeDIffTest extends CDDiffTestBasis {

  @Test
  public void testCD2() {

    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD21.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD22.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff, diff.getHelper());
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
  public void testCD1() {

    ASTCDCompilationUnit compilationUnitNew =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD11.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD12.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff, diff.getHelper());
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
  public void testCD3(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD31.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD32.cd");
    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff, diff.getHelper());
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
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD41.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD42.cd");

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff, diff.getHelper());
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
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD41.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD42.cd");
    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff, diff.getHelper());
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
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD71.cd");
    ASTCDCompilationUnit compilationUnitOld =
        parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD72.cd");
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);

    CDSyntaxDiff diff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(diff, diff.getHelper());
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

  /*--------------------------------------------------------------------*/
  // Syntax Diff Tests

  public static final String dir = "src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;

  // Test for all kinds of changes in attributes
  @Test
  public void testType1() {
    parseModels("Source1.cd", "Target1.cd");

    CDDiffUtil.refreshSymbolTable(src);
    CDDiffUtil.refreshSymbolTable(tgt);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    // System.out.println(syntaxDiff.printDiff());
  }

  // Tests for all kinds of changes in enum constants
  @Test
  public void testType2() {
    parseModels("Source2.cd", "Target2.cd");
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    ASTCDClass astcdClass11 = CDTestHelper.getClass("A", src.getCDDefinition());
    ASTCDClass astcdClass12 = CDTestHelper.getClass("A", tgt.getCDDefinition());
    ASTCDClass astcdClass21 = CDTestHelper.getClass("B", src.getCDDefinition());
    ASTCDClass astcdClass22 = CDTestHelper.getClass("B", tgt.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(src);
    CDDiffUtil.refreshSymbolTable(tgt);

    CDSyntaxDiff diff = new CDSyntaxDiff(src, tgt);
    CDTypeDiff typeDiff1 = new CDTypeDiff(astcdClass11, astcdClass12, tgt, diff.getHelper());
    CDTypeDiff typeDiff2 = new CDTypeDiff(astcdClass21, astcdClass22, tgt, diff.getHelper());

    //System.out.println(diff.printOnlyAdded());

    /*System.out.println(typeDiff1.printSrcCD());
    System.out.println(typeDiff2.printSrcCD());
    System.out.println("--------------------------------");
    System.out.println(typeDiff1.printTgtCD());
    System.out.println(typeDiff2.printTgtCD());*/
  }

  // Test for change of modifiers, extensions, and implementations
  @Test
  public void testType3() {
    parseModels("Source3.cd", "Target3.cd");

    ASTCDClass astcdClass11 = CDTestHelper.getClass("A", src.getCDDefinition());
    ASTCDClass astcdClass12 = CDTestHelper.getClass("A", tgt.getCDDefinition());
    ASTCDClass astcdClass21 = CDTestHelper.getClass("B", src.getCDDefinition());
    ASTCDClass astcdClass22 = CDTestHelper.getClass("B", tgt.getCDDefinition());
    ASTCDClass astcdClass31 = CDTestHelper.getClass("C", src.getCDDefinition());
    ASTCDClass astcdClass32 = CDTestHelper.getClass("C", tgt.getCDDefinition());

    CDSyntaxDiff diff = new CDSyntaxDiff(src, tgt);

    CDTypeDiff typeDiff1 = new CDTypeDiff(astcdClass11, astcdClass12, tgt, diff.getHelper());
    CDTypeDiff typeDiff2 = new CDTypeDiff(astcdClass21, astcdClass22, tgt, diff.getHelper());
    CDTypeDiff typeDiff3 = new CDTypeDiff(astcdClass31, astcdClass32, tgt, diff.getHelper());
    System.out.println(typeDiff1.printSrcCD());
    System.out.println(typeDiff2.printSrcCD());
    System.out.println(typeDiff3.printSrcCD());
    System.out.println("--------------------------------");
    System.out.println(typeDiff1.printTgtCD());
    System.out.println(typeDiff2.printTgtCD());
    System.out.println(typeDiff3.printTgtCD());
    System.out.println("--------------------------------");
    System.out.println(typeDiff1.getBaseDiff());
    System.out.println(typeDiff2.getBaseDiff());
    System.out.println(typeDiff3.getBaseDiff());
  }

  // Test for inherited attributes
  @Test
  public void testType4() {
    parseModels("Source4.cd", "Target4.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("A", src.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("A", tgt.getCDDefinition());

    CDSyntaxDiff diff = new CDSyntaxDiff(src, tgt);
    CDTypeDiff typeDiff = new CDTypeDiff(astcdClass, astcdClass1, tgt, diff.getHelper());
    System.out.println(typeDiff.printSrcCD());
    System.out.println(typeDiff.printTgtCD());
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
