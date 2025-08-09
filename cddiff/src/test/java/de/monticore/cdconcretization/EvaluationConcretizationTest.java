/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconformance.CDConfParameter;
import de.monticore.cdconformance.CDConformanceChecker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class EvaluationConcretizationTest extends AbstractCDConcretizationTest {
  
  @Test
  void testBuilderAndMillPattern() {
    // TODO Remove once we have explicit support for 'forEach' conformance check
    confParameters.add(CDConfParameter.STRICT_PARAMETER_ORDER);
    CDConformanceChecker checker = testConcretizedConformsToRefAndExpectedOut(
        "evaluation/builder/DataModelConc.cd", "evaluation/builder/BuilderAndMillRef.cd",
        "evaluation/builder/BuilderAndMillOut.cd");
    
    // check if bindings are correct
    assertTypeBindingExists(checker, resolveConType("EmployeeBuilder"), "DataClass", "Employee");
    assertTypeBindingExists(checker, resolveConType("DepartmentBuilder"), "DataClass",
        "Department");
    assertAttributeBindingExists(checker, resolveConMethod("EmployeeBuilder.firstName"),
        "DataClass.attribute", "Employee.firstName");
    assertAttributeBindingExists(checker, resolveConMethod("EmployeeBuilder.lastName"),
        "DataClass.attribute", "Employee.lastName");
    assertAttributeBindingExists(checker, resolveConMethod("EmployeeBuilder.salary"),
        "DataClass.attribute", "Employee.salary");
    assertAttributeBindingExists(checker, resolveConMethod("EmployeeBuilder.number"),
        "DataClass.attribute", "Employee.number");
    assertAttributeBindingExists(checker, resolveConMethod("DepartmentBuilder.managerName"),
        "DataClass.attribute", "Department.managerName");
    assertAttributeBindingExists(checker, resolveConMethod("DepartmentBuilder.depId"),
        "DataClass.attribute", "Department.depId");
  }
  
  @Test
  void testGetter() {
    CDConformanceChecker checker = testConcretizedConformsToRefAndExpectedOut(
        "evaluation/getter-setter/DataModelConc.cd", "evaluation/getter-setter/GetterRef.cd",
        "evaluation/getter-setter/GetterOut.cd");
    
    // check if bindings are correct
    assertAttributeBindingExists(checker, resolveConMethod("Employee.getFirstName"),
        "DataClass.attribute", "Employee.firstName");
    assertAttributeBindingExists(checker, resolveConMethod("Employee.getLastName"),
        "DataClass.attribute", "Employee.lastName");
    assertAttributeBindingExists(checker, resolveConMethod("Employee.getSalary"),
        "DataClass.attribute", "Employee.salary");
    assertAttributeBindingExists(checker, resolveConMethod("Employee.getNumber"),
        "DataClass.attribute", "Employee.number");
    assertAttributeBindingExists(checker, resolveConMethod("Department.getManagerName"),
        "DataClass.attribute", "Department.managerName");
    assertAttributeBindingExists(checker, resolveConMethod("Department.getDepId"),
        "DataClass.attribute", "Department.depId");
  }
  
  @Test
  void testSetter() {
    // TODO Remove once we have explicit support for 'forEach' conformance check
    confParameters.add(CDConfParameter.STRICT_PARAMETER_ORDER);
    CDConformanceChecker checker = testConcretizedConformsToRefAndExpectedOut(
        "evaluation/getter-setter/DataModelConc.cd", "evaluation/getter-setter/SetterRef.cd",
        "evaluation/getter-setter/SetterOut.cd");
    
    // check if bindings are correct
    assertAttributeBindingExists(checker, resolveConMethod("Employee.setFirstName"),
        "DataClass.attribute", "Employee.firstName");
    assertAttributeBindingExists(checker, resolveConMethod("Employee.setLastName"),
        "DataClass.attribute", "Employee.lastName");
    assertAttributeBindingExists(checker, resolveConMethod("Employee.setSalary"),
        "DataClass.attribute", "Employee.salary");
    assertAttributeBindingExists(checker, resolveConMethod("Employee.setNumber"),
        "DataClass.attribute", "Employee.number");
    assertAttributeBindingExists(checker, resolveConMethod("Department.setManagerName"),
        "DataClass.attribute", "Department.managerName");
    assertAttributeBindingExists(checker, resolveConMethod("Department.setDepId"),
        "DataClass.attribute", "Department.depId");
  }
  
  @Test
  void testVisitorPattern() {
    // TODO Remove once we have explicit support for 'forEach' conformance check
    confParameters.add(CDConfParameter.STRICT_PARAMETER_ORDER);
    CDConformanceChecker checker = testConcretizedConformsToRefAndExpectedOut(
        "evaluation/visitor/VisitorConc.cd", "evaluation/visitor/VisitorRef.cd",
        "evaluation/visitor/VisitorOut.cd");
    
    assertTypeBindingExists(checker, resolveConType("NodeVisitor"), "Visitable", "Node");
    assertTypeBindingExists(checker, resolveConMethod("NodeVisitor.visit(LeafNode)"),
        "ConcreteVisitable", "LeafNode");
    assertTypeBindingExists(checker, resolveConMethod("NodeVisitor.visit(InnerNode)"),
        "ConcreteVisitable", "InnerNode");
    assertTypeBindingExists(checker, resolveConMethod("NodeVisitor.visit(RootNode)"),
        "ConcreteVisitable", "RootNode");
  }
  
  /**
   * This test shows a limitation of the current incarnation binding concept. Currently,
   * incarnations are completely independent of each other.
   * For each incarnation C of a reference type R we attach a binding R=C to C. This is useful,
   * e.g., in the Getter/Setter example where we want to limit the attribute incarnations to only
   * these within the specific type incarnation.<br>
   * <br>
   * FUTURE work: To implement cross-incarnation-references, we need add a variant of the forEach
   * stereotype that considers all incarnation independently of the current binding context, e.g.:
   * <pre>
   * &lt;&lt;forEachGlobal="Microservice as OtherService"&gt;&gt; void sendToOtherService(...)
   * </pre>
   * (see thesis for details).
   */
  @Test
  @Disabled("shows limitation of current concept")
  void testCrossReferences() {
    // TODO Remove once we have explicit support for 'forEach' conformance check
    confParameters.add(CDConfParameter.STRICT_PARAMETER_ORDER);
    CDConformanceChecker checker = testConcretizedConformsToRefAndExpectedOut(
        "evaluation/cross-references/MicroserviceConc.cd",
        "evaluation/cross-references/MicroserviceRef.cd",
        "evaluation/cross-references/MicroserviceOut.cd");
  }
  
  @Nested
  class Banking {
    
    @Test
    void singleInc() {
      testConcretizedConformsToRefAndExpectedOut("evaluation/banking/singleInc/BankingConc.cd",
          "evaluation/banking/BankingRef.cd", "evaluation/banking/singleInc/BankingOut.cd");
    }
    
    /*
     * Should be resolved when using new incarnation mapping/binding abstractions.
     * See https://git.rwth-aachen.de/se-student/theses/ma-jorden/master-theses/-/issues/68
     */
    @Disabled("currently not working because 'bind' is not considered when completing associations")
    @Test
    void multiInc() {
      testConcretizedConformsToRefAndExpectedOut("evaluation/banking/multiInc/BankingConc.cd",
          "evaluation/banking/BankingRef.cd", "evaluation/banking/multiInc/BankingOut.cd");
    }
    
    @Test
    void usageByExtension() {
      testConcretizedConformsToRefAndExpectedOut(
          "evaluation/banking/usageByExtension/BankingConc.cd", "evaluation/banking/BankingRef.cd",
          "evaluation/banking/usageByExtension/BankingOut.cd");
    }
    
  }
  
  @Nested
  class Observer {
    
    @Test
    void mutualObservers() {
      parseModels("evaluation/observer/mutualObservers/MutualObserversConc.cd",
          "evaluation/observer/ObserverRef.cd");
      ASTCDCompilationUnit expectedCD = parseCD(
          "evaluation/observer/mutualObservers/MutualObserversOut.cd");
      
      ConcretizationCompleter completer = new ConcretizationCompleter("ref1", confParameters);
      
      // 1. concretize and check conformance
      try {
        // TODO Improve API to handle multiple mappings after we tested the basics
        completer.completeCD(conCD, refCD);
        completer.setMapping("ref2");
        completer.completeCD(conCD, refCD);
      }
      catch (CompletionException e) {
        fail("CompletionException", e);
      }
      System.out.println("Concretized CD:");
      System.out.println(CD4CodeMill.prettyPrint(conCD, false));
      
      assertNoFindings("Findings while concretizing CD");
      // 2. check if concretized CD equals expected output
      assertTrue(conCD.deepEquals(expectedCD, false), "Concretized output does not match expected");
    }
    
  }
  
}
