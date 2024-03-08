package de.monticore.cddiff.syndiff;

import de.monticore.ast.ASTNode;
import java.util.List;

public interface ICDMemberDiff {
  ASTNode getSrcElem();

  ASTNode getTgtElem();

  List<DiffTypes> getBaseDiff();

  void setBaseDiff(List<DiffTypes> baseDiff);
}
