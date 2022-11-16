/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

import de.monticore.cardinality.CardinalityMill;
import de.monticore.cardinality._ast.ASTCardinality;

public class ASTCDCardMult extends ASTCDCardMultTOP {
  @Override
  public boolean isMult() {
    return true;
  }

  @Override
  public ASTCardinality toCardinality() {
    return CardinalityMill.cardinalityBuilder()
        .setLowerBoundLitAbsent()
        .setUpperBoundLitAbsent()
        .setNoUpperLimit(true)
        .build();
  }
}
