package de.monticore.sydiff2semdiff.dg2cg;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.sydiff2semdiff.cd2dg.CD2DGGenerator;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompareGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DG2CGGeneratorTest extends CDDiffTestBasis {

  public CompareGroup generateCompareGroupTemp(String folder, String cd1Name, String cd2Name) {
    ASTCDCompilationUnit cd1 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/CompareGroup/" + folder + "/" + cd1Name);

    ASTCDCompilationUnit cd2 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/CompareGroup/" + folder + "/" + cd2Name);
    CD2DGGenerator cd1Generator = new CD2DGGenerator();
    CD2DGGenerator cd2Generator = new CD2DGGenerator();
    DifferentGroup dg1 = cd1Generator.generateDifferentGroup(cd1, DifferentGroup.DifferentGroupType.SINGLE_INSTANCE);
    DifferentGroup dg2 = cd2Generator.generateDifferentGroup(cd2, DifferentGroup.DifferentGroupType.SINGLE_INSTANCE);
    DG2CGGenerator dg2CGGenerator = new DG2CGGenerator();
    return dg2CGGenerator.generateCompareGroup(dg1,dg2);
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * Test for Enum Class
   * CD1: enum E {e1, e2, e3;}
   * CD2: enum E {e1, e2;}
   */
  @Test
  public void testClass4Enum() {
    CompareGroup cg = generateCompareGroupTemp("Class", "Class1B.cd","Class1A.cd");
    Assert.assertEquals(cg.getCompClassResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCompClassResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompClassCategory.EDITED
        && e.getWhichAttributesDiff().get().contains("e3")));
  }

  /**
   * Test for Simple Class
   * CD1: class A {...}
   *      class B extends A {}
   * CD2: class A {...}
   *      class B {}
   */
  @Test
  public void testClass4Inherit() {
    CompareGroup cg = generateCompareGroupTemp("Class", "Class1C.cd","Class1A.cd");
    Assert.assertEquals(cg.getCompClassResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCompClassResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompClassCategory.EDITED
        && e.getName().split("_")[1].contains("B")
        && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
  }

  /**
   * Test for Abstract Class
   * CD1: abstract class A {...}
   *      class B extends A {}
   * CD2: class A {...}
   *      class B {}
   */
  @Test
  public void testClass4AbstractInheritWithDeletedAndEdited() {
    CompareGroup cg = generateCompareGroupTemp("Class", "Class1D.cd","Class1A.cd");
    Assert.assertEquals(cg.getCompClassResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCompClassResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompClassCategory.DELETED
        && e.getName().split("_")[1].contains("A")
        && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
    Assert.assertTrue(cg.getCompClassResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompClassCategory.EDITED
        && e.getName().split("_")[1].contains("B")
        && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
  }

  /**
   * Test for Interface
   * CD1: interface A {...}
   *      class B extends A {}
   * CD2: class A {...}
   *      class B {}
   */
  @Test
  public void testClass4InterfaceInheritWithDeletedAndEdited() {
    CompareGroup cg = generateCompareGroupTemp("Class", "Class1E.cd","Class1A.cd");
    Assert.assertEquals(cg.getCompClassResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCompClassResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompClassCategory.DELETED
        && e.getName().split("_")[1].contains("A")
        && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
    Assert.assertTrue(cg.getCompClassResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompClassCategory.EDITED
        && e.getName().split("_")[1].contains("B")
        && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
  }

  /**
   * Test for Class Subset
   * CD1: class A {...}
   *      class B extends A {}
   * CD2: class A {...}
   *      class B extends A {...}
   */
  @Test
  public void testClass4Subset() {
    CompareGroup cg = generateCompareGroupTemp("Class", "Class1C.cd","Class1F.cd");
    Assert.assertEquals(cg.getCompClassResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCompClassResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompClassCategory.SUBSET
        && e.getName().split("_")[1].contains("B")));
  }

  /**
   * Test for Class Original
   * CD1: class A {...}
   *      class B extends A {}
   * CD2: class A {...}
   *      class B extends A {}
   */
  @Test
  public void testClass4Original() {
    CompareGroup cg = generateCompareGroupTemp("Class", "Class1C.cd","Class1C.cd");
    Assert.assertEquals(cg.getCompClassResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCompClassResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompClassCategory.ORIGINAL
        && e.getName().split("_")[1].contains("A")));
    Assert.assertTrue(cg.getCompClassResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompClassCategory.ORIGINAL
        && e.getName().split("_")[1].contains("B")));
  }

  /********************************************************************
   *******************    Start for Direction    **********************
   *******************************************************************/

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A <- B [*]
   */
  @Test
  public void testAssociation4DirectionOriginal() {
    CompareGroup cg = generateCompareGroupTemp("Association", "Direction1A.cd","Direction1A.cd");
    Assert.assertEquals(cg.getCompAssociationResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCompAssociationResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.ORIGINAL
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] B -> A [*]
   */
  @Test
  public void testAssociation4LeftRightClassExchanged() {
    CompareGroup cg = generateCompareGroupTemp("Association", "Direction1A.cd","Direction1B.cd");
    Assert.assertEquals(cg.getCompAssociationResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCompAssociationResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.DIRECTION_CHANGED_BUT_SAME_MEANING
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A <-> B [*]
   */
  @Test
  public void testAssociation4DirectionChanged() {
    CompareGroup cg = generateCompareGroupTemp("Association", "Direction1A.cd","Direction1C.cd");
    Assert.assertEquals(cg.getCompAssociationResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCompAssociationResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.DIRECTION_CHANGED
        && e.getWhichPartDiff().get() == CompareGroup.WhichPartDiff.DIRECTION));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A -- B [*]
   */
  @Test
  public void testAssociation4DirectionSubset() {
    CompareGroup cg = generateCompareGroupTemp("Association", "Direction1A.cd","Direction1D.cd");
    Assert.assertEquals(cg.getCompAssociationResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCompAssociationResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.DIRECTION_SUBSET
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A (work) <- B [*]
   */
  @Test
  public void testAssociation4Deleted1() {
    CompareGroup cg = generateCompareGroupTemp("Association", "Direction1A.cd","Direction1E.cd");
    Assert.assertEquals(cg.getCompAssociationResultQueueWithDiff().size(), 1);
    Assert.assertEquals(cg.getCompAssociationResultQueueWithoutDiff().size(), 0);
    Assert.assertTrue(cg.getCompAssociationResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.DELETED
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] B -> (work) A [*]
   */
  @Test
  public void testAssociation4Deleted2() {
    CompareGroup cg = generateCompareGroupTemp("Association", "Direction1A.cd","Direction1F.cd");
    Assert.assertEquals(cg.getCompAssociationResultQueueWithDiff().size(), 1);
    Assert.assertEquals(cg.getCompAssociationResultQueueWithoutDiff().size(), 0);
    Assert.assertTrue(cg.getCompAssociationResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.DELETED
        && !e.getWhichPartDiff().isPresent()));
  }

  /********************************************************************
   *******************    Start for Cardinality   *********************
   *******************************************************************/

  /**
   * Test for Cardinality
   * CD1: [1] A <- B [1..*]
   * CD2: [1] A <- B [1..*]
   */
  @Test
  public void testAssociation4CardinalityOriginal() {
    CompareGroup cg = generateCompareGroupTemp("Association", "Cardinality1A.cd","Cardinality1A.cd");
    Assert.assertEquals(cg.getCompAssociationResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCompAssociationResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.ORIGINAL
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Cardinality
   * CD1: [1] A <- B [1..*]
   * CD2: [1] A <- B [*]
   */
  @Test
  public void testAssociation4CardinalitySubset() {
    CompareGroup cg = generateCompareGroupTemp("Association", "Cardinality1A.cd","Cardinality1B.cd");
    Assert.assertEquals(cg.getCompAssociationResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCompAssociationResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.CARDINALITY_SUBSET
        && !e.getWhichPartDiff().isPresent()
      ));
    Assert.assertTrue(cg.getCompAssociationResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.ORIGINAL
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Cardinality
   * CD1: [*] A <- B [1..*]
   * CD2: [1] A <- B [1]
   */
  @Test
  public void testAssociation4CardinalityChanged() {
    CompareGroup cg = generateCompareGroupTemp("Association", "Cardinality1C.cd","Cardinality1D.cd");
    Assert.assertEquals(cg.getCompAssociationResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCompAssociationResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.CARDINALITY_CHANGED
        && e.getWhichPartDiff().get() == CompareGroup.WhichPartDiff.LEFT_CARDINALITY
        && e.getCompLeftClassCardinalityResult().get() == CompareGroup.CompAssociationCardinality.ZERO_AND_TWO_TO_MORE));
    Assert.assertTrue(cg.getCompAssociationResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCompCategory() == CompareGroup.CompAssociationCategory.CARDINALITY_CHANGED
        && e.getWhichPartDiff().get() == CompareGroup.WhichPartDiff.RIGHT_CARDINALITY
        && e.getCompRightClassCardinalityResult().get() == CompareGroup.CompAssociationCardinality.TWO_TO_MORE));
  }
}
