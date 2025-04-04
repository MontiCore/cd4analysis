package de.monticore.cdconcretization.util;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.se_rwth.commons.Names;

import java.util.List;

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
    return Names.constructQualifiedName(declaringTypeNameParts.subList(1, declaringTypeNameParts.size()));
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
}
