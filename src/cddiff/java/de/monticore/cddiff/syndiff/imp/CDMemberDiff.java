package de.monticore.cddiff.syndiff.imp;

import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDMemberDiff;
import java.util.List;

public class CDMemberDiff implements ICDMemberDiff {
  private final ASTCDMember srcElem;
  private final ASTCDMember tgtElem;
  private List<DiffTypes> baseDiff;

  public CDMemberDiff(ASTCDMember srcElem, ASTCDMember elem2) {
    this.srcElem = srcElem;
    this.tgtElem = elem2;
  }

  @Override
  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }

  @Override
  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
  }

  @Override
  public ASTCDMember getSrcElem() {
    return srcElem;
  }

  @Override
  public ASTCDMember getTgtElem() {
    return tgtElem;
  }
}
