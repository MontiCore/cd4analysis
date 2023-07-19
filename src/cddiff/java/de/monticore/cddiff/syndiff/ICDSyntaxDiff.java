package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDTypeDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.List;

public interface ICDSyntaxDiff {
  List<CDTypeDiff> getChangedTypes();

  List<CDTypeDiff> getChangedClasses();

  List<CDAssocDiff> getChangedAssocs();

  List<ASTCDClass> getAddedClasses();

  List<ASTCDClass> getDeletedClasses();

  List<ASTCDInterface> getAddedInterfaces();

  List<ASTCDInterface> getDeletedInterfaces();

  List<ASTCDEnum> getAddedEnums();

  List<ASTCDEnum> getDeletedEnums();

  List<ASTCDAssociation> getAddedAssocs();

  List<ASTCDAssociation> getDeletedAssocs();

  List<Pair<ASTCDClass, ASTCDClass>> getMatchedClasses();

  List<Pair<ASTCDEnum, ASTCDEnum>> getMatchedEnums();

  List<Pair<ASTCDInterface, ASTCDInterface>> getMatchedInterfaces();

  List<Pair<ASTCDAssociation, ASTCDAssociation>> getMatchedAssocs();

  ASTCDCompilationUnit getSrcCD();

  void setSrcCD(ASTCDCompilationUnit srcCD);

  ASTCDCompilationUnit getTgtCD();

  void setTgtCD(ASTCDCompilationUnit tgtCD);

  void setChangedClasses(List<CDTypeDiff> changedCLasses);

  void setChangedAssocs(List<CDAssocDiff> changedAssocs);

  void setAddedClasses(List<ASTCDClass> addedClasses);

  public void setDeletedClasses(List<ASTCDClass> deletedClasses);

  void setAddedEnums(List<ASTCDEnum> addedEnums);

  void setDeletedEnums(List<ASTCDEnum> deletedEnums);

  void setAddedAssocs(List<ASTCDAssociation> addedAssocs);

  void setDeletedAssocs(List<ASTCDAssociation> deletedAssocs);

  void setMatchedClasses(List<Pair<ASTCDClass, ASTCDClass>> matchedClasses);

  void setMatchedAssocs(List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs);

  // Is ASTCDCompilationUnit needed in all functions?

  void setMatchedEnums(List<Pair<ASTCDEnum, ASTCDEnum>> matchedEnums);

  void setMatchedInterfaces(List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces);

  List<DiffTypes> getBaseDiff();

  void setBaseDiff(List<DiffTypes> baseDiff);

  /**
   * Checks if an added @param astcdClass refactors the old structure. The class must be abstarct,
   * its subclasses in the old CD need to have all of its attributes and it can't have new ones.
   *
   * @return true if the class fulfills those requirements.
   */
  boolean isSupClass(ASTCDClass astcdClass);

  /**
   * Check if a deleted @param astcdAssociation was needed in cd2, but not in cd1.
   *
   * @return true if we have a case where we can instantiate a class without instantiating another.
   */
  boolean isNotNeededAssoc(ASTCDAssociation astcdAssociation);

  /**
   * Similar case - the association @param astcdAssociation is needed in cd1, but not in cd2.
   *
   * @return true if a class instantiate another one by @param association.
   */
  boolean isAddedAssoc(ASTCDAssociation astcdAssociation);

//  /**
//   * Deleted Enum-classes always bring a semantical difference - a class can be instantiated without
//   * attribute. Similar case for added ones.
//   *
//   * @param astcdEnum
//   */
//  List<ASTCDClass> getAttForEnum(ASTCDEnum astcdEnum);

  /**
   * Merge all duplicated associations that have the same role names @param duplicatedAssociations
   * and put them in a multymap. This function must be used before handling association difference -
   * possible inconsistent output. Must be done before overlappingAssocs.
   *
   * @return
   */
  //ArrayListMultimap<ASTCDAssociation, ASTCDAssociation> findDuplicatedAssociations();

  /**
   * Find all overlapping associations (same role name in target dir) and put them in a multymap.
   * This function must be used before handling associations difference - possible inconsistent
   * output.
   */
  void findOverlappingAssocs();

  //  /**
  //   *
  //   * Based on the overlappingAssociations update the multymap with subclasses for each
  // association.
  //   * Search for a full matching between the subclasses in cd1 and cd2.
  //   * If none is found, a semantic difference exists.
  //   * This function must be used before handling association
  //   * difference - possible inconsistent output.
  //   */
  //  void createSubClass();

  /**
   * Get the type of difference between two ASTCDTypes or ASTCDAssociations
   *
   * @param diff object of type CDAssocDiff or CDTypeDIff
   * @return difference between the two objects
   */
  String findDiff(Object diff);

//  /**
//   * Get the two classes that are connected via the associations.
//   *
//   * @return pair of two classes.
//   */
////  Pair<ASTCDClass, ASTCDClass> getConnectedClasses(ASTCDAssociation association);
}
