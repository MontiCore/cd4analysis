package de.monticore.syntaxdiff;

public class FieldDiff<Op, ASTElementCD1, ASTElementCD2>{
  Op operation;
  ASTElementCD1 cd1Value;
  ASTElementCD2 cd2Value;

  public Op getOperation() { return operation; }
  public ASTElementCD1 getCd1Value() { return cd1Value; }
  public ASTElementCD2 getCd2Value() { return cd2Value; }

  public FieldDiff(Op op, ASTElementCD1 cd1Value, ASTElementCD2 cd2Value){
    this.operation = op;
    this.cd1Value = cd1Value;
    this.cd2Value = cd2Value;
  }
}
