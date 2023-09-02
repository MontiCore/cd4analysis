package de.monticore.cddiff.syndiff.imp;

import de.monticore.ast.ASTNode;
import java.util.Optional;

/** TODO: Write Comments */
public class CDNodeDiff<SrcType extends ASTNode, TgtType extends ASTNode>{
  protected Actions action;
  protected final Optional<SrcType> srcValue;
  protected final Optional<TgtType> tgtValue;
  public boolean checkForAction() {
    return getAction().isPresent();
  }

  public Optional<Actions> getAction() {
    if (action == null) {
      return Optional.empty();
    } else {
      return Optional.of(action);
    }
  }
  public Optional<SrcType> getSrcValue() {
    return srcValue;
  }
  public Optional<TgtType> getTgtValue() {
    return tgtValue;
  }
  public CDNodeDiff(Optional<SrcType> srcValue, Optional<TgtType> tgtValue) {
    this.srcValue = srcValue;
    this.tgtValue = tgtValue;
    this.action = findAction();
  }
  public CDNodeDiff(Actions action, Optional<SrcType> srcValue, Optional<TgtType> tgtValue) {
    this.srcValue = srcValue;
    this.tgtValue = tgtValue;
    this.action = action;
  }
  protected Actions findAction() {
    if (srcValue.isPresent() && tgtValue.isPresent() && !srcValue.get().deepEquals(tgtValue.get())) {
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
