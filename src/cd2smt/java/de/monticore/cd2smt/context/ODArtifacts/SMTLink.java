package de.monticore.cd2smt.context.ODArtifacts;

import de.monticore.cd2smt.context.CDArtifacts.SMTAssociation;

import java.util.Set;


public class SMTLink {

 private final SMTAssociation smtAssociation;
 private final SMTObject leftObject;
 private final SMTObject rightObject;

  public SMTLink(SMTObject leftObject, SMTObject rightObject, SMTAssociation smtAssociation) {
    this.leftObject = leftObject;
    this.rightObject = rightObject;
    this.smtAssociation = smtAssociation;
  }

  public static boolean containsLink(Set<SMTLink> linkList, SMTLink smtLink) {
    for (SMTLink smtLink1 : linkList) {
      if (smtLink.leftObject.equals(smtLink1.getLeftObject()) && smtLink.rightObject.equals(smtLink1.getRightObject()) && smtLink.smtAssociation.equals(smtLink1.getSmtAssociation())) {
        return true;
      }
    }
    return false;
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
