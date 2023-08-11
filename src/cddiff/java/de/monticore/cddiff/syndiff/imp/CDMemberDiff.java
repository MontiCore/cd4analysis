package de.monticore.cddiff.syndiff.imp;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cddiff.syndiff.interfaces.ICDMemberDiff;
import java.util.List;

public class CDMemberDiff implements ICDMemberDiff {
  private final ASTCDMember srcElem;
  private final ASTCDMember tgtElem;
  private List<DiffTypes> baseDiff;

  String srcAttrName;
  String tgtAttrName;

  public CDMemberDiff(ASTCDMember srcElem, ASTCDMember tgtElem) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
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

  public ASTCDMember compareMember(ASTCDMember srcElem, ASTCDMember tgtElem) {
    if (srcElem instanceof ASTCDAttribute || tgtElem instanceof ASTCDAttribute) {
      ASTCDAttribute srcAttr = (ASTCDAttribute) srcElem;
      ASTCDAttribute tgtAttr = (ASTCDAttribute) tgtElem;

      srcAttrName = srcAttr.getName();
      tgtAttrName = tgtAttr.getName();

      if (srcAttr.getName().equals(tgtAttr.getName())) {
        if (!srcAttr.getMCType().equals(tgtAttr.getMCType())) {
          baseDiff.add(DiffTypes.CHANGED_ATTRIBUTE);
          return srcElem;
        }
        if (!srcAttr.getModifier().equals(tgtAttr.getModifier())) {
          baseDiff.add(DiffTypes.CHANGED_VISIBILITY);
          return srcElem;
        }
      }
    }
    return null;
  }
}
