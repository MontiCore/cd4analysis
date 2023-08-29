package de.monticore.cddiff.syndiff;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.matcher.*;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;

public class AssocDiffTest extends CDDiffTestBasis {
  @BeforeEach
  public void setup() {
    Log.init();
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }
  @Test
  public void testCD10() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD101.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD102.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("Q", compilationUnitNew.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("Q", compilationUnitOld.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "r", compilationUnitOld.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld);
    // Invoke the method
    Pair<ASTCDAssociation, ASTCDClass> result = assocDiff.getChangedTgtClass();
    //List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> result2 = assocDiff.getCardDiff();
    System.out.print(result.b.getName());

    // Assert the result
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list = new ArrayList<>();
    //Assert.assertEquals(list, result2);
    Assert.assertNotNull(result);
  }

  @Test
  public void testCD5() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD51.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD52.cd");
    ASTCDClass astcdClass = CDTestHelper.getClass("S", compilationUnitNew.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("S", compilationUnitOld.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "r", compilationUnitOld.getCDDefinition());
    CDDiffUtil.refreshSymbolTable(compilationUnitNew);
    CDDiffUtil.refreshSymbolTable(compilationUnitOld);
    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(compilationUnitNew, compilationUnitOld);
    syntaxDiff.getHelper().setMaps();

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld);
    // Invoke the method
    boolean result = assocDiff.isDirectionChanged();
    //List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> result2 = assocDiff.getRoleDiff();


    // Assert the result
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> list = new ArrayList<>();
    Assert.assertTrue(result);
    Assert.assertNotNull(list);
  }

  @Test
  public void testCD7() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD71.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD72.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("Q", compilationUnitNew.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("Q", compilationUnitOld.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "r", compilationUnitOld.getCDDefinition());

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld);
    // Invoke the method
    //boolean result = assocDiff.changedTgtClass();
   // List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> result2 = assocDiff.getCardDiff();
   // List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> result = assocDiff.getRoleDiff();

    // Assert the result
    //Assert.assertFalse(result);
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> list = new ArrayList<>();
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list2 = new ArrayList<>();
   // Assert.assertNotEquals(list, result);
    //Assert.assertEquals(list2, result2);
  }

  @Test
  public void testCD8() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD81.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD82.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("Q", compilationUnitNew.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("Q", compilationUnitOld.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "r", compilationUnitOld.getCDDefinition());

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld);
    // Invoke the method
    //List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> result2 = assocDiff.getCardDiff();

    // Assert the result
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list = new ArrayList<>();
    //Assert.assertEquals(list, result2);
  }



  /*--------------------------------------------------------------------*/
  //Syntax Diff Tests

  public static final String dir = "src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/";
  protected ASTCDCompilationUnit tgt;
  protected ASTCDCompilationUnit src;
  @Test
  public void testAssoc1() {
    parseModels("Source1.cd", "Target1.cd");

    ASTCDAssociation assocNew = src.getCDDefinition().getCDAssociationsList().get(0);
    ASTCDAssociation assocOld = tgt.getCDDefinition().getCDAssociationsList().get(0);

    CDAssocDiff associationDiff = new CDAssocDiff(assocNew, assocOld);
    System.out.println(associationDiff.printSrcAssoc());
    System.out.println(associationDiff.printTgtAssoc());
    System.out.println(associationDiff.getBaseDiff());
    //System.out.println(associationDiff.getDiffTypesList());
  }

  @Test
  public void testAssoc2() {
    parseModels("Source2.cd", "Target2.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("Employee", src.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("Woman", tgt.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "consults", src.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "consults", tgt.getCDDefinition());

    CDAssocDiff associationDiff = new CDAssocDiff(assocNew, assocOld);
    System.out.println(associationDiff.printSrcAssoc());
    System.out.println(associationDiff.printTgtAssoc());
    System.out.println(associationDiff.getBaseDiff());
  }

  @Test
  public void testAssoc3() {
    parseModels("Source3.cd", "Target3.cd");

    ASTCDAssociation assocNew = src.getCDDefinition().getCDAssociationsList().get(0);
    ASTCDAssociation assocOld = tgt.getCDDefinition().getCDAssociationsList().get(0);

    CDDiffUtil.refreshSymbolTable(src);
    CDDiffUtil.refreshSymbolTable(tgt);

    CDSyntaxDiff syntaxDiff = new CDSyntaxDiff(src, tgt);
    System.out.println(syntaxDiff.printSrcCD());
    System.out.println(syntaxDiff.printTgtCD());
    //System.out.println(syntaxDiff.getBaseDiff());
    System.out.println("Matched Assocs");
    System.out.println(syntaxDiff.getMatchedAssocs());
    System.out.println("Matched Classes");
    System.out.println(syntaxDiff.getMatchedClasses());
    //System.out.println(associationDiff.getDiffTypesList());
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
