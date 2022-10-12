package de.monticore.cd2smt.context.CDArtifacts;

import de.monticore.cdbasis._ast.ASTCDClass;

public class SMTClass extends SMTCDType {
  private ASTCDClass Class;
@Override
  public ASTCDClass getASTCDType() {
    return Class;
  }

  public void setCDType(ASTCDClass aClass) {
    Class = aClass;
  }


}
