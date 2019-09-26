/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable;

public enum Relationship {
  
  ASSOCIATION("association"), AGGREGATE("aggregate"), COMPOSITE("composite"), PART("part");
  
  private final String kind;
  
  private Relationship(String kind) {
    this.kind = kind;
  }
  
  @Override
  public String toString() {
    return kind;
  }
  
}
