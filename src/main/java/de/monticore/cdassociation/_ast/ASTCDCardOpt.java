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
    return CardinalityMill.cardinalityBuilder().setLowerBound(0).setUpperBound(1).build();
  }
}
