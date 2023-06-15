package de.monticore.cddiff.syndiff.imp;

import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cddiff.syndiff.ICDMemberDiff;
import de.monticore.cddiff.syndiff.DiffTypes;

import java.util.List;

public class CDMemberDiff implements ICDMemberDiff {
  private final ASTCDMember elem1 ;
  private final ASTCDMember elem2;
  private List<DiffTypes> baseDiff;

  public CDMemberDiff(ASTCDMember elem1, ASTCDMember elem2) {
    this.elem1 = elem1;
    this.elem2 = elem2;
  }
  @Override
  public List<DiffTypes> getBaseDiff() { return baseDiff; }
  @Override
  public void setBaseDiff(List<DiffTypes> baseDiff) { this.baseDiff = baseDiff; }
  @Override
  public ASTCDMember getElem1() { return elem1; }
  @Override
  public ASTCDMember getElem2() { return elem2; }
}
