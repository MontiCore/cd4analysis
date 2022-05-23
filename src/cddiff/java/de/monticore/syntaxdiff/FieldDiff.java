package de.monticore.syntaxdiff;

import java.util.Optional;

public class FieldDiff<Op, ASTElementCD1, ASTElementCD2> {
  protected Op operation;

  protected ASTElementCD1 cd1Value;

  protected ASTElementCD2 cd2Value;

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

  public Optional<ASTElementCD1> getCd1Value() {
    if (cd1Value == null) {
      return Optional.empty();
    }
    else {
      return Optional.of(cd1Value);
    }
  }

  public Optional<ASTElementCD2> getCd2Value() {
    if (cd2Value == null) {
      return Optional.empty();
    }
    else {
      return Optional.of(cd2Value);
    }
  }

  public FieldDiff(){
    this.operation = null;
    this.cd1Value = null;
    this.cd2Value = null;
  }

  public FieldDiff(Op op, ASTElementCD1 cd1Value, ASTElementCD2 cd2Value) {
    this.operation = op;
    this.cd1Value = cd1Value;
    this.cd2Value = cd2Value;
  }

}
