package de.monticore.cd2smt.ODArtifacts;


import com.microsoft.z3.BoolSort;
import com.microsoft.z3.FuncDecl;
import de.monticore.cdassociation._ast.ASTCDAssociation;

public class LinkedSMTObject {
  protected SMTObject linkedObject;
  protected ASTCDAssociation association;
  protected boolean isLeft;
  FuncDecl<BoolSort> assocFunc;

  public LinkedSMTObject(ASTCDAssociation association, SMTObject linkedObject, FuncDecl<BoolSort> assocFunc, boolean isLeft) {
    this.isLeft = isLeft;
    this.assocFunc = assocFunc;
    this.linkedObject = linkedObject;
    this.association = association;
  }

  public ASTCDAssociation getAssociation() {
    return association;
  }

  public FuncDecl<BoolSort> getAssocFunc() {
    return assocFunc;
  }

  public SMTObject getLinkedObject() {
    return linkedObject;
  }

  public boolean isLeft() {
    return isLeft;
  }

  public boolean isRight() {
    return !isLeft;
  }
}
