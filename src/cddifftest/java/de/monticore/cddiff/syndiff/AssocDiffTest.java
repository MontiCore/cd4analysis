package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDTypeDiff;
import de.monticore.cddiff.syndiff.imp.ClassSide;
import edu.mit.csail.sdg.alloy4.Pair;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class AssocDiffTest extends CDDiffTestBasis {
  public void testCD10() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD101.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD102.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("Q", compilationUnitNew.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("Q", compilationUnitOld.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "r", compilationUnitOld.getCDDefinition());

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld, false);
    // Invoke the method
    //boolean result = assocDiff.changedTgtClass();
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> result2 = assocDiff.getCardDiff();

    // Assert the result
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list = new ArrayList<>();
    Assert.assertEquals(list, result2);
  }

  public void testCD5() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD51.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD52.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("S", compilationUnitNew.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("S", compilationUnitOld.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "r", compilationUnitOld.getCDDefinition());

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld, false);
    // Invoke the method
    boolean result = assocDiff.isDirectionChanged();
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> result2 = assocDiff.getRoleDiff();


    // Assert the result
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> list = new ArrayList<>();
    Assert.assertTrue(result);
    Assert.assertEquals(list, result2);
  }

  public void testCD7() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD51.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD52.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("Q", compilationUnitNew.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("Q", compilationUnitOld.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "r", compilationUnitOld.getCDDefinition());

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld, false);
    // Invoke the method
    //boolean result = assocDiff.changedTgtClass();
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> result2 = assocDiff.getCardDiff();
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> result = assocDiff.getRoleDiff();

    // Assert the result
    //Assert.assertFalse(result);
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> list = new ArrayList<>();
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list2 = new ArrayList<>();
    Assert.assertEquals(list, result);
    Assert.assertEquals(list2, result2);
  }

  public void testCD8() {
    ASTCDCompilationUnit compilationUnitNew = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD81.cd");
    ASTCDCompilationUnit compilationUnitOld = parseModel("src/cddifftest/resources/de/monticore/cddiff/syndiff/TypeDiff/CD82.cd");

    ASTCDClass astcdClass = CDTestHelper.getClass("Q", compilationUnitNew.getCDDefinition());
    ASTCDClass astcdClass1 = CDTestHelper.getClass("Q", compilationUnitOld.getCDDefinition());
    ASTCDAssociation assocNew = CDTestHelper.getAssociation(astcdClass, "r", compilationUnitNew.getCDDefinition());
    ASTCDAssociation assocOld = CDTestHelper.getAssociation(astcdClass1, "r", compilationUnitOld.getCDDefinition());

    CDAssocDiff assocDiff = new CDAssocDiff(assocNew, assocOld, true);
    // Invoke the method
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> result2 = assocDiff.getCardDiff();

    // Assert the result
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list = new ArrayList<>();
    Assert.assertEquals(list, result2);
  }
}
