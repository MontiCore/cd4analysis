package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDSyntaxDiff;
import de.monticore.cddiff.syndiff.imp.CDTypeDiff;
import de.monticore.cddiff.syndiff.imp.ClassSide;
import edu.mit.csail.sdg.alloy4.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AssocDiffTest extends CDDiffTestBasis {
  //@Test
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

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld, false);
    // Invoke the method
    Pair<ASTCDAssociation, ASTCDClass> result = assocDiff.getChangedTgtClass();
    //List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> result2 = assocDiff.getCardDiff();
    System.out.print(result.b.getName());

    // Assert the result
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list = new ArrayList<>();
    //Assert.assertEquals(list, result2);
    Assert.assertNotNull(result);
  }

  //@Test
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

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld, false);
    // Invoke the method
    boolean result = assocDiff.isDirectionChanged();
    //List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> result2 = assocDiff.getRoleDiff();


    // Assert the result
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> list = new ArrayList<>();
    Assert.assertTrue(result);
    Assert.assertNotNull(list);
  }

  //@Test
  public void testCD7() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD71.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD72.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("Q", compilationUnitNew.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("Q", compilationUnitOld.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "r", compilationUnitOld.getCDDefinition());

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld, false);
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

  //@Test
  public void testCD8() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD81.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/AssocDiff/CD82.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("Q", compilationUnitNew.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("Q", compilationUnitOld.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "r", compilationUnitOld.getCDDefinition());

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld, true);
    // Invoke the method
    //List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> result2 = assocDiff.getCardDiff();

    // Assert the result
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list = new ArrayList<>();
    //Assert.assertEquals(list, result2);
  }
}
