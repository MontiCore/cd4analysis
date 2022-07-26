package de.monticore.cddiff.syntax2semdiff.cdsyntaxdiff2od.metamodel;

import de.monticore.odbasis._ast.ASTODNamedObject;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;

public class CDWrapperObjectPack {
  private ASTODNamedObject namedObject;

  private CDTypeWrapper cDTypeWrapper;

  public CDWrapperObjectPack(ASTODNamedObject namedObject, CDTypeWrapper cDTypeWrapper) {
    this.namedObject = namedObject;
    this.cDTypeWrapper = cDTypeWrapper;
  }

  public ASTODNamedObject getNamedObject() {
    return namedObject;
  }

  public CDTypeWrapper getCDTypeWrapper() {
    return cDTypeWrapper;
  }

}
