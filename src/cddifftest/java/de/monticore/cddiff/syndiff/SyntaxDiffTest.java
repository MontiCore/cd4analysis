package de.monticore.cddiff.syndiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.getConnectedClasses;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class SyntaxDiffTest extends CDDiffTestBasis {
  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }
  @Test
  public void ini(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classC));
    System.out.println("------------");
    for (AssocStruct astcdClass : syntaxDiff.getHelper().getSrcMap().get(classC)){
      System.out.print(getConnectedClasses(astcdClass.getUnmodifiedAssoc(), compilationUnitNew).a.getName() + "" + getConnectedClasses(astcdClass.getUnmodifiedAssoc(), compilationUnitNew).b.getName());
    }
    //System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  //Test for Bad overlapping
  //@Test
  public void testCD1(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD1.cd");

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    for (AssocStruct assocStruct : syntaxDiff.getHelper().getSrcMap().get(classC)){
      System.out.println(assocStruct.getAssociation().getLeft().getCDRole().getName());
    }
    System.out.println("------------");
    for (ASTCDClass astcdClass : syntaxDiff.getHelper().getNotInstanClassesSrc()){
      System.out.print(astcdClass.getName());
    }
    //System.out.println(syntaxDiff.getHelper().getNotInstanClassesSrc());
    System.out.println("------------");
    System.out.println(syntaxDiff.getHelper().getNotInstanClassesTgt());
  }

  //@Test
  public void testCD2(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD2.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD2.cd");

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    for (AssocStruct assocStruct : syntaxDiff.helper.getSrcMap().get(classC)){
      System.out.println(getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).a.getName() + " " + getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).b.getName());
    }
    System.out.println("------------");
    for (ASTCDClass astcdClass : syntaxDiff.helper.getNotInstanClassesSrc()){
      System.out.println(astcdClass.getName());
    }
    System.out.println("------------");
  }

  @Test
  public void testCD3(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD3.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD3.cd");

    ASTCDClass classC = CDTestHelper.getClass("C", compilationUnitNew.getCDDefinition());
    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classC).size());
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classA).size());
    for (AssocStruct assocStruct : syntaxDiff.helper.getSrcMap().get(classC)){
      System.out.println(getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).a.getName() + " " + getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).b.getName());
    }
    System.out.println("------------");
    for (ASTCDClass astcdClass : syntaxDiff.helper.getNotInstanClassesSrc()){
      System.out.println(astcdClass.getName());
    }
    System.out.println("------------");
  }

  @Test
  public void testCD4(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD4.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD4.cd");

    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    System.out.println(syntaxDiff.getHelper().getSrcMap().get(classA));
    for (AssocStruct assocStruct : syntaxDiff.helper.getSrcMap().get(classA)){
      System.out.println(getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).a.getName() + " " + getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).b.getName());
    }
    System.out.println("------------");
    for (ASTCDClass astcdClass : syntaxDiff.helper.getNotInstanClassesSrc()){
      System.out.println(astcdClass.getName());
    }
    System.out.println("------------");
  }

  @Test
  public void testCD5(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD5.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD5.cd");

    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    for (AssocStruct assocStruct : syntaxDiff.helper.getSrcMap().get(classA)){
      System.out.println(getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).a.getName() + " " + getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).b.getName());
    }
    System.out.println("------------");
    for (ASTCDClass astcdClass : syntaxDiff.helper.getNotInstanClassesSrc()){
      System.out.println(astcdClass.getName());
    }
    System.out.println("------------");
  }

  @Test
  public void testCD6(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD6.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD6.cd");

    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    for (AssocStruct assocStruct : syntaxDiff.helper.getSrcMap().get(classA)){
      System.out.println(getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).a.getName() + " " + getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).b.getName());
    }
    System.out.println("------------");
    for (ASTCDClass astcdClass : syntaxDiff.helper.getNotInstanClassesSrc()){
      System.out.println(astcdClass.getName());
    }
    System.out.println("------------");
  }

  @Test
  public void testCD7(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD7.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD7.cd");

    ASTCDClass classK = CDTestHelper.getClass("K", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    for (AssocStruct assocStruct : syntaxDiff.helper.getSrcMap().get(classK)){
      System.out.println(getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).a.getName() + " " + getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).b.getName());
    }
    System.out.println("------------");
    for (ASTCDClass astcdClass : syntaxDiff.helper.getNotInstanClassesSrc()){
      System.out.println(astcdClass.getName());
    }
    System.out.println("------------");
  }

  @Test
  public void testCD8(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD8.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD8.cd");

    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
//    for (AssocStruct assocStruct : syntaxDiff.helper.getSrcMap().get(classA)){
//      System.out.println(getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).a.getName() + " " + getConnectedClasses(assocStruct.getAssociation(), compilationUnitNew).b.getName());
//      System.out.println(assocStruct.getAssociation().getLeft().getCDRole().getName() + " " + assocStruct.getAssociation().getRight().getCDRole().getName());
//      System.out.println(assocStruct.getAssociation().getCDAssocDir());
//    }
//    System.out.println("------------");
//    for (ASTCDClass astcdClass : syntaxDiff.helper.getNotInstanClassesSrc()){
//      System.out.println(astcdClass.getName());
//    }
//    System.out.println("------------");
    for (AssocStruct assocStruct : syntaxDiff.helper.getSrcMap().get(classA)){
      System.out.println(assocStruct.isSuperAssoc());
      System.out.println(assocStruct.getAssociation().getLeft().getCDRole().getName() + " " + assocStruct.getAssociation().getRight().getCDRole().getName());
      System.out.println(assocStruct.getAssociation().getCDAssocDir());
    }
//    System.out.println(CDInheritanceHelper.isSuperOf("A", "A", compilationUnitNew));
  }

  @Test
  public void test11(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD11.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD12.cd");

    ASTCDClass classD = CDTestHelper.getClass("D", compilationUnitNew.getCDDefinition());
    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    assert classD != null;
    boolean isadded = syntaxDiff.isSupClass(classD);
    boolean isClassAdded = syntaxDiff.isInheritanceAdded(classD, classA);

    Assert.assertFalse(isadded);
    Assert.assertFalse(isClassAdded);
  }

  @Test
  public void test21(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD21.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD22.cd");

    ASTCDClass classD = CDTestHelper.getClass("D", compilationUnitOld.getCDDefinition());
    ASTCDClass classA = CDTestHelper.getClass("A", compilationUnitNew.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    boolean isClassDeleted = syntaxDiff.isInheritanceDeleted(classD, classA);

    Assert.assertFalse(isClassDeleted);
  }

  @Test
  public void test31(){
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD31.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/CD32.cd");

    ASTCDClass classD1 = CDTestHelper.getClass("D", compilationUnitNew.getCDDefinition());
    ASTCDClass classD2 = CDTestHelper.getClass("D", compilationUnitOld.getCDDefinition());
    ASTCDAssociation associationNew = CDTestHelper.getAssociation(classD1, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation associationOld = CDTestHelper.getAssociation(classD2, "r", compilationUnitOld.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();
    syntaxDiff.findOverlappingAssocs();
    assert associationNew != null;
    boolean isAssocAdded = syntaxDiff.isAssocAdded(associationNew);
    assert associationOld != null;
    ASTCDClass isAssocDeleted = syntaxDiff.isAssocDeleted(associationOld, classD2);

    Assert.assertTrue(isAssocAdded);
    Assert.assertNull(isAssocDeleted);
  }

  /*--------------------------------------------------------------------*/
  //Syntax Diff Tests

  public static final String dir = "src/cddifftest/resources/de/monticore/cddiff/syndiff/SyntaxDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;


  @Test
  public void testSyntax1() {
    parseModels("Source1.cd", "Target1.cd");
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    //System.out.println(syntaxDiff.printSrcCD());
    //System.out.println(syntaxDiff.printTgtCD());
    //System.out.println(syntaxDiff.getBaseDiff());
    System.out.println("----------------------------");
    System.out.println(syntaxDiff.printOnlyAdded());
    //System.out.println(syntaxDiff.getChangedTypes());
    System.out.println("----------------------------");
    System.out.println(syntaxDiff.printOnlyDeleted());
    System.out.println("----------------------------");
    System.out.println(syntaxDiff.printDiff());
    System.out.println("----------------------------");
  }

  @Test
  public void testSyntax2() {
    parseModels("Source2.cd", "Target2.cd");

    CDDiffUtil.refreshSymbolTable(src);
    CDDiffUtil.refreshSymbolTable(tgt);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    System.out.println(syntaxDiff.printOnlyChanged());
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
}
