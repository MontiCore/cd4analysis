package de.monticore.cddiff.syndiff.interfaces;

import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.syndiff.imp.DiffTypes;
import de.monticore.cddiff.syndiff.imp.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.List;
import java.util.Set;

public interface ICDTypeDiff {
  List<CDMemberDiff> getChangedMembers();
  List<ASTCDAttribute> getAddedAttributes();
  List<ASTCDAttribute> getInheritedAttributes();
  List<ASTCDAttribute> getDeletedAttributes();
  List<ASTCDEnumConstant> getAddedConstants();
  List<ASTCDEnumConstant> getDeletedConstants();
  List<Pair<ASTCDAttribute, ASTCDAttribute>> getMatchedAttributes();
  List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> getMatchedConstants();

  ASTCDType getSrcElem();

  ASTCDType getTgtElem();

  void setChangedMembers(List<CDMemberDiff> changedMembers);

  void setAddedAttributes(List<ASTCDAttribute> addedAttributes);

  void setDeletedAttributes(List<ASTCDAttribute> deletedAttribute);
  void setInheritedAttributes(List<ASTCDAttribute> deletedAttribute);
  void setAddedConstants(List<ASTCDEnumConstant> addedConstants);

  void setDeletedConstants(List<ASTCDEnumConstant> deletedConstants);

  List<DiffTypes> getBaseDiff();

  public void setBaseDiff(List<DiffTypes> baseDiff);

  /**
   * Compute all changed attributes in all classes.
   * @return list of pairs of classes and changed attributes.
   */
  Pair<ASTCDClass, List<ASTCDAttribute>> changedAttribute();

  /**
   * Check for each attribute in the list deletedAttribute if it
   * has been really deleted and add it to a list.
   *
   * @return list of pairs of the class with a deleted attribute.
   */
  Pair<ASTCDClass, List<ASTCDAttribute>> deletedAttributes();

  /**
   * Check for each attribute in the list addedAttributes if it
   * has been really added and add it to a list.
   * @return list of pairs of the class with an added (new) attribute.
   */
  Pair<ASTCDClass, List<ASTCDAttribute>> addedAttributes();

  /**
   * Get all added constants to an enum
   * @return list of added constants
   */
  Pair<ASTCDEnum, List<ASTCDEnumConstant>> newConstants();

  /**
   * Get all attributes with changed types.
   *
   * @param memberDiff
   * @return list of pairs of the class (or subclass) and changed attribute.
   */
  Pair<ASTCDClass, ASTCDAttribute> findMemberDiff(CDMemberDiff memberDiff);

  /**
   * Find if a change of a modifier has a meaning for a diagram. From abstract to non-abstract:
   * semantic difference - class can be instantiated. From non-abstract to abstract: possible
   * semantic difference - another class uses this abstract class.
   *
   * @return true if we have a semantic difference. This function kind of uses multiple others:
   * inheritance hierarchy, comparison of associations.
   */
  ASTCDType isClassNeeded();
}
