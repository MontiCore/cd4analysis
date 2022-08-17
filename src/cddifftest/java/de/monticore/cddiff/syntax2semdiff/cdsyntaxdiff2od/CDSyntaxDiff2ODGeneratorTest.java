package de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od;

import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.cddiff.alloycddiff.alloyRunner.AlloyDiffSolution;
import de.monticore.cddiff.alloycddiff.AlloyCDDiff;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cddiff.CDDiffTestBasis;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.cddiff.ow2cw.ReductionTrafo;
import de.monticore.cddiff.syntax2semdiff.JavaCDDiff;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.CD2CDWrapperGenerator;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDWrapper;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.CDWrapper2CDSyntaxDiffGenerator;
import de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel.CDSyntaxDiff;
import de.se_rwth.artifacts.lang.matcher.CDDiffOD2CDMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.GenerateODHelper.printOD;
import static org.junit.Assert.assertNotNull;

public class CDSyntaxDiff2ODGeneratorTest extends CDDiffTestBasis {
  ASTCDCompilationUnit ast1 = null;

  ASTCDCompilationUnit ast2 = null;

  CDWrapper cdw1 = null;

  CDWrapper cdw2 = null;

  CDSyntaxDiff cdd1 = null;

  protected void generateCDSyntaxDiffTemp(
      String folder,
      String cd1Name,
      String cd2Name,
      CDSemantics cdSemantics) {

    ast1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD/"
            + folder + "/" + cd1Name);

    ast2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD/"
            + folder + "/" + cd2Name);

    CD2CDWrapperGenerator cd1Generator = new CD2CDWrapperGenerator();
    CD2CDWrapperGenerator cd2Generator = new CD2CDWrapperGenerator();
    cdw1 = cd1Generator.generateCDWrapper(ast1, cdSemantics);
    cdw2 = cd2Generator.generateCDWrapper(ast2, cdSemantics);
    CDWrapper2CDSyntaxDiffGenerator cdw2cddiffGenerator4CDW1WithCDW2 =
        new CDWrapper2CDSyntaxDiffGenerator();
    cdd1 = cdw2cddiffGenerator4CDW1WithCDW2.generateCDSyntaxDiff(cdw1, cdw2, cdSemantics);
  }

  public void printODs(List<ASTODArtifact> ods) {
    List<String> resultList = printOD(ods);
    for (String s : resultList) {
      System.out.println(s);
    }
  }

  /********************************************************************
   *********************    Start for Class    ************************
   ********************   simple closed world   ***********************
   *******************************************************************/

  @Test
  public void testGenerateODByClass() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Class",
        "Class1A.cd", "Class1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);
    List<String> resultList = printOD(ods);

    JavaCDDiff.printODs2Dir(ods,"target/generated/od-validity-test-cases/Class");

    Assert.assertTrue(resultList.stream().anyMatch(e -> {
      String result =
          "$Class_C$_$deleted$ {\n" +
              "\n" +
              "  c_0:C{};\n" +
              "\n" +
              "}";
      return e.contains(result);
    }));

    Assert.assertTrue(resultList.stream().anyMatch(e -> {
      String result =
          "$Class_B$_$edited$_$myDate_myList_myE_myMap_id_mySet_myOpt$ {\n" +
              "\n" +
              "  b_0:B{\n" +
              "    E myE = e1;\n" +
              "    List<String> myList = [some_type_String,...];\n" +
              "    Map<Integer,E> myMap = [some_type_Integer -> e1,... -> ...];\n" +
              "    Optional<E> myOpt = e1;\n" +
              "    Set<Boolean> mySet = some_type_Set_Boolean;\n"+
              "    int id = some_type_int;\n" +
              "    Date myDate = some_type_Date;\n" +
              "  };\n" +
              "\n" +
              "}";
      return e.contains(result);
    }));

    Assert.assertTrue(resultList.stream().anyMatch(e -> {
      String result =
          "$Enum_E$_$edited$_$e3$ {\n" +
              "\n" +
              "  b_0:B{\n" +
              "    E myE = e3;\n" +
              "    List<String> myList = [some_type_String,...];\n" +
              "    Map<Integer,E> myMap = [some_type_Integer -> e1,... -> ...];\n" +
              "    Optional<E> myOpt = e1;\n" +
              "    Set<Boolean> mySet = some_type_Set_Boolean;\n" +
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
   ********************   simple closed world   ***********************
   *******************************************************************/

  @Test
  public void testGenerateODByAssociation() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Association",
        "Association1A.cd", "Association1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByCircleAssociation() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Association",
        "CircleTest1A.cd", "CircleTest1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);
    Assert.assertEquals(0, ods.size());
  }

  @Test
  public void testGenerateODByAssocStack4TrgetClass() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Association",
        "AssocStack4TargetClass1A.cd",
        "AssocStack4TargetClass1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByDirection1() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Association",
        "Direction1A.cd", "Direction1G.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByDirection2() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Association",
        "Direction1G.cd", "Direction1A.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    JavaCDDiff.printODs2Dir(ods,"target/generated/od-validity-test-cases/Direction");

    List<String> resultList = printOD(ods);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("$Association_A_a_LeftToRight_b_B$_$direction_changed$")
            && e.contains("a_0:A{};")
            && e.contains("b_0:B{};")
            && e.contains("link a_0 (a) -> (b) b_0;")
            && e.contains("link b_0 (b) -> (a) a_0;")));

    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("$Association_A_a_LeftToRight_b_B$_$cardinality_changed$_$right_cardinality$")
            && e.contains("a_0:A{};")
            && e.contains("b_0:B{};")
            && e.contains("b_1:B{};")
            && e.contains("link a_0 (a) -> (b) b_0;")
            && e.contains("link a_0 (a) -> (b) b_1;")
            && e.contains("link b_0 (b) -> (a) a_0;")));
  }

  @Test
  public void testGenerateODBySubclassDiff() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Association",
        "SubclassDiff1A.cd", "SubclassDiff1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  /********************************************************************
   *********************    Start for Combination    ******************
   ********************   simple closed world   ***********************
   *******************************************************************/
  @Test
  public void testGenerateODByOverlapRefSetAssociation1() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Combination",
        "OverlapRefSetAssociation1A.cd",
        "OverlapRefSetAssociation1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);
    Assert.assertEquals(0, ods.size());
  }

  @Test
  public void testGenerateODByOverlapRefSetAssociation2() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Combination",
        "OverlapRefSetAssociation1C.cd",
        "OverlapRefSetAssociation1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);
    Assert.assertEquals(0, ods.size());
  }

  @Test
  public void testGenerateODByOverlapRefSetAssociation3() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Combination",
        "OverlapRefSetAssociation2A.cd",
        "OverlapRefSetAssociation2B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByOverlapRefSetAssociationWithUndefinedLink() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Combination",
        "OverlapRefSetAssociation3A.cd",
        "OverlapRefSetAssociation3B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    JavaCDDiff.printODs2Dir(ods,"target/generated/od-validity-test-cases/Association");

    List<String> resultList = printOD(ods);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("$Class_Company$_$edited$_$id$") &&
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
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Combination",
        "RefSet1A.cd", "RefSet1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByCombination1() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Combination",
        "Employees1A.cd", "Employees1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByCombination2() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Combination",
        "Employees1B.cd", "Employees1A.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    JavaCDDiff.printODs2Dir(ods,"target/generated/od-validity-test-cases//Combination");

    List<String> resultList = printOD(ods);
    Assert.assertTrue(resultList.stream().anyMatch(e ->
        e.contains("$Association_Employee_work_RightToLeft_area_Area$_$direction_changed$") &&
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
        e.contains("$Association_Employee_work_RightToLeft_area_Area$_$cardinality_changed$_$left_cardinality$") &&
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
        e.contains("$Association_Employee_assignee_Bidirectional_todo_Task$_$direction_changed$") &&
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

  @Test
  public void testGenerateODByCombinationLarge() {
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Combination",
        "Holiday1A.cd", "Holiday1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  /********************************************************************
   *********************    Start for Combination    ******************
   *******************  multi-instance closed world  ******************
   *******************************************************************/

  @Test
  public void testGenerateODBySubclassDiff2() {
    CDSemantics cdSemantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Association",
        "SubclassDiff1A.cd", "SubclassDiff1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    JavaCDDiff.printODs2Dir(ods, "target/generated/od-validity-test-cases/Multi-Instance");

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByCombination3() {
    CDSemantics cdSemantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Combination",
        "Employees1A.cd", "Employees1B.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }
  }

  @Test
  public void testGenerateODByCombinationAbstractInterface() {
    CDSemantics cdSemantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
    generateCDSyntaxDiffTemp("Combination",
        "Employees1A.cd", "Employees1C.cd", cdSemantics);
    CDSyntaxDiff2ODGenerator odGenerator = new CDSyntaxDiff2ODGenerator();
    List<ASTODArtifact> ods =
        odGenerator.generateObjectDiagrams(cdw1, cdd1, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }

    Assert.assertEquals(ods.size(), 0);
  }

  @Test
  public void testGenerateODByCombinationNotMatchedAssocInCompareCDW() {
    String filePath1 = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Combination/NotMatchedAssocInCompareCDW1A.cd";
    String filePath2 = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Combination/NotMatchedAssocInCompareCDW1B.cd";
    CDSemantics cdSemantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
    ASTCDCompilationUnit ast1 = parseModel(filePath1);
    ASTCDCompilationUnit ast2 = parseModel(filePath2);
    List<ASTODArtifact> ods = JavaCDDiff.computeSemDiff(ast1, ast2, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }

    Assert.assertTrue(ods.size() > 0);
  }

  @Test
  public void testOpenWorldDiff() {
    String filePath1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees0.cd";
    String filePath2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd";
    CDSemantics cdSemantics = CDSemantics.MULTI_INSTANCE_CLOSED_WORLD;
    ASTCDCompilationUnit ast1 = parseModel(filePath1);
    ASTCDCompilationUnit ast2 = parseModel(filePath2);
    List<ASTODArtifact> ods = JavaCDDiff.computeSemDiff(ast1, ast2, cdSemantics);

    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    ASTCDDefinition cdDef1 = ast1.getCDDefinition();
    ASTCDDefinition cdDef2 = ast2.getCDDefinition();

    for (ASTODArtifact od : ods) {
      Assert.assertTrue(matcher.checkODConsistency(cdDef1, od.getObjectDiagram()));
      Assert.assertFalse(matcher.checkODConsistency(cdDef2, od.getObjectDiagram()));
    }

    Assert.assertEquals(ods.size(), 1);
  }


  /********************************************************************
   ******************** Using SyntaxDiff2SemanticDiff *****************
   *******************************************************************/
  @Test
  public void testSyntaxDiff2SemanticDiff() {
    ast1 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD/Combination"
            + "/Employees1A.cd");

    ast2 = parseModel(
        "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD/Combination"
            + "/Employees1B.cd");

    String res = JavaCDDiff.printSemDiff(ast1, ast2, CDSemantics.MULTI_INSTANCE_CLOSED_WORLD);
    System.out.println(res);
  }

  @Test
  public void testRunningTime4GenerateODs(){
    String filePath1 = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Combination/Holiday1A.cd";
    String filePath2 = "src/cddifftest/resources/de/monticore/cddiff/syntax2semdiff/GenerateOD"
        + "/Combination/Holiday1B.cd";
    String output = "./target/test-running-time/";
    CDSemantics cdSemantics = CDSemantics.SIMPLE_CLOSED_WORLD;
    ASTCDCompilationUnit ast1_old = parseModel(filePath1);
    ASTCDCompilationUnit ast2_old = parseModel(filePath2);
    ASTCDCompilationUnit ast1_new = parseModel(filePath1);
    ASTCDCompilationUnit ast2_new = parseModel(filePath2);
    assertNotNull(ast1_old);
    assertNotNull(ast2_old);
    assertNotNull(ast1_new);
    assertNotNull(ast2_new);

    // old method
    long startTime_old = System.currentTimeMillis();   // start time
    ReductionTrafo.handleAssocDirections(ast1_old, ast2_old);
    Optional<AlloyDiffSolution> optS =
        AlloyCDDiff.cddiff(ast1_old, ast2_old, 2, cdSemantics, output);
    List<ASTODArtifact> ods_old = optS.get().generateODs();
    long endTime_old = System.currentTimeMillis(); // end time

    // new method
    long startTime_new = System.currentTimeMillis();   // start time
    List<ASTODArtifact> ods_new = JavaCDDiff.computeSemDiff(ast1_new, ast2_new, cdSemantics);
    long endTime_new = System.currentTimeMillis(); // end time

    System.out.println("old witness size: " + ods_old.size());
    System.out.println("Running Time of old method: " + (endTime_old - startTime_old) + "ms");
    System.out.println("new witness size: " + ods_new.size());
    System.out.println("Running Time of new method: " + (endTime_new - startTime_new) + "ms");
  }

}
