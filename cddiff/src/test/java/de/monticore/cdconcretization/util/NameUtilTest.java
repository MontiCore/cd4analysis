/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NameUtilTest {
  
  @ParameterizedTest
  @MethodSource
  void testAdaptTemplatedName(String templatedName, String variable, String value,
      Optional<String> expected) {
    assertEquals(expected, NameUtil.adaptTemplatedName(templatedName, variable, value));
  }
  
  private static Stream<Arguments> testAdaptTemplatedName() {
    return Stream.of(Arguments.of("DataClassBuilder", "DataClass", "Employee", Optional.of(
        "EmployeeBuilder")), Arguments.of("DataClassBuilder", "NonExistingTemplate", "Employee",
            Optional.empty()), Arguments.of("dataClass", "DataClass", "Employee", Optional.of(
                "employee")), Arguments.of("hasModifiedAttribute", "attribute", "firstName",
                    Optional.of("hasModifiedFirstName")),
        // An infix is only replaced if it matches the template exactly or capitalized, but NOT
        // uncapitalized!
        Arguments.of("testForlowercaseinfix", "LowerCaseInfix", "willNotBeUsed", Optional.empty()));
  }
  
  @ParameterizedTest
  @MethodSource
  void testExtractTemplateVariable(String templatedName, String variable, String adaptedName,
      Set<String> expected) {
    assertEquals(expected, NameUtil.extractTemplateVariableCandidates(templatedName, variable,
        adaptedName));
  }
  
  private static Stream<Arguments> testExtractTemplateVariable() {
    return Stream.of(Arguments.of("DataClassBuilder", "DataClass", "EmployeeBuilder", Set.of(
        "Employee")), Arguments.of("DataClassBuilder", "NonExistingTemplate", "DataClassBuilder",
            Set.of()), Arguments.of("dataClass", "DataClass", "employee", Set.of("employee",
                "Employee")), Arguments.of("hasModifiedAttribute", "attribute",
                    "hasModifiedFirstName", Set.of("FirstName", "firstName")),
        // An infix is only replaced if it matches the template exactly or capitalized, but NOT
        // uncapitalized!
        Arguments.of("testForlowercaseinfix", "LowerCaseInfix", "willNotBeUsed", Set.of()));
  }
  
}
