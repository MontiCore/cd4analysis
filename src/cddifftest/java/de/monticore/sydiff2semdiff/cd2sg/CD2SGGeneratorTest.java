package de.monticore.sydiff2semdiff.cd2sg;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportClass;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportRefSetAssociation;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.sydiff2semdiff.cd2sg.SupportHelper.*;

public class CD2SGGeneratorTest extends CDDiffTestBasis {

  public SupportGroup generateSupportGroupTemp(String folder, String cdName) {
    ASTCDCompilationUnit cd = parseModel("src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/SupportGroup/" + folder + "/" + cdName);
    CD2SGGenerator cd2SGGenerator = new CD2SGGenerator();
    return cd2SGGenerator.generateSupportGroup(cd, CDSemantics.SIMPLE_CLOSED_WORLD);
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
    SupportGroup dg = generateSupportGroupTemp("Class", "Class1.cd");
    Assert.assertTrue(dg.getSupportClassGroup().containsKey("SupportEnum_E"));
    Assert.assertTrue(dg.getSupportClassGroup().containsKey("SupportInterface_I"));
    Assert.assertTrue(dg.getSupportClassGroup().containsKey("SupportAbstractClass_A"));
    Assert.assertTrue(dg.getSupportClassGroup().containsKey("SupportClass_B1"));
    Assert.assertTrue(dg.getSupportClassGroup().containsKey("SupportClass_B2"));
    Assert.assertTrue(dg.getSupportClassGroup().containsKey("SupportClass_C"));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportEnum_E").getAttributes().keySet().containsAll(Set.of("e1", "e2")));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportEnum_E").getSupportLink4EnumClass().containsAll(Set.of("SupportClass_B1")));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportInterface_I").getAttributes().keySet().containsAll(Set.of("i")));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportAbstractClass_A").getAttributes().keySet().containsAll(Set.of("a1")));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_B1").getAttributes().keySet().containsAll(Set.of("b1", "element")));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_B2").getAttributes().keySet().containsAll(Set.of("b2")));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_C").getAttributes().isEmpty());
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
    SupportGroup dg = generateSupportGroupTemp("Class", "Class2.cd");
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportAbstractClass_A").getAttributes().keySet().containsAll(Set.of("a1", "i")));
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
    SupportGroup dg = generateSupportGroupTemp("Class", "Class3.cd");
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_B1").getAttributes().keySet().containsAll(Set.of("i", "a1", "b1", "element")));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_B1").getAttributes().get("i").equals("String"));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_B1").getAttributes().get("b1").equals("int"));
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
    SupportGroup dg = generateSupportGroupTemp("Class", "Class4.cd");
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_C").getAttributes().keySet().containsAll(Set.of("i", "a1", "b1", "element")));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_C").getAttributes().get("i").equals("String"));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_C").getAttributes().get("element").equals("E"));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportEnum_E").getSupportLink4EnumClass().containsAll(Set.of("SupportClass_B1", "SupportClass_C")));
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
    SupportGroup dg = generateSupportGroupTemp("Class", "Class5.cd");
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_C").getAttributes().keySet().containsAll(Set.of("i", "a1", "b1", "element", "b2")));
    Assert.assertTrue(dg.getSupportClassGroup().get("SupportClass_C").getAttributes().get("b2").equals("int"));
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
    SupportGroup dg = generateSupportGroupTemp("Class", "Class5.cd");
    Assert.assertFalse(dg.getInheritanceGraph().nodes().contains("SupportEnum_E"));
    Assert.assertTrue(dg.getInheritanceGraph().nodes().containsAll(Set.of("SupportInterface_I", "SupportAbstractClass_A", "SupportClass_B1", "SupportClass_B2", "SupportClass_C")));
    MutableGraph<String> inheritanceG = GraphBuilder.directed().build();
    inheritanceG.putEdge("SupportClass_C", "SupportClass_B1");
    inheritanceG.putEdge("SupportClass_C", "SupportClass_B2");
    inheritanceG.putEdge("SupportClass_B1", "SupportAbstractClass_A");
    inheritanceG.putEdge("SupportAbstractClass_A", "SupportInterface_I");
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
    SupportGroup dg = generateSupportGroupTemp("Association", "Association1.cd");
    dg.getSupportAssociationGroup().forEach((k, v) -> {
      Assert.assertTrue(v.getOriginalElement().getLeftQualifiedName().getQName().equals(v.getSupportLeftClass().getOriginalClassName()));
      Assert.assertTrue(v.getOriginalElement().getRightQualifiedName().getQName().equals(v.getSupportRightClass().getOriginalClassName()));
    });
    Assert.assertTrue(dg.getSupportAssociationGroup().keySet().containsAll(Set.of("SupportAssociation_C_workOn_LeftToRight_toDo_D")));
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_C_workOn_LeftToRight_toDo_D").getSupportKind() == SupportGroup.SupportAssociationKind.SUPPORT_ASC);
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
    SupportGroup dg = generateSupportGroupTemp("Association", "Association2.cd");
    dg.getSupportAssociationGroup().forEach((k, v) -> {
      Assert.assertTrue(v.getOriginalElement().getLeftQualifiedName().getQName().equals(v.getSupportLeftClass().getOriginalClassName()));
      Assert.assertTrue(v.getOriginalElement().getRightQualifiedName().getQName().equals(v.getSupportRightClass().getOriginalClassName()));
    });
    Assert.assertTrue(dg.getSupportAssociationGroup().keySet().containsAll(Set.of("SupportAssociation_C_workOn_LeftToRight_toDo_D", "SupportAssociation_I_i_LeftToRight_d_D", "SupportAssociation_A_i_LeftToRight_d_D", "SupportAssociation_B1_i_LeftToRight_d_D", "SupportAssociation_C_i_LeftToRight_d_D")));
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_C_workOn_LeftToRight_toDo_D").getSupportKind() == SupportGroup.SupportAssociationKind.SUPPORT_ASC);
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_I_i_LeftToRight_d_D").getSupportKind() == SupportGroup.SupportAssociationKind.SUPPORT_ASC);
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_A_i_LeftToRight_d_D").getSupportKind() == SupportGroup.SupportAssociationKind.SUPPORT_INHERIT_ASC);
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_B1_i_LeftToRight_d_D").getSupportKind() == SupportGroup.SupportAssociationKind.SUPPORT_INHERIT_ASC);
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_C_i_LeftToRight_d_D").getSupportKind() == SupportGroup.SupportAssociationKind.SUPPORT_INHERIT_ASC);
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
    SupportGroup dg = generateSupportGroupTemp("Association", "Association3.cd");
    List<SupportRefSetAssociation> list = dg.getRefSetAssociationList();
    Assert.assertEquals(list.size(), 2);
    Assert.assertTrue(list.stream()
      .anyMatch(e -> e.getLeftRefSet().stream()
        .map(SupportClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("C")) &&
        e.getDirection().equals(SupportGroup.SupportAssociationDirection.LEFT_TO_RIGHT) &&
        e.getRightRefSet().stream()
        .map(SupportClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("F", "D"))));

    Assert.assertTrue(list.stream()
      .anyMatch(e -> e.getLeftRefSet()
        .stream()
        .map(SupportClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("I", "A", "B1", "C")) &&
        e.getDirection().equals(SupportGroup.SupportAssociationDirection.LEFT_TO_RIGHT) &&
        e.getRightRefSet().stream()
        .map(SupportClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("F", "D"))));
  }

  @Test
  public void testEditASTCDAssociationLeftSide() {
    SupportGroup dg = generateSupportGroupTemp("Association", "Association3.cd");
    ASTCDAssociation original = dg.getSupportAssociationGroup().get("SupportAssociation_I_i_LeftToRight_d_D").getOriginalElement();
    SupportClass supportClass = dg.getSupportClassGroup().get("SupportClass_C");
    ASTCDAssociation edited = editASTCDAssociationLeftSideBySupportClass(original, supportClass);
    Assert.assertTrue(edited.getLeftQualifiedName().getQName().equals(supportClass.getOriginalClassName()));
  }

  @Test
  public void testEditASTCDAssociationRightSide() {
    SupportGroup dg = generateSupportGroupTemp("Association", "Association3.cd");
    ASTCDAssociation original = dg.getSupportAssociationGroup().get("SupportAssociation_I_i_LeftToRight_d_D").getOriginalElement();
    SupportClass supportClass = dg.getSupportClassGroup().get("SupportClass_C");
    ASTCDAssociation edited = editASTCDAssociationRightSideBySupportClass(original, supportClass);
    Assert.assertTrue(edited.getRightQualifiedName().getQName().equals(supportClass.getOriginalClassName()));
  }

  /**
   * Test for overlap RefSetAssociation
   * CD:
   *   class A;
   *   class A1 extends A;
   *   class B;
   *   class B1 extends B;
   *
   *   association [1..*] A (workOn) -> (toDo) B [1];
   *   association [1] A1 (workOn) -> (toDo) B1 [1..*];
   */
  @Test
  public void testAssociation4OverlapRefSetAssociation1() {
    SupportGroup dg = generateSupportGroupTemp("Association", "OverlapRefSetAssociation1.cd");
    List<SupportRefSetAssociation> list = dg.getRefSetAssociationList();
    Assert.assertEquals(list.size(), 2);
    Assert.assertTrue(list.stream()
      .anyMatch(e -> e.getLeftRefSet().stream()
        .map(SupportClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("A1")) &&
        e.getDirection().equals(SupportGroup.SupportAssociationDirection.LEFT_TO_RIGHT) &&
        e.getRightRefSet().stream()
          .map(SupportClass::getOriginalClassName)
          .collect(Collectors.toSet())
          .containsAll(Set.of("B1"))));

    Assert.assertTrue(list.stream()
      .anyMatch(e -> e.getLeftRefSet()
        .stream()
        .map(SupportClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("A", "A1")) &&
        e.getDirection().equals(SupportGroup.SupportAssociationDirection.LEFT_TO_RIGHT) &&
        e.getRightRefSet().stream()
          .map(SupportClass::getOriginalClassName)
          .collect(Collectors.toSet())
          .containsAll(Set.of("B", "B1"))));
  }

  /**
   * Test for overlap RefSetAssociation
   * CD:
   *   class A;
   *   class A1 extends A;
   *   class B;
   *   class B1 extends B;
   *
   *   association [1..*] A (workOn) -> (toDo) B [1];
   *   association [1..*] B1 (toDo) <- (workOn) A1 [1];
   */
  @Test
  public void testAssociation4OverlapRefSetAssociation2() {
    SupportGroup dg = generateSupportGroupTemp("Association", "OverlapRefSetAssociation2.cd");
    List<SupportRefSetAssociation> list = dg.getRefSetAssociationList();
    Assert.assertEquals(list.size(), 2);
    Assert.assertTrue(list.stream()
      .anyMatch(e -> e.getLeftRefSet().stream()
        .map(SupportClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("B1")) &&
        e.getDirection().equals(SupportGroup.SupportAssociationDirection.RIGHT_TO_LEFT) &&
        e.getRightRefSet().stream()
          .map(SupportClass::getOriginalClassName)
          .collect(Collectors.toSet())
          .containsAll(Set.of("A1"))));

    Assert.assertTrue(list.stream()
      .anyMatch(e -> e.getLeftRefSet()
        .stream()
        .map(SupportClass::getOriginalClassName)
        .collect(Collectors.toSet())
        .containsAll(Set.of("A", "A1")) &&
        e.getDirection().equals(SupportGroup.SupportAssociationDirection.LEFT_TO_RIGHT) &&
        e.getRightRefSet().stream()
          .map(SupportClass::getOriginalClassName)
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
    SupportGroup dg = generateSupportGroupTemp("Association", "DuplicateAssociation1.cd");
    Assert.assertTrue(dg.getSupportAssociationGroup().size() == 1);
    Assert.assertTrue(dg.getSupportAssociationGroup().containsKey("SupportAssociation_A_a_RightToLeft_b_B"));
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
    SupportGroup dg = generateSupportGroupTemp("Association", "DuplicateAssociation2.cd");
    Assert.assertTrue(dg.getSupportAssociationGroup().size() == 1);
    Assert.assertTrue(dg.getSupportAssociationGroup().containsKey("SupportAssociation_B_b_LeftToRight_a_A"));
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_B_b_LeftToRight_a_A").getSupportLeftClassCardinality().equals(SupportGroup.SupportAssociationCardinality.ONE));
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_B_b_LeftToRight_a_A").getSupportRightClassCardinality().equals(SupportGroup.SupportAssociationCardinality.ONE));
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
    SupportGroup dg = generateSupportGroupTemp("Association", "DuplicateAssociation3.cd");
    Assert.assertTrue(dg.getSupportAssociationGroup().size() == 2);
    Assert.assertTrue(dg.getSupportAssociationGroup().containsKey("SupportAssociation_A_a_RightToLeft_b_B"));
    Assert.assertTrue(dg.getSupportAssociationGroup().containsKey("SupportAssociation_A_a_LeftToRight_b_B"));
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_A_a_RightToLeft_b_B").getSupportLeftClassCardinality().equals(SupportGroup.SupportAssociationCardinality.ONE) &&
      dg.getSupportAssociationGroup().get("SupportAssociation_A_a_RightToLeft_b_B").getSupportRightClassCardinality().equals(SupportGroup.SupportAssociationCardinality.MORE));
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_A_a_LeftToRight_b_B").getSupportLeftClassCardinality().equals(SupportGroup.SupportAssociationCardinality.ZORE_TO_ONE) &&
      dg.getSupportAssociationGroup().get("SupportAssociation_A_a_LeftToRight_b_B").getSupportRightClassCardinality().equals(SupportGroup.SupportAssociationCardinality.ONE_TO_MORE));
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
    SupportGroup dg = generateSupportGroupTemp("Association", "DuplicateAssociation4.cd");
    Assert.assertTrue(dg.getSupportAssociationGroup().size() == 1);
    Assert.assertTrue(dg.getSupportAssociationGroup().containsKey("SupportAssociation_A_a_Bidirectional_b_B"));
    Assert.assertTrue(dg.getSupportAssociationGroup().get("SupportAssociation_A_a_Bidirectional_b_B").getSupportLeftClassCardinality().equals(SupportGroup.SupportAssociationCardinality.ONE) &&
      dg.getSupportAssociationGroup().get("SupportAssociation_A_a_Bidirectional_b_B").getSupportRightClassCardinality().equals(SupportGroup.SupportAssociationCardinality.ONE_TO_MORE));
  }


}
