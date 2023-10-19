package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdassociation._ast.ASTCDCardinality;
import edu.mit.csail.sdg.alloy4.Pair;

/**
 * Data structure for cardinalities. This is used to get which cardinalities must be compared based on the matched sides.
 */
public class CardinalityStruc {
  private Pair<ASTCDCardinality, ASTCDCardinality> leftCardinalities;
  private Pair<ASTCDCardinality, ASTCDCardinality> rightCardinalities;

  public CardinalityStruc(
      Pair<ASTCDCardinality, ASTCDCardinality> leftCardinalities,
      Pair<ASTCDCardinality, ASTCDCardinality> rightCardinalities) {
    this.leftCardinalities = leftCardinalities;
    this.rightCardinalities = rightCardinalities;
  }

  public Pair<ASTCDCardinality, ASTCDCardinality> getLeftCardinalities() {
    return leftCardinalities;
  }

  public void setLeftCardinalities(Pair<ASTCDCardinality, ASTCDCardinality> leftCardinalities) {
    this.leftCardinalities = leftCardinalities;
  }

  public Pair<ASTCDCardinality, ASTCDCardinality> getRightCardinalities() {
    return rightCardinalities;
  }

  public void setRightCardinalities(Pair<ASTCDCardinality, ASTCDCardinality> rightCardinalities) {
    this.rightCardinalities = rightCardinalities;
  }
}
