package de.monticore.cd2smt.context;

public class SMTLink {
 private final SMTAssociation  smtAssociation;
 private final SMTObject leftObject;
 private final SMTObject rightObject;

 public SMTLink(SMTObject leftObject, SMTObject rightObject, SMTAssociation smtAssociation){
   this.leftObject = leftObject ;
   this.rightObject = rightObject ;
   this.smtAssociation = smtAssociation;
 }
  public SMTAssociation getSmtAssociation() {
    return smtAssociation;
  }

  public SMTObject getLeftObject() {
    return leftObject;
  }

  public SMTObject getRightObject() {
    return rightObject;
  }

}
