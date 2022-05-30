package de.monticore.sydiff2semdiff.cd2dg;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class CD2DGGeneratorTest extends CDDiffTestBasis {

  public DifferentGroup generateDifferentGroupTemp(String folder, String cdName) {
    ASTCDCompilationUnit cd = parseModel("src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/DifferentGroup/" + folder + "/" + cdName);
    CD2DGGenerator cd2DGGenerator = new CD2DGGenerator();
    return cd2DGGenerator.generateDifferentGroup(cd, DifferentGroup.DifferentGroupType.SINGLE_INSTANCE);
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
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_B1").getAttributes().get("i").get("kind").equals("inherited"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_B1").getAttributes().get("i").get("type").equals("String"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_B1").getAttributes().get("b1").get("kind").equals("original"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_B1").getAttributes().get("b1").get("type").equals("int"));
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
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().get("i").get("kind").equals("inherited"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().get("i").get("type").equals("String"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().get("element").get("kind").equals("inherited"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().get("element").get("type").equals("DiffEnum_E"));
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
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().get("b2").get("kind").equals("inherited"));
    Assert.assertTrue(dg.getDiffClassGroup().get("DiffClass_C").getAttributes().get("b2").get("type").equals("int"));
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
  public void testClass4SimpleAssociation() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "Association1.cd");
    Assert.assertTrue(dg.getDiffAssociationGroup().keySet().containsAll(Set.of("DiffAssociation_C_workOn_toDo_D")));
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_C_workOn_toDo_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_ASC);
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
   * [*] C (workOn) -> (toDo) D [1..*]
   * [1] I -> D [1..*]
   */
  @Test
  public void testClass4InheritedAssociation() {
    DifferentGroup dg = generateDifferentGroupTemp("Association", "Association2.cd");
    Assert.assertTrue(dg.getDiffAssociationGroup().keySet().containsAll(Set.of("DiffAssociation_C_workOn_toDo_D", "DiffAssociation_I_i_d_D", "DiffAssociation_A_i_d_D", "DiffAssociation_B1_i_d_D", "DiffAssociation_C_i_d_D")));
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_C_workOn_toDo_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_ASC);
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_I_i_d_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_ASC);
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_A_i_d_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_INHERIT_ASC);
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_B1_i_d_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_INHERIT_ASC);
    Assert.assertTrue(dg.getDiffAssociationGroup().get("DiffAssociation_C_i_d_D").getDiffKind() == DifferentGroup.DiffAssociationKind.DIFF_INHERIT_ASC);
  }

}
