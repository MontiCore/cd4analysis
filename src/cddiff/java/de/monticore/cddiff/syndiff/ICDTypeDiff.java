package de.monticore.cddiff.syndiff;

import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.List;
import java.util.Optional;

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
   *
   * @return list of pairs of classes and changed attributes.
   */
  List<Pair<ASTCDClass, ASTCDAttribute>> changedAttribute();

  /**
   * Check for each attribute in the list deletedAttribute if it has been really deleted.
   *
   * @return list of pairs of the class with a deleted attribute.
   */
  List<Pair<ASTCDClass, ASTCDAttribute>> deletedAttributes();

  /**
   * Check if an attribute is really deleted.
   *
   * @param attribute from list deletedAttributes.
   * @return false if found in inheritance hierarchy (superclass) or the class is now abstract and
   * the structure is refactored
   */
  Optional<ASTCDClass> isDeleted(ASTCDAttribute attribute);

  /**
   * Check for each attribute in the list addedAttributes if it has been really added and add it to
   * a list.
   *
   * @return list of pairs of the class with an added (new) attribute.
   */
  List<Pair<ASTCDClass, ASTCDAttribute>> addedAttributes();

  /**
   * Check if an attribute is really added.
   *
   * @param attribute from addedList
   * @return false if found in all 'old' subclasses or in some 'old' superClass
   */
  Optional<ASTCDClass> isAdded(ASTCDAttribute attribute);

  /**
   * Get all added constants to an enum
   *
   * @return list of added constants
   */
  Pair<ASTCDEnum, List<ASTCDEnumConstant>> newConstants();

  /**
   * Get all attributes with changed types.
   *
   * @param memberDiff pair of attributes with changed types.
   * @return list of pairs of the class (or subclass) and changed attribute.
   */
  Pair<ASTCDClass, ASTCDAttribute> findMemberDiff(CDMemberDiff memberDiff);

  /**
   * Find if a change of a modifier has a meaning for a diagram. From abstract to non-abstract:
   * semantic difference - class can now be instantiated. From non-abstract to abstract: possible
   * semantic difference - another class uses this abstract class and it doesn't have subclasses.
   *
   * @return true if we have a semantic difference.
   */
  boolean isClassNeeded();
}
