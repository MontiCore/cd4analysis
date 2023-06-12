package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import edu.mit.csail.sdg.alloy4.Pair;

public interface ICDAssocDiff {

  /**
   * Check the difference in the roles of the associations
   * It must also save the information for building the object diagrams.
   * @return A String with the old and new roles.
   */
  String roleDiff();

  /**
   * Check the difference in the direction of the associations
   * It must also save the information for building the object diagrams.
   * @return A String with the old and new direction.
   */
  String dirDiff();

  /**
   * Check the difference in the cardinalities of the associations
   * It must also save the information for building the object diagrams.
   * @return A String with the old and new cardinalities.
   */
  String cardDiff();
}
