package de.monticore.sydiff2semdiff.cd2dg;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffClass;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DiffRefSetAssociation;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.sydiff2semdiff.cd2dg.DifferentHelper.*;

public class CD2DGGeneratorTest extends CDDiffTestBasis {

  public DifferentGroup generateDifferentGroupTemp(String folder, String cdName) {
    ASTCDCompilationUnit cd = parseModel("src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/DifferentGroup/" + folder + "/" + cdName);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    return cd2DGGenerator.generateDifferentGroup(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  /**
   * Test for loading CD
   * CD:
   * enum E {...}
   * interface I {...}
   * abstract class A {...}
   * class B1 {...}
   * class B2 {...}
   * class C;
   */
  @Test
  public void testClass4LoadingCD() {
    DifferentGroup dg = generateDifferentGroupTemp("Class", "Class1.cd");
    Assert.assertTrue(dg.getDiffClassGroup().containsKey("DiffEnum_E"));
    Assert.assertTrue(dg.getDiffClassGroup().containsKey("DiffInterface_I"));
    Assert.assertTrue(dg.getDiffClassGroup().containsKey("DiffAbstractClass_A"));
    Assert.assertTrue(dg.getDiffClassGroup().containsKey("DiffClass_B1"));
    Assert.assertTrue(dg.getDiffClassGroup().containsKey("DiffClass_B2"));
    Assert.assertTrue(dg.getDiffClassGroup().containsKey("DiffClass_C"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffEnum_E").getAttributes().keySet().containsAll(Set.of("e1", "e2")));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffEnum_E").getDiffLink4EnumClass().containsAll(Set.of("DiffClass_B1")));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffInterface_I").getAttributes().keySet().containsAll(Set.of("i")));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffAbstractClass_A").getAttributes().keySet().containsAll(Set.of("a1")));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_B1").getAttributes().keySet().containsAll(Set.of("b1", "element")));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_B2").getAttributes().keySet().containsAll(Set.of("b2")));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().isEmpty());
  }

  /**
   * Test for interface inheritance
   * CD:
   * enum E {...}
   * interface I {...}
   * abstract class A implements I{...}
   * class B1 {...}
   * class B2 {...}
   * class C;
   */
  @Test
  public void testClass4InterfaceInheritance() {
    DifferentGroup dg = generateDifferentGroupTemp("Class", "Class2.cd");
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffAbstractClass_A").getAttributes().keySet().containsAll(Set.of("a1", "i")));
  }

  /**
   * Test for abstract class inheritance
   * CD:
   * enum E {...}
   * interface I {...}
   * abstract class A implements I{...}
   * class B1 extends A {...}
   * class B2 {...}
   * class C;
   */
  @Test
  public void testClass4AbstractClassInheritance() {
    DifferentGroup dg = generateDifferentGroupTemp("Class", "Class3.cd");
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_B1").getAttributes().keySet().containsAll(Set.of("i", "a1", "b1", "element")));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_B1").getAttributes().get("i").equals("String"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_B1").getAttributes().get("b1").equals("int"));
  }

  /**
   * Test for simple class inheritance
   * CD:
   * enum E {...}
   * interface I {...}
   * abstract class A implements I{...}
   * class B1 extends A {...}
   * class B2 {...}
   * class C extends B1;
   */
  @Test
  public void testClass4SimpleClassInheritance() {
    DifferentGroup dg = generateDifferentGroupTemp("Class", "Class4.cd");
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().keySet().containsAll(Set.of("i", "a1", "b1", "element")));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().get("i").equals("String"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().get("element").equals("E"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffEnum_E").getDiffLink4EnumClass().containsAll(Set.of("DiffClass_B1", "DiffClass_C")));
  }

  /**
   * Test for simple class extends two class
   * CD:
   * enum E {...}
   * interface I {...}
   * abstract class A implements I{...}
   * class B1 extends A {...}
   * class B2 {...}
   * class C extends B1, B2;
   */
  @Test
  public void testClass4SimpleClass2Inheritance() {
    DifferentGroup dg = generateDifferentGroupTemp("Class", "Class5.cd");
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().keySet().containsAll(Set.of("i", "a1", "b1", "element", "b2")));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().get("b2").equals("int"));
  }

  /********************************************************************
   **************    Start for inheritance graph    *******************
   *******************************************************************/

  /**
   * Test for inheritance graph
   * CD:
   * enum E {...}
   * interface I {...}
   * abstract class A implements I{...}
   * class B1 extends A {...}
   * class B2 {...}
   * class C extends B1, B2;
   */
  @Test
  public void testClass4inheritanceGraph() {
    DifferentGroup dg = generateDifferentGroupTemp("Class", "Class5.cd");
    Assert.assertFalse(dg.getInheritanceGraph().nodes().contains("DiffEnum_E"));
    Assert.assertTrue(dg.getInheritanceGraph().nodes().containsAll(Set.of("DiffInterface_I", "DiffAbstractClass_A", "DiffClass_B1", "DiffClass_B2", "DiffClass_C")));
    MutableGraph<String> inheritanceG = GraphBuilder.directed().build();
    inheritanceG.putEdge("DiffClass_C", "DiffClass_B1");
    inheritanceG.putEdge("DiffClass_C", "DiffClass_B2");
    inheritanceG.putEdge("DiffClass_B1", "DiffAbstractClass_A");
    inheritanceG.putEdge("DiffAbstractClass_A", "DiffInterface_I");
    Assert.assertTrue(dg.getInheritanceGraph().equals(inheritanceG));
  }

  /********************************************************************
   *******************    Start for Association   *********************
   *******************************************************************/

  /**
   * Test for simple association
   * CD:
   * enum E {...}
   * interface I {...}
   * abstract class A implements I{...}
   * class B1 extends A {...}
   * class B2 {...}
   * class C extends B1, B2;
   * class D;
   * [*] C (workOn) -> (toDo) D [1..*]
   */
  @Test
  public void testAssociation4SimpleAssociation() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "Association1.cd");
    dg.getDiffAssociationGroup().forEach((k, v) -> {
      Assert.assertTrue(v.getOriginalElement().getLeftQualifiedName().getQName().equals(v.getDiffLeftClass().getOriginalClassName()));
      Assert.assertTrue(v.getOriginalElement().getRightQualifiedName().getQName().equals(v.getDiffRightClass().getOriginalClassName()));
    });
    Assert.assertTrue(dg.getDiffAssociationGroup().keySet().containsAll(Set.of("DiffAssociation_C_workOn_LeftToRight_toDo_D")));
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_C_workOn_LeftToRight_toDo_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_ASC);
  }

  /**
   * Test for inherited association
   * CD:
   * enum E {...}
   * interface I {...}
   * abstract class A implements I{...}
   * class B1 extends A {...}
   * class B2 {...}
   * class C extends B1, B2;
   * class D;
   * [*] C (workOn) -> (toDo) D [1..*]
   * [1] I -> D [1..*]
   */
  @Test
  public void testAssociation4InheritedAssociation() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "Association2.cd");
    dg.getDiffAssociationGroup().forEach((k, v) -> {
      Assert.assertTrue(v.getOriginalElement().getLeftQualifiedName().getQName().equals(v.getDiffLeftClass().getOriginalClassName()));
      Assert.assertTrue(v.getOriginalElement().getRightQualifiedName().getQName().equals(v.getDiffRightClass().getOriginalClassName()));
    });
    Assert.assertTrue(dg.getDiffAssociationGroup().keySet().containsAll(Set.of("DiffAssociation_C_workOn_LeftToRight_toDo_D", "DiffAssociation_I_i_LeftToRight_d_D", "DiffAssociation_A_i_LeftToRight_d_D", "DiffAssociation_B1_i_LeftToRight_d_D", "DiffAssociation_C_i_LeftToRight_d_D")));
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_C_workOn_LeftToRight_toDo_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_ASC);
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_I_i_LeftToRight_d_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_ASC);
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_A_i_LeftToRight_d_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_INHERIT_ASC);
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_B1_i_LeftToRight_d_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_INHERIT_ASC);
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_C_i_LeftToRight_d_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_INHERIT_ASC);
  }

  /**
   * Test for inherited association
   * CD:
   * enum E {...}
   * interface I {...}
   * abstract class A implements I{...}
   * class B1 extends A {...}
   * class B2 {...}
   * class C extends B1, B2;
   * class D;
   * class F extends D;
   * [*] C (workOn) -> (toDo) D [1..*]
   * [1] I -> D [1..*]
   */
  @Test
  public void testAssociation4RefSetAssociation() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "Association3.cd");
    Map<String, DiffAssociation> diffAssociationGroup = dg.getDiffAssociationGroup();
    List<DiffRefSetAssociation> list = createDiffRefSetAssociation(diffAssociationGroup);
    Assert.assertEquals(list.size(), 2);
    Assert.assertTrue(list.stream()
      .anyMatch(e -> e.getLeftRefSet().stream()
        .map(DiffClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("C")) &&
        e.getDirection().equals(DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT) &&
        e.getRightRefSet().stream()
        .map(DiffClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("F", "D"))));

    Assert.assertTrue(list.stream()
      .anyMatch(e -> e.getLeftRefSet()
        .stream()
        .map(DiffClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("I", "A", "B1", "C")) &&
        e.getDirection().equals(DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT) &&
        e.getRightRefSet().stream()
        .map(DiffClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("F", "D"))));
  }

  @Test
  public void testEditASTCDAssociationLeftSide() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "Association3.cd");
    ASTCDAssociation original = dg.getDiffAssociationGroup().get("DiffAssociation_I_i_LeftToRight_d_D").getOriginalElement();
    DiffClass diffClass = dg.getDiffClassGroup().get("DiffClass_C");
    ASTCDAssociation edited = editASTCDAssociationLeftSideByDiffClass(original, diffClass);
    Assert.assertTrue(edited.getLeftQualifiedName().getQName().equals(diffClass.getOriginalClassName()));
  }

  @Test
  public void testEditASTCDAssociationRightSide() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "Association3.cd");
    ASTCDAssociation original = dg.getDiffAssociationGroup().get("DiffAssociation_I_i_LeftToRight_d_D").getOriginalElement();
    DiffClass diffClass = dg.getDiffClassGroup().get("DiffClass_C");
    ASTCDAssociation edited = editASTCDAssociationRightSideByDiffClass(original, diffClass);
    Assert.assertTrue(edited.getRightQualifiedName().getQName().equals(diffClass.getOriginalClassName()));
  }

  /**
   * Test for overlap RefSetAssociation
   * CD:
   *   class A;
   *   class A1 extends A;
   *   class B;
   *   class B1 extends B;
   *
   *   association [1..*] A -> (toDo) B [1..*];
   *   association [1] A1 -> (toDo) B1 [1..*];
   */
  @Test
  public void testAssociation4OverlapRefSetAssociation() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "OverlapRefSetAssociation.cd");
    Map<String, DiffAssociation> diffAssociationGroup = dg.getDiffAssociationGroup();
    List<DiffRefSetAssociation> list = createDiffRefSetAssociation(diffAssociationGroup);
    Assert.assertEquals(list.size(), 2);
    Assert.assertTrue(list.stream()
      .anyMatch(e -> e.getLeftRefSet().stream()
        .map(DiffClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("A1")) &&
        e.getDirection().equals(DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT) &&
        e.getRightRefSet().stream()
          .map(DiffClass::getOriginalClassName)
          .collect(Collectors.toSet())
          .containsAll(Set.of("B1"))));

    Assert.assertTrue(list.stream()
      .anyMatch(e -> e.getLeftRefSet()
        .stream()
        .map(DiffClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("A", "A1")) &&
        e.getDirection().equals(DifferentGroup.DiffAssociationDirection.LEFT_TO_RIGHT) &&
        e.getRightRefSet().stream()
          .map(DiffClass::getOriginalClassName)
          .collect(Collectors.toSet())
          .containsAll(Set.of("B", "B1"))));
  }

  /**
   * Test for duplicated association
   * CD:
   *   class A;
   *   class B;
   *   association [*] A (a) <- (b) B [*];
   *   association [*] A (a) <- (b) B [*];
   */
  @Test
  public void testAssociation4DuplicatedAssociation1() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "DuplicateAssociation1.cd");
    Assert.assertTrue(dg.getDiffAssociationGroup().size() == 1);
    Assert.assertTrue(dg.getDiffAssociationGroup().containsKey("DiffAssociation_A_a_RightToLeft_b_B"));
  }

  /**
   * Test for duplicated association
   * CD:
   *   class A;
   *   class B;
   *   association [0..1] A (a) <- (b) B [1..*];
   *   association [1] B (b) -> (a) A [1..*];
   */
  @Test
  public void testAssociation4DuplicatedAssociation2() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "DuplicateAssociation2.cd");
    Assert.assertTrue(dg.getDiffAssociationGroup().size() == 1);
    Assert.assertTrue(dg.getDiffAssociationGroup().containsKey("DiffAssociation_B_b_LeftToRight_a_A"));
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_B_b_LeftToRight_a_A").getDiffLeftClassCardinality().equals(DifferentGroup.DiffAssociationCardinality.ONE));
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_B_b_LeftToRight_a_A").getDiffRightClassCardinality().equals(DifferentGroup.DiffAssociationCardinality.ONE));
  }

  /**
   * Test for duplicated association
   * CD:
   *   class A;
   *   class B;
   *   association [*] A (a) <- (b) B [*];
   *   association [*] A (a) -> (b) B [1..*];
   */
  @Test
  public void testAssociation4DuplicatedAssociation3() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "DuplicateAssociation3.cd");
    Assert.assertTrue(dg.getDiffAssociationGroup().size() == 2);
    Assert.assertTrue(dg.getDiffAssociationGroup().containsKey("DiffAssociation_A_a_RightToLeft_b_B"));
    Assert.assertTrue(dg.getDiffAssociationGroup().containsKey("DiffAssociation_A_a_LeftToRight_b_B"));
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_A_a_RightToLeft_b_B").getDiffLeftClassCardinality().equals(DifferentGroup.DiffAssociationCardinality.ONE) &&
      dg.getDiffAssociationGroup().get("DiffAssociation_A_a_RightToLeft_b_B").getDiffRightClassCardinality().equals(DifferentGroup.DiffAssociationCardinality.MORE));
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_A_a_LeftToRight_b_B").getDiffLeftClassCardinality().equals(DifferentGroup.DiffAssociationCardinality.ZORE_TO_ONE) &&
      dg.getDiffAssociationGroup().get("DiffAssociation_A_a_LeftToRight_b_B").getDiffRightClassCardinality().equals(DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE));
  }

  /**
   * Test for duplicated association
   * CD:
   *   class A;
   *   class B;
   *   association [1] A (a) <- (b) B [*];
   *   association [0..1] A (a) -> (b) B [1..*];
   *   association [*] A (a) <-> (b) B [*];
   */
  @Test
  public void testAssociation4DuplicatedAssociation4() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "DuplicateAssociation4.cd");
    Assert.assertTrue(dg.getDiffAssociationGroup().size() == 1);
    Assert.assertTrue(dg.getDiffAssociationGroup().containsKey("DiffAssociation_A_a_Bidirectional_b_B"));
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_A_a_Bidirectional_b_B").getDiffLeftClassCardinality().equals(DifferentGroup.DiffAssociationCardinality.ONE) &&
      dg.getDiffAssociationGroup().get("DiffAssociation_A_a_Bidirectional_b_B").getDiffRightClassCardinality().equals(DifferentGroup.DiffAssociationCardinality.ONE_TO_MORE));
  }


}