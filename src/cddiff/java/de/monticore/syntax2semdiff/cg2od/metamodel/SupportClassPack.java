package de.monticore.syntax2semdiff.cg2od.metamodel;

import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportClass;

public class SupportClassPack {
  private SupportClass otherSideClass;
  private Position position;
  public enum Position {
    LEFT, RIGHT;
  }

  public SupportClassPack(SupportClass otherSideClass, Position position) {
    this.otherSideClass = otherSideClass;
    this.position = position;
  }

  public SupportClass getOtherSideClass() {
    return otherSideClass;
  }

  public void setOtherSideClass(SupportClass otherSideClass) {
    this.otherSideClass = otherSideClass;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }
}
