package de.monticore.cddiff.syndiff.interfaces;

import de.monticore.ast.ASTNode;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cddiff.syndiff.imp.DiffTypes;

import java.util.List;

public interface ICDMemberDiff {
  ASTNode getSrcElem();

  ASTNode getTgtElem();

  List<DiffTypes> getBaseDiff();

  void setBaseDiff(List<DiffTypes> baseDiff);

}
