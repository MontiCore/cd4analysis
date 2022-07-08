package de.monticore.syntax2semdiff.cg2od.metamodel;

import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.syntax2semdiff.cd2sg.metamodel.SupportClass;

public class SupportObjectPack {
  private ASTODNamedObject namedObject;
  private SupportClass supportClass;

  public SupportObjectPack(ASTODNamedObject namedObject, SupportClass supportClass) {
    this.namedObject = namedObject;
    this.supportClass = supportClass;
  }

  public ASTODNamedObject getNamedObject() {
    return namedObject;
  }

  public SupportClass getSupportClass() {
    return supportClass;
  }
}
