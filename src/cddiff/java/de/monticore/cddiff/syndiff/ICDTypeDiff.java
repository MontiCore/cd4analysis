package de.monticore.cddiff.syndiff;

import de.monticore.cdbasis._ast.*;
import de.monticore.cddiff.syndiff.imp.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.List;
import java.util.Set;

public interface ICDTypeDiff {
  List<CDMemberDiff> getChangedMembers();

  List<ASTCDAttribute> getAddedAttributes();

  List<ASTCDAttribute> getDeletedAttribute();

  List<ASTCDEnumConstant> getAddedConstants();

  List<ASTCDEnumConstant> getDeletedConstants();

  List<Pair<ASTCDAttribute, ASTCDAttribute>> getMatchedAttributes();

  List<Pair<ASTCDEnumConstant, ASTCDEnumConstant>> getMatchedConstants();

  ASTCDType getElem1();

  ASTCDType getElem2();

  void setChangedMembers(List<CDMemberDiff> changedMembers);

  void setAddedAttributes(List<ASTCDAttribute> addedAttributes);

  void setDeletedAttribute(List<ASTCDAttribute> deletedAttribute);

  void setAddedConstants(List<ASTCDEnumConstant> addedConstants);

  void setDeletedConstants(List<ASTCDEnumConstant> deletedConstants);

  List<DiffTypes> getBaseDiffs();

  public void setBaseDiffs(List<DiffTypes> baseDiffs);

  /**
   * Compute the spanned inheritance of a given class.
   * That is we get all classes that are extending (not only direct) a class
   * @param astcdClass
   * @param compilationUnit
   * @return set of extending classes.
   * The implementation is not efficient (no way to go from subclasses to superclasses).
   */
  abstract Set<ASTCDClass> getSpannedInheritance(ASTCDClass astcdClass, ASTCDCompilationUnit compilationUnit);

  /**
   * Compute all changed attributes in all classes.
   * @param compilationUnit
   * @return list of pairs of classes and changed attributes.
   */
  List<Pair<ASTCDClass, ASTCDAttribute>> changedAttribute(ASTCDCompilationUnit compilationUnit);

  /**
   * Get all classes that use the ASTCDEnum as an attribute. We use this function when the typeDiff
   * is between enums.
   *
   * @return list of those classes.
   */
  List<ASTCDClass> getClassesForEnum(ASTCDCompilationUnit compilationUnit);

  /**
   * Check the difference in the modifier of the classes. It must also save the information for
   * building the object diagrams.
   *
   * @return A String with the old and new roles.
   */
  String sterDiff();

  /**
   * Check the difference in the attribute types of the classes. It must also save the information
   * for building the object diagrams.
   *
   * @return A String with the old and new types.
   */
  String attDiff();

  /**
   * Check for each attribute in the list deletedAttribute if it
   * has been really deleted and add it to a list.
   * @param compilationUnit
   * @return list of pairs of the class with a deleted attribute.
   */
  List<Pair<ASTCDClass, ASTCDAttribute>> deletedAttributes(ASTCDCompilationUnit compilationUnit);

  /**
   * Check for each attribute in the list addedAttributes if it
   * has been really added and add it to a list.
   * @param compilationUnit
   * @return list of pairs of the class with an added (new) attribute.
   */
  List<Pair<ASTCDClass, ASTCDAttribute>> addedAttributes(ASTCDCompilationUnit compilationUnit);

  /**
   * Get all added constants to an enum
   * @return list of added constants
   */
  List<Pair<ASTCDClass, ASTCDEnumConstant>> newConstants();

  /**
   * Get all attributes with changed types.
   * @param memberDiff
   * @param compilationUnit
   * @return list of pairs of the class (or subclass) and changed attribute.
   */
  List<Pair<ASTCDClass, ASTCDAttribute>> findMemberDiff(CDMemberDiff memberDiff, ASTCDCompilationUnit compilationUnit);
}
