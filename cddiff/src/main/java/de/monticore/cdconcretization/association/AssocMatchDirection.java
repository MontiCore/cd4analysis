package de.monticore.cdconcretization.association;

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
