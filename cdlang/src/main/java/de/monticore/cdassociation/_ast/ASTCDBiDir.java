/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cdassociation._ast;

public class ASTCDBiDir extends ASTCDBiDirTOP {

  public boolean isBidirectional() {
    return true;
  }

  public boolean isDefinitiveNavigableLeft() {
    return true;
  }

  public boolean isDefinitiveNavigableRight() {
    return true;
  }
}
