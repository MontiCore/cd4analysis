package de.monticore.sydiff2semdiff.cg2od;

import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.sydiff2semdiff.cd2dg.CD2DGGenerator;
import de.monticore.sydiff2semdiff.cd2dg.metamodel.DifferentGroup;
import de.monticore.sydiff2semdiff.dg2cg.DG2CGGenerator;
import de.monticore.sydiff2semdiff.dg2cg.metamodel.CompareGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CG2ODGeneratorTest extends CDDiffTestBasis {
  DifferentGroup dg1 = null;
  DifferentGroup dg2 = null;
  CompareGroup cg1 = null;
  CompareGroup cg2 = null;

  protected void generateCompareGroupTemp(String folder, String cd1Name, String cd2Name) {
    ASTCDCompilationUnit cd1 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/GenerateOD/" + folder + "/" + cd1Name);

    ASTCDCompilationUnit cd2 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/GenerateOD/" + folder + "/" + cd2Name);
    CD2DGGenerator cd1Generator = new CD2DGGenerator();
    CD2DGGenerator cd2Generator = new CD2DGGenerator();
    dg1 = cd1Generator.generateDifferentGroup(cd1, CDSemantics.SIMPLE_CLOSED_WORLD);
    dg2 = cd2Generator.generateDifferentGroup(cd2, CDSemantics.SIMPLE_CLOSED_WORLD);
    DG2CGGenerator dg2CGGenerator4dg1Withdg2 = new DG2CGGenerator();
    DG2CGGenerator dg2CGGenerator4dg2Withdg1 = new DG2CGGenerator();
    cg1 = dg2CGGenerator4dg1Withdg2.generateCompareGroup(dg1, dg2);
//    cg2 = dg2CGGenerator4dg2Withdg1.generateCompareGroup(dg2, dg1);
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  @Test
  public void testGenerateODByClass() {
    generateCompareGroupTemp("Class","Class1A.cd", "Class1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e -> {
      String result =
        "[CompAbstractClass_A]-[deleted] {\n" +
          "\n" +
          "  b_0:B{\n" +
          "    E myE = e1;\n" +
          "    List<String> myList = [some_type_String,...];\n" +
          "    Map<Integer,E> myMap = [some_type_Integer -> e1,... -> ...];\n" +
          "    Optional<E> myOpt = e1;\n" +
          "    Set<Boolean> mySet = some_type_Set<Boolean>;\n" +
          "    int id = some_type_int;\n" +
          "    Date myDate = some_type_Date;\n" +
          "  };\n" +
          "\n" +
          "}";
      return e.contains(result);
    }));

    Assert.assertTrue(resultList.stream().anyMatch(e -> {
      String result =
        "[CompClass_C]-[deleted] {\n" +
          "\n" +
          "  c_0:C{};\n" +
          "\n" +
          "}";
      return e.contains(result);
    }));

    Assert.assertTrue(resultList.stream().anyMatch(e -> {
      String result =
        "[CompClass_B]-[edited] {\n" +
          "\n" +
          "  b_0:B{\n" +
          "    E myE = e1;\n" +
          "    List<String> myList = [some_type_String,...];\n" +
          "    Map<Integer,E> myMap = [some_type_Integer -> e1,... -> ...];\n" +
          "    Optional<E> myOpt = e1;\n" +
          "    Set<Boolean> mySet = some_type_Set<Boolean>;\n" +
          "    int id = some_type_int;\n" +
          "    Date myDate = some_type_Date;\n" +
          "  };\n" +
          "\n" +
          "}";
      return e.contains(result);
    }));

    Assert.assertTrue(resultList.stream().anyMatch(e -> {
      String result =
        "[CompEnum_E]-[edited] {\n" +
        "\n" +
        "  b_0:B{\n" +
        "    E myE = e3;\n" +
        "    List<String> myList = [some_type_String,...];\n" +
        "    Map<Integer,E> myMap = [some_type_Integer -> e1,... -> ...];\n" +
        "    Optional<E> myOpt = e1;\n" +
        "    Set<Boolean> mySet = some_type_Set<Boolean>;\n" +
        "    int id = some_type_int;\n" +
        "    Date myDate = some_type_Date;\n" +
        "  };\n" +
        "\n" +
        "}";
      return e.contains(result);
    }));
  }

  /********************************************************************
   *******************    Start for Association    ********************
   *******************************************************************/

  @Test
  public void testGenerateODByAssociation() {
    generateCompareGroupTemp("Association","Association1A.cd", "Association1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("[CompAssociation_A_a_Bidirectional_b_B]-[deleted]") &&
        e.contains("b_0:B{};") &&
        e.contains("c_0:C{};") &&
        e.contains("link b_0 (a) -> (b) b_0;") &&
        e.contains("link b_0 (b) -> (a) b_0;") &&
        e.contains("link c_0 (c) -> (a) b_0;") &&
        e.contains("link b_0 (a) -> (c) c_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[CompAssociation_A_a_Bidirectional_c_C]-[direction_changed]") &&
        e.contains("b_0:B{};") &&
        e.contains("c_0:C{};") &&
        e.contains("link b_0 (a) -> (c) c_0;") &&
        e.contains("link c_0 (c) -> (a) b_0;") &&
        e.contains("link b_0 (b) -> (a) b_0;") &&
        e.contains("link b_0 (a) -> (b) b_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[CompAssociation_E_work_LeftToRight_todo_F]-[deleted]") &&
        e.contains("e_0:E{};") &&
        e.contains("f_0:F{};") &&
        e.contains("link e_0 (work) -> (todo) f_0;")
    ));

  }

  @Test
  public void testGenerateODByCircleAssociation() {
    generateCompareGroupTemp("Association","CircleTest1A.cd", "CircleTest1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    Assert.assertTrue(resultList.size() == 0);
  }

  @Test
  public void testGenerateODByAssocStack4TrgetClass() {
    generateCompareGroupTemp("Association","AssocStack4TargetClass1A.cd", "AssocStack4TargetClass1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("[CompAssociation_A_a_LeftToRight_b_B]-[cardinality_changed]") &&
        e.contains(
          "  a_0:A{\n" +
          "    int a = some_type_int;\n" +
          "  };") &&
        e.contains("b_0:B{};") &&
        e.contains("b_1:B{};") &&
        e.contains("c_0:C{};") &&
        e.contains("c_1:C{};") &&
        e.contains("link a_0 (a) -> (b) b_0;") &&
        e.contains("link a_0 (a) -> (b) b_1;") &&
        e.contains("link c_0 (c) -> (b) b_0;") &&
        e.contains("link b_0 (b) -> (c) c_0;") &&
        e.contains("link c_1 (c) -> (b) b_1;") &&
        e.contains("link b_1 (b) -> (c) c_1;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[CompClass_A]-[edited]") &&
        e.contains(
          "  a_0:A{\n" +
            "    int a = some_type_int;\n" +
            "  };") &&
        e.contains("b_0:B{};") &&
        e.contains("c_0:C{};") &&
        e.contains("link a_0 (a) -> (b) b_0;") &&
        e.contains("link c_0 (c) -> (b) b_0;") &&
        e.contains("link b_0 (b) -> (c) c_0;")
    ));
  }

  @Test
  public void testGenerateODByDirection1() {
    generateCompareGroupTemp("Association","Direction1A.cd", "Direction1G.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[CompAssociation_A_a_RightToLeft_b_B]-[direction_changed]") &&
        e.contains("a_0:A{};") &&
        e.contains("b_0:B{};") &&
        e.contains("link b_0 (b) -> (a) a_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[CompAssociation_A_a_RightToLeft_b_B]-[cardinality_changed]") &&
        e.contains("b_0:B{};")
    ));
  }

  @Test
  public void testGenerateODByDirection2() {
    generateCompareGroupTemp("Association","Direction1G.cd", "Direction1A.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[CompAssociation_A_a_LeftToRight_b_B]-[direction_changed]") &&
        e.contains("a_0:A{};") &&
        e.contains("b_0:B{};") &&
        e.contains("link a_0 (a) -> (b) b_0;") &&
        e.contains("link b_0 (b) -> (a) a_0;")
    ));

  }

  /********************************************************************
   *********************    Start for Combination    ******************
   *******************************************************************/
  @Test
  public void testGenerateODByOverlapRefSetAssociation1() {
    generateCompareGroupTemp("Combination","OverlapRefSetAssociation1A.cd", "OverlapRefSetAssociation1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    Assert.assertTrue(resultList.size() == 0);
  }

  @Test
  public void testGenerateODByOverlapRefSetAssociation2() {
    generateCompareGroupTemp("Combination","OverlapRefSetAssociation1C.cd", "OverlapRefSetAssociation1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    Assert.assertTrue(resultList.size() == 0);
  }

  @Test
  public void testGenerateODByOverlapRefSetAssociation3() {
    generateCompareGroupTemp("Combination","OverlapRefSetAssociation2A.cd", "OverlapRefSetAssociation2B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[CompClass_A]-[edited]") &&
        e.contains(
          "  a_0:A{\n" +
            "    int id = some_type_int;\n" +
            "  };")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[CompClass_A1]-[edited]") &&
        e.contains(
          "  a1_0:A1{\n" +
            "    int id = some_type_int;\n" +
            "  };") &&
        e.contains("b1_0:B1{};") &&
        e.contains("link a1_0 (workOn) -> (toDo) b1_0;")
    ));
  }

  @Test
  public void testGenerateODByRefSetAssociation() {
    generateCompareGroupTemp("Combination","RefSet1A.cd", "RefSet1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[CompAssociation_Employee_assignee_RightToLeft_todo_Task]-[cardinality_changed]") &&
        e.contains(
          "  employee_0:Employee{\n" +
            "    PositionKind kind = fullTime;\n" +
            "  };")
    ));
  }

//  @Test
//  public void testGenerateODByCombination () {
//    generateCompareGroupTemp("Combination","Employees1A.cd", "Employees1B.cd");
//    CG2ODGenerator odGenerator = new CG2ODGenerator();
//    List<String> resultList = odGenerator.generateObjectDiagrams(dg1, cg1);
//    for(int i = 0; i < resultList.size() ; i++) {
//      System.out.println(resultList.get(i));
//    }
//  }

}
