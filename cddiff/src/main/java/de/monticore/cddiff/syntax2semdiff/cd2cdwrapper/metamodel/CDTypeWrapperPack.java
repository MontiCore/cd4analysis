/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel;

public class CDTypeWrapperPack {
  private CDTypeWrapper otherSideClass;

  private Position position;

  public enum Position {
    LEFT,
    RIGHT;
  }

  public CDTypeWrapperPack(CDTypeWrapper otherSideClass, Position position) {
    this.otherSideClass = otherSideClass;
    this.position = position;
  }

  public CDTypeWrapper getOtherSideClass() {
    return otherSideClass;
  }

  public void setOtherSideClass(CDTypeWrapper otherSideClass) {
    this.otherSideClass = otherSideClass;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }
}
