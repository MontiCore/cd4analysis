/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

import de.monticore.cardinality._ast.ASTCardinality;

public interface ASTCDCardinality extends ASTCDCardinalityTOP {
  default boolean isOne() {
    return false;
  }

  default boolean isOpt() {
    return false;
  }

  default boolean isMult() {
    return false;
  }

  default boolean isAtLeastOne() {
    return false;
  }

  ASTCardinality toCardinality();

  default int getLowerBound() {
    return toCardinality().getLowerBound();
  }

  default int getUpperBound() {
    return toCardinality().getUpperBound();
  }
}
