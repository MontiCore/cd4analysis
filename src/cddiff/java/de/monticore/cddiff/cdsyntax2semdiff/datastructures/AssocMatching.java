package de.monticore.cddiff.cdsyntax2semdiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDClass;

import java.util.ArrayList;
import java.util.List;

public class AssocMatching {
  private ASTCDClass classToInstantiate;
  private List<AssocStruct> notMatchedAssocsInSrc = new ArrayList<>();
  private List<AssocStruct> notMatchedAssocsInTgt = new ArrayList<>();

  public ASTCDClass getClassToInstantiate() {
    return classToInstantiate;
  }

  public void setClassToInstantiate(ASTCDClass classToInstantiate) {
    this.classToInstantiate = classToInstantiate;
  }

  public List<AssocStruct> getNotMatchedAssocsInSrc() {
    return notMatchedAssocsInSrc;
  }

  public void setNotMatchedAssocsInSrc(List<AssocStruct> notMatchedAssocsInSrc) {
    this.notMatchedAssocsInSrc = notMatchedAssocsInSrc;
  }

  public List<AssocStruct> getNotMatchedAssocsInTgt() {
    return notMatchedAssocsInTgt;
  }

  public void setNotMatchedAssocsInTgt(List<AssocStruct> notMatchedAssocsInTgt) {
    this.notMatchedAssocsInTgt = notMatchedAssocsInTgt;
  }
}
