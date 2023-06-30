package de.monticore.cddiff.syndiff;

import de.monticore.cdbasis._ast.ASTCDMember;
import java.util.List;

public interface ICDMemberDiff {
  ASTCDMember getSrcElem();

  ASTCDMember getTgtElem();

  List<DiffTypes> getBaseDiff();

  void setBaseDiff(List<DiffTypes> baseDiff);
}
