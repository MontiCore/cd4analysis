package de.monticore.cd2smt.context;


public class LinkedSMTObject {
  public LinkedSMTObject(SMTObject linkedObject, SMTAssociation smtAssociation, boolean isLeft){
    this.isLeft = isLeft;
    this.linkedObject = linkedObject ;
    this.smtAssociation = smtAssociation ;
  }
 protected   SMTObject linkedObject;
  protected SMTAssociation smtAssociation;
   protected boolean isLeft;

  public SMTObject getLinkedObject() {
    return linkedObject;
  }

  public SMTAssociation getAssociation() {
    return smtAssociation;
  }

public   boolean isLeft(){
    return  isLeft;
  }

 public boolean isRight(){
    return !isLeft;
  }
}
