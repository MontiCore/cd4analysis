package de.monticore.syntax2semdiff.cdsyntaxdiff2od.metamodel;

import de.monticore.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;

public class CDTypeWrapperPack {
  private CDTypeWrapper otherSideClass;

  private Position position;

  public enum Position {
    LEFT, RIGHT;
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