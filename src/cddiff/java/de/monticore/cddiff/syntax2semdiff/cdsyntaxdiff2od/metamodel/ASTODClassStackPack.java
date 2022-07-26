package de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.metamodel;

import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;

import java.util.List;

public class ASTODClassStackPack {
  private List<ASTODNamedObject> namedObjects;

  private CDTypeWrapper cDTypeWrapper;

  public ASTODClassStackPack(List<ASTODNamedObject> namedObjects, CDTypeWrapper cDTypeWrapper) {
    this.namedObjects = namedObjects;
    this.cDTypeWrapper = cDTypeWrapper;
  }

  public List<ASTODNamedObject> getNamedObjects() {
    return namedObjects;
  }

  public void setNamedObjects(List<ASTODNamedObject> namedObjects) {
    this.namedObjects = namedObjects;
  }

  public CDTypeWrapper getCDTypeWrapper() {
    return cDTypeWrapper;
  }

  public void setCDTypeWrapper(CDTypeWrapper CDTypeWrapper) {
    this.cDTypeWrapper = CDTypeWrapper;
  }

}
