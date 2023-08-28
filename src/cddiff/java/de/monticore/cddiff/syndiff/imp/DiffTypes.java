package de.monticore.cddiff.syndiff.imp;

public enum DiffTypes {
  ADDED_ATTRIBUTE,
  REMOVED_ATTRIBUTE,
  CHANGED_ATTRIBUTE_TYPE,
  CHANGED_ATTRIBUTE_MODIFIER,
  ADDED_ASSOCIATION,
  REMOVED_ASSOCIATION,
  CHANGED_ASSOCIATION_CARDINALITY,
  CHANGED_ASSOCIATION_LEFT_MULTIPLICITY,
  CHANGED_ASSOCIATION_RIGHT_MULTIPLICITY,
  CHANGED_ASSOCIATION_DIRECTION,
  CHANGED_ASSOCIATION_ROLE,
  CHANGED_ASSOCIATION_NAME,
  CHANGED_ASSOCIATION_CLASS,
  ADDED_AGGREGATION,
  REMOVED_AGGREGATION,
  ADDED_OPERATION,
  REMOVED_OPERATION,
  ADDED_INHERITANCE,
  REMOVED_INHERITANCE,
  CHANGED_VISIBILITY,
  STEREOTYPE_DIFFERENCE,
  ADDED_CONSTANT,
  DELETED_CONSTANT,
  TGT_NOT_INSTANTIATABLE,
  ADDED_CLASS,
  CHANGED_TARGET,
  REVERSED_ASSOCIATION,
  RENAME,
  RELOCATION,
  REFINEMENT,
  EXPANSION,
  DEFAULT_VALUE_CHANGED,
  ABSTRACTION,
  REPURPOSE,
  SCOPE_CHANGE,
  ROLE_CHANGE,
  DEFAULT_VALUE_ADDED,
  DELETED,
  EQUAL,
  TYPE_CHANGE,
  RESTRICTION,
  RESTRICT_INTERVAL,
  EXPAND_INTERVAL,
  EQUAL_INTERVAL,
  BREAKING_CHANGE,
  ASSOCIATION_INHERITED,
  INHERITED_ATTRIBUTE
}
