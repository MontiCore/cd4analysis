package de.monticore.cddiff.syndiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cddiff.syn2semdiff.datastructures.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.List;
import java.util.Set;

public interface ICDSyntaxDiff {
  List<CDTypeDiff> getChangedTypes();

  List<CDAssocDiff> getChangedAssocs();

  List<ASTCDClass> getAddedClasses();

  List<ASTCDClass> getDeletedClasses();

  List<ASTCDEnum> getAddedEnums();

  List<ASTCDEnum> getDeletedEnums();

  List<ASTCDAssociation> getAddedAssocs();

  List<ASTCDAssociation> getDeletedAssocs();

  List<Pair<ASTCDClass, ASTCDType>> getMatchedClasses();

  List<Pair<ASTCDEnum, ASTCDType>> getMatchedEnums();

  List<Pair<ASTCDInterface, ASTCDType>> getMatchedInterfaces();

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

  void setMatchedClasses(List<Pair<ASTCDClass, ASTCDType>> matchedClasses);

  void setMatchedAssocs(List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs);

  void setMatchedEnums(List<Pair<ASTCDEnum, ASTCDType>> matchedEnums);

  void setMatchedInterfaces(List<Pair<ASTCDInterface, ASTCDType>> matchedInterfaces);

  /**
   * Checks if an added @param astcdClass refactors the old structure. The class must be abstract,
   * its subclasses in the old CD need to have all of its attributes, and it can't have new ones.
   *
   * @return true if the class fulfills those requirements.
   */
  ASTCDClass isSupClass(ASTCDClass astcdClass);

  /**
   * Get a list of all added classes that bring a semantic difference.
   *
   * @return list of the added classes together with a class that can show the difference.
   */
  List<Pair<ASTCDType, ASTCDType>> addedClassList();

  /**
   * Check if a deleted inheritance relation brings a semantic difference.
   *
   * @return pairs of classes and a set of their deleted old superclasses that cause a semantic
   *     difference.
   */
  Set<Pair<ASTCDType, Set<ASTCDType>>> deletedInheritance();

  /**
   * Check if the elements related to the deleted superclass are present in the subclass in srcCD.
   * Those include attributes, associations and otherAssociation (unidirectional associations where
   * the class is target).
   *
   * @param astcdType superclass
   * @param subClassTgt subclass
   * @return true if at least one element is missing
   */
  boolean isInheritanceDeleted(ASTCDType astcdType, ASTCDType subClassTgt);

  /**
   * Check if an added inheritance relation brings a semantic difference.
   *
   * @return pairs of classes and a set of their new superclasses that cause a semantic difference.
   */
  Set<Pair<ASTCDType, Set<ASTCDType>>> addedInheritance();

  /**
   * Check if the elements related to the added superclass are present in the subclass in tgtCD.
   * Those include attributes, associations and otherAssociation (unidirectional associations where
   * the class is target).
   *
   * @param astcdType superclass
   * @param subClass subclass
   * @return true if at least one element is missing
   */
  boolean isInheritanceAdded(ASTCDType astcdType, ASTCDType subClass);

  /**
   * Check if a changed inheritance relation brings a semantic difference.
   *
   * @return pairs of classes and a set of their changed superclasses that cause a semantic
   *     difference.
   */
  Set<InheritanceDiff> mergeInheritanceDiffs();

  /**
   * Check if a deleted association is really deleted in the srcCD. For this, the association must
   * be pushed up in the inheritance hierarchy or all subclasses must have the association.
   *
   * @param association association to be checked
   * @return a list of maximal two classes (one for source and one for target) that don't have this
   *     association.
   */
  List<ASTCDType> isAssocDeleted(ASTCDAssociation association, ASTCDType astcdType);

  /**
   * Get a list of all deleted association that bring a semantic difference.
   *
   * @return pairs of associations and a set of maximal two classes.
   */
  List<Pair<ASTCDAssociation, List<ASTCDType>>> deletedAssocList();

  /**
   * Similar case - the association @param astcdAssociation is needed in cd1, but not in cd2.
   *
   * @return a list of maximal two classes (source and target) that one can have this association.
   */
  List<ASTCDType> isAssocAdded(ASTCDAssociation astcdAssociation);

  /**
   * Get a list of all added association that bring a semantic difference.
   *
   * @return pairs of associations and a set of maximal two classes.
   */
  List<Pair<ASTCDAssociation, List<ASTCDType>>> addedAssocList();

  /**
   * Find all overlapping and all duplicated associations. When comparing associations, we
   * distinguish two cases: 1) association and superAssociation 2) two associations with the same
   * source For the first case we do the following: If the two associations are in conflict(they
   * have the same role name in target direction) and the target classes are in an inheritance
   * relation(B extends C or C extends B), the subAssociation needs to be merged with the
   * superAssociation. If the associations are in a conflict, but aren't in an inheritance relation,
   * then the subAssociation can't exist(A.r would lead to classes with different types). For the
   * last, we also consider the cardinalities of the associations. If they are additionally at least
   * 1, then the subclass(and its subclasses) can't exist (A.r always has to lead to different
   * classes, which is not allowed). The second case is handled the same way. We distinguish the
   * cases, because in the first one additional delete operation for the used datastructure must be
   * executed. The implementation can be changed o work without the cases.
   */
  void findOverlappingAssocs();

  /**
   * Get a list of all changed enums and classes.
   *
   * @return list of TypeDiffStruc (all changes for each type).
   */
  List<TypeDiffStruct> changedTypes();

  /**
   * Get a list of all changed associations.
   *
   * @return list of AssocDiffStruc (all changes for each association).
   */
  List<AssocDiffStruct> changedAssoc();

  /**
   * Get a list of all classes that can be instantiated in srcCD, but not in trgCD because of
   * overlapping.
   *
   * @return list of classes.
   */
  List<ASTCDType> srcExistsTgtNot();

  /**
   * Get a list of classes that need an association in srcCD and tgtCD, but the association is not
   * instantiatable in srcCD because of overlapping.
   *
   * @return list of classes.
   */
  List<Pair<ASTCDClass, List<AssocStruct>>> srcAssocExistsTgtNot();

  /**
   * Get a list of classes that need an association in srcCD and tgtCD, but the association is not
   * instantiatable in trgCD because of overlapping.
   *
   * @return list of classes.
   */
  List<Pair<ASTCDClass, List<AssocStruct>>> tgtAssocsExistsSrcNot();

  /**
   * Return lists of classes that are computed by srcAssocExistsTgtNot() and
   * tgtAssocsExistsSrcNot(). Additionally, a mixed list is included (for classes with both cases).
   *
   * @return list of classes.
   */
  List<AssocMatching> getAssocDiffs();

  /**
   * Get a list of all types that have a different superclass in srcCD and trgCD (STADiff).
   *
   * @return list of classes.
   */
  List<ASTCDType> hasDiffSuper();
}
