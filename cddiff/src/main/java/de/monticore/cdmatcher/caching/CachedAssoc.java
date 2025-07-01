/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmatcher.caching;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDType;

public class CachedAssoc {

  private final ASTCDAssociation assoc;

  private final ASTCDType leftType;
  private final ASTCDType rightType;

  public CachedAssoc(ASTCDAssociation assoc, ASTCDType leftType, ASTCDType rightType) {
    this.assoc = assoc;
    this.leftType = leftType;
    this.rightType = rightType;
  }

  public ASTCDAssociation getAssoc() { return assoc; }

  public ASTCDType getLeftType() { return leftType; }

  public ASTCDType getRightType() { return rightType; }

}
