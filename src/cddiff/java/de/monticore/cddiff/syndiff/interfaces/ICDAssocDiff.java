package de.monticore.cddiff.syndiff.interfaces;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.imp.DiffTypes;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.List;

public interface ICDAssocDiff {
  ASTCDAssociation getSrcElem();

  ASTCDAssociation getTgtElem();

  List<DiffTypes> getBaseDiff();

  /**
   * Set the AssocStructs that contain the both elements. Those are used in all functions as the
   * associations can be modified through overlapping.
   */
  void setStructs();

  /**
   * Compare the cardinalities of the AssocStructs. The changed cardinality(/ies) is returned with
   * the information on which side it is and what number should be used for instantiation.
   */
  Pair<ASTCDAssociation, List<Pair<ClassSide, Integer>>> getCardDiff();

  /**
   * Compare the directions of the AssocStructs.
   *
   * @return true if the direction has changed.
   */
  boolean isDirectionChanged();

  /**
   * Compare the role names of the AssocStructs. The changed ones are returned with the information
   * on which side they are.
   */
  Pair<ASTCDAssociation, List<Pair<ClassSide, ASTCDRole>>> getRoleDiff();

  /**
   * Check if the new class is a superclass or a subclass. Based on the change, check if there are
   * classes that now have or don't have the given association.
   *
   * @return class that now has or doesn't have the association.
   */
  ASTCDClass changedSrc();

  /** Same idea as for changedSrc() but now the source classes are compared. */
  ASTCDClass changedTgt();
}
