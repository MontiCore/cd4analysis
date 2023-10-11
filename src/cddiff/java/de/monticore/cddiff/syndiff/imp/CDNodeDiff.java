package de.monticore.cddiff.syndiff.imp;

import de.monticore.ast.ASTNode;
import java.util.Optional;

/** TODO: Write Comments */
public class CDNodeDiff<SrcType extends ASTNode, TgtType extends ASTNode> {
  protected Actions action;
  protected final Optional<SrcType> srcValue;
  protected final Optional<TgtType> tgtValue;

  /**
   * Checks if an action is present.
   *
   * @return true if an action is present, false otherwise.
   */
  public boolean checkForAction() {
    return getAction().isPresent();
  }

  /**
   * Gets the action as an Optional containing an Actions enum value.
   *
   * @return An Optional containing the action if it is not null, otherwise an empty Optional.
   */
  public Optional<Actions> getAction() {
    if (action == null) {
      return Optional.empty();
    } else {
      return Optional.of(action);
    }
  }

  /**
   * Constructs a CDNodeDiff object with source and target values and determines the associated
   * action.
   *
   * @param srcValue The source value as an Optional.
   * @param tgtValue The target value as an Optional.
   */
  public CDNodeDiff(Optional<SrcType> srcValue, Optional<TgtType> tgtValue) {
    this.srcValue = srcValue;
    this.tgtValue = tgtValue;
    this.action = findAction();
  }

  /**
   * Constructs a CDNodeDiff object with a specified action and source and target values.
   *
   * @param action The action associated with the diff.
   * @param srcValue The source value as an Optional.
   * @param tgtValue The target value as an Optional.
   */
  public CDNodeDiff(Actions action, Optional<SrcType> srcValue, Optional<TgtType> tgtValue) {
    this.srcValue = srcValue;
    this.tgtValue = tgtValue;
    this.action = action;
  }

  /**
   * Determines the action to be taken based on the values of srcValue and tgtValue.
   *
   * @return The action to be taken, which can be CHANGED, ADDED, REMOVED, or null if no action is
   *     needed.
   */
  protected Actions findAction() {
    if (srcValue.isPresent()
        && tgtValue.isPresent()
        && !srcValue.get().deepEquals(tgtValue.get())) {
      return Actions.CHANGED;
    } else if (srcValue.isPresent() && tgtValue.isEmpty()) {
      return Actions.ADDED;
    } else if (srcValue.isEmpty() && tgtValue.isPresent()) {
      return Actions.REMOVED;
    } else {
      return null;
    }
  }
}
