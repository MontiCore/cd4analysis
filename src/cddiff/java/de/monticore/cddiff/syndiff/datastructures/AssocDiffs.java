package de.monticore.cddiff.syndiff.datastructures;

import de.monticore.cdbasis._ast.ASTCDClass;

import java.util.List;

public class AssocDiffs {
  private List<ASTCDClass> allInSrc;
  private List<ASTCDClass> allInTgt;
  private List<ASTCDClass> mixed;

  public AssocDiffs(List<ASTCDClass> allInSrc, List<ASTCDClass> allInTgt, List<ASTCDClass> mixed) {
    this.allInSrc = allInSrc;
    this.allInTgt = allInTgt;
    this.mixed = mixed;
  }

  public List<ASTCDClass> getAllInSrc() {
    return allInSrc;
  }

  public void setAllInSrc(List<ASTCDClass> allInSrc) {
    this.allInSrc = allInSrc;
  }

  public List<ASTCDClass> getAllInTgt() {
    return allInTgt;
  }

  public void setAllInTgt(List<ASTCDClass> allInTgt) {
    this.allInTgt = allInTgt;
  }

  public List<ASTCDClass> getMixed() {
    return mixed;
  }

  public void setMixed(List<ASTCDClass> mixed) {
    this.mixed = mixed;
  }
}
