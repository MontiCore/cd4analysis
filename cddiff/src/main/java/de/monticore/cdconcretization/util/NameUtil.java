/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.util;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.se_rwth.commons.Names;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class NameUtil {
  
  private NameUtil() {
    // Prevent instantiation
  }
  
  /**
   * Returns the qualified name of the given type symbol without the first qualifier part.
   *
   * @param qualifiedName
   * @return
   */
  public static String removeFirstQualifierPart(String qualifiedName) {
    List<String> declaringTypeNameParts = MCQualifiedNameFacade.createPartList(qualifiedName);
    return Names.constructQualifiedName(declaringTypeNameParts.subList(1, declaringTypeNameParts
        .size()));
  }
  
  /**
   * Escapes a qualified name as an identifier by replacing '.' with '_'.
   *
   * @param qualifiedName the qualified name to escape
   * @return the escaped identifier
   */
  public static String escapeQualifiedNameAsIdentifier(String qualifiedName) {
    return qualifiedName.replace('.', '_');
  }
  
  /**
   * Adapts a template name in a given name with a certain value. This function can handle type
   * names as well as variable names (attributes, parameters) and method names. It uses the
   * following rules:
   *
   * <ul>
   * <li>If the name contains the template, it replaces it with the value. e.g., if name is
   * 'DataClassBuilder', template 'DataClass' and value 'Employee', the output is
   * 'EmployeeBuilder'.
   * <li>If the name starts with the uncapitalized template, it replaces it with the uncapitalized
   * value. e.g., if name is 'dataClass', template is 'DataClass', and value 'Employee', the
   * output is 'employee'.
   * <li>If the name contains the capitalized template, it replaces it with the capitalized value.
   * e.g., if name is 'hasModifiedAttribute', template is 'attribute', and value is
   * 'firstName', the output is 'hasModifiedFirstName'.
   * </ul>
   *
   * @param name the name to be modified.
   * @param template the template to be replaced.
   * @param value the value to replace the template with.
   * @return an Optional containing the modified name if the template was found and replaced, or an
   * empty Optional if the template was not found in the name.
   */
  public static Optional<String> adaptTemplatedName(String name, String template, String value) {
    if (name.contains(template)) {
      String newName = name.replace(template, value);
      return Optional.of(newName);
    }
    else if (name.startsWith(StringUtils.uncapitalize(template))) {
      // e.g., if variable name is "dataClass" and template is "DataClass", we want to replace
      // "dataClass" with "employee" if paramType is "Employee"
      String newName = name.replaceFirst(StringUtils.uncapitalize(template), StringUtils
          .uncapitalize(value));
      return Optional.of(newName);
    }
    else if (name.contains(StringUtils.capitalize(template))) {
      // e.g., if variable name is "modifiedAttribute" and template is "attribute", we want to
      // replace
      // "Attribute" with "FirstName" if value is "firstName"
      String newName = name.replace(StringUtils.capitalize(template), StringUtils.capitalize(
          value));
      return Optional.of(newName);
    }
    return Optional.empty();
  }
  
}
