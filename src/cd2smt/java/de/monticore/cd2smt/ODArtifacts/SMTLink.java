/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2smt.ODArtifacts;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import java.util.Set;

public class SMTLink {

  private final ASTCDAssociation smtAssociation;
  private final SMTObject leftObject;
  private final SMTObject rightObject;

  public SMTLink(SMTObject leftObject, SMTObject rightObject, ASTCDAssociation smtAssociation) {
    this.leftObject = leftObject;
    this.rightObject = rightObject;
    this.smtAssociation = smtAssociation;
  }

  public static boolean containsLink(Set<SMTLink> linkList, SMTLink smtLink) {
    for (SMTLink smtLink1 : linkList) {
      if (smtLink.leftObject.equals(smtLink1.getLeftObject())
          && smtLink.rightObject.equals(smtLink1.getRightObject())
          && smtLink.smtAssociation.equals(smtLink1.getAssociation())) {
        return true;
      }
    }
    return false;
  }

  public ASTCDAssociation getAssociation() {
    return smtAssociation;
  }

  public SMTObject getLeftObject() {
    return leftObject;
  }

  public SMTObject getRightObject() {
    return rightObject;
  }
}
