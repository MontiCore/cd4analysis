package de.monticore.cdconcretization.association;

import de.monticore.cdassociation._ast.ASTCDAssociation;

/**
 * Represents an association that matches another association in a certain direction.
 * This is useful when we want to keep the information about the direction of the match for later
 * processing.
 */
public class AssociationMatch {

  /** The matching association. */
  private final ASTCDAssociation association;

  /** The direction of the match. */
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
