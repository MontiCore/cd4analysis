/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

import de.monticore.cardinality.CardinalityMill;
import de.monticore.cardinality._ast.ASTCardinality;

public class ASTCDCardOpt extends ASTCDCardOptTOP {
  @Override
  public boolean isOpt() {
    return true;
  }

  @Override
  public ASTCardinality toCardinality() {
    return CardinalityMill.cardinalityBuilder()
        .setLowerBoundLit(CardinalityMill.natLiteralBuilder().setDigits("0").build())
        .setLowerBoundLit(CardinalityMill.natLiteralBuilder().setDigits("1").build())
        .build();
  }
}
