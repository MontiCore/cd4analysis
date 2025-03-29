package de.monticore.cdconcretization.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;

public class AssociationMatch {
  private final ASTCDAssociation association;
  private final AssocMatchDirection matchDirection;

  public AssociationMatch(ASTCDAssociation association, AssocMatchDirection matchDirection) {
    this.association = association;
    this.matchDirection = matchDirection;
  }

  public ASTCDAssociation getAssociation() {
    return association;
  }

  public AssocMatchDirection getMatchDirection() {
    return matchDirection;
  }
}
