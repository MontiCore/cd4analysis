/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.association;

/**
 * A matching direction indicates whether two association match in the same direction or in the
 * reverse direction. This is required as associations in CD4A are defined in a textual syntax where
 * the same association between two classes 'A' and 'B' can be defined in two different ways:
 *
 * <ul>
 * <li>either: <code>A -> B</code>
 * <li>or: <code>B <- A</code>
 * </ul>
 *
 * Although, both have the same semantic meaning, they are represented in different ways in the AST.
 * In the first case, 'A' is on the left side and 'B' is on the right side of the association. In
 * the second case, 'B' is on the left side and 'A' is on the right side of the association.
 */
public enum AssocMatchDirection {
  /**
   * The association matches in the same direction as the reference association. i.e. left-left,
   * right-right
   */
  SAME_DIRECTION,
  
  /**
   * The association matches in the reverse direction as the reference association. i.e. left-right,
   * right-left
   */
  REVERSE_DIRECTION;
}
