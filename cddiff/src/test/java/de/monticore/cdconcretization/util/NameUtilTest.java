/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NameUtilTest {
  
  @ParameterizedTest
  @MethodSource
  void testAdaptTemplatedName(String name, String template, String value,
      Optional<String> expected) {
    assertEquals(expected, NameUtil.adaptTemplatedName(name, template, value));
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
  
}
