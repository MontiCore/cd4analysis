package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import java.util.List;

public interface ICDAssocDiff {
  ASTCDAssociation getSrcAssoc();

  ASTCDAssociation getTgtAssoc();

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
}
