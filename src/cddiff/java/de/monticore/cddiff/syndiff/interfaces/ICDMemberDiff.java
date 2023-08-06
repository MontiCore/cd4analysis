package de.monticore.cddiff.syndiff.interfaces;

import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.cddiff.syndiff.imp.DiffTypes;

import java.util.List;

public interface ICDMemberDiff {
  ASTCDMember getSrcElem();

  ASTCDMember getTgtElem();

  List<DiffTypes> getBaseDiff();

  void setBaseDiff(List<DiffTypes> baseDiff);

  public default String buildStrings(List<String> stringList) {
    StringBuilder output = new StringBuilder();

    for (String field : stringList) {
      if (!(field == null)) {
        output.append(field).append(" ");
      }
    }

    if (!stringList.isEmpty()) {
      return output.substring(0, output.length() - 1);
    }

    return output.toString();
  }
}
