package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff;

import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CD2CDWrapperGenerator;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CDWrapper2CDSyntaxDiffGeneratorTest extends CDDiffTestBasis {

  public CDSyntaxDiff generateCDSyntaxDiffTemp(String folder, String cd1Name, String cd2Name, CDSemantics cdSemantics) {
    ASTCDCompilationUnit cd1 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/CDSyntaxDiff/" + folder + "/" + cd1Name);

    ASTCDCompilationUnit cd2 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/CDSyntaxDiff/" + folder + "/" + cd2Name);
    CD2CDWrapperGenerator cd1Generator = new CD2CDWrapperGenerator();
    CD2CDWrapperGenerator cd2Generator = new CD2CDWrapperGenerator();
    CDWrapper cdw1 = cd1Generator.generateCDWrapper(cd1, CDSemantics.SIMPLE_CLOSED_WORLD);
    CDWrapper cdw2 = cd2Generator.generateCDWrapper(cd2, CDSemantics.SIMPLE_CLOSED_WORLD);
    CDWrapper2CDSyntaxDiffGenerator CDW2CDDGenerator = new CDWrapper2CDSyntaxDiffGenerator();
    return CDW2CDDGenerator.generateCDSyntaxDiff(cdw1, cdw2, cdSemantics);
  }

  public CDSyntaxDiff generateCDSyntaxDiffTemp(String cd1Path, String cd2Path, CDSemantics cdSemantics) {
    ASTCDCompilationUnit cd1 = parseModel(cd1Path);

    ASTCDCompilationUnit cd2 = parseModel(cd2Path);
    CD2CDWrapperGenerator cd1Generator = new CD2CDWrapperGenerator();
    CD2CDWrapperGenerator cd2Generator = new CD2CDWrapperGenerator();
    CDWrapper cdw1 = cd1Generator.generateCDWrapper(cd1, CDSemantics.SIMPLE_CLOSED_WORLD);
    CDWrapper cdw2 = cd2Generator.generateCDWrapper(cd2, CDSemantics.SIMPLE_CLOSED_WORLD);
    CDWrapper2CDSyntaxDiffGenerator CDW2CDDGenerator = new CDWrapper2CDSyntaxDiffGenerator();
    return CDW2CDDGenerator.generateCDSyntaxDiff(cdw1, cdw2, cdSemantics);
  }

  /********************************************************************
   *********************    Start for Class    ************************
   ********************   simple closed world   ***********************
   *******************************************************************/

  /**
   * Test for Enum Class
   * CD1: enum E {e1, e2, e3;}
   * CD2: enum E {e1, e2;}
   */
  @Test
  public void testClass4Enum() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class",
        "Class1B.cd","Class1A.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.EDITED
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class",
        "Class1C.cd","Class1A.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.EDITED
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class",
        "Class1D.cd","Class1A.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
        && e.getName(false).split("_")[1].contains("A")
        && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.EDITED
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class",
        "Class1E.cd","Class1A.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
        && e.getName(false).split("_")[1].contains("A")
        && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.EDITED
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class",
        "Class1C.cd","Class1F.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.SUBSET
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class",
        "Class1C.cd","Class1C.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.ORIGINAL
        && e.getName(false).split("_")[1].contains("A")));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.ORIGINAL
        && e.getName(false).split("_")[1].contains("B")));
  }

  @Test
  public void testClass4NoDiffInAssoc() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class",
        "Class2A.cd","Class2B.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
  }

  /********************************************************************
   *******************    Start for Direction    **********************
   ********************   simple closed world   ***********************
   *******************************************************************/

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A <- B [*]
   */
  @Test
  public void testAssociation4DirectionOriginal() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Direction1A.cd","Direction1A-duplicate.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.ORIGINAL
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] B -> A [*]
   */
  @Test
  public void testAssociation4LeftRightClassExchanged() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Direction1A.cd","Direction1B.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.DIRECTION_CHANGED_BUT_SAME_MEANING
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A <-> B [*]
   */
  @Test
  public void testAssociation4DirectionChanged() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Direction1A.cd","Direction1C.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.DIRECTION_CHANGED
        && e.getWhichPartDiff().get() == WhichPartDiff.DIRECTION));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A -- B [*]
   */
  @Test
  public void testAssociation4DirectionSubset() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Direction1A.cd","Direction1D.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.DIRECTION_SUBSET
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] A (work) <- B [*]
   */
  @Test
  public void testAssociation4Deleted1() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Direction1A.cd","Direction1E.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithoutDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.DELETED
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [*]
   * CD2: [*] B -> (work) A [*]
   */
  @Test
  public void testAssociation4Deleted2() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Direction1A.cd","Direction1F.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithoutDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.DELETED
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
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Direction1A.cd","Direction1G.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.DIRECTION_CHANGED
        && e.getWhichPartDiff().get() == WhichPartDiff.DIRECTION));
  }

  /**
   * Test for Direction
   * CD1: [*] A <- B [0..1]
   *      [1] A -> B [*]
   * CD2: [*] A <- B [0..1]
   */
  @Test
  public void testAssociation4Duplicate2() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Direction1G.cd","Direction1A.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.DIRECTION_CHANGED
        && e.getWhichPartDiff().get() == WhichPartDiff.DIRECTION));
  }

  /********************************************************************
   *******************    Start for Cardinality   *********************
   ********************   simple closed world   ***********************
   *******************************************************************/

  /**
   * Test for Cardinality
   * CD1: [1] A <- B [1..*]
   * CD2: [1] A <- B [1..*]
   */
  @Test
  public void testAssociation4CardinalityOriginal() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Cardinality1A.cd","Cardinality1A.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.ORIGINAL
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Cardinality
   * CD1: [1] A <- B [1..*]
   * CD2: [1] A <- B [*]
   */
  @Test
  public void testAssociation4CardinalitySubset() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Cardinality1A.cd","Cardinality1B.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 0);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.CARDINALITY_SUBSET
        && !e.getWhichPartDiff().isPresent()));
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithoutDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.ORIGINAL
        && !e.getWhichPartDiff().isPresent()));
  }

  /**
   * Test for Cardinality
   * CD1: [*] A <- B [1..*]
   * CD2: [1] A <- B [1]
   */
  @Test
  public void testAssociation4CardinalityChanged() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Cardinality1C.cd","Cardinality1D.cd", CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.CARDINALITY_CHANGED
        && e.getWhichPartDiff().get() == WhichPartDiff.LEFT_CARDINALITY
        && e.getCDDiffLeftClassCardinalityResult().get() == CDAssociationDiffCardinality.ZERO_AND_TWO_TO_MORE));
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
      .stream()
      .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.CARDINALITY_CHANGED
        && e.getWhichPartDiff().get() == WhichPartDiff.RIGHT_CARDINALITY
        && e.getCDDiffRightClassCardinalityResult().get() == CDAssociationDiffCardinality.TWO_TO_MORE));
  }

  /********************************************************************
   *********************   Start for Conflict   ***********************
   ********************   simple closed world   ***********************
   *******************************************************************/
  @Test
  public void testAssociation4Conflict1() {
    String cd1Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2A.cd";
    String cd2Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2B.cd";
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp(cd1Path, cd2Path, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.CONFLICTING
            && !e.getWhichPartDiff().isPresent()));
  }

  @Test
  public void testAssociation4Conflict1Inheritance() {
    String cd1Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2A_inheritance.cd";
    String cd2Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2B_inheritance.cd";
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp(cd1Path, cd2Path, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.CONFLICTING
            && !e.getWhichPartDiff().isPresent()));
  }

  @Test
  public void testAssociation4Conflict2() {
    String cd1Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2C.cd";
    String cd2Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2D.cd";
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp(cd1Path, cd2Path, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.CONFLICTING
            && !e.getWhichPartDiff().isPresent()));
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
            && e.getName(false).split("_")[1].contains("A")));
  }

  @Test
  public void testAssociation4Conflict2Reverse() {
    String cd1Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2C.cd";
    String cd2Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2D_reverse.cd";
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp(cd1Path, cd2Path, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.CONFLICTING
            && !e.getWhichPartDiff().isPresent()));
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
            && e.getName(false).split("_")[1].contains("A")));
  }

  @Test
  public void testAssociation4Conflict3() {
    String cd1Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2E.cd";
    String cd2Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2F.cd";
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp(cd1Path, cd2Path, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.CONFLICTING
            && !e.getWhichPartDiff().isPresent()));
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 3);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
            && e.getName(false).split("_")[1].contains("A")));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
            && e.getName(false).split("_")[1].contains("B")));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
            && e.getName(false).split("_")[1].contains("C")));
  }

  @Test
  public void testAssociation4Conflict4() {
    String cd1Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2E.cd";
    String cd2Path = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Association/Association2H.cd";
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp(cd1Path, cd2Path, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.DELETED
            && !e.getWhichPartDiff().isPresent()));
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 3);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
            && e.getName(false).split("_")[1].contains("A")));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
            && e.getName(false).split("_")[1].contains("B")));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
            && e.getName(false).split("_")[1].contains("C")));
  }


  /********************************************************************
   *********************    Start for Class    ************************
   ******************* multi-instance closed world ********************
   *******************************************************************/

  @Test
  public void testClass4InheritInMultiInstance() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Class",
        "Class1D.cd","Class1G.cd", CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDTypeDiffResultQueueWithDiff().size(), 2);
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
            && e.getName(false).split("_")[1].contains("A")
            && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
    Assert.assertTrue(cg.getCDTypeDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDTypeDiffCategory.DELETED
            && e.getName(false).split("_")[1].contains("B")
            && e.getWhichAttributesDiff().get().containsAll(List.of("str", "date", "element"))));
  }

  /********************************************************************
   *******************   Start for Association   **********************
   ******************* multi-instance closed world ********************
   *******************************************************************/

  @Test
  public void testAssociation4InheritInMultiInstance() {
    CDSyntaxDiff cg = generateCDSyntaxDiffTemp("Association",
        "Association1A.cd","Association1B.cd", CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
    Assert.assertEquals(cg.getCDAssociationDiffResultQueueWithDiff().size(), 1);
    Assert.assertTrue(cg.getCDAssociationDiffResultQueueWithDiff()
        .stream()
        .anyMatch(e -> e.getCDDiffCategory() == CDAssociationDiffCategory.DELETED));
  }

}
