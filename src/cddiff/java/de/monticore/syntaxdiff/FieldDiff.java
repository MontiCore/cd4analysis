package de.monticore.syntaxdiff;

import java.util.Optional;
/**
 * Diff Type for Fields
 * Use the constructor to create a diff between two given fields
 * This diff type contains information extracted from the provided fields, especially the type of change
 */
public class FieldDiff<Op, ASTNodeType> {
  protected final Op operation;

  protected final ASTNodeType cd1Value;

  protected final ASTNodeType cd2Value;

  public boolean isPresent(){
    return getOperation().isPresent();
  }

  public Optional<Op> getOperation() {
    if (operation == null) {
      return Optional.empty();
    }
    else {
      return Optional.of(operation);
    }
  }

  public Optional<ASTNodeType> getCd1Value() {
    if (cd1Value == null) {
      return Optional.empty();
    }
    else {
      return Optional.of(cd1Value);
    }
  }

  public Optional<ASTNodeType> getCd2Value() {
    if (cd2Value == null) {
      return Optional.empty();
    }
    else {
      return Optional.of(cd2Value);
    }
  }
  /**
   * Constructor of the field diff type for empty(equal) fields
   */
  public FieldDiff(){
    this.operation = null;
    this.cd1Value = null;
    this.cd2Value = null;
  }
  /**
   * Constructor of the field diff type
   * @param op Operation between both fields
   * @param cd1Value Field from the original model
   * @param cd2Value Field from the target(new) model
   */
  public FieldDiff(Op op, ASTNodeType cd1Value, ASTNodeType cd2Value) {
    this.operation = op;
    this.cd1Value = cd1Value;
    this.cd2Value = cd2Value;
  }

}
