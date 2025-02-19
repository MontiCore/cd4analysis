/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators.matcher;

public enum MatchResult {
  /**
   * Apply the decorator for this element
   */
  APPLY,
  /**
   * Defer to te elements parent
   */
  DEFAULT,
  /**
   * Do not apply the decorator for this element
   */
  IGNORE
}
