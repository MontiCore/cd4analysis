/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization;

import static org.junit.jupiter.api.Assertions.fail;

import de.monticore.cdbasis._ast.ASTCDClass;
import java.util.Arrays;
import java.util.List;

import de.monticore.cdconformance.CDConformanceChecker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AttributeConcretizationTest extends AbstractCDConcretizationTest {
  
  /**
   * Test that checks if all the attributes in the reference CD that are missing in the concrete CD
   * are added based on predefined CDs.
   */
  @Test
  void testMissingAttributes() {
    testConcretizedEqualsRef("attributes/valid/AttributesMissingConc.cd",
        "attributes/valid/AttributesMissingRef.cd");
  }
  
  @Test
  void testTwoMissingAttributes() {
    testConcretizedEqualsRef("attributes/valid/TwoAttributesMissingConc.cd",
        "attributes/valid/TwoAttributesMissingRef.cd");
  }
  
  @Test
  void testTwoMissingAttributesOneMatch() {
    testConcretizedEqualsRef("attributes/valid/TwoAttributesMissingOneMatchConc.cd",
        "attributes/valid/TwoAttributesMissingOneMatchRef.cd");
  }
  
  /**
   * The attributes expected by the reference class are already present in the direct superclass of
   * 'Employee'. The concretization should not add them again.
   */
  @Test
  void testAttributeExistsInConcreteSuperclass() {
    testConcretizedConformsToRefAndExpectedOut("attributes/valid/AttributeInSuperClassConc.cd",
        "attributes/valid/AttributeInSuperClassRef.cd",
        "attributes/valid/AttributeInSuperClassOut.cd");
    
    // The conformance check is not enough here. The tool must not add attributes to the concrete
    // class if they
    // are already inherited from a superclass.
    List<String> attributesInSuperclass = Arrays.asList("firstName", "lastName");
    
    ASTCDClass employeeClass = conCD.getCDDefinition().getCDClassesList().stream().filter(
        cdClass -> cdClass.getName().equals("Employee")).findFirst().orElseThrow();
    employeeClass.getCDAttributeList().stream().filter(cdAttribute -> attributesInSuperclass
        .contains(cdAttribute.getName())).findAny().ifPresent(cdAttribute -> {
          fail("Attribute " + cdAttribute
              + " should not be added to the concrete class 'Employee' as it is already "
              + "inherited from the superclass 'Person'");
        });
  }
  
  /**
   * The attributes expected by the reference class are already present "deep" in the class
   * hierarchy of 'Employee'. The concretization should not add them again.
   */
  @Test
  void testAttributeExistsInConcreteDeepSuperclass() {
    testConcretizedConformsToRefAndExpectedOut("attributes/valid/AttributeInDeepSuperClassConc.cd",
        "attributes/valid/AttributeInDeepSuperClassRef.cd",
        "attributes/valid/AttributeInDeepSuperClassOut.cd");
    
    // The conformance check is not enough here. The tool must not add attributes to the concrete
    // class if they
    // are already inherited from a superclass.
    List<String> attributesInSuperclass = Arrays.asList("firstName", "lastName");
    
    ASTCDClass employeeClass = conCD.getCDDefinition().getCDClassesList().stream().filter(
        cdClass -> cdClass.getName().equals("Employee")).findFirst().orElseThrow();
    employeeClass.getCDAttributeList().stream().filter(cdAttribute -> attributesInSuperclass
        .contains(cdAttribute.getName())).findAny().ifPresent(cdAttribute -> {
          fail("Attribute " + cdAttribute
              + " should not be added to the concrete class 'Employee' as it is already "
              + "inherited from the superclass 'Person'");
        });
  }
  
  @Test
  void testAttributeForEachAttribute() {
    testConcretizedConformsToRefAndExpectedOut("attributes/forEach/ForEachAttributeConc.cd",
        "attributes/forEach/ForEachAttributeRef.cd", "attributes/forEach/ForEachAttributeOut.cd");
  }
  
  @Test
  void testAttributeForEachAttributeDifferentName() {
    testConcretizedConformsToRefAndExpectedOut(
        "attributes/forEach/ForEachAttributeDifferentNameConc.cd",
        "attributes/forEach/ForEachAttributeDifferentNameRef.cd",
        "attributes/forEach/ForEachAttributeDifferentNameOut.cd");
  }
  
  @Test
  void testAttributeForEachAttributeDifferentType() {
    testConcretizedConformsToRefAndExpectedOut(
        "attributes/forEach/ForEachAttributeDifferentTypeConc.cd",
        "attributes/forEach/ForEachAttributeDifferentTypeRef.cd",
        "attributes/forEach/ForEachAttributeDifferentTypeOut.cd");
  }
  
  /**
   * We have different names of the 'forEach' annotated attribute and the reference target
   * attribute. Also, we have multiple incarnations of the class with the target attribute.
   * Therefore, the attributes in Builder class get two suffixes, one for the type incarnation and
   * one for the target attribute incarnation name. If we want to have one Builder incarnation per
   * type incarnation, we need to add an additional <<forEach="DataClass">> to the reference Builder
   * class
   */
  @Test
  void testAttributeForEachAttributeDifferentNameClassMI() {
    CDConformanceChecker checker = testConcretizedConformsToRefAndExpectedOut(
        "attributes/forEach/ForEachAttributeDifferentNameClassMIConc.cd",
        "attributes/forEach/ForEachAttributeDifferentNameRef.cd",
        "attributes/forEach/ForEachAttributeDifferentNameClassMIOut.cd");
    
    // check if bindings are correct
    assertAttributeBindingExists(checker, resolveConField("Builder.attrCopy_Employee_firstName"),
        "DataClass.attribute", "Employee.firstName");
    assertAttributeBindingExists(checker, resolveConField("Builder.attrCopy_Employee_lastName"),
        "DataClass.attribute", "Employee.lastName");
    assertAttributeBindingExists(checker, resolveConField("Builder.attrCopy_Employee_salary"),
        "DataClass.attribute", "Employee.salary");
    assertAttributeBindingExists(checker, resolveConField("Builder.attrCopy_Employee_number"),
        "DataClass.attribute", "Employee.number");
    assertAttributeBindingExists(checker, resolveConField("Builder.attrCopy_Department_depId"),
        "DataClass.attribute", "Department.depId");
    assertAttributeBindingExists(checker, resolveConField(
        "Builder.attrCopy_Department_managerName"), "DataClass.attribute",
        "Department.managerName");
  }
  
  /**
   * We have a 'forEach' annotated attribute in the reference CD. But the concrete CD has no
   * incarnations of the referenced target attribute!
   */
  @Disabled("does not work until we have 'matchStructure' or 'optional stereotype")
  @Test
  void testAttributeForEachAttributeNoTargetIncarnations() {
    testConcretizedConformsToRefAndExpectedOut(
        "attributes/forEach/ForEachAttributeNoTargetIncConc.cd",
        "attributes/forEach/ForEachAttributeNoTargetIncRef.cd",
        "attributes/forEach/ForEachAttributeNoTargetIncOut.cd");
  }
  
  /**
   * Same as 'testAttributeForEachAttribute' but now with inheritance of some attribute
   * incarnations from a superclass.<br>
   * <br>
   * Currently disabled as the completed attributes get a suffix for the declaring type which is not
   * expected here. Seems as it could be easily fixed in
   * {@link de.monticore.cdconcretization.type.attribute.ForEachAttributeInTypeCompleter} but
   * requires some deeper changes in how we can query attribute incarnations.
   * 1. we need methods to query attributes/methods filtered by declaring type incarnation (but
   * still considering supertypes)
   * 2. we need to rethink some filtering because of conflicting bindings. Currently, attributes
   * shifted to a superclass are filtered out because their declaring type is not a valid
   * incarnation of the declaring reference type.
   */
  @Disabled("completed attributes get suffix for declaring type which is not expected here")
  @Test
  void testAttributeForEachAttributeInheritance() {
    testConcretizedConformsToRefAndExpectedOut(
        "attributes/forEach/ForEachAttributeInheritanceConc.cd",
        "attributes/forEach/ForEachAttributeRef.cd",
        "attributes/forEach/ForEachAttributeInheritanceOut.cd");
  }
  
  /**
   * An underspecified attribute (type: any) needs to be incarnated at least once. Otherwise, we
   * would have to add an attribute of type 'any' to the concrete CD which is not allowed.
   */
  @Test
  void testAttributeTypeUnderspecifiedNoIncarnationError() {
    try {
      parseAndConcretize("attributes/underspecified/AttributeTypeUnderspecifiedNoIncConc.cd",
          "attributes/underspecified/AttributeTypeUnderspecifiedRef.cd");
      fail("Expected CompletionException. But the concretization was successful.");
    }
    catch (CompletionException e) {
      System.out.println("Completion failed as expected: " + e.getMessage());
    }
  }
  
  @Test
  void testAttributeTypeUnderspecifiedDifferentIncarnationTypes() {
    testConcretizedConformsToRefAndExpectedOut(
        "attributes/underspecified/AttributeTypeUnderspecifiedDifferentIncTypesConc.cd",
        "attributes/underspecified/AttributeTypeUnderspecifiedRef.cd",
        "attributes/underspecified/AttributeTypeUnderspecifiedDifferentIncTypesOut.cd");
  }
  
}
