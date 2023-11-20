/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper;

import static de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CDWrapper4AssocHelper.*;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.*;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class CDWrapperGeneratorTest extends CDDiffTestBasis {

  public CDWrapper generateCDWrapperTemp(String folder, String cdName) {
    ASTCDCompilationUnit cd =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syntax2semdiff/CDWrapper/"
                + folder
                + "/"
                + cdName);
    CDWrapperGenerator cDWrapperGenerator = new CDWrapperGenerator();
    return cDWrapperGenerator.generateCDWrapper(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * Test for loading CD CD: enum E {...} interface I {...} abstract class A {...} class B1 {...}
   * class B2 {...} class C;
   */
  @Test
  public void testClass4LoadingCD() {
    CDWrapper dg = generateCDWrapperTemp("Class", "Class1.cd");
    Assert.assertTrue(dg.getCDTypeWrapperGroup().containsKey("CDWrapperEnum_E"));
    Assert.assertTrue(dg.getCDTypeWrapperGroup().containsKey("CDWrapperInterface_I"));
    Assert.assertTrue(dg.getCDTypeWrapperGroup().containsKey("CDWrapperAbstractClass_A"));
    Assert.assertTrue(dg.getCDTypeWrapperGroup().containsKey("CDWrapperClass_B1"));
    Assert.assertTrue(dg.getCDTypeWrapperGroup().containsKey("CDWrapperClass_B2"));
    Assert.assertTrue(dg.getCDTypeWrapperGroup().containsKey("CDWrapperClass_C"));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperEnum_E")
            .getAttributes()
            .keySet()
            .containsAll(Set.of("e1", "e2")));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperEnum_E")
            .getCDWrapperLink4EnumClass()
            .containsAll(Set.of("CDWrapperClass_B1")));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperInterface_I")
            .getAttributes()
            .keySet()
            .containsAll(Set.of("i")));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperAbstractClass_A")
            .getAttributes()
            .keySet()
            .containsAll(Set.of("a1")));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperClass_B1")
            .getAttributes()
            .keySet()
            .containsAll(Set.of("b1", "element")));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperClass_B2")
            .getAttributes()
            .keySet()
            .containsAll(Set.of("b2")));
    Assert.assertTrue(dg.getCDTypeWrapperGroup().get("CDWrapperClass_C").getAttributes().isEmpty());
  }

  /**
   * Test for interface inheritance CD: enum E {...} interface I {...} abstract class A implements
   * I{...} class B1 {...} class B2 {...} class C;
   */
  @Test
  public void testClass4InterfaceInheritance() {
    CDWrapper dg = generateCDWrapperTemp("Class", "Class2.cd");
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperAbstractClass_A")
            .getAttributes()
            .keySet()
            .containsAll(Set.of("a1", "i")));
  }

  /**
   * Test for abstract class inheritance CD: enum E {...} interface I {...} abstract class A
   * implements I{...} class B1 extends A {...} class B2 {...} class C;
   */
  @Test
  public void testClass4AbstractClassInheritance() {
    CDWrapper dg = generateCDWrapperTemp("Class", "Class3.cd");
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperClass_B1")
            .getAttributes()
            .keySet()
            .containsAll(Set.of("i", "a1", "b1", "element")));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperClass_B1")
            .getAttributes()
            .get("i")
            .equals("String"));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperClass_B1")
            .getAttributes()
            .get("b1")
            .equals("int"));
  }

  /**
   * Test for simple class inheritance CD: enum E {...} interface I {...} abstract class A
   * implements I{...} class B1 extends A {...} class B2 {...} class C extends B1;
   */
  @Test
  public void testClass4SimpleClassInheritance() {
    CDWrapper dg = generateCDWrapperTemp("Class", "Class4.cd");
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperClass_C")
            .getAttributes()
            .keySet()
            .containsAll(Set.of("i", "a1", "b1", "element")));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperClass_C")
            .getAttributes()
            .get("i")
            .equals("String"));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperClass_C")
            .getAttributes()
            .get("element")
            .equals("E"));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperEnum_E")
            .getCDWrapperLink4EnumClass()
            .containsAll(Set.of("CDWrapperClass_B1", "CDWrapperClass_C")));
  }

  /**
   * Test for simple class extends two class CD: enum E {...} interface I {...} abstract class A
   * implements I{...} class B1 extends A {...} class B2 {...} class C extends B1, B2;
   */
  @Test
  public void testClass4SimpleClass2Inheritance() {
    CDWrapper dg = generateCDWrapperTemp("Class", "Class5.cd");
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperClass_C")
            .getAttributes()
            .keySet()
            .containsAll(Set.of("i", "a1", "b1", "element", "b2")));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_C").getAttributes().get("b2").equals("int"));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperClass_C")
            .getSuperclasses()
            .containsAll(
                Set.of(
                    "CDWrapperInterface_I",
                    "CDWrapperAbstractClass_A",
                    "CDWrapperClass_B1",
                    "CDWrapperClass_B2",
                    "CDWrapperClass_C")));
  }

  /********************************************************************
   **************    Start for inheritance graph    *******************
   *******************************************************************/

  /**
   * Test for inheritance graph CD: enum E {...} interface I {...} abstract class A implements
   * I{...} class B1 extends A {...} class B2 {...} class C extends B1, B2;
   */
  @Test
  public void testClass4inheritanceGraph() {
    CDWrapper dg = generateCDWrapperTemp("Class", "Class5.cd");
    Assert.assertTrue(dg.getInheritanceGraph().nodes().contains("CDWrapperEnum_E"));
    Assert.assertTrue(
        dg.getInheritanceGraph()
            .nodes()
            .containsAll(
                Set.of(
                    "CDWrapperInterface_I",
                    "CDWrapperAbstractClass_A",
                    "CDWrapperClass_B1",
                    "CDWrapperClass_B2",
                    "CDWrapperClass_C")));
    MutableGraph<String> inheritanceG = GraphBuilder.directed().build();
    inheritanceG.putEdge("CDWrapperClass_C", "CDWrapperClass_B1");
    inheritanceG.putEdge("CDWrapperClass_C", "CDWrapperClass_B2");
    inheritanceG.putEdge("CDWrapperClass_B1", "CDWrapperAbstractClass_A");
    inheritanceG.putEdge("CDWrapperAbstractClass_A", "CDWrapperInterface_I");
    inheritanceG.addNode("CDWrapperEnum_E");
    Assert.assertTrue(dg.getInheritanceGraph().equals(inheritanceG));
  }

  /********************************************************************
   *******************    Start for Association   *********************
   *******************************************************************/

  /**
   * Test for simple association CD: enum E {...} interface I {...} abstract class A implements
   * I{...} class B1 extends A {...} class B2 {...} class C extends B1, B2; class D; [*] C (workOn)
   * -> (toDo) D [1..*]
   */
  @Test
  public void testAssociation4SimpleAssociation() {
    CDWrapper dg = generateCDWrapperTemp("Association", "Association1.cd");
    dg.getCDAssociationWrapperGroup()
        .forEach(
            (k, v) -> {
              Assert.assertTrue(
                  v.getOriginalElement()
                      .getLeftQualifiedName()
                      .getQName()
                      .equals(v.getCDWrapperLeftClass().getOriginalClassName()));
              Assert.assertTrue(
                  v.getOriginalElement()
                      .getRightQualifiedName()
                      .getQName()
                      .equals(v.getCDWrapperRightClass().getOriginalClassName()));
            });
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .keySet()
            .containsAll(Set.of("CDAssociationWrapper_C_workOn_LeftToRight_toDo_D")));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_C_workOn_LeftToRight_toDo_D")
                .getCDWrapperKind()
            == CDAssociationWrapperKind.CDWRAPPER_ASC);
  }

  /**
   * Test for inherited association CD: enum E {...} interface I {...} abstract class A implements
   * I{...} class B1 extends A {...} class B2 {...} class C extends B1, B2; class D; [*] C (workOn)
   * -> (toDo) D [1..*] [1] I -> D [1..*]
   */
  @Test
  public void testAssociation4InheritedAssociation() {
    CDWrapper dg = generateCDWrapperTemp("Association", "Association2.cd");
    dg.getCDAssociationWrapperGroup()
        .forEach(
            (k, v) -> {
              Assert.assertTrue(
                  v.getOriginalElement()
                      .getLeftQualifiedName()
                      .getQName()
                      .equals(v.getCDWrapperLeftClass().getOriginalClassName()));
              Assert.assertTrue(
                  v.getOriginalElement()
                      .getRightQualifiedName()
                      .getQName()
                      .equals(v.getCDWrapperRightClass().getOriginalClassName()));
            });
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .keySet()
            .containsAll(
                Set.of(
                    "CDAssociationWrapper_C_workOn_LeftToRight_toDo_D",
                    "CDAssociationWrapper_I_i_LeftToRight_d_D",
                    "CDAssociationWrapper_A_i_LeftToRight_d_D",
                    "CDAssociationWrapper_B1_i_LeftToRight_d_D",
                    "CDAssociationWrapper_C_i_LeftToRight_d_D")));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_C_workOn_LeftToRight_toDo_D")
                .getCDWrapperKind()
            == CDAssociationWrapperKind.CDWRAPPER_ASC);
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_I_i_LeftToRight_d_D")
                .getCDWrapperKind()
            == CDAssociationWrapperKind.CDWRAPPER_ASC);
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_A_i_LeftToRight_d_D")
                .getCDWrapperKind()
            == CDAssociationWrapperKind.CDWRAPPER_INHERIT_ASC);
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_B1_i_LeftToRight_d_D")
                .getCDWrapperKind()
            == CDAssociationWrapperKind.CDWRAPPER_INHERIT_ASC);
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_C_i_LeftToRight_d_D")
                .getCDWrapperKind()
            == CDAssociationWrapperKind.CDWRAPPER_INHERIT_ASC);
  }

  /**
   * Test for inherited association CD: enum E {...} interface I {...} abstract class A implements
   * I{...} class B1 extends A {...} class B2 {...} class C extends B1, B2; class D; class F extends
   * D; [*] C (workOn) -> (toDo) D [1..*] [1] I -> D [1..*]
   */
  @Test
  public void testAssociation4RefSetAssociation() {
    CDWrapper dg = generateCDWrapperTemp("Association", "Association3.cd");
    List<CDRefSetAssociationWrapper> list = dg.getRefSetAssociationList();
    Assert.assertEquals(list.size(), 2);
    Assert.assertTrue(
        list.stream()
            .anyMatch(
                e ->
                    e.getLeftRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("C"))
                        && e.getDirection().equals(CDAssociationWrapperDirection.LEFT_TO_RIGHT)
                        && e.getRightRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("F", "D"))));

    Assert.assertTrue(
        list.stream()
            .anyMatch(
                e ->
                    e.getLeftRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("I", "A", "B1", "C"))
                        && e.getDirection().equals(CDAssociationWrapperDirection.LEFT_TO_RIGHT)
                        && e.getRightRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("F", "D"))));
  }

  @Test
  public void testEditASTCDAssociationLeftSide() {
    CDWrapper dg = generateCDWrapperTemp("Association", "Association3.cd");
    ASTCDAssociation original =
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_I_i_LeftToRight_d_D")
            .getOriginalElement();
    CDTypeWrapper CDTypeWrapper = dg.getCDTypeWrapperGroup().get("CDWrapperClass_C");
    ASTCDAssociation edited = editASTCDAssociationLeftSideByCDTypeWrapper(original, CDTypeWrapper);
    Assert.assertTrue(
        edited.getLeftQualifiedName().getQName().equals(CDTypeWrapper.getOriginalClassName()));
  }

  @Test
  public void testEditASTCDAssociationRightSide() {
    CDWrapper dg = generateCDWrapperTemp("Association", "Association3.cd");
    ASTCDAssociation original =
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_I_i_LeftToRight_d_D")
            .getOriginalElement();
    CDTypeWrapper CDTypeWrapper = dg.getCDTypeWrapperGroup().get("CDWrapperClass_C");
    ASTCDAssociation edited = editASTCDAssociationRightSideByCDTypeWrapper(original, CDTypeWrapper);
    Assert.assertTrue(
        edited.getRightQualifiedName().getQName().equals(CDTypeWrapper.getOriginalClassName()));
  }

  /**
   * Test for overlap RefSetAssociation CD: class A; class A1 extends A; class B; class B1 extends
   * B;
   *
   * <p>association [1..*] A (workOn) -> (toDo) B [1]; association [1] A1 (workOn) -> (toDo) B1
   * [1..*];
   */
  @Test
  public void testAssociation4OverlapRefSetAssociation1() {
    CDWrapper dg = generateCDWrapperTemp("Association", "OverlapRefSetAssociation1.cd");
    List<CDRefSetAssociationWrapper> list = dg.getRefSetAssociationList();
    Assert.assertEquals(list.size(), 2);
    Assert.assertTrue(
        list.stream()
            .anyMatch(
                e ->
                    e.getLeftRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("A1"))
                        && e.getDirection().equals(CDAssociationWrapperDirection.LEFT_TO_RIGHT)
                        && e.getRightRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("B1"))));

    Assert.assertTrue(
        list.stream()
            .anyMatch(
                e ->
                    e.getLeftRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("A", "A1"))
                        && e.getDirection().equals(CDAssociationWrapperDirection.LEFT_TO_RIGHT)
                        && e.getRightRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("B", "B1"))));
  }

  /**
   * Test for overlap RefSetAssociation CD: class A; class A1 extends A; class B; class B1 extends
   * B;
   *
   * <p>association [1..*] A (workOn) -> (toDo) B [1]; association [1..*] B1 (toDo) <- (workOn) A1
   * [1];
   */
  @Test
  public void testAssociation4OverlapRefSetAssociation2() {
    CDWrapper dg = generateCDWrapperTemp("Association", "OverlapRefSetAssociation2.cd");
    List<CDRefSetAssociationWrapper> list = dg.getRefSetAssociationList();
    Assert.assertEquals(list.size(), 2);
    Assert.assertTrue(
        list.stream()
            .anyMatch(
                e ->
                    e.getLeftRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("B1"))
                        && e.getDirection().equals(CDAssociationWrapperDirection.RIGHT_TO_LEFT)
                        && e.getRightRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("A1"))));

    Assert.assertTrue(
        list.stream()
            .anyMatch(
                e ->
                    e.getLeftRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("A", "A1"))
                        && e.getDirection().equals(CDAssociationWrapperDirection.LEFT_TO_RIGHT)
                        && e.getRightRefSet().stream()
                            .map(CDTypeWrapper::getOriginalClassName)
                            .collect(Collectors.toSet())
                            .containsAll(Set.of("B", "B1"))));
  }

  /**
   * Test for duplicated association CD: class A; class B; association [*] A (a) <- (b) B [*];
   * association [*] A (a) <- (b) B [*];
   */
  @Test
  public void testAssociation4DuplicatedAssociation1() {
    CDWrapper dg = generateCDWrapperTemp("Association", "DuplicateAssociation1.cd");
    Assert.assertTrue(dg.getCDAssociationWrapperGroup().size() == 1);
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup().containsKey("CDAssociationWrapper_A_a_RightToLeft_b_B"));
  }

  /**
   * Test for duplicated association CD: class A; class B; association [0..1] A (a) <- (b) B [1..*];
   * association [1] B (b) -> (a) A [1..*];
   */
  @Test
  public void testAssociation4DuplicatedAssociation2() {
    CDWrapper dg = generateCDWrapperTemp("Association", "DuplicateAssociation2.cd");
    Assert.assertTrue(dg.getCDAssociationWrapperGroup().size() == 1);
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup().containsKey("CDAssociationWrapper_A_a_RightToLeft_b_B"));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_RightToLeft_b_B")
            .getCDWrapperLeftClassCardinality()
            .equals(CDAssociationWrapperCardinality.ONE));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_RightToLeft_b_B")
            .getCDWrapperRightClassCardinality()
            .equals(CDAssociationWrapperCardinality.ONE));
  }

  /**
   * Test for duplicated association CD: class A; class B; association [*] A (a) <- (b) B [*];
   * association [*] A (a) -> (b) B [1..*];
   */
  @Test
  public void testAssociation4DuplicatedAssociation3() {
    CDWrapper dg = generateCDWrapperTemp("Association", "DuplicateAssociation3.cd");
    Assert.assertTrue(dg.getCDAssociationWrapperGroup().size() == 2);
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup().containsKey("CDAssociationWrapper_A_a_RightToLeft_b_B"));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup().containsKey("CDAssociationWrapper_A_a_LeftToRight_b_B"));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_A_a_RightToLeft_b_B")
                .getCDWrapperLeftClassCardinality()
                .equals(CDAssociationWrapperCardinality.ONE)
            && dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_A_a_RightToLeft_b_B")
                .getCDWrapperRightClassCardinality()
                .equals(CDAssociationWrapperCardinality.MULTIPLE));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_A_a_LeftToRight_b_B")
                .getCDWrapperLeftClassCardinality()
                .equals(CDAssociationWrapperCardinality.OPTIONAL)
            && dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_A_a_LeftToRight_b_B")
                .getCDWrapperRightClassCardinality()
                .equals(CDAssociationWrapperCardinality.AT_LEAST_ONE));
  }

  /**
   * Test for duplicated association CD: class A; class B; association [1] A (a) <- (b) B [*];
   * association [0..1] A (a) -> (b) B [1..*]; association [*] A (a) <-> (b) B [*];
   */
  @Test
  public void testAssociation4DuplicatedAssociation4() {
    CDWrapper dg = generateCDWrapperTemp("Association", "DuplicateAssociation4.cd");
    Assert.assertTrue(dg.getCDAssociationWrapperGroup().size() == 1);
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .containsKey("CDAssociationWrapper_A_a_Bidirectional_b_B"));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_A_a_Bidirectional_b_B")
                .getCDWrapperLeftClassCardinality()
                .equals(CDAssociationWrapperCardinality.ONE)
            && dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_A_a_Bidirectional_b_B")
                .getCDWrapperRightClassCardinality()
                .equals(CDAssociationWrapperCardinality.AT_LEAST_ONE));
  }

  /** Test for no cardinality CD: class A; class B; association A (a) <-> (b) B [1]; */
  @Test
  public void testAssociation4NoCardinality() {
    CDWrapper dg = generateCDWrapperTemp("Association", "NoCardinality.cd");
    Assert.assertTrue(dg.getCDAssociationWrapperGroup().size() == 1);
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .containsKey("CDAssociationWrapper_A_a_Bidirectional_b_B"));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_A_a_Bidirectional_b_B")
                .getCDWrapperLeftClassCardinality()
                .equals(CDAssociationWrapperCardinality.MULTIPLE)
            && dg.getCDAssociationWrapperGroup()
                .get("CDAssociationWrapper_A_a_Bidirectional_b_B")
                .getCDWrapperRightClassCardinality()
                .equals(CDAssociationWrapperCardinality.ONE));
  }

  @Test
  public void testConflict1() {
    ASTCDCompilationUnit cd =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
                + "/Conflict/Association2B.cd");
    CDWrapperGenerator cDWrapperGenerator = new CDWrapperGenerator();
    CDWrapper dg = cDWrapperGenerator.generateCDWrapper(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_A").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_B").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_C").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_LeftToRight_r_B")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_LeftToRight_r_C")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
  }

  @Test
  public void testConflict1_Inheritance() {
    ASTCDCompilationUnit cd =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
                + "/Conflict/Association2B_inheritance.cd");
    CDWrapperGenerator cDWrapperGenerator = new CDWrapperGenerator();
    CDWrapper dg = cDWrapperGenerator.generateCDWrapper(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_A1").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_A2").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_B").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_C").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A1_a_LeftToRight_r_B")
            .getStatus()
            .equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A2_a_LeftToRight_r_B")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A2_a_LeftToRight_r_C")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
  }

  @Test
  public void testConflict2() {
    ASTCDCompilationUnit cd =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
                + "/Conflict/Association2D.cd");
    CDWrapperGenerator cDWrapperGenerator = new CDWrapperGenerator();
    CDWrapper dg = cDWrapperGenerator.generateCDWrapper(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_A").getStatus().equals(CDStatus.LOCKED));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_B").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_C").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_LeftToRight_r_B")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_LeftToRight_r_C")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
  }

  @Test
  public void testConflict2_Reverse() {
    ASTCDCompilationUnit cd =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
                + "/Conflict/Association2D_reverse.cd");
    CDWrapperGenerator cDWrapperGenerator = new CDWrapperGenerator();
    CDWrapper dg = cDWrapperGenerator.generateCDWrapper(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_A").getStatus().equals(CDStatus.LOCKED));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_B").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_C").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_LeftToRight_r_B")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_C_r_RightToLeft_a_A")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
  }

  @Test
  public void testConflict3() {
    ASTCDCompilationUnit cd =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
                + "/Conflict/Association2F.cd");
    CDWrapperGenerator cDWrapperGenerator = new CDWrapperGenerator();
    CDWrapper dg = cDWrapperGenerator.generateCDWrapper(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_A").getStatus().equals(CDStatus.LOCKED));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_B").getStatus().equals(CDStatus.LOCKED));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_C").getStatus().equals(CDStatus.LOCKED));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_LeftToRight_r_B")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_LeftToRight_r_C")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
  }

  @Test
  public void testConflict4() {
    ASTCDCompilationUnit cd =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
                + "/Conflict/Association2H.cd");
    CDWrapperGenerator cDWrapperGenerator = new CDWrapperGenerator();
    CDWrapper dg = cDWrapperGenerator.generateCDWrapper(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_A").getStatus().equals(CDStatus.LOCKED));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_B").getStatus().equals(CDStatus.LOCKED));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_C").getStatus().equals(CDStatus.LOCKED));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_Bidirectional_r_C")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_Bidirectional_r_B")
            .getStatus()
            .equals(CDStatus.CONFLICTING));
  }

  @Test
  public void testNoConflict() {
    ASTCDCompilationUnit cd =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
                + "/Conflict/Association2G.cd");
    CDWrapperGenerator cDWrapperGenerator = new CDWrapperGenerator();
    CDWrapper dg = cDWrapperGenerator.generateCDWrapper(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_A").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_B").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_C").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_RightToLeft_r_B")
            .getStatus()
            .equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_RightToLeft_r_C")
            .getStatus()
            .equals(CDStatus.OPEN));
  }

  @Test
  public void testAttributeConflict() {
    ASTCDCompilationUnit cd =
        parseModel(
            "src/test/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
                + "/Conflict/Class2A.cd");
    CDWrapperGenerator cDWrapperGenerator = new CDWrapperGenerator();
    CDWrapper dg = cDWrapperGenerator.generateCDWrapper(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperInterface_I").getStatus().equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup()
            .get("CDWrapperAbstractClass_A")
            .getStatus()
            .equals(CDStatus.OPEN));
    Assert.assertTrue(
        dg.getCDTypeWrapperGroup().get("CDWrapperClass_B").getStatus().equals(CDStatus.LOCKED));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_B_a_LeftToRight_b_B")
            .getStatus()
            .equals(CDStatus.LOCKED));
    Assert.assertTrue(
        dg.getCDAssociationWrapperGroup()
            .get("CDAssociationWrapper_A_a_LeftToRight_b_B")
            .getStatus()
            .equals(CDStatus.LOCKED));
  }
}
