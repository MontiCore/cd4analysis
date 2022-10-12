package de.monticore.cd2smt.context.CDArtifacts;

import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

public class SMTInterface extends SMTCDType {
  private ASTCDInterface Interface;

  public ASTCDInterface getASTCDType() {
    return Interface;
  }

  @Override
  public void setCDType(ASTCDInterface anInterface) {
    Interface = anInterface;
  }


}
