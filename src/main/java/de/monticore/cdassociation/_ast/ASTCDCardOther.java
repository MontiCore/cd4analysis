package de.monticore.cdassociation._ast;

import de.monticore.cardinality.CardinalityMill;
import de.monticore.cardinality._ast.ASTCardinality;

public class ASTCDCardOther extends ASTCDCardOtherTOP {
  @Override
  public ASTCardinality toCardinality() {
    return CardinalityMill.cardinalityBuilder().setLowerBound(Integer.parseInt(lower)).setUpperBound(Integer.parseInt(upper)).build();
  }
}
