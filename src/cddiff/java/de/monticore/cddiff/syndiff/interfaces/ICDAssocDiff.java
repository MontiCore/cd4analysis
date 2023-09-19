package de.monticore.cddiff.syndiff.interfaces;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cddiff.syndiff.imp.DiffTypes;

import java.util.List;

public interface ICDAssocDiff {
  ASTCDAssociation getSrcElem();

  ASTCDAssociation getTgtElem();

  List<DiffTypes> getBaseDiff();

  void setBaseDiff(List<DiffTypes> baseDiff);
}
