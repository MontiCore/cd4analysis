package de.monticore.cddiff.syndiff.imp;

import com.google.common.collect.ArrayListMultimap;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.ow2cw.CDInheritanceHelper;
import de.monticore.cddiff.syndiff.OD.ODBuilder;
import de.monticore.cddiff.syndiff.datastructures.*;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.matchRoleNames;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isSuperOf;

public class Syn2SemDiffHelper {

  private static Syn2SemDiffHelper instance;

  public static Syn2SemDiffHelper getInstance() {
    if (instance == null) {
      instance = new Syn2SemDiffHelper();
    }
    return instance;
  }
  public Syn2SemDiffHelper() {
  }

  private ODBuilder ODBuilder = new ODBuilder();
  /**
   * Map with all possible associations (as AssocStructs) for classes
   * from srcCD where the given class serves as source.
   * The non-instantiatable classes and associations are removed
   * after the function findOverlappingAssocs().
   */
  private ArrayListMultimap<ASTCDClass, AssocStruct> srcMap = ArrayListMultimap.create();

  /**
   * Map with all possible associations (as AssocStructs) for classes
   * from trgCd where the given class serves as target.
   * The non-instantiatable classes and associations are removed
   * after the function findOverlappingAssocs().
   */
  private ArrayListMultimap<ASTCDClass, AssocStruct> trgMap = ArrayListMultimap.create();

  /**
   * Set with all classes that are not instantiatable in srcCD.
   */
  private Set<ASTCDClass> notInstanClassesSrc = new HashSet<>();

  /**
   * Set with all classes that are not instantiatable in trgCD.
   */
  private Set<ASTCDClass> notInstanClassesTgt = new HashSet<>();

  private ASTCDCompilationUnit srcCD;

  private ASTCDCompilationUnit tgtCD;

  private List<Pair<ASTCDClass, ASTCDClass>> matchedClasses;

  public Pair<ASTCDClass, ASTCDClass> getClassesForAssoc(Pair<ASTCDClass, ASTCDClass> pair){
    ASTCDClass left = null;
    ASTCDClass right = null;
    if (pair.a.getModifier().isAbstract()){
      left = minSubClass(pair.a);
    }
    if (pair.b.getModifier().isAbstract()){
      right = minSubClass(pair.b);
    }
    if (left != null && right != null){
      return new Pair<>(left, right);
    }
    return null;
  }

  public ArrayListMultimap<ASTCDClass, AssocStruct> getSrcMap() {
    return srcMap;
  }

  public ArrayListMultimap<ASTCDClass, AssocStruct> getTrgMap() {
    return trgMap;
  }

  public ASTCDCompilationUnit getSrcCD() {
    return srcCD;
  }

  public void setSrcCD(ASTCDCompilationUnit srcCD) {
    this.srcCD = srcCD;
  }

  public ASTCDCompilationUnit getTgtCD() {
    return tgtCD;
  }

  public void setTgtCD(ASTCDCompilationUnit tgtCD) {
    this.tgtCD = tgtCD;
  }

  public Set<ASTCDClass> getNotInstanClassesSrc() {
    return notInstanClassesSrc;
  }

  public Set<ASTCDClass> getNotInstanClassesTgt() {
    return notInstanClassesTgt;
  }

  public void updateSrc(ASTCDClass astcdClass){
    notInstanClassesSrc.add(astcdClass);
  }

  public void updateTgt(ASTCDClass astcdClass){
    notInstanClassesTgt.add(astcdClass);
  }

  public boolean isSubclassWithSuper(ASTCDClass superClass, ASTCDClass subClass) {
    return isSuperOf(superClass.getSymbol().getInternalQualifiedName(),
      subClass.getSymbol().getInternalQualifiedName(), srcCD);
  }

  public boolean isSubClassWithSuperTgt(ASTCDClass superClass, ASTCDClass subClass) {
    return isSuperOf(superClass.getSymbol().getInternalQualifiedName(),
      subClass.getSymbol().getInternalQualifiedName(), tgtCD);
  }

  /**
   * Check if an association is a superassociation of another one.
   * For this, the direction and the role names must be matched in the target direction.
   * The associated classes of the superassociation must be superclasses of the associated classes
   * of the subAssoc.
   * @param superAssoc superassociation as AssocStruct
   * @param subAssoc subassociation as AssocStruct
   * @return true if condition is fulfilled
   */
  //CHECKED
  public boolean isSubAssociationSrcSrc(AssocStruct superAssoc, AssocStruct subAssoc) {
    if (subAssoc.getSide().equals(ClassSide.Left)
      && superAssoc.getSide().equals(ClassSide.Left)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
      && isSubclassWithSuper(getConnectedClasses(superAssoc.getAssociation(), srcCD).a, getConnectedClasses(subAssoc.getAssociation(), srcCD).a)
      && isSubclassWithSuper(getConnectedClasses(superAssoc.getAssociation(), srcCD).b, getConnectedClasses(subAssoc.getAssociation(), srcCD).b)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
      && superAssoc.getSide().equals(ClassSide.Right)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
      && isSubclassWithSuper(getConnectedClasses(superAssoc.getAssociation(), srcCD).a, getConnectedClasses(subAssoc.getAssociation(), srcCD).b)
      && isSubclassWithSuper(getConnectedClasses(superAssoc.getAssociation(), srcCD).b, getConnectedClasses(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
      && superAssoc.getSide().equals(ClassSide.Left)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
      && isSubclassWithSuper(getConnectedClasses(superAssoc.getAssociation(), srcCD).a, getConnectedClasses(subAssoc.getAssociation(), srcCD).b)
      && isSubclassWithSuper(getConnectedClasses(superAssoc.getAssociation(), srcCD).b, getConnectedClasses(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
      && superAssoc.getSide().equals(ClassSide.Right)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
      && isSubclassWithSuper(getConnectedClasses(superAssoc.getAssociation(), srcCD).a, getConnectedClasses(subAssoc.getAssociation(), srcCD).a)
      && isSubclassWithSuper(getConnectedClasses(superAssoc.getAssociation(), srcCD).b, getConnectedClasses(subAssoc.getAssociation(), srcCD).b)) {
      return true;
    }
    return false;
  }

  /**
   * Get all needed associations from the srcMap that use the given class
   * as target. The associations are strictly unidirectional.
   * Needed associations - the cardinality must be at least one.
   * @param astcdClass target class
   * @return list of associations
   */
  //CHECKED
  public List<AssocStruct> getOtherAssocFromSuper(ASTCDClass astcdClass){
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDClass classToCheck : srcMap.keySet()) {
      if (classToCheck != astcdClass) {
        for (AssocStruct assocStruct : srcMap.get(classToCheck)) {
          if (assocStruct.getSide().equals(ClassSide.Left)
            && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
            && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())
            && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).b == astcdClass) {
            list.add(assocStruct.deepClone());
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
            && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
            || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())
            && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).a == astcdClass) {
            list.add(assocStruct.deepClone());
          }
        }
      }
    }
    return list;
  }

  /**
   * Get all needed associations from the tgtMap that use the given class
   * as target. The associations are strictly unidirectional.
   * Needed associations - the cardinality must be at least one.
   * @param astcdClass target class
   * @return list of associations
   */
  //CHECKED
  public List<AssocStruct> getOtherAssocsTgt(ASTCDClass astcdClass){
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDClass classToCheck : trgMap.keySet()) {
      if (classToCheck != astcdClass) {
        for (AssocStruct assocStruct : trgMap.get(classToCheck)) {
          assert assocStruct.getAssociation().getLeft() != null;
          assert assocStruct.getAssociation().getRight() != null;
          if (assocStruct.getSide().equals(ClassSide.Left)
            && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
            && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())
            && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), tgtCD).b == astcdClass) {
            list.add(assocStruct.deepClone());
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
            && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
            || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())
            && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), tgtCD).a == astcdClass) {
            list.add(assocStruct.deepClone());
          }
        }
      }
    }
    return list;
  }

  /**
   * Get all needed associations (including superclasses) from the tgtMap that
   * use the given class as target. The associations are strictly unidirectional.
   * Needed associations - the cardinality must be at least one.
   * 'Subassociations' might be included.
   * @param astcdClass target class
   * @return list of associations
   */
  //CHECKED
  public List<AssocStruct> getAllOtherAssocsTgt(ASTCDClass astcdClass){
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDClass astcdClass1 : CDDiffUtil.getAllSuperclasses(astcdClass, tgtCD.getCDDefinition().getCDClassesList())){
      list.addAll(getOtherAssocsTgt(astcdClass1));
    }
    return list;
  }

  /**
   * Get all needed associations (including superclasses) from the tgtMap that
   * use the given class as target. The associations are strictly unidirectional.
   * Needed associations - the cardinality must be at least one.
   * 'Subassociations' might be included.
   * @param astcdClass target class
   * @return list of associations
   */
  //CHECKED
  public List<AssocStruct> getAllOtherAssocsSrc(ASTCDClass astcdClass){
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDClass astcdClass1 : CDDiffUtil.getAllSuperclasses(astcdClass, srcCD.getCDDefinition().getCDClassesList())){
      list.addAll(getOtherAssocFromSuper(astcdClass1));
    }
    return list;
  }

  //CHECKED
  public ASTCDClass findMatchedClass(ASTCDClass astcdClass){
    ASTCDClass matchedClass = null;
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      if(pair.a.equals(astcdClass)){
        matchedClass = pair.b;
      }
    }
    return matchedClass;
  }

  //CHECKED
  public ASTCDClass findMatchedSrc(ASTCDClass astcdClass){
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses){
      if (pair.b.equals(astcdClass)){
        return pair.a;
      }
    }
    return null;
  }

  public void setMatchedClasses(List<Pair<ASTCDClass, ASTCDClass>> matchedClasses) {
    this.matchedClasses = matchedClasses;
  }

  public static AssocDirection getDirection(ASTCDAssociation association) {
    if (association.getCDAssocDir() == null) {
      return AssocDirection.Unspecified;
    }
    if (association.getCDAssocDir().isBidirectional()) {
      return AssocDirection.BiDirectional;
    }
    if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      return AssocDirection.RightToLeft;
    }
    if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
      return AssocDirection.LeftToRight;
    }
    return null;
  }

  /**
   * When merging associations, the role names of the bidirectional
   * association are used instead of the role names of the unidirectional.
   * @param association association
   * @param superAssoc superassociation
   */
  //CHECKED
  static void setBiDirRoleName(AssocStruct association, AssocStruct superAssoc){
    if (!association.getDirection().equals(AssocDirection.BiDirectional)
      && superAssoc.getDirection().equals(AssocDirection.BiDirectional)) {
      if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Left)) {
        association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
      } else if (association.getSide().equals(ClassSide.Left) && superAssoc.getSide().equals(ClassSide.Right)) {
        association.getAssociation().getLeft().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
      } else if (association.getSide().equals(ClassSide.Right) && superAssoc.getSide().equals(ClassSide.Left)) {
        association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
      } else {
        association.getAssociation().getRight().setCDRole(superAssoc.getAssociation().getRight().getCDRole());
      }
    }
  }

  /**
   * Merge the cardinalities and the direction of two associations.
   * @param association association
   * @param superAssoc superassociation
   */
  //CHECKED
  static void mergeAssocs(AssocStruct association, AssocStruct superAssoc){
    ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
    CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
    AssocCardinality cardinalityLeft = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
    AssocCardinality cardinalityRight = Syn2SemDiffHelper.intersectCardinalities(Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a), Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
    association.getAssociation().setCDAssocDir(direction);
    if (association.getSide().equals(ClassSide.Left)) {
      association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
      association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
    } else {
      association.getAssociation().getLeft().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
      association.getAssociation().getRight().setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
    }
  }

  //CHECKED
  /**
   * Modified version of the function inConflict in CDAssociationHelper.
   * In the map, all association that can be created from a class
   * are saved in the values for this class (key).
   * Because of that we don't need to check if the source classes of both
   * associations are in an inheritance relation.
   * @param association association from the class
   * @param superAssociation superAssociation for that class
   * @return true, if the role names in target direction are the same
   */
  public static boolean isInConflict(AssocStruct association, AssocStruct superAssociation){
    ASTCDAssociation srcAssoc = association.getAssociation();
    ASTCDAssociation targetAssoc = superAssociation.getAssociation();

    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Left)){
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight());
    }
    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Right)){
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft());
    }
    if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Left)){
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight());
    }
    if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Right)){
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft());
    }

    return false;
  }

  /**
   * Check if the role names on the source side are the same.
   * @param assocDown association
   * @param assocUp association
   * @return true if the role names are the same
   */
  //CHECKED
  public static boolean sameRoleNamesSrc(AssocStruct assocDown, AssocStruct assocUp){
    if (assocDown.getSide().equals(ClassSide.Left) && assocUp.getSide().equals(ClassSide.Left)){
      return CDDiffUtil.inferRole(assocDown.getAssociation().getLeft()).equals(CDDiffUtil.inferRole(assocUp.getAssociation().getLeft()));
    } else if (assocDown.getSide().equals(ClassSide.Left) && assocUp.getSide().equals(ClassSide.Right)) {
      return CDDiffUtil.inferRole(assocDown.getAssociation().getLeft()).equals(CDDiffUtil.inferRole(assocUp.getAssociation().getRight()));
    } else if (assocDown.getSide().equals(ClassSide.Right) && assocUp.getSide().equals(ClassSide.Left)){
      return CDDiffUtil.inferRole(assocDown.getAssociation().getRight()).equals(CDDiffUtil.inferRole(assocUp.getAssociation().getLeft()));
    } else {
      return CDDiffUtil.inferRole(assocDown.getAssociation().getRight()).equals(CDDiffUtil.inferRole(assocUp.getAssociation().getRight()));
    }
  }

  /**
   * Given the two associations, get the role name that causes the conflict
   * @param association base association
   * @param superAssociation association from superclass
   * @return role name
   */
  public static ASTCDRole getConflict(AssocStruct association, AssocStruct superAssociation){
    ASTCDAssociation srcAssoc = association.getAssociation();
    ASTCDAssociation targetAssoc = superAssociation.getAssociation();

    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Left)
      && matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight())){
      return srcAssoc.getRight().getCDRole();
    }
    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Right)
      && matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft())) {
      return srcAssoc.getRight().getCDRole();
    }
    if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Left)
      && matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight())) {
      return srcAssoc.getLeft().getCDRole();
    }
    if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Right)
      && matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft())) {
      return srcAssoc.getLeft().getCDRole();
    }

    return null;
  }

  //CHECKED
  /**
   * Merge the directions of two associations
   * @param association association from the class
   * @param superAssociation association from the class or superAssociation
   * @return merged direction in ASTCDAssocDir
   */
  public static ASTCDAssocDir mergeAssocDir(AssocStruct association, AssocStruct superAssociation){
    if (association.getDirection().equals(AssocDirection.BiDirectional) || superAssociation.getDirection().equals(AssocDirection.BiDirectional)){
      return new ASTCDBiDir();
    } else if (association.getDirection().equals(AssocDirection.LeftToRight)) {
      if (superAssociation.getDirection().equals(AssocDirection.LeftToRight)){
        return new ASTCDLeftToRightDir();
      }
      if (superAssociation.getDirection().equals(AssocDirection.RightToLeft)){
        return new ASTCDBiDir();
      }
    } else if (association.getDirection().equals(AssocDirection.RightToLeft)){
      if (superAssociation.getDirection().equals(AssocDirection.RightToLeft)){
        return new ASTCDRightToLeftDir();
      }
      if (superAssociation.getDirection().equals(AssocDirection.LeftToRight)){
        return new ASTCDBiDir();
      }
    }
    return null;
  }

  //CHECKED
  /**
   * Group corresponding cardinalities
   * @param association association
   * @param superAssociation association
   * @return structure with two pairs of corresponding cardinalities
   */
  public static CardinalityStruc getCardinalities(AssocStruct association, AssocStruct superAssociation){
    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Left)){
      return new CardinalityStruc(new Pair<>(association.getAssociation().getLeft().getCDCardinality(), superAssociation.getAssociation().getLeft().getCDCardinality()),
        new Pair<>(association.getAssociation().getRight().getCDCardinality(), superAssociation.getAssociation().getRight().getCDCardinality()));
    } else if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Right)){
      return new CardinalityStruc(new Pair<>(association.getAssociation().getLeft().getCDCardinality(), superAssociation.getAssociation().getRight().getCDCardinality()),
        new Pair<>(association.getAssociation().getRight().getCDCardinality(), superAssociation.getAssociation().getLeft().getCDCardinality()));
    } else if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Left)){
      return new CardinalityStruc(new Pair<>(association.getAssociation().getRight().getCDCardinality(), superAssociation.getAssociation().getLeft().getCDCardinality()),
        new Pair<>(association.getAssociation().getLeft().getCDCardinality(), superAssociation.getAssociation().getRight().getCDCardinality()));
    } else {
      return new CardinalityStruc(new Pair<>(association.getAssociation().getRight().getCDCardinality(), superAssociation.getAssociation().getRight().getCDCardinality()),
        new Pair<>(association.getAssociation().getLeft().getCDCardinality(), superAssociation.getAssociation().getLeft().getCDCardinality()));
    }
  }

  //CHECKED
  /**
   * Transform the internal cardinality to original
   * @param assocCardinality cardinality to transform
   * @return cardinality with type ASTCDCardinality
   */
  public static ASTCDCardinality createCardinality(AssocCardinality assocCardinality){
    if (assocCardinality.equals(AssocCardinality.One)){
      return new ASTCDCardOne();
    } else if (assocCardinality.equals(AssocCardinality.Optional)) {
      return new ASTCDCardOpt();
    } else if (assocCardinality.equals(AssocCardinality.AtLeastOne)) {
      return new ASTCDCardAtLeastOne();
    } else {
      return new ASTCDCardMult();
    }
  }

  //CHECKED
  /**
   * Check if the associations allow 0 objects from target class
   * @param association association
   * @param superAssociation association
   * @return true if the condition is fulfilled
   */
  public static boolean areZeroAssocs(AssocStruct association, AssocStruct superAssociation){
    if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Left)){
      return (association.getAssociation().getRight().getCDCardinality().isMult() || association.getAssociation().getRight().getCDCardinality().isOpt())
        && (superAssociation.getAssociation().getRight().getCDCardinality().isMult() || superAssociation.getAssociation().getRight().getCDCardinality().isOpt());
    } else if (association.getSide().equals(ClassSide.Left) && superAssociation.getSide().equals(ClassSide.Right)){
      return (association.getAssociation().getRight().getCDCardinality().isMult() || association.getAssociation().getRight().getCDCardinality().isOpt())
        && (superAssociation.getAssociation().getLeft().getCDCardinality().isMult() || superAssociation.getAssociation().getLeft().getCDCardinality().isOpt());
    } else if (association.getSide().equals(ClassSide.Right) && superAssociation.getSide().equals(ClassSide.Left)){
      return (association.getAssociation().getLeft().getCDCardinality().isMult() || association.getAssociation().getLeft().getCDCardinality().isOpt())
        && (superAssociation.getAssociation().getRight().getCDCardinality().isMult() || superAssociation.getAssociation().getRight().getCDCardinality().isOpt());
    } else {
      return (association.getAssociation().getLeft().getCDCardinality().isMult() || association.getAssociation().getLeft().getCDCardinality().isOpt())
        && (superAssociation.getAssociation().getLeft().getCDCardinality().isMult() || superAssociation.getAssociation().getLeft().getCDCardinality().isOpt());
    }
  }

  public boolean isAdded(AssocStruct assocStruct, AssocStruct assocStruct2, ASTCDClass astcdClass, Set<DeleteStruc> set){
    for (DeleteStruc deleteStruc : set){
      if (((deleteStruc.getAssociation().equals(assocStruct)
        && deleteStruc.getSuperAssoc().equals(assocStruct2))
        || ((deleteStruc.getAssociation().equals(assocStruct2) && deleteStruc.getSuperAssoc().equals(assocStruct))))
        && deleteStruc.getAstcdClass().equals(astcdClass)){
        return true;
      }
    }
    return false;
  }

  /**
   * Check if all matched subclasses in srcCD of a class from trgCD have
   * the same association or a subassociation.
   * @param association association from trgCD.
   * @param astcdClass class from trgCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  //CHECKED
  public ASTCDClass allSubclassesHaveIt(AssocStruct association, ASTCDClass astcdClass){
    List<ASTCDClass> subClassesTgt = getSpannedInheritance(tgtCD, astcdClass);
    List<ASTCDClass> subclassesSrc = getSrcClasses(subClassesTgt);
    for (ASTCDClass subClass : subclassesSrc){
      boolean isContained = false;
      for (AssocStruct assocStruct : srcMap.get(subClass)){
        if (sameAssociationTypeTgtSrc(assocStruct, association)){
          isContained = true;
          break;
        }
      }
      if (!isContained){
        return subClass;
      }
    }
    return null;
  }

  /**
   * Check if all matched subclasses in trgCD of a class from srcCD have
   * the same association or a subassociation.
   * @param association association from srcCD.
   * @param astcdClass class from srcCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  //CHECKED
  public ASTCDClass allSubClassesHaveItTgt(AssocStruct association, ASTCDClass astcdClass){
    List<ASTCDClass> subClassesSrc = getSpannedInheritance(srcCD, astcdClass);
    List<ASTCDClass> subClassesTgt = getTgtClasses(subClassesSrc);
    for (ASTCDClass subClass : subClassesTgt){
      boolean isContained = false;
      for (AssocStruct assocStruct : trgMap.get(subClass)){
        if (sameAssociationTypeSrcTgt(assocStruct, association)){
          isContained = true;
          break;
        }
      }
      if (!isContained){
        return subClass;
      }
    }
    return null;
  }

  /**
   * Similar to the function above, but the now the classes must be
   * target of the association.
   * @param matchedAssocStruc association from srcCD.
   * @param astcdClass class from srcCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  //CHECKED
  public ASTCDClass allSubClassesAreTgtSrcTgt(AssocStruct matchedAssocStruc, ASTCDClass astcdClass) {
    List<ASTCDClass> subClasses = getSpannedInheritance(srcCD, astcdClass);
    List<ASTCDClass> subClassesTgt = getTgtClasses(subClasses);
    for (ASTCDClass subClass : subClassesTgt) {
      boolean contained = false;
      for (AssocStruct assocStruct : getAllOtherAssocsTgt(subClass)) {
        if (sameAssociationTypeSrcTgt(assocStruct, matchedAssocStruc)) {
          contained = true;
          break;
        }
      }
      if (!contained) {
        return subClass;
      }
    }
    return null;
  }

  public ASTCDClass allSubClassesAreTgtTgtSrc(AssocStruct matchedAssocStruct, ASTCDClass astcdClass) {
    List<ASTCDClass> subClasses = getSpannedInheritance(tgtCD, astcdClass);
    List<ASTCDClass> subClassesSrc = getSrcClasses(subClasses);
    for (ASTCDClass subClass : subClassesSrc) {
      boolean contained = false;
      for (AssocStruct assocStruct : getAllOtherAssocsTgt(subClass)) {
        if (sameAssociationTypeTgtSrc(assocStruct, matchedAssocStruct)) {
          contained = true;
          break;
        }
      }
      if (!contained) {
        return subClass;
      }
    }
    return null;
  }

  /**
   * Check if the second association from trgCD is a subassociation of the first
   * association from srcCD.
   * @param superAssoc association from srcCD.
   * @param subAssoc association from trgCD.
   * @return true if the condition is fulfilled.
   */
  public boolean isSubAssociationTgtSrc(AssocStruct superAssoc, AssocStruct subAssoc){
    if (subAssoc.getSide().equals(ClassSide.Left)
      && superAssoc.getSide().equals(ClassSide.Left)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
      && compareTgtSrc(getConnectedClasses(superAssoc.getAssociation(), tgtCD).a, getConnectedClasses(subAssoc.getAssociation(), srcCD).a)
      && compareTgtSrc(getConnectedClasses(superAssoc.getAssociation(), tgtCD).b, getConnectedClasses(subAssoc.getAssociation(), srcCD).b)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
      && superAssoc.getSide().equals(ClassSide.Right)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
      && compareTgtSrc(getConnectedClasses(superAssoc.getAssociation(), tgtCD).a, getConnectedClasses(subAssoc.getAssociation(), srcCD).b)
      && compareTgtSrc(getConnectedClasses(superAssoc.getAssociation(), tgtCD).b, getConnectedClasses(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
      && superAssoc.getSide().equals(ClassSide.Left)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
      && compareTgtSrc(getConnectedClasses(superAssoc.getAssociation(), tgtCD).a, getConnectedClasses(subAssoc.getAssociation(), srcCD).b)
      && compareTgtSrc(getConnectedClasses(superAssoc.getAssociation(), tgtCD).b, getConnectedClasses(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
      && superAssoc.getSide().equals(ClassSide.Right)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
      && compareTgtSrc(getConnectedClasses(superAssoc.getAssociation(), tgtCD).a, getConnectedClasses(subAssoc.getAssociation(), srcCD).a)
      && compareTgtSrc(getConnectedClasses(superAssoc.getAssociation(), tgtCD).b, getConnectedClasses(subAssoc.getAssociation(), srcCD).b)) {
      return true;
    }
    return false;
  }

  /**
   * Check if the second association from trgCD is a subassociation of the first
   * association from trgCD.
   * @param superAssoc association from srcCD.
   * @param subAssoc association from trgCD.
   * @return true if the condition is fulfilled.
   */
  public boolean isSubAssociationTgtTgt(AssocStruct superAssoc, AssocStruct subAssoc){
    if (subAssoc.getSide().equals(ClassSide.Left)
      && superAssoc.getSide().equals(ClassSide.Left)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
      && (isSubClassWithSuperTgt(getConnectedClasses(superAssoc.getAssociation(), tgtCD).a, getConnectedClasses(subAssoc.getAssociation(), tgtCD).a)
      && isSubClassWithSuperTgt(getConnectedClasses(superAssoc.getAssociation(), tgtCD).b, getConnectedClasses(subAssoc.getAssociation(), tgtCD).b))) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
      && superAssoc.getSide().equals(ClassSide.Right)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
      && isSubClassWithSuperTgt(getConnectedClasses(superAssoc.getAssociation(), tgtCD).a, getConnectedClasses(subAssoc.getAssociation(), tgtCD).b)
      && isSubClassWithSuperTgt(getConnectedClasses(superAssoc.getAssociation(), tgtCD).b, getConnectedClasses(subAssoc.getAssociation(), tgtCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
      && superAssoc.getSide().equals(ClassSide.Left)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
      && isSubClassWithSuperTgt(getConnectedClasses(superAssoc.getAssociation(), tgtCD).a, getConnectedClasses(subAssoc.getAssociation(), tgtCD).b)
      && isSubClassWithSuperTgt(getConnectedClasses(superAssoc.getAssociation(), tgtCD).b, getConnectedClasses(subAssoc.getAssociation(), tgtCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
      && superAssoc.getSide().equals(ClassSide.Right)
      && matchDirection(superAssoc,  new Pair<>(subAssoc, subAssoc.getSide()))
      && matchRoleNames(superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
      && matchRoleNames(superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
      && isSubClassWithSuperTgt(getConnectedClasses(superAssoc.getAssociation(), tgtCD).a, getConnectedClasses(subAssoc.getAssociation(), tgtCD).a)
      && isSubClassWithSuperTgt(getConnectedClasses(superAssoc.getAssociation(), tgtCD).b, getConnectedClasses(subAssoc.getAssociation(), tgtCD).b)) {
      return true;
    }
    return false;
  }

  /**
   * Check if the classes are in an inheritance relation.
   * For this, the matched classes in trgCD of the srcClass are compared
   * with isSuper() to the tgtClass.
   * @param srcClass class from srcCD.
   * @param tgtClass class from trgCD.
   * @return true if the condition is fulfilled.
   */
  public boolean compareSrcTgt(ASTCDClass srcClass, ASTCDClass tgtClass){
    if (findMatchedSrc(tgtClass) != null){
      return isSuperOf(srcClass.getSymbol().getInternalQualifiedName(),
        findMatchedSrc(tgtClass).getSymbol().getInternalQualifiedName(),
        (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    }
    List<ASTCDClass> subClasses = getSpannedInheritance(srcCD, srcClass);
    List<ASTCDClass> subClassesTgt = getTgtClasses(subClasses);
    for (ASTCDClass subClass : subClassesTgt){
      if (isSuperOf(tgtClass.getSymbol().getInternalQualifiedName(),
        subClass.getSymbol().getInternalQualifiedName(),
        (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())){
        return true;
      }
    }
    return false;
  }

  /**
   * Check if the classes are in an inheritance relation.
   * For this, the matched classes in srcCD of the tgtClass are compared
   * with isSuper() to the srcClass.
   * @param tgtClass class from trgCD.
   * @param srcClass class from srcCD.
   * @return true if the condition is fulfilled.
   */
  public boolean compareTgtSrc(ASTCDClass tgtClass, ASTCDClass srcClass) {
    if (findMatchedClass(srcClass) != null) {
      return isSuperOf(tgtClass.getSymbol().getInternalQualifiedName(),
        findMatchedClass(srcClass).getSymbol().getInternalQualifiedName(),
        (ICD4CodeArtifactScope) tgtCD.getEnclosingScope());
    }
    List<ASTCDClass> subClasses = getSpannedInheritance(tgtCD, tgtClass);
    List<ASTCDClass> subClassesSrc = getSrcClasses(subClasses);
    for (ASTCDClass subClass : subClassesSrc) {
      if (isSuperOf(srcClass.getSymbol().getInternalQualifiedName(),
        subClass.getSymbol().getInternalQualifiedName(),
        (ICD4CodeArtifactScope) srcCD.getEnclosingScope())) {
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public boolean classHasAssociationSrcSrc(AssocStruct association, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : srcMap.get(astcdClass)){
      if (sameAssociationType(association, assocStruct)){
        return true;
      }
    }
    return false;
  }

  public boolean classHasAssociationTgtTgt(AssocStruct association, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : trgMap.get(astcdClass)){
      if (sameAssociationTypeTgtTgt(assocStruct, association)){
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public boolean classHasAssociationTgtSrc(AssocStruct assocStruct, ASTCDClass astcdClass){
    for (AssocStruct assocStruct1 : srcMap.get(astcdClass)){
      if (isSubAssociationTgtSrc(assocStruct, assocStruct1)){
        return true;
      }
    }
    return false;
  }


  //CHECKED
  public boolean classHasAssociationSrcTgt(AssocStruct assocStruct, ASTCDClass astcdClass){
    for (AssocStruct assocStruct1 : trgMap.get(astcdClass)){
      if (sameAssociationTypeSrcTgt(assocStruct1, assocStruct)){
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public boolean classIsTarget(AssocStruct association, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : getAllOtherAssocsSrc(astcdClass)){
      if (sameAssociationType(association, assocStruct)){
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public boolean classIsTgtTgtTgt(AssocStruct association, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : getAllOtherAssocsTgt(astcdClass)){
      if (sameAssociationTypeTgtTgt(assocStruct, association)){
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public boolean classIsTgtSrcTgt(AssocStruct association, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : getAllOtherAssocsTgt(astcdClass)){
      if (sameAssociationTypeSrcTgt(assocStruct, association)){
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public boolean classIsTargetTgtSrc(AssocStruct association, ASTCDClass astcdClass){
    for (AssocStruct assocStruct : getAllOtherAssocsSrc(astcdClass)){
      if (sameAssociationTypeTgtSrc(assocStruct, association)){
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public List<ASTCDClass> getSrcClasses(List<ASTCDClass> classes){
    List<ASTCDClass> srcClasses = new ArrayList<>();
    for (ASTCDClass astcdClass : classes){
      ASTCDClass matched = findMatchedSrc(astcdClass);
      if (matched != null){
        srcClasses.add(matched);
      }
    }
    return srcClasses;
  }

  //CHECKED
  public List<ASTCDClass> getTgtClasses(List<ASTCDClass> classes){
    List<ASTCDClass> tgtClasses = new ArrayList<>();
    for (ASTCDClass astcdClass : classes){
      ASTCDClass matched = findMatchedClass(astcdClass);
      if (matched != null){
        tgtClasses.add(matched);
      }
    }
    return tgtClasses;
  }

  //CHECKED
  public AssocStruct getAssocStrucForClassTgt(ASTCDClass astcdClass, ASTCDAssociation association){
    for (AssocStruct assocStruct : trgMap.get(astcdClass)){
      if (sameAssociation(assocStruct.getAssociation(), association)){
        return assocStruct;
      }
    }
    return null;
  }

  //CHECKED
  /**
   * Get the AssocStruc that has the same type
   * @param astcdClass class to search in
   * @param association association to match with
   * @return matched association, if found
   */
  public AssocStruct getAssocStrucForClass(ASTCDClass astcdClass, ASTCDAssociation association){
    for (AssocStruct assocStruct : srcMap.get(astcdClass)){
      if (sameAssociation(assocStruct.getAssociation(), association)){
        return assocStruct;
      }
    }
    return null;
  }

  /**
   * Check if two associations are exactly the same.
   * @param association association.
   * @param association2 association.
   * @return true if the condition is fulfilled.
   */
  //CHECKED
  public boolean sameAssociation(ASTCDAssociation association, ASTCDAssociation association2){
    Pair<ASTCDCardinality, ASTCDCardinality> cardinalities = getCardinality(association2);
    if (association.getLeftQualifiedName().getQName().equals(association2.getLeftQualifiedName().getQName())
      && association
      .getRightQualifiedName()
      .getQName()
      .equals(association2.getRightQualifiedName().getQName())) {
      return matchRoleNames(association.getLeft(), association2.getLeft())
        && matchRoleNames(association.getRight(), association2.getRight())
        && Syn2SemDiffHelper.getDirection(association).equals(Syn2SemDiffHelper.getDirection(association2))
        && cardToEnum(association.getLeft().getCDCardinality()).equals(cardToEnum(cardinalities.a))
        && cardToEnum(association.getRight().getCDCardinality()).equals(cardToEnum(cardinalities.b));
    }
    return false;
  }

  //CHECKED
  public boolean srcAssocExistsTgtNot(ASTCDAssociation association, ASTCDAssociation association2) {
    boolean exists1 = false;
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
    AssocStruct assocStructLeft = getAssocStrucForClass(pair.a, association);
    AssocStruct assocStructRight = getAssocStrucForClass(pair.b, association);
    if (association.getCDAssocDir().isBidirectional()) {
      if (assocStructLeft != null && assocStructRight != null) {
        exists1 = true;
      }
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      if (assocStructRight != null) {
        exists1 = true;
      }
    } else {
      if (assocStructLeft != null) {
        exists1 = true;
      }
    }

    boolean exists2 = false;
    Pair<ASTCDClass, ASTCDClass> pair2 = Syn2SemDiffHelper.getConnectedClasses(association2, tgtCD);
    AssocStruct assocStructLeftTgt = getAssocStrucForClassTgt(pair2.a, association2);
    AssocStruct assocStructRightTgt = getAssocStrucForClassTgt(pair2.b, association2);
    if (association.getCDAssocDir().isBidirectional()) {
      if (assocStructLeftTgt != null && assocStructRightTgt != null) {
        exists2 = true;
      }
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      if (assocStructRightTgt != null) {
        exists2 = true;
      }
    } else {
      if (assocStructLeftTgt != null) {
        exists2 = true;
      }
    }
    return exists1 && !exists2;
  }

  //CHECKED
  public boolean srcNotTgtExists(ASTCDAssociation association, ASTCDAssociation association2){
    boolean exists1 = false;
    Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
    AssocStruct assocStructLeft = getAssocStrucForClass(pair.a, association);
    AssocStruct assocStructRight = getAssocStrucForClass(pair.b, association);
    if (association.getCDAssocDir().isBidirectional()) {
      if (assocStructLeft != null && assocStructRight != null) {
        exists1 = true;
      }
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      if (assocStructRight != null) {
        exists1 = true;
      }
    } else {
      if (assocStructLeft != null) {
        exists1 = true;
      }
    }

    boolean exists2 = false;
    Pair<ASTCDClass, ASTCDClass> pair2 = Syn2SemDiffHelper.getConnectedClasses(association2, tgtCD);
    AssocStruct assocStructLeftTgt = getAssocStrucForClass(pair2.a, association2);
    AssocStruct assocStructRightTgt = getAssocStrucForClass(pair2.b, association2);
    if (association.getCDAssocDir().isBidirectional()) {
      if (assocStructLeftTgt != null && assocStructRightTgt != null) {
        exists2 = true;
      }
    } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
      if (assocStructRightTgt != null) {
        exists2 = true;
      }
    } else {
      if (assocStructLeftTgt != null) {
        exists2 = true;
      }
    }
    return !exists1 && exists2;
  }

  //CHECKED
  /**
   * Check if the target classes of the two associations are in an inheritance relation
   * @param association base association
   * @param superAssociation association from superclass
   * @return true, if they fulfill the condition
   */
  public boolean inInheritanceRelation(AssocStruct association, AssocStruct superAssociation){
    if (association.getSide().equals(ClassSide.Left)
      && superAssociation.getSide().equals(ClassSide.Left)){
      return isSuperOf(association.getAssociation().getRightQualifiedName().getQName(),
        superAssociation.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getRightQualifiedName().getQName(), association.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
      //do I also need to check the other way around
    } else if (association.getSide().equals(ClassSide.Left)
      && superAssociation.getSide().equals(ClassSide.Right)) {
      return isSuperOf(association.getAssociation().getRightQualifiedName().getQName(),
        superAssociation.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getLeftQualifiedName().getQName(), association.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    } else if (association.getSide().equals(ClassSide.Right)
      && superAssociation.getSide().equals(ClassSide.Left)){
      return isSuperOf(association.getAssociation().getLeftQualifiedName().getQName(),
        superAssociation.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getRightQualifiedName().getQName(), association.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    } else {
      return isSuperOf(association.getAssociation().getLeftQualifiedName().getQName(),
        superAssociation.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        || isSuperOf(superAssociation.getAssociation().getLeftQualifiedName().getQName(), association.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    }
  }

  //CHECKED
  /**
   * Get all attributes that need to be added from inheritance structure to an object of a given type
   * @param astcdClass class
   * @return Pair of the class and a list of attributes
   */
  public Pair<ASTCDClass, List<ASTCDAttribute>> getAllAttr(ASTCDClass astcdClass){
    List<ASTCDAttribute> attributes = new ArrayList<>();
    Set<ASTCDType> classes = getAllSuper(astcdClass, (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    for (ASTCDType classToCheck : classes){
      if (classToCheck instanceof ASTCDClass) {
        attributes.addAll(classToCheck.getCDAttributeList());
      }
    }
    return new Pair<>(astcdClass, attributes);
  }

  //CHECKED
  public static boolean isAttContainedInClass(ASTCDAttribute attribute, ASTCDClass astcdClass){
    for (ASTCDAttribute att : astcdClass.getCDAttributeList()){
      if ((att.getName().equals(attribute.getName())
        && att.getMCType().printType().equals(attribute.getMCType().printType()))){
        return true;
      }
    }
    return false;
  }

  //CHECKED
  public boolean sameAssociationType(AssocStruct assocStruct, AssocStruct assocStruct1){
    if ((assocStruct1.getSide().equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Left))) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getRightQualifiedName().getQName(),
        assocStruct.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
        assocStruct.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if ((assocStruct1.getSide().equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Right))) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getRightQualifiedName().getQName(),
        assocStruct.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
        assocStruct.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
        assocStruct.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getRightQualifiedName().getQName(),
        assocStruct.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
        assocStruct.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getRightQualifiedName().getQName(),
        assocStruct.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    }
    return false;
  }

  //CHECKED
  public boolean sameAssociationTypeSrcTgt(AssocStruct assocStruct1, AssocStruct assocStruct){
    if ((assocStruct1.getSide().equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Left))) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && compareSrcTgt(getConnectedClasses(assocStruct1.getAssociation(), srcCD).a, getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)
        && compareSrcTgt(getConnectedClasses(assocStruct1.getAssociation(), srcCD).b, getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if ((assocStruct1.getSide().equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Right))) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && compareSrcTgt(getConnectedClasses(assocStruct1.getAssociation(), srcCD).a, getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)
        && compareSrcTgt(getConnectedClasses(assocStruct1.getAssociation(), srcCD).b, getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && compareSrcTgt(getConnectedClasses(assocStruct1.getAssociation(), srcCD).a, getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)
        && compareSrcTgt(getConnectedClasses(assocStruct1.getAssociation(), srcCD).b, getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && compareSrcTgt(getConnectedClasses(assocStruct1.getAssociation(), srcCD).a, getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)
        && compareSrcTgt(getConnectedClasses(assocStruct1.getAssociation(), srcCD).b, getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    }
    return false;
  }

  //CHECKED
  public boolean sameAssociationTypeTgtSrc(AssocStruct assocStruct1, AssocStruct assocStruct){
    if ((assocStruct1.getSide().equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Left))) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        &&compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a, getConnectedClasses(assocStruct.getAssociation(), srcCD).a)
        && compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b, getConnectedClasses(assocStruct.getAssociation(), srcCD).b)
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if ((assocStruct1.getSide().equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Right))) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a, getConnectedClasses(assocStruct.getAssociation(), srcCD).b)
        && compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b, getConnectedClasses(assocStruct.getAssociation(), srcCD).a)
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a, getConnectedClasses(assocStruct.getAssociation(), srcCD).a)
        && compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b, getConnectedClasses(assocStruct.getAssociation(), srcCD).b)
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a, getConnectedClasses(assocStruct.getAssociation(), srcCD).b)
        && compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b, getConnectedClasses(assocStruct.getAssociation(), srcCD).a)
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    }
    return false;
  }

  //CHECKED
  public boolean sameAssociationTypeTgtTgt(AssocStruct assocStruct1, AssocStruct assocStruct){
    if ((assocStruct1.getSide().equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Left))) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getRightQualifiedName().getQName(),
        assocStruct.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
        assocStruct.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if ((assocStruct1.getSide().equals(ClassSide.Left) && assocStruct.getSide().equals(ClassSide.Right))) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getRightQualifiedName().getQName(),
        assocStruct.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
        assocStruct.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
        assocStruct.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getRightQualifiedName().getQName(),
        assocStruct.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right) && assocStruct.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
        && matchRoleNames(assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
        && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
        assocStruct.getAssociation().getRightQualifiedName().getQName(), (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
        && CDInheritanceHelper.isSuperOf(assocStruct1.getAssociation().getRightQualifiedName().getQName(),
        assocStruct.getAssociation().getLeftQualifiedName().getQName(), (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
        && isContainedIn(cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()), cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    }
    return false;
  }

  //CHECKED
  /**
   * Given the following two cardinalities, find their intersection
   * @param cardinalityA first cardinality
   * @param cardinalityB second cardinality
   * @return intersection of the cardinalities
   */
  public static AssocCardinality intersectCardinalities(AssocCardinality cardinalityA, AssocCardinality cardinalityB) {
    if (cardinalityA == null){
      return cardinalityB;
    }
    int i = 0;
    if (cardinalityA.equals(AssocCardinality.One)) {
      return AssocCardinality.One;
    } else if (cardinalityA.equals(AssocCardinality.Optional)) {
      if (cardinalityB.equals(AssocCardinality.One)) {
        return AssocCardinality.One;
      } else if (cardinalityB.equals(AssocCardinality.Multiple) || cardinalityB.equals(AssocCardinality.Optional)) {
        return AssocCardinality.Optional;
      } else if (cardinalityB.equals(AssocCardinality.AtLeastOne)) {
        return AssocCardinality.One;
      }
    } else if (cardinalityA.equals(AssocCardinality.Multiple)) {
      if (cardinalityB.equals(AssocCardinality.One)) {
        return AssocCardinality.One;
      } else if (cardinalityB.equals(AssocCardinality.Optional)) {
        return AssocCardinality.Optional;
      } else if (cardinalityB.equals(AssocCardinality.Multiple)) {
        return AssocCardinality.Multiple;
      } else if (cardinalityB.equals(AssocCardinality.AtLeastOne)) {
        return AssocCardinality.AtLeastOne;
      }
    } else if (cardinalityA.equals(AssocCardinality.AtLeastOne)) {
      if (cardinalityB.equals(AssocCardinality.One) || cardinalityB.equals(AssocCardinality.Optional)) {
        return AssocCardinality.AtLeastOne;
      } else if (cardinalityB.equals(AssocCardinality.Multiple) || cardinalityB.equals(AssocCardinality.AtLeastOne)) {
        return AssocCardinality.AtLeastOne;
      }
    }
    assert false;
    return null;
  }

  public List<ASTCDAssociation> getCDAssociationsListForTypeSrc(ASTCDType type) {
    List<ASTCDAssociation> result = new ArrayList<>();
    for (ASTCDAssociation association : srcCD.getCDDefinition().getCDAssociationsList()){
      if (association.getLeftQualifiedName().getQName().equals(type.getSymbol().getInternalQualifiedName())
        && association.getCDAssocDir().isDefinitiveNavigableRight()){
        result.add(association);
      }
      else if (association.getRightQualifiedName().getQName().equals(type.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft()){
        result.add(association);
      }
    }
    return result;
  }

  public List<ASTCDAssociation> getCDAssociationsListForTypeTgt(ASTCDType type) {
    List<ASTCDAssociation> result = new ArrayList<>();
    for (ASTCDAssociation association : tgtCD.getCDDefinition().getCDAssociationsList()){

      if (association.getLeftQualifiedName().getQName().equals(type.getSymbol().getInternalQualifiedName())
        && association.getCDAssocDir().isDefinitiveNavigableRight()){
        result.add(association);
      }
      else if (association.getRightQualifiedName().getQName().equals(type.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft()){
        result.add(association);
      }
    }
    return result;
  }

  //CHECKED
  /**
   * Compute what associations can be used from a class (associations that were from the class and superAssociations).
   * For each class and each possible association we save the direction and
   * also on which side the class is.
   * Two maps are created - srcMap (for srcCD) and trgMap (for trgCD).
   */
  public void setMaps(){
    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
      for (ASTCDAssociation astcdAssociation : getCDAssociationsListForTypeSrc(astcdClass)){
        Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation, getSrcCD());
        if (pair.a == null){
          continue;
        }
        ASTCDAssociation copyAssoc = astcdAssociation.deepClone();
        copyAssoc.setName(" ");
        if (!copyAssoc.getLeft().isPresentCDCardinality()){
          copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
        }
        if (!copyAssoc.getRight().isPresentCDCardinality()){
          copyAssoc.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
        }
        if (copyAssoc.getCDAssocType().isComposition()){
          copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
        }
        copyAssoc.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(astcdAssociation.getLeft())).build());
        copyAssoc.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(astcdAssociation.getRight())).build());
        if ((pair.a.getSymbol().getInternalQualifiedName()
          .equals(astcdClass.getSymbol().getInternalQualifiedName())
          && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())){
          if (astcdAssociation.getCDAssocDir().isBidirectional() || getDirection(astcdAssociation).equals(AssocDirection.Unspecified)) {
            assert copyAssoc.getLeft().isPresentCDCardinality();
            assert copyAssoc.getRight().isPresentCDCardinality();
            getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left));
          }
          else {
            assert copyAssoc.getLeft().isPresentCDCardinality();
            assert copyAssoc.getRight().isPresentCDCardinality();
            getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left));
          }
        } if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional() || getDirection(astcdAssociation).equals(AssocDirection.Unspecified)) {
            assert copyAssoc.getLeft().isPresentCDCardinality();
            assert copyAssoc.getRight().isPresentCDCardinality();
            getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right));
          }
          else {
            assert copyAssoc.getLeft().isPresentCDCardinality();
            assert copyAssoc.getRight().isPresentCDCardinality();
            getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right));
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getTgtCD().getCDDefinition().getCDClassesList()){
      for (ASTCDAssociation astcdAssociation : getCDAssociationsListForTypeTgt(astcdClass)){
        Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(astcdAssociation, getTgtCD());
        if (pair.a == null){
          continue;
        }
        ASTCDAssociation copyAssoc = astcdAssociation.deepClone();
        copyAssoc.setName(" ");
        if (!copyAssoc.getLeft().isPresentCDCardinality()){
          copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
        }
        if (!copyAssoc.getRight().isPresentCDCardinality()){
          copyAssoc.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
        }
        copyAssoc.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(astcdAssociation.getLeft())).build());
        copyAssoc.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(astcdAssociation.getRight())).build());
        if (copyAssoc.getCDAssocType().isComposition()){
          copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
        }
        if ((pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight())){
          if (astcdAssociation.getCDAssocDir().isBidirectional() || getDirection(astcdAssociation).equals(AssocDirection.Unspecified)) {
            assert copyAssoc.getLeft().isPresentCDCardinality();
            assert copyAssoc.getRight().isPresentCDCardinality();
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left));
          }
          else {
            assert copyAssoc.getLeft().isPresentCDCardinality();
            assert copyAssoc.getRight().isPresentCDCardinality();
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left));
          }
        } if ((pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName()) && astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft())) {
          if (astcdAssociation.getCDAssocDir().isBidirectional() || getDirection(astcdAssociation).equals(AssocDirection.Unspecified)) {
            assert copyAssoc.getLeft().isPresentCDCardinality();
            assert copyAssoc.getRight().isPresentCDCardinality();
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right) );
          }
          else {
            assert copyAssoc.getLeft().isPresentCDCardinality();
            assert copyAssoc.getRight().isPresentCDCardinality();
            getTrgMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right));
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getSrcCD().getCDDefinition().getCDClassesList()){
      //Set<ASTCDType> superClasses = getAllSuper(astcdClass, (ICD4CodeArtifactScope) getSrcCD().getEnclosingScope());//falsch
      Set<ASTCDType> superClasses = CDDiffUtil.getAllSuperTypes(astcdClass, getSrcCD().getCDDefinition());
      superClasses.remove(astcdClass);
      for (ASTCDType superClass : superClasses){//getAllSuperTypes CDDffUtils
        if (superClass instanceof ASTCDClass){
          ASTCDClass superC = (ASTCDClass) superClass;
          for (ASTCDAssociation association : getCDAssociationsListForTypeSrc(superClass)){
            Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(association, getSrcCD());
            if (pair.a == null){
              continue;
            }
            if ((pair.a.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName())
              && association.getCDAssocDir().isDefinitiveNavigableRight())){
              ASTCDAssociation copyAssoc = association.deepClone();
              copyAssoc.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(association.getLeft())).build());
              copyAssoc.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(association.getRight())).build());
              copyAssoc.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getSymbol().getInternalQualifiedName())).build());
              copyAssoc.setName(" ");
              if (!copyAssoc.getLeft().isPresentCDCardinality()){
                copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (!copyAssoc.getRight().isPresentCDCardinality()){
                copyAssoc.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (copyAssoc.getCDAssocType().isComposition()){
                copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
              }
              if (association.getCDAssocDir().isBidirectional() || getDirection(association).equals(AssocDirection.Unspecified)) {
                getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left, true));
              }
              else {
                getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left, true));
              }
            }
            if ((pair.b.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              ASTCDAssociation copyAssoc = association.deepClone();
              copyAssoc.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(association.getLeft())).build());
              copyAssoc.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(association.getRight())).build());
              copyAssoc.getRight().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getSymbol().getInternalQualifiedName())).build());
              copyAssoc.setName(" ");
              if (!copyAssoc.getLeft().isPresentCDCardinality()){
                copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (!copyAssoc.getRight().isPresentCDCardinality()){
                copyAssoc.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (copyAssoc.getCDAssocType().isComposition()){
                copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
              }
              if (association.getCDAssocDir().isBidirectional() || getDirection(association).equals(AssocDirection.Unspecified)) {
                getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right, true));
              }
              else {
                getSrcMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right, true));
              }
            }
          }
        }
      }
    }

    for (ASTCDClass astcdClass : getTgtCD().getCDDefinition().getCDClassesList()){
      Set<ASTCDType> superClasses = CDDiffUtil.getAllSuperTypes(astcdClass, getSrcCD().getCDDefinition());
      superClasses.remove(astcdClass);
      for (ASTCDType superClass : superClasses){
        if (superClass instanceof ASTCDClass){
          ASTCDClass superC = (ASTCDClass) superClass;
          for (ASTCDAssociation association : getCDAssociationsListForTypeTgt(superClass)){
            Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(association, getTgtCD());
            if (pair.a == null){
              continue;
            }
            if ((pair.a.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableRight())){
              //change left side from superClass to subClass
              ASTCDAssociation assocForSubClass = association.deepClone();
              assocForSubClass.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(association.getLeft())).build());
              assocForSubClass.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(association.getRight())).build());
              assocForSubClass.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getSymbol().getInternalQualifiedName())).build());
              assocForSubClass.setName(" ");
              if (!assocForSubClass.getLeft().isPresentCDCardinality()){
                assocForSubClass.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (!assocForSubClass.getRight().isPresentCDCardinality()){
                association.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (assocForSubClass.getCDAssocType().isComposition()){
                assocForSubClass.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
              }
              if (association.getCDAssocDir().isBidirectional() || getDirection(association).equals(AssocDirection.Unspecified)) {
                getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.BiDirectional, ClassSide.Left, true));
              }
              else {
                getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.LeftToRight, ClassSide.Left, true));
              }
            }
            if ((pair.b.getSymbol().getInternalQualifiedName().equals(superC.getSymbol().getInternalQualifiedName()) && association.getCDAssocDir().isDefinitiveNavigableLeft())) {
              //change right side from superClass to subclass
              ASTCDAssociation assocForSubClass = association.deepClone();
              assocForSubClass.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(association.getLeft())).build());
              assocForSubClass.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(association.getRight())).build());
              assocForSubClass.getRight().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(astcdClass.getName())).build());
              assocForSubClass.setName(" ");
              if (!assocForSubClass.getLeft().isPresentCDCardinality()){
                assocForSubClass.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (!assocForSubClass.getRight().isPresentCDCardinality()){
                association.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (assocForSubClass.getCDAssocType().isComposition()){
                assocForSubClass.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
              }
              if (association.getCDAssocDir().isBidirectional() || getDirection(association).equals(AssocDirection.Unspecified)) {
                getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.BiDirectional, ClassSide.Right, true));
              }
              else {
                getTrgMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.RightToLeft, ClassSide.Right, true));
              }
            }
          }
        }
      }
    }
    for(ASTCDClass astcdClass : srcCD.getCDDefinition().getCDClassesList()){
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
      for (ASTCDAttribute attribute : attributes){
        for (ASTCDAttribute attribute1 : attributes){
          if (attribute != attribute1
            && attribute.getName().equals(attribute1.getName())
            && !attribute.getMCType().printType().equals(attribute1.getMCType().printType())){
            notInstanClassesSrc.add(astcdClass);
            break;
          }
          break;
        }
      }
    }
    for(ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()) {
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
      for (ASTCDAttribute attribute : attributes) {
        for (ASTCDAttribute attribute1 : attributes) {
          if (attribute != attribute1
            && attribute.getName().equals(attribute1.getName())
            && !attribute.getMCType().printType().equals(attribute1.getMCType().printType())) {
            notInstanClassesTgt.add(astcdClass);
            break;
          }
          break;
        }
      }
    }
    for (ASTCDClass astcdClass : srcCD.getCDDefinition().getCDClassesList()){
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
      for (ASTCDAttribute attribute : attributes){
        if (sameRoleNameAndClass(attribute.getName(), astcdClass)){
          notInstanClassesSrc.add(astcdClass);
          break;
        }
      }
    }
    for (ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()){
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
      for (ASTCDAttribute attribute : attributes){
        if (sameRoleNameAndClassTgt(attribute.getName(), astcdClass)){
          notInstanClassesTgt.add(astcdClass);
          break;
        }
      }
    }
    reduceMaps();
  }

  //CHECKED
  private boolean sameRoleNameAndClass(String roleName, ASTCDClass astcdClass){
    String roleName1 = roleName.substring(0, 1).toUpperCase() + roleName.substring(1);
    for (AssocStruct assocStruct : srcMap.get(astcdClass)){
      if (assocStruct.getSide().equals(ClassSide.Left)){
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(roleName)
          && getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getName().equals(roleName1)){
          return true;
        }
      }
      else {
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(roleName)
          && getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getName().equals(roleName1)){
          return true;
        }
      }
    }
    return false;
  }
  //CHECKED
  //when comparing class with role name, first character must be big - done
  private boolean sameRoleNameAndClassTgt(String roleName, ASTCDClass astcdClass){
    String roleName1 = roleName.substring(0, 1).toUpperCase() + roleName.substring(1);
    for (AssocStruct assocStruct : trgMap.get(astcdClass)){
      if (assocStruct.getSide().equals(ClassSide.Left)){
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(roleName)
          && getConnectedClasses(assocStruct.getAssociation(), tgtCD).b.getName().equals(roleName1)){
          return true;
        }
      }
      else {
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(roleName)
          && getConnectedClasses(assocStruct.getAssociation(), tgtCD).a.getName().equals(roleName1)){
          return true;
        }
      }
    }
    return false;
  }

  //TODO: doesn't work with classes inside packages - ask Max
  public static Pair<ASTCDClass, ASTCDClass> getConnectedClasses(ASTCDAssociation association, ASTCDCompilationUnit compilationUnit) {
    Optional<CDTypeSymbol> astcdClass =
      compilationUnit
        .getEnclosingScope()
        .resolveCDTypeDown(association.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> astcdClass1 =
      compilationUnit
        .getEnclosingScope()
        .resolveCDTypeDown(association.getRightQualifiedName().getQName());
    if (astcdClass.isPresent() && astcdClass1.isPresent()
      && astcdClass.get().getAstNode() instanceof ASTCDClass
      && astcdClass1.get().getAstNode() instanceof ASTCDClass) {
      return new Pair<>(
        (ASTCDClass) astcdClass.get().getAstNode(), (ASTCDClass) astcdClass1.get().getAstNode());
    }
    return new Pair<>(null, null);
  }

  //CHECKED
  /**
   * Compute the classes that extend a given class.
   *
   * @param compilationUnit diagram
   * @param astcdClass root class for spanned inheritance
   * @return list of extending classes. This function is similar to getClassHierarchy().
   */
  public static List<ASTCDClass> getSpannedInheritance(ASTCDCompilationUnit compilationUnit, ASTCDClass astcdClass){
    List<ASTCDClass> subclasses = new ArrayList<>();
    for (ASTCDClass childClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if (childClass != astcdClass && !childClass.getSymbol().getInternalQualifiedName().equals("ASub4Diff")
        && (CDInheritanceHelper.isSuperOf(astcdClass.getSymbol().getInternalQualifiedName(), childClass.getSymbol().getInternalQualifiedName(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope()))) {
        subclasses.add(childClass);
      }
    }
    subclasses.remove(astcdClass);
    return subclasses;
  }

  //CHECKED
  public static List<ASTCDClass> getSuperClasses(ASTCDCompilationUnit compilationUnit, ASTCDClass astcdClass){
    List<ASTCDClass> superClasses = new ArrayList<>();
    for (ASTCDType type : getAllSuper(astcdClass, (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())){
      if (type instanceof ASTCDClass){
        superClasses.add((ASTCDClass) type);
      }
    }
    return superClasses;
  }

  //CHECKED
  /**
   * Check if the first cardinality is contained in the second cardinality
   * @param cardinality1 first cardinality
   * @param cardinality2 second cardinality
   * @return true if first cardinality is contained in the second one
   */
  public static boolean isContainedIn(AssocCardinality cardinality1, AssocCardinality cardinality2){
    if (cardinality1.equals(AssocCardinality.One)
      || cardinality2.equals(AssocCardinality.Multiple)){
      return true;
    } else if (cardinality1.equals(AssocCardinality.Optional)){
      return !(cardinality2.equals(AssocCardinality.One) || cardinality2.equals(AssocCardinality.AtLeastOne));
    } else if (cardinality1.equals(AssocCardinality.AtLeastOne)){
      return cardinality2.equals(AssocCardinality.AtLeastOne);
    } else{
      return false;
    }
  }

  //CHECKED
  static AssocCardinality cardToEnum(ASTCDCardinality cardinality){
    if (cardinality.isOne()) {
      return AssocCardinality.One;
    } else if (cardinality.isOpt()) {
      return AssocCardinality.Optional;
    } else if (cardinality.isAtLeastOne()) {
      return AssocCardinality.AtLeastOne;
    } else {
      return AssocCardinality.Multiple;
    }
  }

  //CHECKED
  public ASTCDClass minSubClass(ASTCDClass baseClass) {

    List<ASTCDClass> subClasses = getSpannedInheritance(srcCD, baseClass);

    int lowestCount = Integer.MAX_VALUE;
    ASTCDClass subclassWithLowestCount = null;

    for (ASTCDClass subclass : subClasses) {
      if (!subclass.getModifier().isAbstract() && !notInstanClassesSrc.contains(subclass)) {
        int attributeCount = getAllAttr(baseClass).b.size();
        int associationCount = getAssociationCount(subclass);
        int otherAssocsCount = getOtherAssocFromSuper(subclass).size();
        int totalCount = attributeCount + associationCount + otherAssocsCount;

        if (totalCount < lowestCount) {
          lowestCount = totalCount;
          subclassWithLowestCount = subclass;
        }
      }
    }

    return subclassWithLowestCount;
  }

  //CHECKED
  private int getAssociationCount(ASTCDClass astcdClass) {
    int count = 0;
    for (AssocStruct assocStruct : srcMap.get(astcdClass)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if ((assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne()
          || assocStruct.getAssociation().getRight().getCDCardinality().isOne())
          && !getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getSymbol().getInternalQualifiedName().equals(getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getSymbol().getInternalQualifiedName())) {
          count++;
        }
      }
      else {
        if ((assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isOne())
          && !getConnectedClasses(assocStruct.getAssociation(), srcCD).b.getSymbol().getInternalQualifiedName().equals(getConnectedClasses(assocStruct.getAssociation(), srcCD).a.getSymbol().getInternalQualifiedName())){
          count++;
        }
      }
    }
    return count;
  }

  //CHECKED
  public List<String> getSuperClasses(ASTCDClass astcdClass){
    List<ASTCDClass> superClasses = getSuperClasses(srcCD, astcdClass);
    List<String> classes = new ArrayList<>();
    for (int i = superClasses.size() - 1; i >= 0; i--) {
      String className = superClasses.get(i).getSymbol().getInternalQualifiedName().replace(".", "_");
      classes.add(className);
    }
    return classes;
  }

  //CHECKED
  public List<ASTODAttribute> getAttributesOD(ASTCDClass astcdClass) {
    List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
    List<ASTODAttribute> odAttributes = new ArrayList<>();
    for (ASTCDAttribute attribute : attributes) {
      odAttributes.add(ODBuilder.buildAttr(attribute.getMCType().printType(), attribute.getName()));
    }
    return odAttributes;
  }

  //CHECKED
  public ASTCDClass getCDClass(ASTCDCompilationUnit compilationUnit, String className) {
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if (astcdClass.getSymbol().getInternalQualifiedName().equals(className)) {
        return astcdClass;
      }
    }
    return null;
  }

  //CHECKED
  public boolean matchDirectionInReverse(AssocStruct srcStruct, Pair<AssocStruct, ClassSide> tgtStruct){
    if (((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Right))
      || (srcStruct.getSide().equals(ClassSide.Right) && tgtStruct.b.equals(ClassSide.Left)))
      && srcStruct.getDirection() == tgtStruct.a.getDirection()){
      return true;
    } else if (((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Left))
      || (srcStruct.getSide().equals(ClassSide.Right) && tgtStruct.b.equals(ClassSide.Right)))
      && ((srcStruct.getDirection().equals(AssocDirection.BiDirectional)
      && tgtStruct.a.getDirection().equals(AssocDirection.BiDirectional))
      || (srcStruct.getDirection().equals(AssocDirection.LeftToRight) && tgtStruct.a.getDirection().equals(AssocDirection.RightToLeft))
      || (srcStruct.getDirection().equals(AssocDirection.RightToLeft) && tgtStruct.a.getDirection().equals(AssocDirection.LeftToRight)))){
      return true;
    }
    return false;
  }

  //CHECKED
  public static boolean matchDirection(AssocStruct srcStruct, Pair<AssocStruct, ClassSide> tgtStruct){
    if (((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Left))
      || (srcStruct.getSide().equals(ClassSide.Right) && tgtStruct.b.equals(ClassSide.Right)))
      && srcStruct.getDirection() == tgtStruct.a.getDirection()){
      return true;
    } else if (((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Right))
      || (srcStruct.getSide().equals(ClassSide.Right) && tgtStruct.b.equals(ClassSide.Left)))
      && ((srcStruct.getDirection().equals(AssocDirection.BiDirectional)
      && tgtStruct.a.getDirection().equals(AssocDirection.BiDirectional))
      || (srcStruct.getDirection().equals(AssocDirection.LeftToRight) && tgtStruct.a.getDirection().equals(AssocDirection.RightToLeft))
      || (srcStruct.getDirection().equals(AssocDirection.RightToLeft) && tgtStruct.a.getDirection().equals(AssocDirection.LeftToRight)))) {
      return true;
    }
    return false;
  }

  //CHECKED
  public boolean sameAssocStruct(AssocStruct srcStruct, AssocStruct tgtStruct){
    if (CDDiffUtil.inferRole(srcStruct.getAssociation().getLeft()).equals(CDDiffUtil.inferRole(tgtStruct.getAssociation().getLeft()))
      && CDDiffUtil.inferRole(srcStruct.getAssociation().getRight()).equals(CDDiffUtil.inferRole(tgtStruct.getAssociation().getRight()))
      && matchDirection(srcStruct, new Pair<>(tgtStruct, tgtStruct.getSide()))
      && matchRoleNames(srcStruct.getAssociation().getLeft(), tgtStruct.getAssociation().getLeft())
      && matchRoleNames(srcStruct.getAssociation().getRight(), tgtStruct.getAssociation().getRight())){
      return true;
    }
    return false;
  }

  //CHECKED
  public boolean sameAssocStructInReverse(AssocStruct struct, AssocStruct tgtStruct){
    if (CDDiffUtil.inferRole(struct.getAssociation().getLeft()).equals(CDDiffUtil.inferRole(tgtStruct.getAssociation().getRight()))
      && CDDiffUtil.inferRole(struct.getAssociation().getRight()).equals(CDDiffUtil.inferRole(tgtStruct.getAssociation().getLeft()))
      && matchDirectionInReverse(struct, new Pair<>(tgtStruct, tgtStruct.getSide()))
      && matchRoleNames(struct.getAssociation().getLeft(), tgtStruct.getAssociation().getRight())
      && matchRoleNames(struct.getAssociation().getRight(), tgtStruct.getAssociation().getLeft())){
      return true;
    }
    return false;
  }

  //CHECKED
  public int getClassSize(ASTCDClass astcdClass){
    int attributeCount = getAllAttr(astcdClass).b.size();
    int associationCount = getAssociationCount(astcdClass);
    int otherAssocsCount = getOtherAssocFromSuper(astcdClass).size();
    return attributeCount + associationCount + otherAssocsCount;
  }

  //TODO: doesn't work in Gradle - ask Max
  //FUNCTION IS NEEDED!!!!!!!
  public void reduceMaps(){
    for (ASTCDClass astcdClass : srcMap.keySet()){
      for (AssocStruct assocStruct : srcMap.get(astcdClass)){
        for (AssocStruct assocStruct1 : srcMap.get(astcdClass)){
          if (isLoopStruct(assocStruct1)
             && assocStruct != assocStruct1){
            if (assocStruct.getSide().equals(ClassSide.Left)
              && assocStruct1.getSide().equals(ClassSide.Left)
              && sameAssocStruct(assocStruct, assocStruct1)){
              assocStruct.setToBeProcessed(false);
            } else if (assocStruct.getSide().equals(ClassSide.Right)
              && assocStruct1.getSide().equals(ClassSide.Right)){
              assocStruct.setToBeProcessed(false);
            } else if (assocStruct.getSide().equals(ClassSide.Left)
              && assocStruct1.getSide().equals(ClassSide.Right)){
              assocStruct.setToBeProcessed(false);
            } else if (assocStruct.getSide().equals(ClassSide.Right)
              && assocStruct1.getSide().equals(ClassSide.Left)){
              assocStruct.setToBeProcessed(false);
            }
          }
        }
      }
    }
  }

  //CHECKED
  public boolean isLoopStruct(AssocStruct assocStruct){
    Pair<ASTCDClass, ASTCDClass> pair = getConnectedClasses(assocStruct.getAssociation(), srcCD);
    return pair.a.equals(pair.b);
  }

  //CHECKED
  public void deleteOtherSideSrc(ASTCDClass astcdClass) {
    List<Pair<ASTCDClass, List<AssocStruct>>> toDelete = new ArrayList<>();
    for (ASTCDClass toCheck : srcMap.keySet()) {
      if (toCheck != astcdClass) {
        List<AssocStruct> toDeleteStructs = new ArrayList<>();
        for (AssocStruct struct : srcMap.get(toCheck)) {
          if (struct.getSide().equals(ClassSide.Left)
            && getConnectedClasses(struct.getAssociation(), srcCD).b != null
            && getConnectedClasses(struct.getAssociation(), srcCD).b.equals(astcdClass)) {
            toDeleteStructs.add(struct);
          } else if (struct.getSide().equals(ClassSide.Right)
            && getConnectedClasses(struct.getAssociation(), srcCD).a != null
            && getConnectedClasses(struct.getAssociation(), srcCD).a.equals(astcdClass)) {
            toDeleteStructs.add(struct);
          }
        }
        toDelete.add(new Pair<>(toCheck, toDeleteStructs));
      }
    }
    for (Pair<ASTCDClass, List<AssocStruct>> pair : toDelete) {
      for (AssocStruct struct : pair.b) {
        srcMap.get(pair.a).remove(struct);
      }
    }
  }

  //CHECKED
  public void deleteOtherSideTgt(ASTCDClass astcdClass) {
    List<Pair<ASTCDClass, List<AssocStruct>>> toDelete = new ArrayList<>();
    for (ASTCDClass toCheck : trgMap.keySet()) {
      if (toCheck != astcdClass) {
        List<AssocStruct> toDeleteStructs = new ArrayList<>();
        for (AssocStruct struct : trgMap.get(toCheck)) {
          if (struct.getSide().equals(ClassSide.Left)
            && getConnectedClasses(struct.getAssociation(), tgtCD).b != null
            && getConnectedClasses(struct.getAssociation(), tgtCD).b.equals(astcdClass)) {
            toDeleteStructs.add(struct);
          } else if (struct.getSide().equals(ClassSide.Right)
            && getConnectedClasses(struct.getAssociation(), tgtCD).a != null
            && getConnectedClasses(struct.getAssociation(), tgtCD).a.equals(astcdClass)) {
            toDeleteStructs.add(struct);
          }
        }
        toDelete.add(new Pair<>(toCheck, toDeleteStructs));
      }
    }
    for (Pair<ASTCDClass, List<AssocStruct>> pair : toDelete) {
      for (AssocStruct struct : pair.b) {
        trgMap.get(pair.a).remove(struct);
      }
    }
  }

  //CHECKED
  public void deleteAssocOtherSideSrc(AssocStruct assocStruct){
    if (assocStruct.getDirection().equals(AssocDirection.BiDirectional)) {
      if (assocStruct.getSide().equals(ClassSide.Left)){
        for (AssocStruct struct : srcMap.get(getConnectedClasses(assocStruct.getAssociation(), srcCD).b)){
          if (struct.getSide().equals(ClassSide.Right)
            && getConnectedClasses(struct.getAssociation(), srcCD).a.equals(getConnectedClasses(assocStruct.getAssociation(), srcCD).a)
            && sameAssocStruct(assocStruct, struct)){
            srcMap.get(getConnectedClasses(assocStruct.getAssociation(), srcCD).b).remove(struct);
            break;
          }
        }
      } else {
        for (AssocStruct struct : srcMap.get(getConnectedClasses(assocStruct.getAssociation(), srcCD).a)){
          if (struct.getSide().equals(ClassSide.Left)
            && getConnectedClasses(struct.getAssociation(), srcCD).b.equals(getConnectedClasses(assocStruct.getAssociation(), srcCD).b)
            && sameAssocStruct(assocStruct, struct)){
            srcMap.get(getConnectedClasses(assocStruct.getAssociation(), srcCD).a).remove(struct);
            break;
          }
        }
      }
    }
  }

  //CHECKED
  public void deleteAssocOtherSideTgt(AssocStruct assocStruct){
    if (assocStruct.getDirection().equals(AssocDirection.BiDirectional)) {
      if (assocStruct.getSide().equals(ClassSide.Left)){
        for (AssocStruct struct : trgMap.get(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)){
          if (struct.getSide().equals(ClassSide.Right)
            && getConnectedClasses(struct.getAssociation(), tgtCD).a.equals(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)
            && sameAssocStruct(assocStruct, struct)){
            trgMap.get(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b).remove(struct);
            break;
          }
        }
      } else {
        for (AssocStruct struct : trgMap.get(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)){
          if (struct.getSide().equals(ClassSide.Left)
            && getConnectedClasses(struct.getAssociation(), tgtCD).b.equals(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)
            && sameAssocStruct(assocStruct, struct)){
            trgMap.get(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a).remove(struct);
            break;
          }
        }
      }
    }
  }

  //CHECKED
  public void makeSimpleSem(List<ASTODArtifact> list){
    for (ASTODArtifact artifact : list){
      for (ASTODElement element : artifact.getObjectDiagram().getODElementList()){
        if (element instanceof ASTODObject){
          String type = ((ASTODObject) element).getMCObjectType().printType();
          ((ASTODObject) element).getModifier().getStereotype().removeValues("instanceof");
          ((ASTODObject) element).setModifier(OD4ReportMill.modifierBuilder()
            .setStereotype(OD4ReportMill.stereotypeBuilder().addValues(OD4ReportMill.stereoValueBuilder()
              .setName("instanceof")
              .setContent(String.join(type))
              .setText(OD4ReportMill.stringLiteralBuilder()
                .setSource(String.join(type))
                .build()).build()).build()).build());
        }
      }
    }
  }

  //CHECKED
  public Pair<ASTCDCardinality, ASTCDCardinality> getCardinality(ASTCDAssociation association){
    ASTCDCardinality left;
    ASTCDCardinality right;
    if (!association.getLeft().isPresentCDCardinality()){
      left = new ASTCDCardMult();
    } else {
      left = association.getLeft().getCDCardinality();
    }

    if (!association.getRight().isPresentCDCardinality()){
      right = new ASTCDCardMult();
    } else {
      right = association.getRight().getCDCardinality();
    }
    return new Pair<>(left, right);
  }

  //CHECKED
  public Pair<AssocStruct, AssocStruct> getStructsForAssocDiff(ASTCDAssociation srcAssoc, ASTCDAssociation tgtAssoc){
    Pair<ASTCDClass, ASTCDClass> srcCLasses = getConnectedClasses(srcAssoc, srcCD);
    Pair<ASTCDClass, ASTCDClass> tgtCLasses = getConnectedClasses(tgtAssoc, tgtCD);
    AssocStruct srcStruct = null;
    AssocStruct tgtStruct = null;
    if (getAssocStrucForClass(srcCLasses.a, srcAssoc) != null){
      srcStruct = getAssocStrucForClass(srcCLasses.a, srcAssoc);
    } else if (getAssocStrucForClass(srcCLasses.b, srcAssoc) != null){
      srcStruct = getAssocStrucForClass(srcCLasses.b, srcAssoc);
    }

    if (getAssocStrucForClassTgt(tgtCLasses.a, tgtAssoc) != null){
      tgtStruct = getAssocStrucForClassTgt(tgtCLasses.a, tgtAssoc);
    } else if (getAssocStrucForClassTgt(tgtCLasses.b, tgtAssoc) != null){
      tgtStruct = getAssocStrucForClassTgt(tgtCLasses.b, tgtAssoc);
    }
    return new Pair<>(srcStruct, tgtStruct);
  }
}
