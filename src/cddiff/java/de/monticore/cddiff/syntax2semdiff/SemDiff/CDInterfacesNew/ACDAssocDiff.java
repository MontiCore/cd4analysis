package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfacesNew;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces.ICDAssocDiff;
import de.monticore.cddiff.syntax2semdiff.SemDiff.DiffTypes;

import java.util.List;

public abstract class ACDAssocDiff implements ICDAssocDiff {
  private final ASTCDAssociation elem1;
  private final ASTCDAssociation elem2;
  private List<DiffTypes> baseDiff;

  public ASTCDAssociation getElem1() {
    return elem1;
  }

  public ASTCDAssociation getElem2() {
    return elem2;
  }

  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }

  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
  }

  protected ACDAssocDiff(ASTCDAssociation elem1, ASTCDAssociation elem2) {
    this.elem1 = elem1;
    this.elem2 = elem2;
  }
}
