package de.monticore.cddiff.syndiff;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.TestHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getConnectedClasses;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class SyntaxDiffTest extends CDDiffTestBasis {
  @Test
  public void ini(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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

  //Test for Bad overlapping
  //@Test
  public void testCD1(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
  public void testCD2(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD2.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD3.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD3.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
  public void testCD4(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD4.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD4.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
  public void testCD5(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD5.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD5.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
  public void testCD6(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD6.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD6.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
  public void testCD7(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD7.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD7.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
  public void testCD8(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD8.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD8.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
  public void test11(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD11.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD12.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
  public void test21(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD21.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD22.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
  public void test31(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD31.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD32.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    TestHelper testHelper = new TestHelper(syntaxDiff);
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
  //Syntax Diff Tests

  public static final String dir = "src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/";
  public static final String pathDir = "src/cddifftest/resources/validation/Performance/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;


  @Test
  public void testSyntax1() {
    parseModels("Source1.cd", "Target1.cd");

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    //System.out.println(syntaxDiff.printSrcCD());
    //System.out.println(syntaxDiff.printTgtCD());
    //System.out.println(syntaxDiff.getBaseDiff());
    //System.out.println("----------------------------");
    //System.out.println(syntaxDiff.printOnlyAdded());
    //System.out.println(syntaxDiff.getChangedTypes());
    //System.out.println("----------------------------");
    //System.out.println(syntaxDiff.printOnlyDeleted());
    //System.out.println("----------------------------");
    System.out.println(syntaxDiff.printDiff());
    //System.out.println("----------------------------");
  }

  @Test
  public void testSyntax2() {
    parseModels("Source2.cd", "Target2.cd");

    CDDiffUtil.refreshSymbolTable(src);
    CDDiffUtil.refreshSymbolTable(tgt);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    System.out.println(syntaxDiff.printOnlyChanged());
  }

  @Test
  public void testSyntax3() {
    parseMaxModels("10A.cd", "10B.cd");

    CDDiffUtil.refreshSymbolTable(src);
    CDDiffUtil.refreshSymbolTable(tgt);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    System.out.println(syntaxDiff.printDiff());
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
        fail("Could not parse CDs.");
      }

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  public void parseMaxModels(String concrete, String ref) {
    try {
      Optional<ASTCDCompilationUnit> src =
        CD4CodeMill.parser().parseCDCompilationUnit(pathDir + concrete);
      Optional<ASTCDCompilationUnit> tgt = CD4CodeMill.parser().parseCDCompilationUnit(pathDir + ref);
      if (src.isPresent() && tgt.isPresent()) {
        CD4CodeMill.scopesGenitorDelegator().createFromAST(src.get());
        CD4CodeMill.scopesGenitorDelegator().createFromAST(tgt.get());
        src.get().accept(new CD4CodeSymbolTableCompleter(src.get()).getTraverser());
        tgt.get().accept(new CD4CodeSymbolTableCompleter(tgt.get()).getTraverser());
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
