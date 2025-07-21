/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.util;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

public class NameUtil {
  
  private NameUtil() {
    // Prevent instantiation
  }
  
  /**
   * Returns the qualified name of the given type symbol without the first qualifier part.
   *
   * @param qualifiedName a qualified name, e.g., "de.monticore.cd.CDType"
   * @return the qualified name without the first qualifier part, e.g., "monticore.cd.CDType"
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
   * Unescapes a qualified name from an identifier by replacing '_' with '.'.
   *
   * @param identifier the identifier to unescape
   * @return the unescaped qualified name
   */
  public static String unescapeQualifiedNameFromIdentifier(String identifier) {
    return identifier.replace('_', '.');
  }
  
  /**
   * Adapts a template name in a given name with a certain value. This function can handle type
   * names as well as variable names (attributes, parameters) and method names. It uses the
   * following rules:
   *
   * <ul>
   * <li>If the name contains the variable, it replaces it with the value. e.g., if name is
   * 'DataClassBuilder', variable 'DataClass' and value 'Employee', the output is
   * 'EmployeeBuilder'.
   * <li>If the name starts with the uncapitalized variable, it replaces it with the uncapitalized
   * value. e.g., if name is 'dataClass', variable is 'DataClass', and value 'Employee', the
   * output is 'employee'.
   * <li>If the name contains the capitalized variable, it replaces it with the capitalized value.
   * e.g., if name is 'hasModifiedAttribute', variable is 'attribute', and value is
   * 'firstName', the output is 'hasModifiedFirstName'.
   * </ul>
   *
   * @param templatedName the name to be modified.
   * @param variable the variable to be replaced.
   * @param value the value to replace the variable with.
   * @return an Optional containing the modified name if the template was found and replaced, or an
   * empty Optional if the variable was not found in the name.
   */
  public static Optional<String> adaptTemplatedName(String templatedName, String variable,
      String value) {
    if (templatedName.contains(variable)) {
      String newName = templatedName.replace(variable, value);
      return Optional.of(newName);
    }
    else if (templatedName.startsWith(StringUtils.uncapitalize(variable))) {
      // e.g., if variable name is "dataClass" and template is "DataClass", we want to replace
      // "dataClass" with "employee" if paramType is "Employee"
      String newName = templatedName.replaceFirst(StringUtils.uncapitalize(variable), StringUtils
          .uncapitalize(value));
      return Optional.of(newName);
    }
    else if (templatedName.contains(StringUtils.capitalize(variable))) {
      // e.g., if variable name is "modifiedAttribute" and template is "attribute", we want to
      // replace
      // "Attribute" with "FirstName" if value is "firstName"
      String newName = templatedName.replace(StringUtils.capitalize(variable), StringUtils
          .capitalize(value));
      return Optional.of(newName);
    }
    return Optional.empty();
  }
  
  /**
   * Extracts all possible candidates for a template variable from a templated name and an adapted
   * name. This method checks if the templated name contains the variable in different forms
   * (e.g., as is, uncapitalized, or capitalized) and extracts the corresponding values from the
   * adapted name.<br>
   * It returns a set of all possible candidates that could have been used to get the adapted name
   * from the templated name.<br>
   * <br>
   * See {@link #adaptTemplatedName(String, String, String)} for the rules used to adapt the
   * templated name.
   *
   * @param templatedName the name with the template variable
   * @param variable the variable to extract candidates for
   * @param adaptedName the name that has been adapted with the variable value
   * @return a set of candidates that could have been used to adapt the templated name to the
   * adapted name. If no candidates are found, an empty set is returned.
   */
  public static Set<String> extractTemplateVariableCandidates(String templatedName, String variable,
      String adaptedName) {
    if (templatedName.contains(variable)) {
      return extractTemplateVariableRaw(templatedName, variable, adaptedName).stream().collect(
          Collectors.toSet());
    }
    else if (templatedName.startsWith(StringUtils.uncapitalize(variable))) {
      // e.g., if variable name is "dataClass" and template is "DataClass", we want to replace
      // "dataClass" with "employee" if paramType is "Employee"
      Optional<String> extractedValue = extractTemplateVariableRaw(templatedName, StringUtils
          .uncapitalize(variable), adaptedName);
      if (extractedValue.isPresent()) {
        String capitalized = StringUtils.capitalize(extractedValue.get());
        return Set.of(extractedValue.get(), capitalized);
      }
      return Collections.emptySet();
    }
    else if (templatedName.contains(StringUtils.capitalize(variable))) {
      // e.g., if variable name is "modifiedAttribute" and template is "attribute", we want to
      // replace
      // "Attribute" with "FirstName" if value is "firstName"
      Optional<String> extractedValue = extractTemplateVariableRaw(templatedName, StringUtils
          .capitalize(variable), adaptedName);
      if (extractedValue.isPresent()) {
        String uncapitalize = StringUtils.uncapitalize(extractedValue.get());
        return Set.of(extractedValue.get(), uncapitalize);
      }
      return Collections.emptySet();
    }
    return Collections.emptySet();
  }
  
  /**
   * Extracts the value of a template variable from a templated name and an adapted name.
   * This method uses regex to match the variable in the templated name and extract its value
   * from the adapted name.
   * It assumes that the templated name contains the variable at least once.
   *
   * @param templatedName the name with the template variable
   * @param variable the variable to extract
   * @param adaptedName the name that has been adapted with the variable value
   * @return an Optional containing the extracted value if found, or an empty Optional if not found
   */
  private static Optional<String> extractTemplateVariableRaw(String templatedName, String variable,
      String adaptedName) {
    // make sure we have at least one occurrence of the variable name in the templated name
    // if not we cannot extract the variable value
    if (!templatedName.contains(variable)) {
      return Optional.empty();
    }
    // Split the templated name into parts around the variable
    String[] parts = templatedName.split(Pattern.quote(variable), -1); // include trailing empty parts
    
    // Build the regex pattern with capture groups for the variable
    StringBuilder regexBuilder = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      regexBuilder.append(Pattern.quote(parts[i]));
      if (i < parts.length - 1) {
        regexBuilder.append("(.*?)"); // capture group for each variable occurrence
      }
    }
    
    Pattern pattern = Pattern.compile(regexBuilder.toString());
    Matcher matcher = pattern.matcher(adaptedName);
    if (!matcher.matches()) {
      // Adapted name does not match the pattern defined by the templated name. Cannot extract the
      // variable value.
      return Optional.empty();
    }
    
    String extracted = matcher.group(1);
    // Check all captures are the same
    for (int i = 2; i <= matcher.groupCount(); i++) {
      if (!extracted.equals(matcher.group(i))) {
        Log.warn("Inconsistent variable values detected in name '" + adaptedName + "'."
            + "Different values for variable '" + variable + "' in templated name '" + templatedName
            + "': '" + extracted + "' and '" + matcher.group(i) + "'.");
        return Optional.empty();
      }
    }
    // we found a match for the variable
    return Optional.of(extracted);
  }
  
}
