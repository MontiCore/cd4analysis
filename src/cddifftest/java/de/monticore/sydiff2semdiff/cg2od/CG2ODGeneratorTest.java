package de.monticore.sydiff2semdiff.cg2od;

import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.sydiff2semdiff.cd2sg.CD2SGGenerator;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportGroup;
import de.monticore.sydiff2semdiff.sg2cg.SG2CGGenerator;
import de.monticore.sydiff2semdiff.sg2cg.metamodel.CompareGroup;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CG2ODGeneratorTest extends CDDiffTestBasis {
  SupportGroup sg1 = null;
  SupportGroup sg2 = null;
  CompareGroup cg1 = null;

  protected void generateCompareGroupTemp(String folder, String cd1Name, String cd2Name) {
    ASTCDCompilationUnit cd1 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/GenerateOD/" + folder + "/" + cd1Name);

    ASTCDCompilationUnit cd2 = parseModel(
      "src/cddifftest/resources/de/monticore/cddiff/sydiff2semdiff/GenerateOD/" + folder + "/" + cd2Name);
    CD2SGGenerator cd1Generator = new CD2SGGenerator();
    CD2SGGenerator cd2Generator = new CD2SGGenerator();
    sg1 = cd1Generator.generateSupportGroup(cd1, CDSemantics.SIMPLE_CLOSED_WORLD);
    sg2 = cd2Generator.generateSupportGroup(cd2, CDSemantics.SIMPLE_CLOSED_WORLD);
    SG2CGGenerator sg2CGGenerator4sg1Withsg2 = new SG2CGGenerator();
    cg1 = sg2CGGenerator4sg1Withsg2.generateCompareGroup(sg1, sg2);
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  @Test
  public void testGenerateODByClass() {
    generateCompareGroupTemp("Class","Class1A.cd", "Class1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e -> {
      String result =
        "[Class_C]-[deleted] {\n" +
          "\n" +
          "  c_0:C{};\n" +
          "\n" +
          "}";
      return e.contains(result);
    }));

    Assert.assertTrue(resultList.stream().anyMatch(e -> {
      String result =
        "[Class_B]-[edited]-[myDate, myList, myE, myMap, id, mySet, myOpt] {\n" +
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
        "[Enum_E]-[edited]-[e3] {\n" +
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
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_E_work_LeftToRight_todo_F]-[deleted]") &&
        e.contains("e_0:E{};") &&
        e.contains("f_0:F{};") &&
        e.contains("link e_0 (work) -> (todo) f_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_A_a_Bidirectional_c_C]-[direction_changed]") &&
        e.contains("b_0:B{};") &&
        e.contains("c_0:C{};") &&
        e.contains("link b_0 (a) -> (c) c_0;") &&
        e.contains("link c_0 (c) -> (a) b_0;") &&
        e.contains("link b_0 (a) -> (b) b_0;") &&
        e.contains("link b_0 (b) -> (a) b_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("[Association_A_a_Bidirectional_b_B]-[deleted]") &&
        e.contains("b_0:B{};") &&
        e.contains("c_0:C{};") &&
        e.contains("link b_0 (a) -> (b) b_0;") &&
        e.contains("link b_0 (b) -> (a) b_0;") &&
        e.contains("link b_0 (a) -> (c) c_0;") &&
        e.contains("link c_0 (c) -> (a) b_0;")
    ));
  }

  @Test
  public void testGenerateODByCircleAssociation() {
    generateCompareGroupTemp("Association","CircleTest1A.cd", "CircleTest1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.size() == 0);
  }

  @Test
  public void testGenerateODByAssocStack4TrgetClass() {
    generateCompareGroupTemp("Association","AssocStack4TargetClass1A.cd", "AssocStack4TargetClass1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("[Association_A_a_LeftToRight_b_B]-[cardinality_changed]-[right_cardinality]") &&
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
      e.contains("[Class_A]-[edited]-[a]") &&
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
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_A_a_RightToLeft_b_B]-[direction_changed]") &&
        e.contains("a_0:A{};") &&
        e.contains("b_0:B{};") &&
        e.contains("link b_0 (b) -> (a) a_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_A_a_RightToLeft_b_B]-[cardinality_changed]") &&
        e.contains("b_0:B{};")
    ));
  }

  @Test
  public void testGenerateODByDirection2() {
    generateCompareGroupTemp("Association","Direction1G.cd", "Direction1A.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_A_a_LeftToRight_b_B]-[direction_changed]") &&
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
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.size() == 0);
  }

  @Test
  public void testGenerateODByOverlapRefSetAssociation2() {
    generateCompareGroupTemp("Combination","OverlapRefSetAssociation1C.cd", "OverlapRefSetAssociation1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.size() == 0);
  }

  @Test
  public void testGenerateODByOverlapRefSetAssociation3() {
    generateCompareGroupTemp("Combination","OverlapRefSetAssociation2A.cd", "OverlapRefSetAssociation2B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Class_A]-[edited]-[id]") &&
        e.contains(
          "  a_0:A{\n" +
            "    int id = some_type_int;\n" +
            "  };")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Class_A1]-[edited]-[id]") &&
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
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_Employee_assignee_RightToLeft_todo_Task]-[cardinality_changed]-[right_cardinality]") &&
        e.contains(
          "  employee_0:Employee{\n" +
            "    PositionKind kind = fullTime;\n" +
            "  };")
    ));
  }

  @Test
  public void testGenerateODByCombination1() {
    generateCompareGroupTemp("Combination","Employees1A.cd", "Employees1B.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_Employee_assignee_RightToLeft_todo_Task]-[direction_changed]") &&
        e.contains(
            "  employee_0:Employee{\n" +
            "    PositionKind kind = fullTime;\n" +
            "    List<Long> devices = [some_type_Long,...];\n" +
            "    int personId = some_type_int;\n" +
            "  };") &&
        e.contains(
            "  task_0:Task{\n" +
            "    int taskId = some_type_int;\n" +
            "    String taskName = some_type_String;\n" +
            "    Date startDate = some_type_Date;\n" +
            "    Date endDate = some_type_Date;\n" +
            "  };") &&
        e.contains(
            "  company_0:Company{\n" +
            "    String address = some_type_String;\n" +
            "    String country = some_type_String;\n" +
            "    String timeZone = some_type_String;\n" +
            "  };") &&
        e.contains("link task_0 (todo) -> (assignee) employee_0;") &&
        e.contains("link employee_0 (work) -> (area) company_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_Employee_work_LeftToRight_area_Area]-[cardinality_changed]-[right_cardinality]") &&
        e.contains(
            "  employee_0:Employee{\n" +
            "    PositionKind kind = fullTime;\n" +
            "    List<Long> devices = [some_type_Long,...];\n" +
            "    int personId = some_type_int;\n" +
            "  };") &&
        e.contains(
            "  company_0:Company{\n" +
            "    String address = some_type_String;\n" +
            "    String country = some_type_String;\n" +
            "    String timeZone = some_type_String;\n" +
            "  };") &&
        e.contains(
            "  company_1:Company{\n" +
            "    String address = some_type_String;\n" +
            "    String country = some_type_String;\n" +
            "    String timeZone = some_type_String;\n" +
            "  };") &&
        e.contains("link employee_0 (work) -> (area) company_0;") &&
        e.contains("link employee_0 (work) -> (area) company_1;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_Employee_work_LeftToRight_area_Area]-[direction_changed]") &&
        e.contains(
            "  employee_0:Employee{\n" +
            "    PositionKind kind = fullTime;\n" +
            "    List<Long> devices = [some_type_Long,...];\n" +
            "    int personId = some_type_int;\n" +
            "  };") &&
        e.contains(
            "  company_0:Company{\n" +
            "    String address = some_type_String;\n" +
            "    String country = some_type_String;\n" +
            "    String timeZone = some_type_String;\n" +
            "  };") &&
        e.contains("link employee_0 (work) -> (area) company_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Interface_Area]-[edited]-[timeZone]") &&
        e.contains(
              "  employee_0:Employee{\n" +
                "    PositionKind kind = fullTime;\n" +
                "    List<Long> devices = [some_type_Long,...];\n" +
                "    int personId = some_type_int;\n" +
                "  };") &&
        e.contains(
              "  company_0:Company{\n" +
                "    String address = some_type_String;\n" +
                "    String country = some_type_String;\n" +
                "    String timeZone = some_type_String;\n" +
                "  };") &&
        e.contains("link employee_0 (work) -> (area) company_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Enum_PositionKind]-[edited]-[other]") &&
        e.contains(
            "  employee_0:Employee{\n" +
              "    PositionKind kind = other;\n" +
              "    List<Long> devices = [some_type_Long,...];\n" +
              "    int personId = some_type_int;\n" +
              "  };") &&
        e.contains(
            "  task_0:Task{\n" +
              "    int taskId = some_type_int;\n" +
              "    String taskName = some_type_String;\n" +
              "    Date startDate = some_type_Date;\n" +
              "    Date endDate = some_type_Date;\n" +
              "  };") &&
        e.contains(
          "  company_0:Company{\n" +
            "    String address = some_type_String;\n" +
            "    String country = some_type_String;\n" +
            "    String timeZone = some_type_String;\n" +
            "  };") &&
        e.contains("link task_0 (todo) -> (assignee) employee_0;") &&
        e.contains("link employee_0 (work) -> (area) company_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Class_Employee]-[edited]-[devices]") &&
        e.contains(
            "  employee_0:Employee{\n" +
            "    PositionKind kind = fullTime;\n" +
            "    List<Long> devices = [some_type_Long,...];\n" +
            "    int personId = some_type_int;\n" +
            "  };") &&
        e.contains(
          "  task_0:Task{\n" +
            "    int taskId = some_type_int;\n" +
            "    String taskName = some_type_String;\n" +
            "    Date startDate = some_type_Date;\n" +
            "    Date endDate = some_type_Date;\n" +
            "  };") &&
        e.contains(
            "  company_0:Company{\n" +
            "    String address = some_type_String;\n" +
            "    String country = some_type_String;\n" +
            "    String timeZone = some_type_String;\n" +
            "  };") &&
        e.contains("link task_0 (todo) -> (assignee) employee_0;") &&
        e.contains("link employee_0 (work) -> (area) company_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Class_Manager]-[edited]-[devices]") &&
        e.contains(
          "  managementTask_0:ManagementTask{\n" +
            "    int priority = some_type_int;\n" +
            "    int taskId = some_type_int;\n" +
            "    String taskName = some_type_String;\n" +
            "    Date startDate = some_type_Date;\n" +
            "    Date endDate = some_type_Date;\n" +
            "  };") &&
        e.contains(
            "  manager_0:Manager{\n" +
            "    Department inChargeOf = Sales;\n" +
            "    PositionKind kind = fullTime;\n" +
            "    List<Long> devices = [some_type_Long,...];\n" +
            "    int personId = some_type_int;\n" +
            "  };") &&
        e.contains(
          "  company_0:Company{\n" +
            "    String address = some_type_String;\n" +
            "    String country = some_type_String;\n" +
            "    String timeZone = some_type_String;\n" +
            "  };") &&
        e.contains("link managementTask_0 (todo) -> (assignee) manager_0;") &&
        e.contains("link manager_0 (assignee) -> (todo) managementTask_0;") &&
        e.contains("link manager_0 (work) -> (area) company_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Class_Company]-[edited]-[timeZone]") &&
        e.contains(
          "  manager_0:Manager{\n" +
            "    Department inChargeOf = Sales;\n" +
            "    PositionKind kind = fullTime;\n" +
            "    List<Long> devices = [some_type_Long,...];\n" +
            "    int personId = some_type_int;\n" +
            "  };") &&
        e.contains(
              "  company_0:Company{\n" +
              "    String address = some_type_String;\n" +
              "    String country = some_type_String;\n" +
              "    String timeZone = some_type_String;\n" +
              "  };") &&
        e.contains(
            "  managementTask_0:ManagementTask{\n" +
            "    int priority = some_type_int;\n" +
            "    int taskId = some_type_int;\n" +
            "    String taskName = some_type_String;\n" +
            "    Date startDate = some_type_Date;\n" +
            "    Date endDate = some_type_Date;\n" +
            "  };") &&
        e.contains("link manager_0 (work) -> (area) company_0;") &&
        e.contains("link managementTask_0 (todo) -> (assignee) manager_0;") &&
        e.contains("link manager_0 (assignee) -> (todo) managementTask_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Class_ManagementTask]-[edited]-[taskName]") &&
        e.contains(
            "  managementTask_0:ManagementTask{\n" +
            "    int priority = some_type_int;\n" +
            "    int taskId = some_type_int;\n" +
            "    String taskName = some_type_String;\n" +
            "    Date startDate = some_type_Date;\n" +
            "    Date endDate = some_type_Date;\n" +
            "  };") &&
        e.contains(
            "  manager_0:Manager{\n" +
            "    Department inChargeOf = Sales;\n" +
            "    PositionKind kind = fullTime;\n" +
            "    List<Long> devices = [some_type_Long,...];\n" +
            "    int personId = some_type_int;\n" +
            "  };") &&
        e.contains(
            "  company_0:Company{\n" +
            "    String address = some_type_String;\n" +
            "    String country = some_type_String;\n" +
            "    String timeZone = some_type_String;\n" +
            "  };") &&
        e.contains("link managementTask_0 (todo) -> (assignee) manager_0;") &&
        e.contains("link manager_0 (assignee) -> (todo) managementTask_0;") &&
        e.contains("link manager_0 (work) -> (area) company_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Class_Task]-[edited]-[taskName]") &&
        e.contains(
              "  task_0:Task{\n" +
              "    int taskId = some_type_int;\n" +
              "    String taskName = some_type_String;\n" +
              "    Date startDate = some_type_Date;\n" +
              "    Date endDate = some_type_Date;\n" +
              "  };") &&
        e.contains(
              "  employee_0:Employee{\n" +
              "    PositionKind kind = fullTime;\n" +
              "    List<Long> devices = [some_type_Long,...];\n" +
              "    int personId = some_type_int;\n" +
              "  };") &&
        e.contains(
              "  company_0:Company{\n" +
              "    String address = some_type_String;\n" +
              "    String country = some_type_String;\n" +
              "    String timeZone = some_type_String;\n" +
              "  };") &&
        e.contains("link task_0 (todo) -> (assignee) employee_0;") &&
        e.contains("link employee_0 (work) -> (area) company_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[AbstractClass_Person]-[deleted]") &&
        e.contains(
            "  employee_0:Employee{\n" +
            "    PositionKind kind = fullTime;\n" +
            "    List<Long> devices = [some_type_Long,...];\n" +
            "    int personId = some_type_int;\n" +
            "  };") &&
        e.contains(
            "  task_0:Task{\n" +
            "    int taskId = some_type_int;\n" +
            "    String taskName = some_type_String;\n" +
            "    Date startDate = some_type_Date;\n" +
            "    Date endDate = some_type_Date;\n" +
            "  };") &&
        e.contains(
          "  company_0:Company{\n" +
          "    String address = some_type_String;\n" +
          "    String country = some_type_String;\n" +
          "    String timeZone = some_type_String;\n" +
          "  };") &&
        e.contains("link task_0 (todo) -> (assignee) employee_0;") &&
        e.contains("link employee_0 (work) -> (area) company_0;")
    ));
  }

  @Test
  public void testGenerateODByCombination2() {
    generateCompareGroupTemp("Combination", "Employees1B.cd", "Employees1A.cd");
    CG2ODGenerator odGenerator = new CG2ODGenerator();
    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_Employee_assignee_Bidirectional_todo_Task]-[direction_changed]") &&
        e.contains(
          "  employee_0:Employee{\n" +
            "    int personId = some_type_int;\n" +
            "    PositionKind kind = fullTime;\n" +
            "  };") &&
        e.contains(
          "  task_0:Task{\n" +
            "    int taskId = some_type_int;\n" +
            "    Date startDate = some_type_Date;\n" +
            "    Date endDate = some_type_Date;\n" +
            "  };") &&
        e.contains(
          "  company_0:Company{\n" +
            "    String address = some_type_String;\n" +
            "    String country = some_type_String;\n" +
            "  };") &&
        e.contains("link employee_0 (assignee) -> (todo) task_0;") &&
        e.contains("link task_0 (todo) -> (assignee) employee_0;") &&
        e.contains("link company_0 (area) -> (work) employee_0;") &&
        e.contains("link employee_0 (work) -> (area) company_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
      e.contains("[Association_Employee_work_RightToLeft_area_Area]-[direction_changed]") &&
        e.contains(
          "  employee_0:Employee{\n" +
            "    int personId = some_type_int;\n" +
            "    PositionKind kind = fullTime;\n" +
            "  };") &&
        e.contains(
          "  company_0:Company{\n" +
            "    String address = some_type_String;\n" +
            "    String country = some_type_String;\n" +
            "  };") &&
        e.contains("link company_0 (area) -> (work) employee_0;") &&
        e.contains("link employee_0 (work) -> (area) company_0;")
    ));
  }

//  @Test
//  public void testGenerateODByCombinationTest () {
//    generateCompareGroupTemp("Combination", "test1.cd", "test2.cd");
//    CG2ODGenerator odGenerator = new CG2ODGenerator();
//    List<String> resultList = odGenerator.generateObjectDiagrams(sg1, cg1);
//    for (int i = 0; i < resultList.size(); i++) {
//      System.out.println(resultList.get(i));
//    }
//  }

}
