package de.monticore.cddiff.syndiff;

public enum DiffTypes {
  ADDED_ATTRIBUTE,
  REMOVED_ATTRIBUTE,
  CHANGED_ATTRIBUTE,
  ADDED_ASSOCIATION,
  REMOVED_ASSOCIATION,
  CHANGED_ASSOCIATION_LEFT_MULTIPLICITY,
  CHANGED_ASSOCIATION_RIGHT_MULTIPLICITY,
  CHANGED_ASSOCIATION_DIRECTION, // Do we need to make this even more detailed?
  CHANGED_ASSOCIATION_ROLE,
  ADDED_AGGREGATION,
  REMOVED_AGGREGATION,
  ADDED_OPERATION,
  REMOVED_OPERATION,
  ADDED_INHERITANCE,
  REMOVED_INHERITANCE,
  CHANGED_VISIBILITY,
  STEREOTYPE_DIFFERENCE,
  ADDED_CONSTANTS,
  REMOVED_CONSTANTS,
  TGT_NOT_INSTANTIATABLE,
  ADDED_CLASS,
  CHANGED_TARGET
}
