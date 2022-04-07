/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

import de.monticore.cardinality.CardinalityMill;
import de.monticore.cardinality._ast.ASTCardinality;

public class ASTCDCardOne extends ASTCDCardOneTOP {
  @Override
  public boolean isOne() {
    return true;
  }

  @Override
  public ASTCardinality toCardinality() {
    return CardinalityMill
        .cardinalityBuilder()
        .setLowerBound(1)
        .setUpperBound(1)
        .build();
  }
}
