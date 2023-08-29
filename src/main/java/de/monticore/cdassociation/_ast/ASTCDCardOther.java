package de.monticore.cdassociation._ast;

import de.monticore.cardinality._ast.ASTCardinality;

public class ASTCDCardOther extends ASTCDCardOtherTOP {
  @Override
  public ASTCardinality toCardinality() {
    return cardinality;
  }
}
