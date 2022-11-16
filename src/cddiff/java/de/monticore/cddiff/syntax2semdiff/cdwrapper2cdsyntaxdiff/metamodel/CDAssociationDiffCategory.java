package de.monticore.cddiff.syntax2semdiff.cdwrapper2cdsyntaxdiff.metamodel;

public enum CDAssociationDiffCategory {
  ORIGINAL,
  DIRECTION_CHANGED_BUT_SAME_MEANING,
  DIRECTION_SUBSET,
  CARDINALITY_SUBSET,
  DELETED,
  DIRECTION_CHANGED,
  CARDINALITY_CHANGED,
  SUBCLASS_DIFF,
  CONFLICTING
}
