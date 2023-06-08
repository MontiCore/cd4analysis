package de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfaces;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfacesNew.ACDAssocDiff;
import de.monticore.cddiff.syntax2semdiff.SemDiff.CDInterfacesNew.ACDTypeDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;

import java.util.List;

public interface ICDSyntaxDiff {
  List<ACDTypeDiff> getChangedClasses();
  List<ACDAssocDiff> getChangedAssocs();
  List<ASTCDClass> getAddedClasses();
  List<ASTCDClass> getDeletedClasses();
  List<ASTCDEnum> getAddedEnums();
  List<ASTCDEnum> getDeletedEnums();
  List<ASTCDAssociation> getAddedAssocs();
  List<ASTCDAssociation> getDeletedAssocs();
  List<ACDTypeDiff> getMatchedClasses();
  List<ACDAssocDiff> getMatchedAssocs();

  //Is ASTCDCompilationUnit needed in all functions?
  /**
   *
   * Checks if an added @param astcdClass refactors the old structure.
   * The class must be abstarct, its subclasses in the old CD need to have all of its attributes
   * and it can't have new ones.
   * @return true if the class fulfills those requirements.
   */
  boolean isSuperclass(ASTCDClass astcdClass);

  /**
   *
   * Get the whole inheritance hierarchy that @param astcdClass.
   * is part of - all direct and indirect superclasses.
   * @return a list of the superclasses.
   */
  List<ASTCDClass> getClassHierarchy(ASTCDClass astcdClass);

  /**
   *
   * Check if a deleted @param astcdAssociation was need in cd2, but not in cd1.
   * @return true if we have a case where we can instantiate a class without instantiating another.
   */
  boolean isNotNeededAssoc(ASTCDAssociation astcdAssociation);

  /**
   *
   * Similar case - the association @param astcdAssociation is needed in cd1, but not in cd2.
   * @return true if a class instantiate another one by @param association.
   */
  boolean isAlwaysNeededAssoc(ASTCDAssociation astcdAssociation);

  /**
   * Deleted Enum-classes always bring a semantical difference - a class can be instantiated without attribute.
   * Similar case for added ones.
   * @param astcdEnum
   */
  List<ASTCDClass> getAttForEnum(ASTCDEnum astcdEnum);

  /**
   * Compute the classes that extend a given class.
   * @param astcdClass
   * @return list of extending classes.
   * This function is similar to getClassHierarchy().
   */
  List<ASTCDClass> getSpannedInheritance(ASTCDClass astcdClass);
}
