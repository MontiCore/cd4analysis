package de.monticore.syntax2semdiff.cdsyntaxdiff2od;

import de.monticore.alloycddiff.CDSemantics;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.syntax2semdiff.cd2cdwrapper.CD2CDWrapperGenerator;
import de.monticore.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDWrapper2CDSyntaxDiffGenerator;
import de.monticore.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDSyntaxDiff;
import de.se_rwth.artifacts.lang.matcher.CDDiffOD2CDMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static de.monticore.syntax2semdiff.cdsyntaxdiff2od.GenerateODHelper.printOD;

public class CDSyntaxDiff2ODGeneratorTest extends CDDiffTestBasis {
  ASTCDCompilationUnit cd1 = null;

  ASTCDCompilationUnit cd2 = null;

  CDWrapper cdw1 = null;

  CDWrapper cdw2 = null;

  CDSyntaxDiff cdd1 = null;

  protected void generateCDSyntaxDiffTemp(String folder, String cd1Name, String cd2Name) {
    cd1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD/" + folder + "/"
            + cd1Name);

    cd2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD/" + folder + "/"
            + cd2Name);
    CD2CDWrapperGenerator cd1Generator = new CD2CDWrapperGenerator();
    CD2CDWrapperGenerator cd2Generator = new CD2CDWrapperGenerator();
    cdw1 = cd1Generator.generateCDWrapper(cd1, CDSemantics.SIMPLE_CLOSED_WORLD);
    cdw2 = cd2Generator.generateCDWrapper(cd2, CDSemantics.SIMPLE_CLOSED_WORLD);
    CDWrapper2CDSyntaxDiffGenerator cdw2cddiffGenerator4CDW1WithCDW2 =
        new CDWrapper2CDSyntaxDiffGenerator();
    cdd1 = cdw2cddiffGenerator4CDW1WithCDW2.generateCDSyntaxDiff(cdw1, cdw2);
  }

  /********************************************************************
   *********************    Start for Class    ************************
   *******************************************************************/

  @Test
  public void testGenerateODByClass() {
    generateCDSyntaxDiffTemp("Class", "Class1A.cd", "Class1B.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);
    List<String> resultList = printOD(ods);

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
    generateCDSyntaxDiffTemp("Association", "Association1A.cd", "Association1B.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = cd1.getCDDefinition();
    ASTCDDefinition cdDef2 = cd2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByCircleAssociation() {
    generateCDSyntaxDiffTemp("Association", "CircleTest1A.cd", "CircleTest1B.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);
    Assert.assertTrue(ods.size() == 0);
  }

  @Test
  public void testGenerateODByAssocStack4TrgetClass() {
    generateCDSyntaxDiffTemp("Association", "AssocStack4TargetClass1A.cd",
        "AssocStack4TargetClass1B.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = cd1.getCDDefinition();
    ASTCDDefinition cdDef2 = cd2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByDirection1() {
    generateCDSyntaxDiffTemp("Association", "Direction1A.cd", "Direction1G.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = cd1.getCDDefinition();
    ASTCDDefinition cdDef2 = cd2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByDirection2() {
    generateCDSyntaxDiffTemp("Association", "Direction1G.cd", "Direction1A.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);

    List<String> resultList = printOD(ods);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("[Association_A_a_LeftToRight_b_B]-[direction_changed]")
            && e.contains("a_0:A{};")
            && e.contains("b_0:B{};")
            && e.contains("link a_0 (a) -> (b) b_0;")
            && e.contains("link b_0 (b) -> (a) a_0;")));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("[Association_A_a_LeftToRight_b_B]-[cardinality_changed]-[right_cardinality]")
            && e.contains("a_0:A{};")
            && e.contains("b_0:B{};")
            && e.contains("b_1:B{};")
            && e.contains("link a_0 (a) -> (b) b_0;")
            && e.contains("link a_0 (a) -> (b) b_1;")
            && e.contains("link b_0 (b) -> (a) a_0;")));
  }

  /********************************************************************
   *********************    Start for Combination    ******************
   *******************************************************************/
  @Test
  public void testGenerateODByOverlapRefSetAssociation1() {
    generateCDSyntaxDiffTemp("Combination", "OverlapRefSetAssociation1A.cd",
        "OverlapRefSetAssociation1B.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);
    Assert.assertTrue(ods.size() == 0);
  }

  @Test
  public void testGenerateODByOverlapRefSetAssociation2() {
    generateCDSyntaxDiffTemp("Combination", "OverlapRefSetAssociation1C.cd",
        "OverlapRefSetAssociation1B.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);
    Assert.assertTrue(ods.size() == 0);
  }

  @Test
  public void testGenerateODByOverlapRefSetAssociation3() {
    generateCDSyntaxDiffTemp("Combination", "OverlapRefSetAssociation2A.cd",
        "OverlapRefSetAssociation2B.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = cd1.getCDDefinition();
    ASTCDDefinition cdDef2 = cd2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByOverlapRefSetAssociationWithUndefinedLink() {
    generateCDSyntaxDiffTemp("Combination", "OverlapRefSetAssociation3A.cd",
        "OverlapRefSetAssociation3B.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);

    List<String> resultList = printOD(ods);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("[Class_Company]-[edited]-[id]") &&
            e.contains("manager_0:Manager{};") &&
            e.contains(
                "  company_0:Company{\n" +
                "    Integer id = some_type_Integer;\n" +
                "  };") &&
            e.contains("managementTask_0:ManagementTask{};") &&
            e.contains("link manager_0 (work) -> (area) company_0;") &&
            e.contains("link manager_0 (assignee) -> (todo) managementTask_0;") &&
            e.contains("link managementTask_0 (todo) -> (assignee) manager_0;")
    ));

  }

  @Test
  public void testGenerateODByRefSetAssociation() {
    generateCDSyntaxDiffTemp("Combination", "RefSet1A.cd", "RefSet1B.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = cd1.getCDDefinition();
    ASTCDDefinition cdDef2 = cd2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByCombination1() {
    generateCDSyntaxDiffTemp("Combination", "Employees1A.cd", "Employees1B.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = cd1.getCDDefinition();
    ASTCDDefinition cdDef2 = cd2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByCombination2() {
    generateCDSyntaxDiffTemp("Combination", "Employees1B.cd", "Employees1A.cd");
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);

    List<String> resultList = printOD(ods);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("[Association_Employee_work_RightToLeft_area_Area]-[direction_changed]") &&
            e.contains(
                "  employee_0:Employee{\n" +
                "    Integer personId = some_type_Integer;\n" +
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

    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("[Association_Employee_work_RightToLeft_area_Area]-[cardinality_changed]-[left_cardinality]") &&
            e.contains(
                "  employee_0:Employee{\n" +
                "    Integer personId = some_type_Integer;\n" +
                "    PositionKind kind = fullTime;\n" +
                "  };") &&
            e.contains(
                "  employee_1:Employee{\n" +
                "    Integer personId = some_type_Integer;\n" +
                "    PositionKind kind = fullTime;\n" +
                "  };") &&
            e.contains(
                "  company_0:Company{\n" +
                "    String address = some_type_String;\n" +
                "    String country = some_type_String;\n" +
                "  };") &&
            e.contains("link company_0 (area) -> (work) employee_0;") &&
            e.contains("link company_0 (area) -> (work) employee_1;") &&
            e.contains("link employee_0 (work) -> (area) company_0;")
    ));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("[Association_Employee_assignee_Bidirectional_todo_Task]-[direction_changed]") &&
            e.contains(
                "  employee_0:Employee{\n" +
                "    Integer personId = some_type_Integer;\n" +
                "    PositionKind kind = fullTime;\n" +
                "  };") &&
            e.contains(
                "  task_0:Task{\n" +
                "    Integer taskId = some_type_Integer;\n" +
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
  }

//  @Test
//  public void testGenerateODByCombinationTest () {
//    generateCDSyntaxDiffTemp("Combination", "test1.cd", "test2.cd");
//    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
//    List<ASTODArtifact> ods = odGenerator.generateObjectDiagrams(cdw1, cdd1);
//
//    List<String> resultList = printOD(ods);
//    for (int i = 0; i < resultList.size(); i++) {
//      System.out.println(resultList.get(i));
//    }
//  }

}
