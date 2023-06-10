package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfacesNew;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces.ICDMemberDiff;
import de.monticore.cddiff.syntax2semdiff.SemDiff.DiffTypes;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;

import java.util.List;

public abstract class ACDMemberDiff implements ICDMemberDiff {
  private final Object elem1 ;
  private final Object elem2;

  private List<DiffTypes> baseDiff;

  public ACDMemberDiff(ASTCDAttribute elem1, ASTCDAttribute elem2){
      this.elem1 = elem1;
      this.elem2 = elem2;
  }

  public ACDMemberDiff(ASTCDEnumConstant elem1, ASTCDEnumConstant elem2){
    this.elem1 = elem1;
    this.elem2 = elem2;
  }

  public Object getElem1() {
    return elem1;
  }

  public Object getElem2() {
    return elem2;
  }
}
