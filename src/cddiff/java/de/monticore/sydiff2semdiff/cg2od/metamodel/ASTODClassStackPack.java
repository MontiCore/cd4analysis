package de.monticore.sydiff2semdiff.cg2od.metamodel;

import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.sydiff2semdiff.cd2sg.metamodel.SupportClass;

import java.util.List;

public class ASTODClassStackPack {
  private List<ASTODNamedObject> namedObjects;
  private SupportClass supportClass;

  public ASTODClassStackPack(List<ASTODNamedObject> namedObjects, SupportClass supportClass) {
    this.namedObjects = namedObjects;
    this.supportClass = supportClass;
  }

  public List<ASTODNamedObject> getNamedObjects() {
    return namedObjects;
  }

  public void setNamedObjects(List<ASTODNamedObject> namedObjects) {
    this.namedObjects = namedObjects;
  }

  public SupportClass getSupportClass() {
    return supportClass;
  }

  public void setSupportClass(SupportClass supportClass) {
    this.supportClass = supportClass;
  }
}
