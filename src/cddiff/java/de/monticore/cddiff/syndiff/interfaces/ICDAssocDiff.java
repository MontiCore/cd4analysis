package de.monticore.cddiff.syndiff.interfaces;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cddiff.syndiff.imp.DiffTypes;

import java.util.List;

public interface ICDAssocDiff {
  ASTCDAssociation getSrcElem();

  ASTCDAssociation getTgtElem();

  List<DiffTypes> getBaseDiff();

  void setBaseDiff(List<DiffTypes> baseDiff);

  /**
   * Check the difference in the roles of the associations It must also save the information for
   * building the object diagrams.
   *
   * @return A String with the old and new roles.
   */
  String roleDiff();

  /**
   * Check the difference in the direction of the associations It must also save the information for
   * building the object diagrams.
   *
   * @return A String with the old and new direction.
   */
  String dirDiff();

  /**
   * Check the difference in the cardinalities of the associations It must also save the information
   * for building the object diagrams.
   *
   * @return A String with the old and new cardinalities.
   */
  String cardDiff();

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
