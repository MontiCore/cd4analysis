package de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.metamodel;

import de.monticore.odbasis._ast.ASTODNamedObject;

import java.util.List;

public class ASTODNamedObjectPack {
  private List<ASTODNamedObject> namedObjects;

  private boolean isInList;

  public ASTODNamedObjectPack() {
  }

  public ASTODNamedObjectPack(List<ASTODNamedObject> namedObjects, boolean isInList) {
    this.namedObjects = namedObjects;
    this.isInList = isInList;
  }

  public List<ASTODNamedObject> getNamedObjects() {
    return namedObjects;
  }

  public void setNamedObjects(List<ASTODNamedObject> namedObjects) {
    this.namedObjects = namedObjects;
  }

  public boolean isInList() {
    return isInList;
  }

  public void setInList(boolean inList) {
    isInList = inList;
  }

  public boolean isEmpty() {
    return namedObjects.isEmpty();
  }

}
