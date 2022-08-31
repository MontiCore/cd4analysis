/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.log;

/**
 * Merge Phase indicates steps in the model merging process for logging and tracing purposes.
 */
public enum MergePhase {
  NONE, PREPARING, MATCHING, CD_MERGING, TYPE_MERGING, ATTRIBUTE_MERGING, ASSOCIATION_MERGING,
  MODEL_REFACTORING, VALIDATION, FINALIZING
}
