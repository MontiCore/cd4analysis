package de.monticore.cddiff.syndiff.interfaces;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syndiff.imp.DiffTypes;
import de.monticore.cddiff.syndiff.imp.CDAssocDiff;
import de.monticore.cddiff.syndiff.imp.CDTypeDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.List;

public interface ICDSyntaxDiff {
  List<CDTypeDiff> getChangedTypes();
  List<CDAssocDiff> getChangedAssocs();
  List<ASTCDClass> getAddedClasses();
  List<ASTCDClass> getDeletedClasses();
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
  List<DiffTypes> getBaseDiff();
  public void setBaseDiff(List<DiffTypes> baseDiff);
  void setChangedTypes(List<CDTypeDiff> changedTypes);
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

  /**
   * Checks if an added @param astcdClass refactors the old structure. The class must be abstarct,
   * its subclasses in the old CD need to have all of its attributes and it can't have new ones.
   *
   * @return true if the class fulfills those requirements.
   */
  ASTCDClass isSupClass(ASTCDClass astcdClass);

  /**
   * Similar case - the association @param astcdAssociation is needed in cd1, but not in cd2.
   *
   * @return true if a class instantiate another one by @param association.
   */
  List<ASTCDClass> isAssocAdded(ASTCDAssociation astcdAssociation);


  /**
   * Find all overlapping associations (same role name in target dir) and put them in a multymap.
   * This function must be used before handling associations difference - possible inconsistent
   * output.
   */
  void findOverlappingAssocs();


}
