package de.monticore.cd2smt.context.CDArtifacts;

import de.monticore.cd2smt.Helper.CDHelper;
import de.monticore.cd2smt.Helper.SMTNameHelper;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import de.monticore.cd2smt.context.CDArtifacts.SMTClass;
import de.monticore.cd2smt.context.CDContext;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;

public class SMTAssociation {
  private  SMTClass left;
  private  String leftRole;
  private  String rightRole;
  private  SMTClass right;
  private  FuncDecl<BoolSort> assocFunc;
  private String name;


  public SMTAssociation() {}

  public FuncDecl<BoolSort> getAssocFunc() {
    return assocFunc;
  }

  boolean isPresentName() {
    return name != null;
  }

  public SMTClass getLeft() {
    return left;
  }

  public SMTClass getRight() {
    return right;
  }

  public String getRightRole() {
    return rightRole;
  }

  public String getLeftRole() {
    return leftRole;
  }

  public String getName() {
    return name;
  }

  public void setRightRole(String rightRole) {
    this.rightRole = rightRole;
  }

  public void setLeftRole(String leftRole) {
    this.leftRole = leftRole;
  }

  public void setAssocFunc(FuncDecl<BoolSort> assocFunc) {
    this.assocFunc = assocFunc;
  }

  public void setLeft(SMTClass left) {
    this.left = left;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRight(SMTClass right) {
    this.right = right;
  }
}
