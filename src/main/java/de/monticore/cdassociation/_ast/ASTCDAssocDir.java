/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdassociation._ast;

public interface ASTCDAssocDir extends ASTCDAssocDirTOP {

  default boolean isBidirectional() {
    return false;
  }

  default boolean isDefinitiveNavigableLeft() {
    return false;
  }

  default boolean isDefinitiveNavigableRight() {
    return false;
  }
}
