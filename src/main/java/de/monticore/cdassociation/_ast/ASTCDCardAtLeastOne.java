/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

import de.monticore.cardinality.CardinalityMill;
import de.monticore.cardinality._ast.ASTCardinality;

public class ASTCDCardAtLeastOne extends ASTCDCardAtLeastOneTOP {
  @Override
  public boolean isAtLeastOne() {
    return true;
  }

  @Override
  public ASTCardinality toCardinality() {
    return CardinalityMill
        .cardinalityBuilder()
        .setLowerBound(1)
        .setUpperBoundLitAbsent()
        .setNoUpperLimit(true)
        .build();
  }
}
