package de.monticore.cddiff.cdsyntax2semdiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;

import java.util.List;

public class AssocDiffs {
  private List<ASTCDType> allInSrc;
  private List<ASTCDType> allInTgt;
  private List<ASTCDType> mixed;

  public AssocDiffs(List<ASTCDType> allInSrc, List<ASTCDType> allInTgt, List<ASTCDType> mixed) {
    this.allInSrc = allInSrc;
    this.allInTgt = allInTgt;
    this.mixed = mixed;
  }

  public List<ASTCDType> getAllInSrc() {
    return allInSrc;
  }

  public void setAllInSrc(List<ASTCDType> allInSrc) {
    this.allInSrc = allInSrc;
  }

  public List<ASTCDType> getAllInTgt() {
    return allInTgt;
  }

  public void setAllInTgt(List<ASTCDType> allInTgt) {
    this.allInTgt = allInTgt;
  }

  public List<ASTCDType> getMixed() {
    return mixed;
  }

  public void setMixed(List<ASTCDType> mixed) {
    this.mixed = mixed;
  }
}
