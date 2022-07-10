package de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff;

import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.syntax2semdiff.cd2cdwrapper.CD2CDWrapperGenerator;
import de.monticore.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDSyntaxDiff;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CDWrapper2CDSyntaxDiffGeneratorTest extends CDDiffTestBasis {

  public CDSyntaxDiff generateCDSyntaxDiffTemp(String folder, String cd1Name, String cd2Name) {
    ASTCDCompilationUnit cd1 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/CDSyntaxDiff/" + folder + "/" + cd1Name);

    ASTCDCompilationUnit cd2 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/CDSyntaxDiff/" + folder + "/" + cd2Name);
    CD2CDWrapperGenerator cd1Generator = new CD2CDWrapperGenerator();
    CD2CDWrapperGenerator cd2Generator = new CD2CDWrapperGenerator();
    CDWrapper cdw1 = cd1Generator.generateCDWrapper(cd1, CDSemantics.SIMPLE_CLOSED_WORLD);
    CDWrapper cdw2 = cd2Generator.generateCDWrapper(cd2, CDSemantics.SIMPLE_CLOSED_WORLD);
    CDWrapper2CDSyntaxDiffGenerator CDW2CDDGenerator = new CDWrapper2CDSyntaxDiffGenerator();
    return CDW2CDDGenerator.generateCDSyntaxDiff(cdw1,cdw2);
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class", "Class1B.cd","Class1A.cd");
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.EDITED
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class", "Class1C.cd","Class1A.cd");
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.EDITED
        && e.getName(false).split("_")[1].contains("B")
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class", "Class1D.cd","Class1A.cd");
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.DELETED
        && e.getName(false).split("_")[1].contains("A")
        && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.EDITED
        && e.getName(false).split("_")[1].contains("B")
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class", "Class1E.cd","Class1A.cd");
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.DELETED
        && e.getName(false).split("_")[1].contains("A")
        && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.EDITED
        && e.getName(false).split("_")[1].contains("B")
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class", "Class1C.cd","Class1F.cd");
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.SUBSET
        && e.getName(false).split("_")[1].contains("B")));
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class", "Class1C.cd","Class1C.cd");
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.ORIGINAL
        && e.getName(false).split("_")[1].contains("A")));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDTypeDiffCategory.ORIGINAL
        && e.getName(false).split("_")[1].contains("B")));
  }

  @Test
  public void testClass4NoDiffInAssoc() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class", "Class2A.cd","Class2B.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Direction1A.cd","Direction1A-duplicate.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.ORIGINAL
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] B -> A [*]
   */
  @Test
  public void testAssociation4LeftRightClassExchanged() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Direction1A.cd","Direction1B.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.DIRECTION_CHANGED_BUT_SAME_MEANING
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A <-> B [*]
   */
  @Test
  public void testAssociation4DirectionChanged() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Direction1A.cd","Direction1C.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.DIRECTION_CHANGED
        && e.getWhichPartDiff().get() == CDSyntaxDiff.WhichPartDiff.DIRECTION));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A -- B [*]
   */
  @Test
  public void testAssociation4DirectionSubset() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Direction1A.cd","Direction1D.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.DIRECTION_SUBSET
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A (work) <- B [*]
   */
  @Test
  public void testAssociation4Deleted1() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Direction1A.cd","Direction1E.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithoutDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.DELETED
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] B -> (work) A [*]
   */
  @Test
  public void testAssociation4Deleted2() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Direction1A.cd","Direction1F.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithoutDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.DELETED
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [0..1]
   * CD2: [*] A <- B [0..1]
   *      [1] A -> B [*]
   */
  @Test
  public void testAssociation4Duplicate1() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Direction1A.cd","Direction1G.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.DIRECTION_CHANGED
        && e.getWhichPartDiff().get() == CDSyntaxDiff.WhichPartDiff.DIRECTION));
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.CARDINALITY_CHANGED
        && e.getWhichPartDiff().get() == CDSyntaxDiff.WhichPartDiff.LEFT_CARDINALITY
        && e.getCDDiffLeftClassCardinalityResult().get() == CDSyntaxDiff.CDAssociationDiffCardinality.ZERO_AND_TWO_TO_MORE));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [0..1]
   *      [1] A -> B [*]
   * CD2: [*] A <- B [0..1]
   */
  @Test
  public void testAssociation4Duplicate2() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Direction1G.cd","Direction1A.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.DIRECTION_CHANGED
        && e.getWhichPartDiff().get() == CDSyntaxDiff.WhichPartDiff.DIRECTION));
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.CARDINALITY_CHANGED
            && e.getWhichPartDiff().get() == CDSyntaxDiff.WhichPartDiff.RIGHT_CARDINALITY
            && e.getCDDiffRightClassCardinalityResult().get() == CDSyntaxDiff.CDAssociationDiffCardinality.TWO_TO_MORE));
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Cardinality1A.cd","Cardinality1A.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.ORIGINAL
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Cardinality
   * CD1: [1] A <- B [1..*]
   * CD2: [1] A <- B [*]
   */
  @Test
  public void testAssociation4CardinalitySubset() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Cardinality1A.cd","Cardinality1B.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.CARDINALITY_SUBSET
        && !e.getWhichPartDiff().isPresent()));
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.ORIGINAL
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Cardinality
   * CD1: [*] A <- B [1..*]
   * CD2: [1] A <- B [1]
   */
  @Test
  public void testAssociation4CardinalityChanged() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association", "Cardinality1C.cd","Cardinality1D.cd");
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.CARDINALITY_CHANGED
        && e.getWhichPartDiff().get() == CDSyntaxDiff.WhichPartDiff.LEFT_CARDINALITY
        && e.getCDDiffLeftClassCardinalityResult().get() == CDSyntaxDiff.CDAssociationDiffCardinality.ZERO_AND_TWO_TO_MORE));
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDSyntaxDiff.CDAssociationDiffCategory.CARDINALITY_CHANGED
        && e.getWhichPartDiff().get() == CDSyntaxDiff.WhichPartDiff.RIGHT_CARDINALITY
        && e.getCDDiffRightClassCardinalityResult().get() == CDSyntaxDiff.CDAssociationDiffCardinality.TWO_TO_MORE));
  }
}
