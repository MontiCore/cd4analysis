package de.monticore.cddiff.syndiff;

import de.monticore.cdbasis._ast.ASTCDMember;
import java.util.List;

public interface ICDMemberDiff {
  ASTCDMember getElem1();

  ASTCDMember getElem2();

  List<DiffTypes> getBaseDiff();

  void setBaseDiff(List<DiffTypes> baseDiff);
}
