package de.monticore.cddiff.cdsyntax2semdiff;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.matchRoleNames;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isSuperOf;

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
import de.monticore.cddiff.syndiff.datastructures.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis.ODBasisMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.*;
import java.util.function.Function;

/**
 * This is a helper class that is accessible from all classes for semantic difference and generation of object diagrams. It contains
 * functions for comparing associations, attributes, classes, generating part of object diagrams. It further contains multiple maps that
 * reduce the complexity of the implementation. The function setMaps() that computes possible associations for each class is also
 * implemented in this class.
 */
public class Syn2SemDiffHelper {

  public Syn2SemDiffHelper() {
  }

  private ODBuilder ODBuilder = new ODBuilder();
  /**
   * Map with all possible associations (as AssocStructs) for classes from srcCD where the given
   * class serves as source. The non-instantiatable classes and associations are removed after the
   * function findOverlappingAssocs().
   */
  private ArrayListMultimap<ASTCDType, AssocStruct> srcMap;

  /**
   * Map with all possible associations (as AssocStructs) for classes from trgCd where the given
   * class serves as target. The non-instantiatable classes and associations are removed after the
   * function findOverlappingAssocs().
   */
  private ArrayListMultimap<ASTCDType, AssocStruct> tgtMap;

  /**
   * Map with all subclasses of a class from srcCD.
   * This is used to reduce the complexity for computing the underlying inheritance tree.
   */
  private ArrayListMultimap<ASTCDType, ASTCDClass> srcSubMap; //TODO: check usages

  /**
   * Map with all subclasses of a class from trgCD.
   * This is used to reduce the complexity for computing the underlying inheritance tree.
   */
  private ArrayListMultimap<ASTCDType, ASTCDClass> tgtSubMap;

  public ArrayListMultimap<ASTCDInterface, ASTCDInterface> getSrcInterfaceMap() {
    return srcInterfaceMap;
  }

  public void setSrcInterfaceMap(ArrayListMultimap<ASTCDInterface, ASTCDInterface> srcInterfaceMap) {
    this.srcInterfaceMap = srcInterfaceMap;
  }

  public ArrayListMultimap<ASTCDInterface, ASTCDInterface> getTgtInterfaceMap() {
    return tgtInterfaceMap;
  }

  public void setTgtInterfaceMap(ArrayListMultimap<ASTCDInterface, ASTCDInterface> tgtInterfaceMap) {
    this.tgtInterfaceMap = tgtInterfaceMap;
  }

  private ArrayListMultimap<ASTCDInterface, ASTCDInterface> srcInterfaceMap;

  private ArrayListMultimap<ASTCDInterface, ASTCDInterface> tgtInterfaceMap;

  /**
   * Set with all classes that are not instantiatable in srcCD.
   * Those are classes that cannot exist because of overlapping.
   * The second possibility is that the class has an attribute and a relation to the same class, e.g., int age and -> (age) Age.
   */
  private Set<ASTCDType> notInstClassesSrc;

  /**
   * Set with all classes that are not instantiatable in trgCD.
   * Those are classes that cannot exist because of overlapping.
   * The second possibility is that the class has an attribute and a relation to the same class, e.g., int age and -> (age) Age.
   */
  private Set<ASTCDType> notInstClassesTgt;

  /**
   * This is a copy of the srcCD so that it can be accessed from all classes for semantic difference.
   */
  private ASTCDCompilationUnit srcCD;

  /**
   * This is a copy of the trgCD so that it can be accessed from all classes for semantic difference.
   */
  private ASTCDCompilationUnit tgtCD;

  /**
   * Those are the matched classes from the analysis of the syntax.
   * This way some functionalities were moved to this helper class.
   */
  private List<Pair<ASTCDClass, ASTCDClass>> matchedClasses;

  /**
   * Those are the matched associations from the analysis of the syntax.
   * This way some functionalities were moved to this helper class.
   */
  private List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs;

  /**
   * Those are the matched interfaces from the analysis of the syntax.
   * This way some functionalities were moved to this helper class.
   */
  private List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces;

  /**
   * Those are the added associations from the analysis of the syntax.
   * This way some functionalities were moved to this helper class.
   */
  private List<ASTCDAssociation> addedAssocs;

  /**
   * Those are the deleted associations from the analysis of the syntax.
   * This way some functionalities were moved to this helper class.
   */
  private List<ASTCDAssociation> deletedAssocs;

  private final String subClassInterface = "ObjectSub4Diff"; //This is also used in getConnectedClasses(), but the function is static!

  private final String classForInter = "Object4Diff";

  private final ASTCDClass classForInterface = CD4CodeMill.cDClassBuilder()
    .setName(classForInter)
    .setModifier(CD4CodeMill.modifierBuilder().build())
    .build();

  private final ASTCDClass classForInterfaceTgt = CD4CodeMill.cDClassBuilder()
    .setName(classForInter)
    .setModifier(CD4CodeMill.modifierBuilder().build())
    .build();

  public ArrayListMultimap<ASTCDType, AssocStruct> getSrcMap() {
    return srcMap;
  }

  public ArrayListMultimap<ASTCDType, AssocStruct> getTgtMap() {
    return tgtMap;
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

  public Set<ASTCDType> getNotInstClassesSrc() {
    return notInstClassesSrc;
  }

  public Set<ASTCDType> getNotInstClassesTgt() {
    return notInstClassesTgt;
  }

  public ArrayListMultimap<ASTCDType, ASTCDClass> getSrcSubMap() {
    return srcSubMap;
  }

  public ArrayListMultimap<ASTCDType, ASTCDClass> getTgtSubMap() {
    return tgtSubMap;
  }

  public void setNotInstClassesSrc(Set<ASTCDType> notInstClassesSrc) {
    this.notInstClassesSrc = notInstClassesSrc;
  }

  public void setNotInstClassesTgt(Set<ASTCDType> notInstClassesTgt) {
    this.notInstClassesTgt = notInstClassesTgt;
  }

  public void updateSrc(ASTCDType astcdClass){
    notInstClassesSrc.add(astcdClass);
  }

  public void updateTgt(ASTCDType astcdClass) {
    notInstClassesTgt.add(astcdClass);
  }

  public void setMatchedAssocs(List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs) {
    this.matchedAssocs = matchedAssocs;
  }

  public void setDeletedAssocs(List<ASTCDAssociation> deletedAssocs) {
    this.deletedAssocs = deletedAssocs;
  }

  public void setAddedAssocs(List<ASTCDAssociation> addedAssocs) {
    this.addedAssocs = addedAssocs;
  }

  public boolean isSubclassWithSuper(ASTCDType superClass, ASTCDType subClass) {
    return isSuperOf(
        superClass.getSymbol().getInternalQualifiedName(),
        subClass.getSymbol().getInternalQualifiedName(),
        srcCD);
  }

  public boolean isSubClassWithSuperTgt(ASTCDType superClass, ASTCDType subClass) {
    return isSuperOf(
        superClass.getSymbol().getInternalQualifiedName(),
        subClass.getSymbol().getInternalQualifiedName(),
        tgtCD);
  }

  /**
   * Check if an association is a superassociation of another one. For this, the direction and the
   * role names must be matched in the target direction. The associated classes of the
   * superassociation must be superclasses of the associated classes of the subAssoc.
   *
   * @param superAssoc superassociation as AssocStruct
   * @param subAssoc subassociation as AssocStruct
   * @return true if condition is fulfilled
   */
  // CHECKED
  public boolean isSubAssociationSrcSrc(AssocStruct superAssoc, AssocStruct subAssoc) {
    if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Left)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
        && isSubclassWithSuper(
            getConnectedClasses(superAssoc.getAssociation(), srcCD).a,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).a)
        && isSubclassWithSuper(
            getConnectedClasses(superAssoc.getAssociation(), srcCD).b,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).b)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Right)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && isSubclassWithSuper(
            getConnectedClasses(superAssoc.getAssociation(), srcCD).a,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).b)
        && isSubclassWithSuper(
            getConnectedClasses(superAssoc.getAssociation(), srcCD).b,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
        && superAssoc.getSide().equals(ClassSide.Left)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && isSubclassWithSuper(
            getConnectedClasses(superAssoc.getAssociation(), srcCD).a,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).b)
        && isSubclassWithSuper(
            getConnectedClasses(superAssoc.getAssociation(), srcCD).b,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else return subAssoc.getSide().equals(ClassSide.Right)
            && superAssoc.getSide().equals(ClassSide.Right)
            && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
            && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
            && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
            && isSubclassWithSuper(
            getConnectedClasses(superAssoc.getAssociation(), srcCD).a,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).a)
            && isSubclassWithSuper(
            getConnectedClasses(superAssoc.getAssociation(), srcCD).b,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).b);
  }

  /**
   * Get all needed associations from the srcMap that use the given class as target. The
   * associations are strictly unidirectional. Needed associations - the cardinality must be at
   * least one.
   *
   * @param astcdClass target class
   * @return list of associations
   */
  // CHECKED
  public List<AssocStruct> getOtherAssocFromSuper(ASTCDType astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDType classToCheck : srcMap.keySet()) {
      if (classToCheck != astcdClass) {
        for (AssocStruct assocStruct : srcMap.get(classToCheck)) {
          if (assocStruct.getSide().equals(ClassSide.Left)
              && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
              && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
                  || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())
              && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).b
                  == astcdClass) {
            list.add(assocStruct.deepClone());
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
              && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
                  || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())
              && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), srcCD).a
                  == astcdClass) {
            list.add(assocStruct.deepClone());
          }
        }
      }
    }
    return list;
  }

  /**
   * Get all needed associations from the tgtMap that use the given class as target. The
   * associations are strictly unidirectional. Needed associations - the cardinality must be at
   * least one.
   *
   * @param astcdClass target class
   * @return list of associations
   */
  // CHECKED
  public List<AssocStruct> getOtherAssocsTgt(ASTCDType astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDType classToCheck : tgtMap.keySet()) {
      if (classToCheck != astcdClass) {
        for (AssocStruct assocStruct : tgtMap.get(classToCheck)) {
          if (assocStruct.getSide().equals(ClassSide.Left)
              && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
              && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
                  || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())
              && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), tgtCD).b
                  == astcdClass) {
            list.add(assocStruct.deepClone());
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
              && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
                  || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())
              && Syn2SemDiffHelper.getConnectedClasses(assocStruct.getAssociation(), tgtCD).a
                  == astcdClass) {
            list.add(assocStruct.deepClone());
          }
        }
      }
    }
    return list;
  }

  /**
   * Get all needed associations (including superclasses) from the tgtMap that use the given class
   * as target. The associations are strictly unidirectional. Needed associations - the cardinality
   * must be at least one. 'Subassociations' might be included.
   *
   * @param astcdClass target class
   * @return list of associations
   */
  // CHECKED
  public List<AssocStruct> getAllOtherAssocsTgt(ASTCDType astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDType astcdClass1 :
        CDDiffUtil.getAllSuperTypes(astcdClass, tgtCD.getCDDefinition())) {
      list.addAll(getOtherAssocsTgt(astcdClass1));
    }
    return list;
  }

  /**
   * Get all needed associations (including superclasses) from the tgtMap that use the given class
   * as target. The associations are strictly unidirectional. Needed associations - the cardinality
   * must be at least one. 'Subassociations' might be included.
   *
   * @param astcdClass target class
   * @return list of associations
   */
  // CHECKED
  public List<AssocStruct> getAllOtherAssocsSrc(ASTCDType astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDType astcdClass1 :
        CDDiffUtil.getAllSuperTypes(astcdClass, srcCD.getCDDefinition())) {
      list.addAll(getOtherAssocFromSuper(astcdClass1));
    }
    return list;
  }

  public Optional<ASTCDType> findMatchedTypeSrc(ASTCDType astcdType) {
    if (astcdType instanceof ASTCDClass) {
      Optional<ASTCDClass> result = findMatchedSrc((ASTCDClass) astcdType);
      return result.map(Function.identity());
    } else if (astcdType instanceof ASTCDInterface) {
      Optional<ASTCDInterface> result = findMatchedInterfaceSrc((ASTCDInterface) astcdType);
      return result.map(Function.identity());
    } else {
      return Optional.empty();
    }
  }

  public Optional<ASTCDType> findMatchedTypeTgt(ASTCDType astcdType) {
    if (astcdType instanceof ASTCDClass) {
      Optional<ASTCDClass> result = findMatchedClass((ASTCDClass) astcdType);
      return result.map(Function.identity());
    } else if (astcdType instanceof ASTCDInterface) {
      Optional<ASTCDInterface> result = findMatchedInterfaceTgt((ASTCDInterface) astcdType);
      return result.map(Function.identity());
    } else {
      return Optional.empty();
    }
  }

  // CHECKED
  public Optional<ASTCDClass> findMatchedClass(ASTCDClass astcdClass) {
    ASTCDClass matchedClass = null;
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses) {
      if (pair.a.equals(astcdClass)) {
        matchedClass = pair.b;
      }
    }
    return Optional.ofNullable(matchedClass);
  }

  // CHECKED
  public Optional<ASTCDClass> findMatchedSrc(ASTCDClass astcdClass) {
    for (Pair<ASTCDClass, ASTCDClass> pair : matchedClasses) {
      if (pair.b.equals(astcdClass)) {
        return Optional.ofNullable(pair.a);
      }
    }
    return Optional.empty();
  }

  public Optional<ASTCDInterface> findMatchedInterfaceSrc(ASTCDInterface astcdInterface){
    for (Pair<ASTCDInterface, ASTCDInterface> pair : matchedInterfaces) {
      if (pair.b.equals(astcdInterface)) {
        return Optional.ofNullable(pair.a);
      }
    }
    return Optional.empty();
  }

  public Optional<ASTCDInterface> findMatchedInterfaceTgt(ASTCDInterface astcdInterface){
    for (Pair<ASTCDInterface, ASTCDInterface> pair : matchedInterfaces) {
      if (pair.a.equals(astcdInterface)) {
        return Optional.ofNullable(pair.b);
      }
    }
    return Optional.empty();
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
    } else {
      return AssocDirection.LeftToRight;
    }
  }

  /**
   * When merging associations, the role names of the bidirectional association are used instead of
   * the role names of the unidirectional.
   *
   * @param association association
   * @param superAssoc superassociation
   */
  // CHECKED
  public static void setBiDirRoleName(AssocStruct association, AssocStruct superAssoc) {
    if (!association.getDirection().equals(AssocDirection.BiDirectional)
        && superAssoc.getDirection().equals(AssocDirection.BiDirectional)) {
      if (association.getSide().equals(ClassSide.Left)
          && superAssoc.getSide().equals(ClassSide.Left)) {
        association
            .getAssociation()
            .getLeft()
            .setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
      } else if (association.getSide().equals(ClassSide.Left)
          && superAssoc.getSide().equals(ClassSide.Right)) {
        association
            .getAssociation()
            .getLeft()
            .setCDRole(superAssoc.getAssociation().getRight().getCDRole());
      } else if (association.getSide().equals(ClassSide.Right)
          && superAssoc.getSide().equals(ClassSide.Left)) {
        association
            .getAssociation()
            .getRight()
            .setCDRole(superAssoc.getAssociation().getLeft().getCDRole());
      } else {
        association
            .getAssociation()
            .getRight()
            .setCDRole(superAssoc.getAssociation().getRight().getCDRole());
      }
    }
  }

  /**
   * Merge the cardinalities and the direction of two associations.
   *
   * @param association association
   * @param superAssoc superassociation
   */
  // CHECKED
  public static void mergeAssocs(AssocStruct association, AssocStruct superAssoc) {
    ASTCDAssocDir direction = mergeAssocDir(association, superAssoc);
    CardinalityStruc cardinalities = getCardinalities(association, superAssoc);
    AssocCardinality cardinalityLeft =
        Syn2SemDiffHelper.intersectCardinalities(
            Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().a),
            Syn2SemDiffHelper.cardToEnum(cardinalities.getLeftCardinalities().b));
    AssocCardinality cardinalityRight =
        Syn2SemDiffHelper.intersectCardinalities(
            Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().a),
            Syn2SemDiffHelper.cardToEnum(cardinalities.getRightCardinalities().b));
    association.getAssociation().setCDAssocDir(direction);
    association.setDirection(getDirection(association.getAssociation()));
    if (association.getSide().equals(ClassSide.Left)) {
      association
          .getAssociation()
          .getLeft()
          .setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
      association
          .getAssociation()
          .getRight()
          .setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
    } else {
      association
          .getAssociation()
          .getLeft()
          .setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityRight)));
      association
          .getAssociation()
          .getRight()
          .setCDCardinality(createCardinality(Objects.requireNonNull(cardinalityLeft)));
    }
  }

  // CHECKED
  /**
   * Modified version of the function inConflict in CDAssociationHelper. In the map, all association
   * that can be created from a class are saved in the values for this class (key). Because of that
   * we don't need to check if the source classes of both associations are in an inheritance
   * relation.
   *
   * @param association association from the class
   * @param superAssociation superAssociation for that class
   * @return true, if the role names in target direction are the same
   */
  public static boolean isInConflict(AssocStruct association, AssocStruct superAssociation) {
    ASTCDAssociation srcAssoc = association.getAssociation();
    ASTCDAssociation targetAssoc = superAssociation.getAssociation();

    if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight());
    }
    if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft());
    }
    if (association.getSide().equals(ClassSide.Right)
        && superAssociation.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight());
    }
    if (association.getSide().equals(ClassSide.Right)
        && superAssociation.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft());
    }

    return false;
  }

  /**
   * Check if the role names on the source side are the same.
   *
   * @param assocDown association
   * @param assocUp association
   * @return true if the role names are the same
   */
  // CHECKED
  public static boolean sameRoleNamesSrc(AssocStruct assocDown, AssocStruct assocUp) {
    if (assocDown.getSide().equals(ClassSide.Left) && assocUp.getSide().equals(ClassSide.Left)) {
      return CDDiffUtil.inferRole(assocDown.getAssociation().getLeft())
          .equals(CDDiffUtil.inferRole(assocUp.getAssociation().getLeft()));
    } else if (assocDown.getSide().equals(ClassSide.Left)
        && assocUp.getSide().equals(ClassSide.Right)) {
      return CDDiffUtil.inferRole(assocDown.getAssociation().getLeft())
          .equals(CDDiffUtil.inferRole(assocUp.getAssociation().getRight()));
    } else if (assocDown.getSide().equals(ClassSide.Right)
        && assocUp.getSide().equals(ClassSide.Left)) {
      return CDDiffUtil.inferRole(assocDown.getAssociation().getRight())
          .equals(CDDiffUtil.inferRole(assocUp.getAssociation().getLeft()));
    } else {
      return CDDiffUtil.inferRole(assocDown.getAssociation().getRight())
          .equals(CDDiffUtil.inferRole(assocUp.getAssociation().getRight()));
    }
  }

  /**
   * Given the two associations, get the role name that causes the conflict
   *
   * @param association base association
   * @param superAssociation association from superclass
   * @return role name
   */
  public static ASTCDRole getConflict(AssocStruct association, AssocStruct superAssociation) {
    ASTCDAssociation srcAssoc = association.getAssociation();
    ASTCDAssociation targetAssoc = superAssociation.getAssociation();

    if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Left)
        && matchRoleNames(srcAssoc.getRight(), targetAssoc.getRight())) {
      return srcAssoc.getRight().getCDRole();
    }
    if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Right)
        && matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft())) {
      return srcAssoc.getRight().getCDRole();
    }
    if (association.getSide().equals(ClassSide.Right)
        && superAssociation.getSide().equals(ClassSide.Left)
        && matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight())) {
      return srcAssoc.getLeft().getCDRole();
    }
    if (association.getSide().equals(ClassSide.Right)
        && superAssociation.getSide().equals(ClassSide.Right)
        && matchRoleNames(srcAssoc.getLeft(), targetAssoc.getLeft())) {
      return srcAssoc.getLeft().getCDRole();
    }

    return null;
  }

  // CHECKED
  /**
   * Merge the directions of two associations
   *
   * @param association association from the class
   * @param superAssociation association from the class or superAssociation
   * @return merged direction in ASTCDAssocDir
   */
  public static ASTCDAssocDir mergeAssocDir(AssocStruct association, AssocStruct superAssociation) {
    if (association.getDirection().equals(AssocDirection.BiDirectional)
        || superAssociation.getDirection().equals(AssocDirection.BiDirectional)) {
      return CD4CodeMill.cDBiDirBuilder().build();
    } else if (association.getDirection().equals(AssocDirection.LeftToRight)) {
      if (superAssociation.getDirection().equals(AssocDirection.LeftToRight)) {
        return CD4CodeMill.cDLeftToRightDirBuilder().build();
      }
      if (superAssociation.getDirection().equals(AssocDirection.RightToLeft)) {
        return CD4CodeMill.cDBiDirBuilder().build();
      }
    } else if (association.getDirection().equals(AssocDirection.RightToLeft)) {
      if (superAssociation.getDirection().equals(AssocDirection.RightToLeft)) {
        return CD4CodeMill.cDRightToLeftDirBuilder().build();
      }
      if (superAssociation.getDirection().equals(AssocDirection.LeftToRight)) {
        return CD4CodeMill.cDBiDirBuilder().build();
      }
    } else if (association.getDirection().equals(AssocDirection.Unspecified)
      && superAssociation.getDirection().equals(AssocDirection.Unspecified)) {
      return CD4CodeMill.cDUnspecifiedDirBuilder().build();
    } else if (association.getDirection().equals(AssocDirection.Unspecified)) {
      if (superAssociation.getDirection().equals(AssocDirection.LeftToRight)) {
        return CD4CodeMill.cDLeftToRightDirBuilder().build();
      }
      if (superAssociation.getDirection().equals(AssocDirection.RightToLeft)) {
        return CD4CodeMill.cDRightToLeftDirBuilder().build();
      }
    } else if (superAssociation.getDirection().equals(AssocDirection.Unspecified)) {
      if (association.getDirection().equals(AssocDirection.LeftToRight)) {
        return CD4CodeMill.cDLeftToRightDirBuilder().build();
      }
      if (association.getDirection().equals(AssocDirection.RightToLeft)) {
        return CD4CodeMill.cDRightToLeftDirBuilder().build();
      }
    }
    return null;
  }

  // CHECKED
  /**
   * Group corresponding cardinalities
   *
   * @param association association
   * @param superAssociation association
   * @return structure with two pairs of corresponding cardinalities
   */
  public static CardinalityStruc getCardinalities(
      AssocStruct association, AssocStruct superAssociation) {
    if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Left)) {
      return new CardinalityStruc(
          new Pair<>(
              association.getAssociation().getLeft().getCDCardinality(),
              superAssociation.getAssociation().getLeft().getCDCardinality()),
          new Pair<>(
              association.getAssociation().getRight().getCDCardinality(),
              superAssociation.getAssociation().getRight().getCDCardinality()));
    } else if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Right)) {
      return new CardinalityStruc(
          new Pair<>(
              association.getAssociation().getLeft().getCDCardinality(),
              superAssociation.getAssociation().getRight().getCDCardinality()),
          new Pair<>(
              association.getAssociation().getRight().getCDCardinality(),
              superAssociation.getAssociation().getLeft().getCDCardinality()));
    } else if (association.getSide().equals(ClassSide.Right)
        && superAssociation.getSide().equals(ClassSide.Left)) {
      return new CardinalityStruc(
          new Pair<>(
              association.getAssociation().getRight().getCDCardinality(),
              superAssociation.getAssociation().getLeft().getCDCardinality()),
          new Pair<>(
              association.getAssociation().getLeft().getCDCardinality(),
              superAssociation.getAssociation().getRight().getCDCardinality()));
    } else {
      return new CardinalityStruc(
          new Pair<>(
              association.getAssociation().getRight().getCDCardinality(),
              superAssociation.getAssociation().getRight().getCDCardinality()),
          new Pair<>(
              association.getAssociation().getLeft().getCDCardinality(),
              superAssociation.getAssociation().getLeft().getCDCardinality()));
    }
  }

  // CHECKED
  /**
   * Transform the internal cardinality to original
   *
   * @param assocCardinality cardinality to transform
   * @return cardinality with type ASTCDCardinality
   */
  public static ASTCDCardinality createCardinality(AssocCardinality assocCardinality) {
    if (assocCardinality.equals(AssocCardinality.One)) {
      return new ASTCDCardOne();
    } else if (assocCardinality.equals(AssocCardinality.Optional)) {
      return new ASTCDCardOpt();
    } else if (assocCardinality.equals(AssocCardinality.AtLeastOne)) {
      return new ASTCDCardAtLeastOne();
    } else {
      return new ASTCDCardMult();
    }
  }

  // CHECKED
  /**
   * Check if the associations allow 0 objects from target class
   *
   * @param association association
   * @param superAssociation association
   * @return true if the condition is fulfilled
   */
  public static boolean areZeroAssocs(AssocStruct association, AssocStruct superAssociation) {
    if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Left)) {
      return (association.getAssociation().getRight().getCDCardinality().isMult()
              || association.getAssociation().getRight().getCDCardinality().isOpt())
          && (superAssociation.getAssociation().getRight().getCDCardinality().isMult()
              || superAssociation.getAssociation().getRight().getCDCardinality().isOpt());
    } else if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Right)) {
      return (association.getAssociation().getRight().getCDCardinality().isMult()
              || association.getAssociation().getRight().getCDCardinality().isOpt())
          && (superAssociation.getAssociation().getLeft().getCDCardinality().isMult()
              || superAssociation.getAssociation().getLeft().getCDCardinality().isOpt());
    } else if (association.getSide().equals(ClassSide.Right)
        && superAssociation.getSide().equals(ClassSide.Left)) {
      return (association.getAssociation().getLeft().getCDCardinality().isMult()
              || association.getAssociation().getLeft().getCDCardinality().isOpt())
          && (superAssociation.getAssociation().getRight().getCDCardinality().isMult()
              || superAssociation.getAssociation().getRight().getCDCardinality().isOpt());
    } else {
      return (association.getAssociation().getLeft().getCDCardinality().isMult()
              || association.getAssociation().getLeft().getCDCardinality().isOpt())
          && (superAssociation.getAssociation().getLeft().getCDCardinality().isMult()
              || superAssociation.getAssociation().getLeft().getCDCardinality().isOpt());
    }
  }

  public boolean isAdded(
      AssocStruct assocStruct,
      AssocStruct assocStruct2,
      ASTCDType astcdClass,
      Set<DeleteStruct> set) {
    for (DeleteStruct deleteStruct : set) {
      if (((deleteStruct.getAssociation().equals(assocStruct)
                  && deleteStruct.getSuperAssoc().equals(assocStruct2))
              || ((deleteStruct.getAssociation().equals(assocStruct2)
                  && deleteStruct.getSuperAssoc().equals(assocStruct))))
          && deleteStruct.getAstcdClass().equals(astcdClass)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if all matched subclasses in srcCD of a class from trgCD have the same association or a
   * subassociation.
   *
   * @param association association from trgCD.
   * @param astcdClass class from trgCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  // CHECKED
  public ASTCDType allSubclassesHaveIt(AssocStruct association, ASTCDType astcdClass) {
    List<ASTCDClass> subClassesTgt = tgtSubMap.get(astcdClass);
    List<ASTCDType> subclassesSrc = getSrcTypes(subClassesTgt);
    for (ASTCDType subClass : subclassesSrc) {
      boolean isContained = false;
      for (AssocStruct assocStruct : srcMap.get(subClass)) {
        if (sameAssociationTypeTgtSrc(assocStruct, association)) {
          isContained = true;
          break;
        }
      }
      if (!isContained) {
        return subClass;
      }
    }
    return null;
  }

  /**
   * Check if all matched subclasses in trgCD of a class from srcCD have the same association or a
   * subassociation.
   *
   * @param association association from srcCD.
   * @param astcdClass class from srcCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  // CHECKED
  public ASTCDType allSubClassesHaveItTgt(AssocStruct association, ASTCDType astcdClass) {
    List<ASTCDClass> subClassesSrc = srcSubMap.get(astcdClass);
    List<ASTCDType> subClassesTgt = getTgtTypes(subClassesSrc);
    for (ASTCDType subClass : subClassesTgt) {
      boolean isContained = false;
      for (AssocStruct assocStruct : tgtMap.get(subClass)){
        if (sameAssociationTypeSrcTgt(assocStruct, association)){
          isContained = true;
          break;
        }
      }
      if (!isContained) {
        return subClass;
      }
    }
    return null;
  }

  /**
   * Similar to the function above, but the now the classes must be target of the association.
   *
   * @param matchedAssocStruc association from srcCD.
   * @param astcdClass class from srcCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  // CHECKED
  public ASTCDType allSubClassesAreTgtSrcTgt(
      AssocStruct matchedAssocStruc, ASTCDType astcdClass) {
    List<ASTCDClass> subClasses = srcSubMap.get(astcdClass);
    List<ASTCDType> subClassesTgt = getTgtTypes(subClasses);
    for (ASTCDType subClass : subClassesTgt) {
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

  public ASTCDType allSubClassesAreTgtTgtSrc(
      AssocStruct matchedAssocStruct, ASTCDType astcdClass) {
    List<ASTCDClass> subClasses = tgtSubMap.get(astcdClass);
    List<ASTCDType> subClassesSrc = getSrcTypes(subClasses);
    for (ASTCDType subClass : subClassesSrc) {
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
   * Check if the second association from trgCD is a subassociation of the first association from
   * srcCD.
   *
   * @param superAssoc association from srcCD.
   * @param subAssoc association from trgCD.
   * @return true if the condition is fulfilled.
   */
  public boolean isSubAssociationTgtSrc(AssocStruct superAssoc, AssocStruct subAssoc) {
    if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Left)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
        && compareTgtSrc(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).a,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).a)
        && compareTgtSrc(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).b,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).b)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Right)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && compareTgtSrc(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).a,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).b)
        && compareTgtSrc(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).b,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
        && superAssoc.getSide().equals(ClassSide.Left)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && compareTgtSrc(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).a,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).b)
        && compareTgtSrc(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).b,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else return subAssoc.getSide().equals(ClassSide.Right)
            && superAssoc.getSide().equals(ClassSide.Right)
            && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
            && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
            && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
            && compareTgtSrc(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).a,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).a)
            && compareTgtSrc(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).b,
            getConnectedClasses(subAssoc.getAssociation(), srcCD).b);
  }

  /**
   * Check if the second association from trgCD is a subassociation of the first association from
   * trgCD.
   *
   * @param superAssoc association from srcCD.
   * @param subAssoc association from trgCD.
   * @return true if the condition is fulfilled.
   */
  public boolean isSubAssociationTgtTgt(AssocStruct superAssoc, AssocStruct subAssoc) {
    if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Left)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
        && (isSubClassWithSuperTgt(
                getConnectedClasses(superAssoc.getAssociation(), tgtCD).a,
                getConnectedClasses(subAssoc.getAssociation(), tgtCD).a)
            && isSubClassWithSuperTgt(
                getConnectedClasses(superAssoc.getAssociation(), tgtCD).b,
                getConnectedClasses(subAssoc.getAssociation(), tgtCD).b))) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Right)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && isSubClassWithSuperTgt(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).a,
            getConnectedClasses(subAssoc.getAssociation(), tgtCD).b)
        && isSubClassWithSuperTgt(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).b,
            getConnectedClasses(subAssoc.getAssociation(), tgtCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
        && superAssoc.getSide().equals(ClassSide.Left)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && isSubClassWithSuperTgt(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).a,
            getConnectedClasses(subAssoc.getAssociation(), tgtCD).b)
        && isSubClassWithSuperTgt(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).b,
            getConnectedClasses(subAssoc.getAssociation(), tgtCD).a)) {
      return true;
    } else return subAssoc.getSide().equals(ClassSide.Right)
            && superAssoc.getSide().equals(ClassSide.Right)
            && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
            && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
            && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
            && isSubClassWithSuperTgt(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).a,
            getConnectedClasses(subAssoc.getAssociation(), tgtCD).a)
            && isSubClassWithSuperTgt(
            getConnectedClasses(superAssoc.getAssociation(), tgtCD).b,
            getConnectedClasses(subAssoc.getAssociation(), tgtCD).b);
  }

  /**
   * Check if the classes are in an inheritance relation. For this, the matched classes in trgCD of
   * the srcClass are compared with isSuper() to the tgtClass.
   *
   * @param srcClass class from srcCD.
   * @param tgtClass class from trgCD.
   * @return true if the condition is fulfilled.
   */
  public boolean compareSrcTgt(ASTCDType srcClass, ASTCDType tgtClass) {
    Optional<ASTCDType> typeToMatch = findMatchedTypeTgt(tgtClass);
    if (typeToMatch.isPresent()) {
      return isSuperOf(
          srcClass.getSymbol().getInternalQualifiedName(),
          typeToMatch.get().getSymbol().getInternalQualifiedName(),
          (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    }
    List<ASTCDClass> subClasses = srcSubMap.get(srcClass);
    List<ASTCDType> subClassesTgt = getTgtTypes(subClasses);
    for (ASTCDType subClass : subClassesTgt) {
      if (isSuperOf(
          tgtClass.getSymbol().getInternalQualifiedName(),
          subClass.getSymbol().getInternalQualifiedName(),
          (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if the classes are in an inheritance relation. For this, the matched classes in srcCD of
   * the tgtClass are compared with isSuper() to the srcClass.
   *
   * @param tgtClass class from trgCD.
   * @param srcClass class from srcCD.
   * @return true if the condition is fulfilled.
   */
  public boolean compareTgtSrc(ASTCDType tgtClass, ASTCDType srcClass) {
    Optional<ASTCDType> typeToMatch = findMatchedTypeSrc(srcClass);
    if (typeToMatch.isPresent()) {
      return isSuperOf(
          tgtClass.getSymbol().getInternalQualifiedName(),
          typeToMatch.get().getSymbol().getInternalQualifiedName(),
          (ICD4CodeArtifactScope) tgtCD.getEnclosingScope());
    }
    List<ASTCDClass> subClasses = tgtSubMap.get(tgtClass);
    List<ASTCDType> subClassesSrc = getSrcTypes(subClasses);
    for (ASTCDType subClass : subClassesSrc) {
      if (isSuperOf(
          srcClass.getSymbol().getInternalQualifiedName(),
          subClass.getSymbol().getInternalQualifiedName(),
          (ICD4CodeArtifactScope) srcCD.getEnclosingScope())) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public boolean classHasAssociationSrcSrc(AssocStruct association, ASTCDType astcdClass) {
    for (AssocStruct assocStruct : srcMap.get(astcdClass)) {
      if (sameAssociationType(association, assocStruct)) {
        return true;
      }
    }
    return false;
  }

  public boolean classHasAssociationTgtTgt(AssocStruct association, ASTCDType astcdClass){
    for (AssocStruct assocStruct : tgtMap.get(astcdClass)){
      if (sameAssociationTypeTgtTgt(assocStruct, association)){
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public boolean classHasAssociationTgtSrc(AssocStruct assocStruct, ASTCDType astcdClass) {
    for (AssocStruct assocStruct1 : srcMap.get(astcdClass)) {
      if (isSubAssociationTgtSrc(assocStruct, assocStruct1)) {
        return true;
      }
    }
    return false;
  }


  //CHECKED
  public boolean classHasAssociationSrcTgt(AssocStruct assocStruct, ASTCDType astcdClass){
    for (AssocStruct assocStruct1 : tgtMap.get(astcdClass)){
      if (sameAssociationTypeSrcTgt(assocStruct1, assocStruct)){
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public boolean classIsTarget(AssocStruct association, ASTCDType astcdClass) {
    for (AssocStruct assocStruct : getAllOtherAssocsSrc(astcdClass)) {
      if (sameAssociationType(association, assocStruct)) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public boolean classIsTgtTgtTgt(AssocStruct association, ASTCDClass astcdClass) {
    for (AssocStruct assocStruct : getAllOtherAssocsTgt(astcdClass)) {
      if (sameAssociationTypeTgtTgt(assocStruct, association)) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public boolean classIsTgtSrcTgt(AssocStruct association, ASTCDType astcdClass) {
    for (AssocStruct assocStruct : getAllOtherAssocsTgt(astcdClass)) {
      if (sameAssociationTypeSrcTgt(assocStruct, association)) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public boolean classIsTargetTgtSrc(AssocStruct association, ASTCDType astcdClass) {
    for (AssocStruct assocStruct : getAllOtherAssocsSrc(astcdClass)) {
      if (sameAssociationTypeTgtSrc(assocStruct, association)) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public List<ASTCDType> getSrcTypes(List<? extends ASTCDType> classes) {
    List<ASTCDType> srcTypes = new ArrayList<>();
    if (!classes.isEmpty() && classes.get(0) instanceof ASTCDClass) {
      for (ASTCDType astcdType : classes) {
        Optional<ASTCDClass> matched = findMatchedSrc((ASTCDClass) astcdType);
        matched.ifPresent(srcTypes::add);
      }
    } else {
      for (ASTCDType astcdType : classes) {
        Optional<ASTCDInterface> matched = findMatchedInterfaceSrc((ASTCDInterface) astcdType);
        matched.ifPresent(srcTypes::add);
      }
    }
    return srcTypes;
  }

  // CHECKED
  public List<ASTCDType> getTgtTypes(List<? extends ASTCDType> classes) {
    List<ASTCDType> tgtTypes = new ArrayList<>();
    if (!classes.isEmpty() && classes.get(0) instanceof ASTCDClass) {
      for (ASTCDType astcdType : classes) {
        Optional<ASTCDClass> matched = findMatchedClass((ASTCDClass) astcdType);
        matched.ifPresent(tgtTypes::add);
      }
    } else {
      for (ASTCDType astcdType : classes) {
        Optional<ASTCDInterface> matched = findMatchedInterfaceTgt((ASTCDInterface) astcdType);
        matched.ifPresent(tgtTypes::add);
      }
    }
    return tgtTypes;
  }

  //CHECKED
  public AssocStruct getAssocStrucForClassTgt(ASTCDClass astcdClass, ASTCDAssociation association){
    for (AssocStruct assocStruct : tgtMap.get(astcdClass)){
      if (sameAssociation(assocStruct.getAssociation(), association)){
        return assocStruct;
      }
    }
    return null;
  }

  // CHECKED
  /**
   * Get the AssocStruc that has the same type
   *
   * @param astcdClass class to search in
   * @param association association to match with
   * @return matched association, if found
   */
  public AssocStruct getAssocStrucForClass(ASTCDType astcdClass, ASTCDAssociation association) {
    for (AssocStruct assocStruct : srcMap.get(astcdClass)) {
      if (sameAssociation(assocStruct.getAssociation(), association)) {
        return assocStruct;
      }
    }
    return null;
  }

  /**
   * Check if two associations are exactly the same.
   *
   * @param association association.
   * @param association2 association.
   * @return true if the condition is fulfilled.
   */
  // CHECKED
  public boolean sameAssociation(ASTCDAssociation association, ASTCDAssociation association2) {
    Pair<ASTCDCardinality, ASTCDCardinality> cardinalities = getCardinality(association2);
    if (association
            .getLeftQualifiedName()
            .getQName()
            .equals(association2.getLeftQualifiedName().getQName())
        && association
            .getRightQualifiedName()
            .getQName()
            .equals(association2.getRightQualifiedName().getQName())) {
      return matchRoleNames(association.getLeft(), association2.getLeft())
          && matchRoleNames(association.getRight(), association2.getRight())
          && Syn2SemDiffHelper.getDirection(association)
              .equals(Syn2SemDiffHelper.getDirection(association2))
          && cardToEnum(association.getLeft().getCDCardinality())
              .equals(cardToEnum(cardinalities.a))
          && cardToEnum(association.getRight().getCDCardinality())
              .equals(cardToEnum(cardinalities.b));
    }
    return false;
  }

  // CHECKED
  public boolean srcAssocExistsTgtNot(ASTCDAssociation association, ASTCDAssociation association2) {
    boolean exists1 = false;
    Pair<ASTCDType, ASTCDType> pair = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
    AssocStruct assocStructLeft = getAssocStructByUnmod(pair.a, association);
    AssocStruct assocStructRight = getAssocStructByUnmod(pair.b, association);
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
    Pair<ASTCDType, ASTCDType> pair2 = Syn2SemDiffHelper.getConnectedClasses(association2, tgtCD);
    AssocStruct assocStructLeftTgt = getAssocStructByUnmodTgt(pair2.a, association2);
    AssocStruct assocStructRightTgt = getAssocStructByUnmodTgt(pair2.b, association2);
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

  // CHECKED
  public boolean srcNotTgtExists(ASTCDAssociation association, ASTCDAssociation association2) {
    boolean exists1 = false;
    Pair<ASTCDType, ASTCDType> pair = Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
    AssocStruct assocStructLeft = getAssocStructByUnmod(pair.a, association);
    AssocStruct assocStructRight = getAssocStructByUnmod(pair.b, association);
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
    Pair<ASTCDType, ASTCDType> pair2 = Syn2SemDiffHelper.getConnectedClasses(association2, tgtCD);
    AssocStruct assocStructLeftTgt = getAssocStructByUnmodTgt(pair2.a, association2);
    AssocStruct assocStructRightTgt = getAssocStructByUnmod(pair2.b, association2);
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

  // CHECKED
  /**
   * Check if the target classes of the two associations are in an inheritance relation
   *
   * @param association base association
   * @param superAssociation association from superclass
   * @return true, if they fulfill the condition
   */
  public boolean inInheritanceRelation(AssocStruct association, AssocStruct superAssociation, ASTCDCompilationUnit compilationUnit) {
    if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Left)) {
      return isSuperOf(
              superAssociation.getAssociation().getRightQualifiedName().getQName(),
              association.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
      // do I also need to check the other way around
    } else if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Right)) {
      return isSuperOf(
              superAssociation.getAssociation().getLeftQualifiedName().getQName(),
              association.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
    } else if (association.getSide().equals(ClassSide.Right)
        && superAssociation.getSide().equals(ClassSide.Left)) {
      return isSuperOf(
              superAssociation.getAssociation().getRightQualifiedName().getQName(),
              association.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
    } else {
      return isSuperOf(
              superAssociation.getAssociation().getLeftQualifiedName().getQName(),
              association.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
    }
  }

  public boolean inheritanceTgt(AssocStruct assocStruct, AssocStruct assocStruct1){
    if (assocStruct.getSide().equals(ClassSide.Left)
      && assocStruct1.getSide().equals(ClassSide.Left)){
      return compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b, getConnectedClasses(assocStruct.getAssociation(), srcCD).b);
    } else if (assocStruct.getSide().equals(ClassSide.Left)
      && assocStruct1.getSide().equals(ClassSide.Right)){
      return compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a, getConnectedClasses(assocStruct.getAssociation(), srcCD).b);
    } else if (assocStruct.getSide().equals(ClassSide.Right)
      && assocStruct1.getSide().equals(ClassSide.Left)) {
      return compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b, getConnectedClasses(assocStruct.getAssociation(), srcCD).a);
    } else {
      return compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a, getConnectedClasses(assocStruct.getAssociation(), srcCD).a);
    }
  }

  public boolean inheritanceSrc(AssocStruct assocStruct, AssocStruct assocStruct1){
    if (assocStruct.getSide().equals(ClassSide.Left)
      && assocStruct1.getSide().equals(ClassSide.Left)){
      return compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a, getConnectedClasses(assocStruct.getAssociation(), srcCD).a);
    } else if (assocStruct.getSide().equals(ClassSide.Left)
      && assocStruct1.getSide().equals(ClassSide.Right)){
      return compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b, getConnectedClasses(assocStruct.getAssociation(), srcCD).a);
    } else if (assocStruct.getSide().equals(ClassSide.Right)
      && assocStruct1.getSide().equals(ClassSide.Left)) {
      return compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a, getConnectedClasses(assocStruct.getAssociation(), srcCD).b);
    } else {
      return compareTgtSrc(getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b, getConnectedClasses(assocStruct.getAssociation(), srcCD).b);
    }
  }

  // CHECKED
  /**
   * Get all attributes that need to be added from inheritance structure to an object of a given
   * type
   *
   * @param astcdClass class
   * @return Pair of the class and a list of attributes
   */
  public Pair<ASTCDClass, List<ASTCDAttribute>> getAllAttr(ASTCDClass astcdClass) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    Set<ASTCDType> classes =
        getAllSuper(astcdClass, (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    for (ASTCDType classToCheck : classes) {
      if (classToCheck instanceof ASTCDClass) {
        attributes.addAll(classToCheck.getCDAttributeList());
      }
    }
    return new Pair<>(astcdClass, attributes);
  }

  public Pair<ASTCDClass, List<ASTCDAttribute>> getAllAttrTgt(ASTCDClass astcdClass) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    Set<ASTCDType> classes =
      getAllSuper(astcdClass, (ICD4CodeArtifactScope) tgtCD.getEnclosingScope());
    for (ASTCDType classToCheck : classes) {
      if (classToCheck instanceof ASTCDClass) {
        attributes.addAll(classToCheck.getCDAttributeList());
      }
    }
    return new Pair<>(astcdClass, attributes);
  }

  // CHECKED
  public static boolean isAttContainedInClass(ASTCDAttribute attribute, ASTCDClass astcdClass) {
    int indexAttribute = attribute.getMCType().printType().lastIndexOf(".");
    for (ASTCDAttribute att : astcdClass.getCDAttributeList()) {
      int indexCurrent = att.getMCType().printType().lastIndexOf(".");
      if (indexCurrent == -1
        && indexAttribute == -1
        && (att.getName().equals(attribute.getName())
          && att.getMCType().printType().equals(attribute.getMCType().printType()))) {
        return true;
      } else if (indexCurrent == -1
        && indexAttribute != -1
        && (att.getName().equals(attribute.getName())
          && att.getMCType().printType().equals(attribute.getMCType().printType().substring(indexAttribute + 1)))) {
        return true;
      } else if (indexCurrent != -1
        && indexAttribute == -1
        && (att.getName().equals(attribute.getName())
          && att.getMCType().printType().substring(indexCurrent + 1).equals(attribute.getMCType().printType()))) {
        return true;
      } else if (indexCurrent != -1
        && indexAttribute != -1
        && (att.getName().equals(attribute.getName())
          && att.getMCType().printType().substring(indexCurrent + 1).equals(attribute.getMCType().printType().substring(indexAttribute + 1)))) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public boolean sameAssociationType(AssocStruct assocStruct, AssocStruct assocStruct1) {
    if ((assocStruct1.getSide().equals(ClassSide.Left)
        && assocStruct.getSide().equals(ClassSide.Left))) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getRightQualifiedName().getQName(),
              assocStruct.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
              assocStruct.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if ((assocStruct1.getSide().equals(ClassSide.Left)
        && assocStruct.getSide().equals(ClassSide.Right))) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getRightQualifiedName().getQName(),
              assocStruct.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
              assocStruct.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right)
        && assocStruct.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
              assocStruct.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getRightQualifiedName().getQName(),
              assocStruct.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right)
        && assocStruct.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
              assocStruct.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getRightQualifiedName().getQName(),
              assocStruct.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    }
    return false;
  }

  // CHECKED
  public boolean sameAssociationTypeSrcTgt(AssocStruct assocStruct1, AssocStruct assocStruct) {
    if ((assocStruct1.getSide().equals(ClassSide.Left)
        && assocStruct.getSide().equals(ClassSide.Left))) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && compareSrcTgt(
              getConnectedClasses(assocStruct1.getAssociation(), srcCD).a,
              getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)
          && compareSrcTgt(
              getConnectedClasses(assocStruct1.getAssociation(), srcCD).b,
              getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if ((assocStruct1.getSide().equals(ClassSide.Left)
        && assocStruct.getSide().equals(ClassSide.Right))) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && compareSrcTgt(
              getConnectedClasses(assocStruct1.getAssociation(), srcCD).a,
              getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)
          && compareSrcTgt(
              getConnectedClasses(assocStruct1.getAssociation(), srcCD).b,
              getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right)
        && assocStruct.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && compareSrcTgt(
              getConnectedClasses(assocStruct1.getAssociation(), srcCD).a,
              getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)
          && compareSrcTgt(
              getConnectedClasses(assocStruct1.getAssociation(), srcCD).b,
              getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right)
        && assocStruct.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && compareSrcTgt(
              getConnectedClasses(assocStruct1.getAssociation(), srcCD).a,
              getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)
          && compareSrcTgt(
              getConnectedClasses(assocStruct1.getAssociation(), srcCD).b,
              getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    }
    return false;
  }

  // CHECKED
  public boolean sameAssociationTypeTgtSrc(AssocStruct assocStruct1, AssocStruct assocStruct) {
    if ((assocStruct1.getSide().equals(ClassSide.Left)
        && assocStruct.getSide().equals(ClassSide.Left))) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && compareTgtSrc(
              getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a,
              getConnectedClasses(assocStruct.getAssociation(), srcCD).a)
          && compareTgtSrc(
              getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b,
              getConnectedClasses(assocStruct.getAssociation(), srcCD).b)
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if ((assocStruct1.getSide().equals(ClassSide.Left)
        && assocStruct.getSide().equals(ClassSide.Right))) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && compareTgtSrc(
              getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a,
              getConnectedClasses(assocStruct.getAssociation(), srcCD).b)
          && compareTgtSrc(
              getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b,
              getConnectedClasses(assocStruct.getAssociation(), srcCD).a)
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right)
        && assocStruct.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && compareTgtSrc(
              getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a,
              getConnectedClasses(assocStruct.getAssociation(), srcCD).a)
          && compareTgtSrc(
              getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b,
              getConnectedClasses(assocStruct.getAssociation(), srcCD).b)
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right)
        && assocStruct.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && compareTgtSrc(
              getConnectedClasses(assocStruct1.getAssociation(), tgtCD).a,
              getConnectedClasses(assocStruct.getAssociation(), srcCD).b)
          && compareTgtSrc(
              getConnectedClasses(assocStruct1.getAssociation(), tgtCD).b,
              getConnectedClasses(assocStruct.getAssociation(), srcCD).a)
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    }
    return false;
  }

  // CHECKED
  public boolean sameAssociationTypeTgtTgt(AssocStruct assocStruct1, AssocStruct assocStruct) {
    if ((assocStruct1.getSide().equals(ClassSide.Left)
        && assocStruct.getSide().equals(ClassSide.Left))) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getRightQualifiedName().getQName(),
              assocStruct.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
              assocStruct.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if ((assocStruct1.getSide().equals(ClassSide.Left)
        && assocStruct.getSide().equals(ClassSide.Right))) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getRightQualifiedName().getQName(),
              assocStruct.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
              assocStruct.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right)
        && assocStruct.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getLeft())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getRight())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
              assocStruct.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getRightQualifiedName().getQName(),
              assocStruct.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()));
    } else if (assocStruct1.getSide().equals(ClassSide.Right)
        && assocStruct.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(
              assocStruct1.getAssociation().getLeft(), assocStruct.getAssociation().getRight())
          && matchRoleNames(
              assocStruct1.getAssociation().getRight(), assocStruct.getAssociation().getLeft())
          && matchDirection(assocStruct1, new Pair<>(assocStruct, assocStruct.getSide()))
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getLeftQualifiedName().getQName(),
              assocStruct.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              assocStruct1.getAssociation().getRightQualifiedName().getQName(),
              assocStruct.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) tgtCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(assocStruct1.getAssociation().getRight().getCDCardinality()),
              cardToEnum(assocStruct.getAssociation().getLeft().getCDCardinality()));
    }
    return false;
  }

  // CHECKED
  /**
   * Given the following two cardinalities, find their intersection
   *
   * @param cardinalityA first cardinality
   * @param cardinalityB second cardinality
   * @return intersection of the cardinalities
   */
  public static AssocCardinality intersectCardinalities(
      AssocCardinality cardinalityA, AssocCardinality cardinalityB) {
    if (cardinalityA == null) {
      return cardinalityB;
    }
    if (cardinalityA.equals(AssocCardinality.One)) {
      return AssocCardinality.One;
    } else if (cardinalityA.equals(AssocCardinality.Optional)) {
      if (cardinalityB.equals(AssocCardinality.One)) {
        return AssocCardinality.One;
      } else if (cardinalityB.equals(AssocCardinality.Multiple)
          || cardinalityB.equals(AssocCardinality.Optional)) {
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
      if (cardinalityB.equals(AssocCardinality.One)
          || cardinalityB.equals(AssocCardinality.Optional)) {
        return AssocCardinality.AtLeastOne;
      } else if (cardinalityB.equals(AssocCardinality.Multiple)
          || cardinalityB.equals(AssocCardinality.AtLeastOne)) {
        return AssocCardinality.AtLeastOne;
      }
    }
    assert false;
    return null;
  }

  /**
   * This is the same function from CDDefinition, but it compares the classes based on the
   * qualified name of the class.
   * @param type class from srcCD to get related associations.
   * @return list of associations.
   */
  public List<ASTCDAssociation> getCDAssociationsListForTypeSrc(ASTCDType type) {
    List<ASTCDAssociation> result = new ArrayList<>();
    for (ASTCDAssociation association : srcCD.getCDDefinition().getCDAssociationsList()) {
      if (association
              .getLeftQualifiedName()
              .getQName()
              .equals(type.getSymbol().getInternalQualifiedName())
          && association.getCDAssocDir().isDefinitiveNavigableRight()) {
        result.add(association);
      } else if (association
              .getRightQualifiedName()
              .getQName()
              .equals(type.getSymbol().getInternalQualifiedName())
          && association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        result.add(association);
      }
    }
    return result;
  }

  /**
   * This is the same function from CDDefinition, but it compares the classes based on the
   * qualified name of the class.
   * @param type class from tgtCD to get related associations.
   * @return list of associations.
   */
  public List<ASTCDAssociation> getCDAssociationsListForTypeTgt(ASTCDType type) {
    List<ASTCDAssociation> result = new ArrayList<>();
    for (ASTCDAssociation association : tgtCD.getCDDefinition().getCDAssociationsList()) {

      if (association
              .getLeftQualifiedName()
              .getQName()
              .equals(type.getSymbol().getInternalQualifiedName())
          && association.getCDAssocDir().isDefinitiveNavigableRight()) {
        result.add(association);
      } else if (association
              .getRightQualifiedName()
              .getQName()
              .equals(type.getSymbol().getInternalQualifiedName())
          && association.getCDAssocDir().isDefinitiveNavigableLeft()) {
        result.add(association);
      }
    }
    return result;
  }

  //CHECKED
  /**
   * Compute what associations can be used from a class (associations that were from the class and
   * superAssociations). For each class and each possible association we save the direction and also
   * on which side the class is. Two maps are created - srcMap (for srcCD) and trgMap (for trgCD).
   */
  public void setMaps(){//TODO: associations must also be added from interfaces and interfaces must be added tto those two maps
    srcMap = ArrayListMultimap.create();
    tgtMap = ArrayListMultimap.create();
    List<ASTCDType> srcTypes = new ArrayList<>();
    srcTypes.addAll(srcCD.getCDDefinition().getCDClassesList());
    srcTypes.addAll(srcCD.getCDDefinition().getCDInterfacesList());
    for (ASTCDType astcdClass : srcTypes){
      for (ASTCDAssociation astcdAssociation : getCDAssociationsListForTypeSrc(astcdClass)){
        Pair<ASTCDType, ASTCDType> pair = getConnectedClasses(astcdAssociation, getSrcCD());
        if (pair.a == null) {
          continue;
        }
        ASTCDAssociation copyAssoc = astcdAssociation.deepClone();
        copyAssoc.setName(" ");
        if (!copyAssoc.getLeft().isPresentCDCardinality()) {
          copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
        }
        if (!copyAssoc.getRight().isPresentCDCardinality()) {
          copyAssoc.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
        }
        if (copyAssoc.getCDAssocType().isComposition()) {
          copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
        }
        copyAssoc
            .getLeft()
            .setCDRole(
                CD4CodeMill.cDRoleBuilder()
                    .setName(CDDiffUtil.inferRole(astcdAssociation.getLeft()))
                    .build());
        copyAssoc
            .getRight()
            .setCDRole(
                CD4CodeMill.cDRoleBuilder()
                    .setName(CDDiffUtil.inferRole(astcdAssociation.getRight()))
                    .build());
        if ((pair.a
                .getSymbol()
                .getInternalQualifiedName()
                .equals(astcdClass.getSymbol().getInternalQualifiedName()))) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            getSrcMap()
                .put(
                    astcdClass,
                    new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left, astcdClass, pair.b));
          } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight()) {
            getSrcMap()
                .put(
                    astcdClass,
                    new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left, astcdClass, pair.b));
          }
        }
        if ((pair.b
                .getSymbol()
                .getInternalQualifiedName()
                .equals(astcdClass.getSymbol().getInternalQualifiedName()))) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            getSrcMap()
                .put(
                    astcdClass,
                    new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right, astcdClass, pair.a));
          } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()) {
            getSrcMap()
                .put(
                    astcdClass,
                    new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right, astcdClass, pair.a));
          }
        }
      }
    }

    List<ASTCDType> tgtTypes = new ArrayList<>();
    tgtTypes.addAll(tgtCD.getCDDefinition().getCDClassesList());
    tgtTypes.addAll(tgtCD.getCDDefinition().getCDInterfacesList());
    for (ASTCDType astcdClass : tgtTypes) {
      for (ASTCDAssociation astcdAssociation : getCDAssociationsListForTypeTgt(astcdClass)) {
        Pair<ASTCDType, ASTCDType> pair = getConnectedClasses(astcdAssociation, getTgtCD());
        if (pair.a == null) {
          continue;
        }
        ASTCDAssociation copyAssoc = astcdAssociation.deepClone();
        copyAssoc.setName(" ");
        if (!copyAssoc.getLeft().isPresentCDCardinality()) {
          copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
        }
        if (!copyAssoc.getRight().isPresentCDCardinality()) {
          copyAssoc.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
        }
        copyAssoc
            .getLeft()
            .setCDRole(
                CD4CodeMill.cDRoleBuilder()
                    .setName(CDDiffUtil.inferRole(astcdAssociation.getLeft()))
                    .build());
        copyAssoc
            .getRight()
            .setCDRole(
                CD4CodeMill.cDRoleBuilder()
                    .setName(CDDiffUtil.inferRole(astcdAssociation.getRight()))
                    .build());
        if (copyAssoc.getCDAssocType().isComposition()) {
          copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
        }
        if ((pair.a
                .getSymbol()
                .getInternalQualifiedName()
                .equals(astcdClass.getSymbol().getInternalQualifiedName()))) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            getTgtMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left, astcdClass, pair.b));
          }
          else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight()) {
            getTgtMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left, astcdClass, pair.b));
          }
        }
        if ((pair.b
                .getSymbol()
                .getInternalQualifiedName()
                .equals(astcdClass.getSymbol().getInternalQualifiedName()))) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            getTgtMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Right, astcdClass, pair.a));
          }
          else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()) {
            getTgtMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.RightToLeft, ClassSide.Right, astcdClass, pair.a));
          }
        }
      }
    }

    for (ASTCDType astcdClass : srcTypes) {
        Set<ASTCDType> superTypes =
          CDDiffUtil.getAllSuperTypes(astcdClass, getSrcCD().getCDDefinition());
      superTypes.remove(astcdClass);
      for (ASTCDType superClass : superTypes) { // getAllSuperTypes CDDffUtils
          ASTCDType superC = superClass;
          for (ASTCDAssociation association : getCDAssociationsListForTypeSrc(superClass)) {
            Pair<ASTCDType, ASTCDType> pair = getConnectedClasses(association, getSrcCD());
            if (pair.a == null) {
              continue;
            }
            if ((pair.a
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(superC.getSymbol().getInternalQualifiedName()))) {
              ASTCDAssociation copyAssoc = association.deepClone();
              copyAssoc
                  .getLeft()
                  .setCDRole(
                      CD4CodeMill.cDRoleBuilder()
                          .setName(CDDiffUtil.inferRole(association.getLeft()))
                          .build());
              copyAssoc
                  .getRight()
                  .setCDRole(
                      CD4CodeMill.cDRoleBuilder()
                          .setName(CDDiffUtil.inferRole(association.getRight()))
                          .build());
              copyAssoc
                  .getLeft()
                  .setMCQualifiedType(
                      CD4CodeMill.mCQualifiedTypeBuilder()
                          .setMCQualifiedName(
                              MCQualifiedNameFacade.createQualifiedName(
                                  astcdClass.getSymbol().getInternalQualifiedName()))
                          .build());
              copyAssoc.setName(" ");
              if (!copyAssoc.getLeft().isPresentCDCardinality()) {
                copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (!copyAssoc.getRight().isPresentCDCardinality()) {
                copyAssoc.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (copyAssoc.getCDAssocType().isComposition()) {
                copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
              }
              if (association.getCDAssocDir().isBidirectional()) {
                getSrcMap()
                    .put(
                        astcdClass,
                        new AssocStruct(
                            copyAssoc, AssocDirection.BiDirectional, ClassSide.Left, true, superC, pair.b));
              } else if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
                getSrcMap()
                    .put(
                        astcdClass,
                        new AssocStruct(
                            copyAssoc, AssocDirection.LeftToRight, ClassSide.Left, true, superC, pair.b));
              }
            }
            if ((pair.b
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(superC.getSymbol().getInternalQualifiedName()))) {
              ASTCDAssociation copyAssoc = association.deepClone();
              copyAssoc
                  .getLeft()
                  .setCDRole(
                      CD4CodeMill.cDRoleBuilder()
                          .setName(CDDiffUtil.inferRole(association.getLeft()))
                          .build());
              copyAssoc
                  .getRight()
                  .setCDRole(
                      CD4CodeMill.cDRoleBuilder()
                          .setName(CDDiffUtil.inferRole(association.getRight()))
                          .build());
              copyAssoc
                  .getRight()
                  .setMCQualifiedType(
                      CD4CodeMill.mCQualifiedTypeBuilder()
                          .setMCQualifiedName(
                              MCQualifiedNameFacade.createQualifiedName(
                                  astcdClass.getSymbol().getInternalQualifiedName()))
                          .build());
              copyAssoc.setName(" ");
              if (!copyAssoc.getLeft().isPresentCDCardinality()) {
                copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (!copyAssoc.getRight().isPresentCDCardinality()) {
                copyAssoc.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (copyAssoc.getCDAssocType().isComposition()) {
                copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
              }
              if (association.getCDAssocDir().isBidirectional()) {
                getSrcMap()
                    .put(
                        astcdClass,
                        new AssocStruct(
                            copyAssoc, AssocDirection.BiDirectional, ClassSide.Right, true, superC, pair.a));
              } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
                getSrcMap()
                    .put(
                        astcdClass,
                        new AssocStruct(
                            copyAssoc, AssocDirection.RightToLeft, ClassSide.Right, true, superC, pair.a));
              }
            }
          }
        }
      }

    for (ASTCDType astcdClass : tgtTypes) {
      Set<ASTCDType> superClasses =
          CDDiffUtil.getAllSuperTypes(astcdClass, tgtCD.getCDDefinition());
      superClasses.remove(astcdClass);
      for (ASTCDType superClass : superClasses) {
        ASTCDType superC = superClass;
          for (ASTCDAssociation association : getCDAssociationsListForTypeTgt(superClass)) {
            Pair<ASTCDType, ASTCDType> pair = getConnectedClasses(association, getTgtCD());
            if (pair.a == null) {
              continue;
            }
            if ((pair.a
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(superC.getSymbol().getInternalQualifiedName()))) {
              // change left side from superClass to subClass
              ASTCDAssociation copyAssoc = association.deepClone();
              copyAssoc
                  .getLeft()
                  .setCDRole(
                      CD4CodeMill.cDRoleBuilder()
                          .setName(CDDiffUtil.inferRole(association.getLeft()))
                          .build());
              copyAssoc
                  .getRight()
                  .setCDRole(
                      CD4CodeMill.cDRoleBuilder()
                          .setName(CDDiffUtil.inferRole(association.getRight()))
                          .build());
              copyAssoc
                  .getLeft()
                  .setMCQualifiedType(
                      CD4CodeMill.mCQualifiedTypeBuilder()
                          .setMCQualifiedName(
                              MCQualifiedNameFacade.createQualifiedName(
                                  astcdClass.getSymbol().getInternalQualifiedName()))
                          .build());
              copyAssoc.setName(" ");
              if (!copyAssoc.getLeft().isPresentCDCardinality()) {
                copyAssoc
                    .getLeft()
                    .setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (!copyAssoc.getLeft().isPresentCDCardinality()){
                copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (!copyAssoc.getRight().isPresentCDCardinality()) {
                copyAssoc.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (copyAssoc.getCDAssocType().isComposition()) {
                copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
              }
              if (association.getCDAssocDir().isBidirectional()) {
                getTgtMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, ClassSide.Left, true, superC, pair.b));
              }
              else if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
                getTgtMap().put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.LeftToRight, ClassSide.Left, true, superC, pair.b));
              }
            }
            if ((pair.b
                    .getSymbol()
                    .getInternalQualifiedName()
                    .equals(superC.getSymbol().getInternalQualifiedName()))) {
              // change right side from superClass to subclass
              ASTCDAssociation assocForSubClass = association.deepClone();
              assocForSubClass
                  .getLeft()
                  .setCDRole(
                      CD4CodeMill.cDRoleBuilder()
                          .setName(CDDiffUtil.inferRole(association.getLeft()))
                          .build());
              assocForSubClass
                  .getRight()
                  .setCDRole(
                      CD4CodeMill.cDRoleBuilder()
                          .setName(CDDiffUtil.inferRole(association.getRight()))
                          .build());
              assocForSubClass
                  .getRight()
                  .setMCQualifiedType(
                      CD4CodeMill.mCQualifiedTypeBuilder()
                          .setMCQualifiedName(
                              MCQualifiedNameFacade.createQualifiedName(astcdClass.getName()))
                          .build());
              assocForSubClass.setName(" ");
              if (!assocForSubClass.getLeft().isPresentCDCardinality()) {
                assocForSubClass
                    .getLeft()
                    .setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (!assocForSubClass.getRight().isPresentCDCardinality()) {
                assocForSubClass.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
              }
              if (assocForSubClass.getCDAssocType().isComposition()) {
                assocForSubClass.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
              }
              if (association.getCDAssocDir().isBidirectional()) {
                getTgtMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.BiDirectional, ClassSide.Right, true, superC, pair.a));
              }
              else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
                getTgtMap().put(astcdClass, new AssocStruct(assocForSubClass, AssocDirection.RightToLeft, ClassSide.Right, true, superC, pair.a));
              }
            }
          }
        }
      }
    for (ASTCDClass astcdClass : srcCD.getCDDefinition().getCDClassesList()) {
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
      for (ASTCDAttribute attribute : attributes) {
        for (ASTCDAttribute attribute1 : attributes) {
          if (attribute != attribute1
              && attribute.getName().equals(attribute1.getName())
              && !attribute.getMCType().printType().equals(attribute1.getMCType().printType())) {
            notInstClassesSrc.add(astcdClass);
            break;
          }
          break;
        }
      }
    }
    for (ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()) {
      List<ASTCDAttribute> attributes = getAllAttrTgt(astcdClass).b;
      for (ASTCDAttribute attribute : attributes) {
        for (ASTCDAttribute attribute1 : attributes) {
          if (attribute != attribute1
              && attribute.getName().equals(attribute1.getName())
              && !attribute.getMCType().printType().equals(attribute1.getMCType().printType())) {
            notInstClassesTgt.add(astcdClass);
            break;
          }
          break;
        }
      }
    }
    for (ASTCDClass astcdClass : srcCD.getCDDefinition().getCDClassesList()) {
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
      for (ASTCDAttribute attribute : attributes) {
        if (sameRoleNameAndClass(attribute.getName(), astcdClass)) {
          notInstClassesSrc.add(astcdClass);
          break;
        }
      }
    }
    for (ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()) {
      List<ASTCDAttribute> attributes = getAllAttrTgt(astcdClass).b;
      for (ASTCDAttribute attribute : attributes) {
        if (sameRoleNameAndClassTgt(attribute.getName(), astcdClass)) {
          notInstClassesTgt.add(astcdClass);
          break;
        }
      }
    }
  }

  /**
   * Compute the subclasses for each class in the diagrams.
   */
  public void setSubMaps(){
    srcSubMap = ArrayListMultimap.create();
    tgtSubMap = ArrayListMultimap.create();
    for (ASTCDClass astcdClass : srcCD.getCDDefinition().getCDClassesList()){
      for (ASTCDClass subClass : getSpannedInheritance(srcCD, astcdClass)){
        srcSubMap.put(astcdClass, subClass);
      }
    }

    for (ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()){
      for (ASTCDClass subClass : getSpannedInheritance(tgtCD, astcdClass)){
        tgtSubMap.put(astcdClass, subClass);
      }
    }

    List<ASTCDInterface> interfaces = srcCD.getCDDefinition().getCDInterfacesList();
    for (ASTCDInterface astcdInterface : interfaces){
      for (ASTCDClass astcdClass : srcCD.getCDDefinition().getCDClassesList()){
        if (CDInheritanceHelper.isSuperOf(astcdInterface.getSymbol().getInternalQualifiedName(),
          astcdClass.getSymbol().getInternalQualifiedName(),
          srcCD)){
          srcSubMap.put(astcdInterface, astcdClass);
        }
      }
    }
    List<ASTCDInterface> interfacesTgt = tgtCD.getCDDefinition().getCDInterfacesList();
    for (ASTCDInterface astcdInterface : interfacesTgt){
      for (ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()){
        if (CDInheritanceHelper.isSuperOf(astcdInterface.getSymbol().getInternalQualifiedName(),
          astcdClass.getSymbol().getInternalQualifiedName(),
          tgtCD)){
          tgtSubMap.put(astcdInterface, astcdClass);
        }
      }
    }
  }

  public void setInterfaceMap() {
    srcInterfaceMap = ArrayListMultimap.create();
    tgtInterfaceMap = ArrayListMultimap.create();

    for (ASTCDInterface astcdInterface : srcCD.getCDDefinition().getCDInterfacesList()){
      for (ASTCDInterface sub : srcCD.getCDDefinition().getCDInterfacesList()){
        if (astcdInterface != sub && CDInheritanceHelper.isSuperOf(astcdInterface.getSymbol().getInternalQualifiedName(),
          sub.getSymbol().getInternalQualifiedName(),
          srcCD)){
          srcInterfaceMap.put(astcdInterface, sub);
        }
      }
    }

    for (ASTCDInterface astcdInterface : tgtCD.getCDDefinition().getCDInterfacesList()){
      for (ASTCDInterface sub : tgtCD.getCDDefinition().getCDInterfacesList()){
        if (astcdInterface != sub && CDInheritanceHelper.isSuperOf(astcdInterface.getSymbol().getInternalQualifiedName(),
          sub.getSymbol().getInternalQualifiedName(),
          tgtCD)){
          tgtInterfaceMap.put(astcdInterface, sub);
        }
      }
    }
  }

  // CHECKED
  private boolean sameRoleNameAndClass(String roleName, ASTCDClass astcdClass) {
    String roleName1 = roleName.substring(0, 1).toUpperCase() + roleName.substring(1);
    for (AssocStruct assocStruct : srcMap.get(astcdClass)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(roleName)
            && getConnectedClasses(assocStruct.getAssociation(), srcCD)
                .b
                .getName()
                .equals(roleName1)) {
          return true;
        }
      } else {
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(roleName)
            && getConnectedClasses(assocStruct.getAssociation(), srcCD)
                .a
                .getName()
                .equals(roleName1)) {
          return true;
        }
      }
    }
    return false;
  }
  // CHECKED
  private boolean sameRoleNameAndClassTgt(String roleName, ASTCDClass astcdClass) {
    String roleName1 = roleName.substring(0, 1).toUpperCase() + roleName.substring(1);
    for (AssocStruct assocStruct : tgtMap.get(astcdClass)){
      if (assocStruct.getSide().equals(ClassSide.Left)){
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(roleName)
            && getConnectedClasses(assocStruct.getAssociation(), tgtCD)
                .b
                .getName()
                .equals(roleName1)) {
          return true;
        }
      } else {
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(roleName)
            && getConnectedClasses(assocStruct.getAssociation(), tgtCD)
                .a
                .getName()
                .equals(roleName1)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Get the classes that are connected with the association.
   * The function returns null if the associated objects aren't classes.
   * @param association association.
   * @param compilationUnit diagram.
   * @return pair of classes that are connected with the association.
   */
  public static Pair<ASTCDType, ASTCDType> getConnectedClasses(
      ASTCDAssociation association, ASTCDCompilationUnit compilationUnit) {
    Optional<CDTypeSymbol> astcdClass =
        compilationUnit
            .getEnclosingScope()
            .resolveCDTypeDown(association.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> astcdClass1 =
        compilationUnit
            .getEnclosingScope()
            .resolveCDTypeDown(association.getRightQualifiedName().getQName());
    if (astcdClass.isPresent()
        && astcdClass1.isPresent()) {
      return new Pair<>(
              astcdClass.get().getAstNode(), astcdClass1.get().getAstNode());
    }
    return new Pair<>(null, null);
  }

  // CHECKED
  /**
   * Compute the classes that extend a given class.
   *
   * @param compilationUnit diagram
   * @param astcdClass root class for spanned inheritance
   * @return list of extending classes. This function is similar to getClassHierarchy().
   */
  public static List<ASTCDClass> getSpannedInheritance(
      ASTCDCompilationUnit compilationUnit, ASTCDClass astcdClass) {
    List<ASTCDClass> subclasses = new ArrayList<>();
    for (ASTCDClass childClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if (childClass != astcdClass
          && (CDInheritanceHelper.isSuperOf(
              astcdClass.getSymbol().getInternalQualifiedName(),
              childClass.getSymbol().getInternalQualifiedName(),
              (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope()))) {
        subclasses.add(childClass);
      }
    }
    subclasses.remove(astcdClass);
    return subclasses;
  }

  // CHECKED
  public static List<ASTCDClass> getSuperTypes(
      ASTCDCompilationUnit compilationUnit, ASTCDClass astcdClass) {
    List<ASTCDClass> superClasses = new ArrayList<>();
    for (ASTCDType type :
        getAllSuper(astcdClass, (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope())) {
      if (type instanceof ASTCDClass) {
        superClasses.add((ASTCDClass) type);
      }
    }
    return superClasses;
  }

  // CHECKED
  /**
   * Check if the first cardinality is contained in the second cardinality
   *
   * @param cardinality1 first cardinality
   * @param cardinality2 second cardinality
   * @return true if first cardinality is contained in the second one
   */
  public static boolean isContainedIn(
      AssocCardinality cardinality1, AssocCardinality cardinality2) {
    if (cardinality1.equals(AssocCardinality.One)
        || cardinality2.equals(AssocCardinality.Multiple)) {
      return true;
    } else if (cardinality1.equals(AssocCardinality.Optional)) {
      return !(cardinality2.equals(AssocCardinality.One)
          || cardinality2.equals(AssocCardinality.AtLeastOne));
    } else if (cardinality1.equals(AssocCardinality.AtLeastOne)) {
      return cardinality2.equals(AssocCardinality.AtLeastOne);
    } else {
      return false;
    }
  }

  // CHECKED
  public static AssocCardinality cardToEnum(ASTCDCardinality cardinality) {
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

  // CHECKED
  public ASTCDClass minSubClass(ASTCDType baseClass) {

    List<ASTCDClass> subClasses = srcSubMap.get(baseClass);

    int lowestCount = Integer.MAX_VALUE;
    ASTCDClass subclassWithLowestCount = null;

    for (ASTCDClass subclass : subClasses) {
      if (!subclass.getModifier().isAbstract() && !notInstClassesSrc.contains(subclass)) {
        int attributeCount = getAllAttr(subclass).b.size();
        int associationCount = getAssociationCount(subclass);
        int otherAssocsCount = getAllOtherAssocsSrc(subclass).size();
        int totalCount = attributeCount + associationCount + otherAssocsCount;

        if (totalCount < lowestCount) {
          lowestCount = totalCount;
          subclassWithLowestCount = subclass;
        }
      }
    }

    return subclassWithLowestCount;
  }

  public ASTCDClass minSubClassTgt(ASTCDType baseClass){
    List<ASTCDClass> subClasses = tgtSubMap.get(baseClass);

    int lowestCount = Integer.MAX_VALUE;
    ASTCDClass subclassWithLowestCount = null;

    for (ASTCDClass subclass : subClasses) {
      if (!subclass.getModifier().isAbstract() && !notInstClassesTgt.contains(subclass)) {
        int attributeCount = getAllAttrTgt(subclass).b.size();
        int associationCount = getAssociationCountTgt(subclass);
        int otherAssocsCount = getAllOtherAssocsTgt(subclass).size();
        int totalCount = attributeCount + associationCount + otherAssocsCount;

        if (totalCount < lowestCount) {
          lowestCount = totalCount;
          subclassWithLowestCount = subclass;
        }
      }
    }

    return subclassWithLowestCount;
  }

  // CHECKED
  private int getAssociationCount(ASTCDType astcdClass) {
    int count = 0;
    for (AssocStruct assocStruct : srcMap.get(astcdClass)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if ((assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne()
                || assocStruct.getAssociation().getRight().getCDCardinality().isOne())
            && !getConnectedClasses(assocStruct.getAssociation(), srcCD)
                .b
                .getSymbol()
                .getInternalQualifiedName()
                .equals(
                    getConnectedClasses(assocStruct.getAssociation(), srcCD)
                        .a
                        .getSymbol()
                        .getInternalQualifiedName())) {
          count++;
        }
      } else {
        if ((assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
                || assocStruct.getAssociation().getLeft().getCDCardinality().isOne())
            && !getConnectedClasses(assocStruct.getAssociation(), srcCD)
                .b
                .getSymbol()
                .getInternalQualifiedName()
                .equals(
                    getConnectedClasses(assocStruct.getAssociation(), srcCD)
                        .a
                        .getSymbol()
                        .getInternalQualifiedName())) {
          count++;
        }
      }
    }
    return count;
  }

  private int getAssociationCountTgt(ASTCDType astcdClass){
    int count = 0;
    for (AssocStruct assocStruct : tgtMap.get(astcdClass)){
      if (assocStruct.getSide().equals(ClassSide.Left)){
        if ((assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne()
            || assocStruct.getAssociation().getRight().getCDCardinality().isOne())
            && !getConnectedClasses(assocStruct.getAssociation(), tgtCD)
            .b
            .getSymbol()
            .getInternalQualifiedName()
            .equals(
                getConnectedClasses(assocStruct.getAssociation(), tgtCD)
                    .a
                    .getSymbol()
                    .getInternalQualifiedName())) {
          count++;
        }
      } else {
        if ((assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isOne())
            && !getConnectedClasses(assocStruct.getAssociation(), tgtCD)
            .b
            .getSymbol()
            .getInternalQualifiedName()
            .equals(
                getConnectedClasses(assocStruct.getAssociation(), tgtCD)
                    .a
                    .getSymbol()
                    .getInternalQualifiedName())) {
          count++;
        }
      }
    }
    return count;
  }

  // CHECKED
  public List<String> getSuperTypes(ASTCDClass astcdClass) {
    List<ASTCDClass> superClasses = getSuperTypes(srcCD, astcdClass);
    List<String> classes = new ArrayList<>();
    for (int i = superClasses.size() - 1; i >= 0; i--) {
      String className =
          superClasses.get(i).getSymbol().getInternalQualifiedName().replace(".", "_");
      classes.add(className);
    }
    List<ASTMCObjectType> interfaces = astcdClass.getInterfaceList();
    for (ASTMCObjectType interf : interfaces) {
      String className = interf.printType();
      className = className.replace(".", "_");
      classes.add(className);
    }
    return classes;
  }

  // CHECKED
  public List<ASTODAttribute> getAttributesOD(
      ASTCDClass astcdClass, Pair<ASTCDAttribute, String> pair) {
    List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
    List<ASTODAttribute> odAttributes = new ArrayList<>();
    if (pair != null){
      odAttributes.add(ODBuilder.buildAttr(pair.a.getMCType().printType(), pair.a.getName(), pair.b));
      attributes.remove(pair.a);
    }
    for (ASTCDAttribute attribute : attributes) {
      Pair<Boolean, String> attIsEnum = attIsEnum(attribute);
      if (attIsEnum.a) {
        odAttributes.add(
          ODBuilder.buildAttr(attribute.getMCType().printType(), attribute.getName(), attIsEnum.b));
      } else {
        odAttributes.add(ODBuilder.buildAttr(attribute.getMCType().printType(), attribute.getName()));
      }
    }
    return odAttributes;
  }

  // CHECKED
  public static ASTCDClass getCDClass(ASTCDCompilationUnit compilationUnit, String className) {
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if (astcdClass.getSymbol().getInternalQualifiedName().equals(className)) {
        return astcdClass;
      }
    }
    return null;
  }

  // CHECKED
  public boolean matchDirectionInReverse(
      AssocStruct srcStruct, Pair<AssocStruct, ClassSide> tgtStruct) {
    if (((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Right))
            || (srcStruct.getSide().equals(ClassSide.Right) && tgtStruct.b.equals(ClassSide.Left)))
        && srcStruct.getDirection() == tgtStruct.a.getDirection()) {
      return true;
    } else return ((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Left))
            || (srcStruct.getSide().equals(ClassSide.Right) && tgtStruct.b.equals(ClassSide.Right)))
            && ((srcStruct.getDirection().equals(AssocDirection.BiDirectional)
            && tgtStruct.a.getDirection().equals(AssocDirection.BiDirectional))
            || (srcStruct.getDirection().equals(AssocDirection.LeftToRight)
            && tgtStruct.a.getDirection().equals(AssocDirection.RightToLeft))
            || (srcStruct.getDirection().equals(AssocDirection.RightToLeft)
            && tgtStruct.a.getDirection().equals(AssocDirection.LeftToRight)));
  }

  // CHECKED
  public static boolean matchDirection(
      AssocStruct srcStruct, Pair<AssocStruct, ClassSide> tgtStruct) {
    if (((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Left))
            || (srcStruct.getSide().equals(ClassSide.Right) && tgtStruct.b.equals(ClassSide.Right)))
        && srcStruct.getDirection() == tgtStruct.a.getDirection()) {
      return true;
    } else return ((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Right))
            || (srcStruct.getSide().equals(ClassSide.Right) && tgtStruct.b.equals(ClassSide.Left)))
            && ((srcStruct.getDirection().equals(AssocDirection.BiDirectional)
            && tgtStruct.a.getDirection().equals(AssocDirection.BiDirectional))
            || (srcStruct.getDirection().equals(AssocDirection.LeftToRight)
            && tgtStruct.a.getDirection().equals(AssocDirection.RightToLeft))
            || (srcStruct.getDirection().equals(AssocDirection.RightToLeft)
            && tgtStruct.a.getDirection().equals(AssocDirection.LeftToRight)));
  }

  // CHECKED
  public boolean sameAssocStruct(AssocStruct srcStruct, AssocStruct tgtStruct) {
    assert srcStruct != null;
    assert tgtStruct != null;
      return CDDiffUtil.inferRole(srcStruct.getAssociation().getLeft())
              .equals(CDDiffUtil.inferRole(tgtStruct.getAssociation().getLeft()))
              && CDDiffUtil.inferRole(srcStruct.getAssociation().getRight())
              .equals(CDDiffUtil.inferRole(tgtStruct.getAssociation().getRight()))
              && matchDirection(srcStruct, new Pair<>(tgtStruct, tgtStruct.getSide()))
              && matchRoleNames(
              srcStruct.getAssociation().getLeft(), tgtStruct.getAssociation().getLeft())
              && matchRoleNames(
              srcStruct.getAssociation().getRight(), tgtStruct.getAssociation().getRight());
  }

  // CHECKED
  public boolean sameAssocStructInReverse(AssocStruct struct, AssocStruct tgtStruct) {
      return CDDiffUtil.inferRole(struct.getAssociation().getLeft())
              .equals(CDDiffUtil.inferRole(tgtStruct.getAssociation().getRight()))
              && CDDiffUtil.inferRole(struct.getAssociation().getRight())
              .equals(CDDiffUtil.inferRole(tgtStruct.getAssociation().getLeft()))
              && matchDirectionInReverse(struct, new Pair<>(tgtStruct, tgtStruct.getSide()))
              && matchRoleNames(struct.getAssociation().getLeft(), tgtStruct.getAssociation().getRight())
              && matchRoleNames(
              struct.getAssociation().getRight(), tgtStruct.getAssociation().getLeft());
  }

  // CHECKED
  public int getClassSize(ASTCDClass astcdClass) {
    int attributeCount = getAllAttr(astcdClass).b.size();
    int associationCount = getAssociationCount(astcdClass);
    int otherAssocsCount = getOtherAssocFromSuper(astcdClass).size();
    return attributeCount + associationCount + otherAssocsCount;
  }

  /**
   * Compare associations.
   * If for a pair of associations one of them is a subassociation and a loop association, the
   * other one is marked so that it won't be look at for generation of object diagrams.
   */
  public void reduceMaps() {
    for (ASTCDType astcdClass : srcMap.keySet()) {
      for (AssocStruct assocStruct : srcMap.get(astcdClass)) {
        for (AssocStruct assocStruct1 : srcMap.get(astcdClass)) {
          if (isLoopStruct(assocStruct1)
            && assocStruct != assocStruct1
            && assocStruct.isToBeProcessed()
            && assocStruct1.isToBeProcessed()
            && sameAssociationSpec(assocStruct.getAssociation(), assocStruct1.getAssociation())) {
            assocStruct.setToBeProcessed(false);
          }
        }
      }
    }
  }

  public boolean sameAssociationSpec(ASTCDAssociation association, ASTCDAssociation association2){
    Pair<ASTCDCardinality, ASTCDCardinality> cardinalities = getCardinality(association2);
    Pair<ASTCDType, ASTCDType> conncected1 = getConnectedClasses(association, srcCD);
    Pair<ASTCDType, ASTCDType> conncected2 = getConnectedClasses(association2, srcCD);
    if (isSubclassWithSuper(conncected1.a, conncected2.a)
      && isSubclassWithSuper(conncected1.b, conncected2.b)) {
      return matchRoleNames(association.getLeft(), association2.getLeft())
        && matchRoleNames(association.getRight(), association2.getRight())
        && Syn2SemDiffHelper.getDirection(association)
        .equals(Syn2SemDiffHelper.getDirection(association2))
        && cardToEnum(association.getLeft().getCDCardinality())
        .equals(cardToEnum(cardinalities.a))
        && cardToEnum(association.getRight().getCDCardinality())
        .equals(cardToEnum(cardinalities.b));
    }
    return false;
  }

  // CHECKED
  public boolean isLoopStruct(AssocStruct assocStruct) {
    Pair<ASTCDType, ASTCDType> pair = getConnectedClasses(assocStruct.getAssociation(), srcCD);
    return pair.a.equals(pair.b);
  }

  // CHECKED
  public void deleteOtherSideSrc(ASTCDType astcdClass) {
    List<Pair<ASTCDType, List<AssocStruct>>> toDelete = new ArrayList<>();
    for (ASTCDType toCheck : srcMap.keySet()) {
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
    for (Pair<ASTCDType, List<AssocStruct>> pair : toDelete) {
      for (AssocStruct struct : pair.b) {
        srcMap.get(pair.a).remove(struct);
      }
    }
  }

  // CHECKED
  public void deleteOtherSideTgt(ASTCDType astcdClass) {
    List<Pair<ASTCDType, List<AssocStruct>>> toDelete = new ArrayList<>();
    for (ASTCDType toCheck : tgtMap.keySet()) {
      if (toCheck != astcdClass) {
        List<AssocStruct> toDeleteStructs = new ArrayList<>();
        for (AssocStruct struct : tgtMap.get(toCheck)) {
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
    for (Pair<ASTCDType, List<AssocStruct>> pair : toDelete) {
      for (AssocStruct struct : pair.b) {
        tgtMap.get(pair.a).remove(struct);
      }
    }
  }

  // CHECKED
  public void deleteAssocOtherSideSrc(AssocStruct assocStruct) {
    if (assocStruct.getDirection().equals(AssocDirection.BiDirectional)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        for (AssocStruct struct :
            srcMap.get(getConnectedClasses(assocStruct.getAssociation(), srcCD).b)) {
          if (struct.getSide().equals(ClassSide.Right)
              && getConnectedClasses(struct.getAssociation(), srcCD)
                  .a
                  .equals(getConnectedClasses(assocStruct.getAssociation(), srcCD).a)
              && sameAssocStruct(assocStruct, struct)) {
            srcMap.get(getConnectedClasses(assocStruct.getAssociation(), srcCD).b).remove(struct);
            break;
          }
        }
      } else {
        for (AssocStruct struct :
            srcMap.get(getConnectedClasses(assocStruct.getAssociation(), srcCD).a)) {
          if (struct.getSide().equals(ClassSide.Left)
              && getConnectedClasses(struct.getAssociation(), srcCD)
                  .b
                  .equals(getConnectedClasses(assocStruct.getAssociation(), srcCD).b)
              && sameAssocStruct(assocStruct, struct)) {
            srcMap.get(getConnectedClasses(assocStruct.getAssociation(), srcCD).a).remove(struct);
            break;
          }
        }
      }
    }
  }

  // CHECKED
  public void deleteAssocOtherSideTgt(AssocStruct assocStruct) {
    if (assocStruct.getDirection().equals(AssocDirection.BiDirectional)) {
      if (assocStruct.getSide().equals(ClassSide.Left)){
        for (AssocStruct struct : tgtMap.get(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)){
          if (struct.getSide().equals(ClassSide.Right)
            && getConnectedClasses(struct.getAssociation(), tgtCD).a.equals(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)
            && sameAssocStruct(assocStruct, struct)){
            tgtMap.get(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b).remove(struct);
            break;
          }
        }
      } else {
        for (AssocStruct struct : tgtMap.get(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a)){
          if (struct.getSide().equals(ClassSide.Left)
            && getConnectedClasses(struct.getAssociation(), tgtCD).b.equals(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b)
            && sameAssocStruct(assocStruct, struct)){
            tgtMap.get(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a).remove(struct);
            break;
          }
        }
      }
    }
  }

  //CHECKED

  /**
   * This function is used when the object diagrams are derived under simple semantics.
   * The function changes the stereotype to contain only the base type of the object without the superclasses.
   * @param list list of object diagrams.
   */
  public void makeSimpleSem(List<ASTODArtifact> list) {
    for (ASTODArtifact artifact : list) {
      for (ASTODElement element : artifact.getObjectDiagram().getODElementList()) {
        if (element instanceof ASTODObject) {
          String type = ((ASTODObject) element).getMCObjectType().printType();
          ((ASTODObject) element).getModifier().getStereotype().removeValues("instanceof");
          ((ASTODObject) element)
              .setModifier(
                  OD4ReportMill.modifierBuilder()
                      .setStereotype(
                          OD4ReportMill.stereotypeBuilder()
                              .addValues(
                                  OD4ReportMill.stereoValueBuilder()
                                      .setName("instanceof")
                                      .setContent(type)
                                      .setText(
                                          OD4ReportMill.stringLiteralBuilder()
                                              .setSource(type)
                                              .build())
                                      .build())
                              .build())
                      .build());
          ((ASTODObject) element).setMCObjectType(
            ODBasisMill.mCQualifiedTypeBuilder()
              .setMCQualifiedName(
                ODBasisMill.mCQualifiedNameBuilder()
                  .setPartsList(Collections.singletonList(type))
                  .build())
              .build());
        }
      }
    }
  }

  // CHECKED
  public Pair<ASTCDCardinality, ASTCDCardinality> getCardinality(ASTCDAssociation association) {
    ASTCDCardinality left;
    ASTCDCardinality right;
    if (!association.getLeft().isPresentCDCardinality()) {
      left = new ASTCDCardMult();
    } else {
      left = association.getLeft().getCDCardinality();
    }

    if (!association.getRight().isPresentCDCardinality()) {
      right = new ASTCDCardMult();
    } else {
      right = association.getRight().getCDCardinality();
    }
    return new Pair<>(left, right);
  }

  // CHECKED

  /**
   * Get the matching AssocStructs for a given pair.
   * The id 'unmodifiedAssoc' is used to identify the association in the map.
   * @param srcAssoc source association.
   * @param tgtAssoc target association.
   * @return pair of AssocStructs for both associations.
   */
  public Pair<AssocStruct, AssocStruct> getStructsForAssocDiff(
      ASTCDAssociation srcAssoc, ASTCDAssociation tgtAssoc) {
    Pair<ASTCDType, ASTCDType> srcCLasses = getConnectedClasses(srcAssoc, srcCD);
    Pair<ASTCDType, ASTCDType> tgtCLasses = getConnectedClasses(tgtAssoc, tgtCD);
    AssocStruct srcStruct = null;
    AssocStruct tgtStruct = null;
    if (getAssocStructByUnmod(srcCLasses.a, srcAssoc) != null) {
      srcStruct = getAssocStructByUnmod(srcCLasses.a, srcAssoc);
    } else if (getAssocStructByUnmod(srcCLasses.b, srcAssoc) != null) {
      srcStruct = getAssocStructByUnmod(srcCLasses.b, srcAssoc);
    }

    if (getAssocStructByUnmodTgt(tgtCLasses.a, tgtAssoc) != null) {
      tgtStruct = getAssocStructByUnmodTgt(tgtCLasses.a, tgtAssoc);
    } else if (getAssocStructByUnmodTgt(tgtCLasses.b, tgtAssoc) != null) {
      tgtStruct = getAssocStructByUnmodTgt(tgtCLasses.b, tgtAssoc);
    }
    return new Pair<>(srcStruct, tgtStruct);
  }

  public AssocStruct getAssocStructByUnmod(ASTCDType astcdClass, ASTCDAssociation association) {
    for (AssocStruct struct : srcMap.get(astcdClass)) {
      if (sameAssociation(struct.getUnmodifiedAssoc(), association)){
        return struct;
      }
    }
    return null;
  }

  public AssocStruct getAssocStructByUnmodTgt(ASTCDType astcdClass, ASTCDAssociation association){
    for (AssocStruct struct : tgtMap.get(astcdClass)){
      if (sameAssociation(struct.getUnmodifiedAssoc(), association)){
        return struct;
      }
    }
    return null;
  }

  public Pair<Boolean, String> attIsEnum(ASTCDAttribute attribute) {
    for (ASTCDEnum enum_ : srcCD.getCDDefinition().getCDEnumsList()) {
      if (enum_.getSymbol().getInternalQualifiedName().equals(attribute.getMCType().printType())) {
        return new Pair<>(true, enum_.getCDEnumConstant(0).getName());
      }
    }
    return new Pair<>(false, "");
  }

  // CHECKED

  /**
   * Check if the superclasses of the given one are the same in the source and target diagram.
   * @param astcdType class to check.
   * @return true if the superclasses are the different.
   */
  public boolean hasDiffSuper(ASTCDType astcdType) {
    Optional<ASTCDType> oldType = findMatchedTypeTgt(astcdType);
    Set<ASTCDType> oldTypes = CDDiffUtil.getAllSuperTypes(oldType.get(), tgtCD.getCDDefinition());
    Set<ASTCDType> newTypes = CDDiffUtil.getAllSuperTypes(astcdType, srcCD.getCDDefinition());
    for (ASTCDType class1 : oldTypes) {
      boolean foundMatch = false;
      for (ASTCDType type2 : newTypes) {
        Optional<ASTCDType> matched = findMatchedTypeSrc(class1);
        if (matched.isPresent() && matched.get() == type2) {
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        return true;
      }
    }
    for (ASTCDType type1 : newTypes) {
      boolean foundMatch = false;
      for (ASTCDType class2 : oldTypes) {
        Optional<ASTCDType> matched = findMatchedTypeTgt(type1);
        if (matched.isPresent() && matched.get() == class2) {
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        return true;
      }
    }
    return false;
  }


  /**
   * Delete associations from srcMap with a specific role name
   *
   * @param astcdClass source class
   * @param role role name
   */
  public void deleteAssocsFromSrc(ASTCDType astcdClass, ASTCDRole role) {
    Iterator<AssocStruct> iterator = getSrcMap().get(astcdClass).iterator();
    while (iterator.hasNext()) {
      AssocStruct assocStruct = iterator.next();
      if (assocStruct.getSide().equals(ClassSide.Left)
        && CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(role.getName())) {
        if (isOtherSideNeeded(assocStruct)) {
          notInstClassesSrc.add(getConnectedClasses(assocStruct.getAssociation(), srcCD).b);
          srcMap.removeAll(getConnectedClasses(assocStruct.getAssociation(), srcCD).b);
        } else {
          deleteAssocOtherSideSrc(assocStruct);
        }
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
        && CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(role.getName())) {
        if (isOtherSideNeeded(assocStruct)) {
          notInstClassesSrc.add(getConnectedClasses(assocStruct.getAssociation(), srcCD).a);
          srcMap.removeAll(getConnectedClasses(assocStruct.getAssociation(), srcCD).a);
        } else {
          deleteAssocOtherSideSrc(assocStruct);
        }
        iterator.remove();
      }
    }
  }

  public boolean isOtherSideNeeded(AssocStruct assocStruct){
    if (assocStruct.getSide().equals(ClassSide.Left)
      && (assocStruct.getAssociation().getRight().getCDCardinality().isOne() || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())){
      return true;
    }
    if (assocStruct.getSide().equals(ClassSide.Right)
      && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne() || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())){
      return true;
    }
    return false;
  }

  // CHECKED

  /**
   * Check for all compositions if a subcomponent cannot be instantiated.
   * If this is the case, the composite class cannot be instantiated either.
   */
  public void deleteCompositions() {
    for (ASTCDType astcdClass : srcMap.keySet()){
      for (ASTCDAssociation association : getCDAssociationsListForTypeSrc(astcdClass)) {
        Pair<ASTCDType, ASTCDType> pair =
          Syn2SemDiffHelper.getConnectedClasses(association, srcCD);
        AssocStruct assocStruct = getAssocStructByUnmod(pair.a, association);
        if (association.getCDAssocType().isComposition() && assocStruct != null) {
          if (getNotInstClassesSrc().contains(pair.b)
            || (pair.b.getModifier().isAbstract() && minSubClass(pair.b) == null)) {
            updateSrc(pair.a);
            for (ASTCDType subClass : getSrcSubMap().get(pair.a)) {
              getSrcMap().removeAll(subClass);
              updateSrc(subClass);
            }
          }
        }
      }
    }

    for (ASTCDType astcdClass : tgtMap.keySet()){
      for (ASTCDAssociation association : getCDAssociationsListForTypeTgt(astcdClass)) {
      Pair<ASTCDType, ASTCDType> pair =
        Syn2SemDiffHelper.getConnectedClasses(association, tgtCD);
      AssocStruct assocStruct = getAssocStructByUnmod(pair.a, association);
      if (association.getCDAssocType().isComposition() && assocStruct != null) {
        if (getNotInstClassesTgt().contains(pair.b)
          || (pair.b.getModifier().isAbstract() && minSubClassTgt(pair.b) == null)) {
          updateTgt(pair.a);
          for (ASTCDType subClass : getTgtSubMap().get(pair.a)) {
            getTgtMap().removeAll(subClass);
            updateTgt(subClass);
          }
        }
      }
    }
  }
  }

  /**
   * Delete associations from trgMap with a specific role name
   *
   * @param astcdClass source class
   * @param role role name
   */
  public void deleteAssocsFromTgt(ASTCDType astcdClass, ASTCDRole role){
    Iterator<AssocStruct> iterator = getTgtMap().get(astcdClass).iterator();
    while (iterator.hasNext()){
      AssocStruct assocStruct = iterator.next();
      if (assocStruct.getSide().equals(ClassSide.Left)
        && CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(role.getName())) {
        if (isOtherSideNeeded(assocStruct)) {
          notInstClassesTgt.add(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b);
          tgtMap.removeAll(getConnectedClasses(assocStruct.getAssociation(), tgtCD).b);
        } else {
          deleteAssocOtherSideTgt(assocStruct);
        }
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
        && CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(role.getName())) {
        if (isOtherSideNeeded(assocStruct)) {
          notInstClassesTgt.add(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a);
          tgtMap.removeAll(getConnectedClasses(assocStruct.getAssociation(), tgtCD).a);
        } else {
          deleteAssocOtherSideTgt(assocStruct);
        }
        iterator.remove();
      }
    }
  }

  // CHECKED
  public List<AssocStruct> deletedAssocsForClass(ASTCDType astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDAssociation association : deletedAssocs) {
      AssocStruct matched = getAssocStructByUnmodTgt(astcdClass, association);
      if (matched != null) {
        list.add(matched);
      }
    }
    return list;
  }

  // CHECKED

  /**
   * Search for an association in srcCD that can't be matched with an association in tgtCD.
   * @param assocStructs list of associations without those from addedAssocs.
   * @param astcdClass class to check.
   * @return
   */
  public boolean srcAssocsExist(List<AssocStruct> assocStructs, ASTCDType astcdClass) {
    for (AssocStruct assocStruct : assocStructs) {
      ASTCDAssociation matched = findMatchedAssociation(assocStruct.getAssociation());
      if (matched != null && getAssocStructByUnmodTgt(astcdClass, matched) == null) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public ASTCDAssociation findMatchedAssociation(ASTCDAssociation association) {
    for (Pair<ASTCDAssociation, ASTCDAssociation> pair : matchedAssocs) {
      if (pair.a.equals(association)) {
        return pair.b;
      }
    }
    return null;
  }

  // CHECKED

  /**
   * Search for an association in tgtCD that can't be matched with an association in srcCD.
   * @param assocStructs list of associations without those from deletedAssocs.
   * @param astcdClass class to check.
   * @return true if there is an association that can't be matched.
   */
  public boolean tgtAssocsExist(List<AssocStruct> assocStructs, ASTCDType astcdClass) {
    for (AssocStruct assocStruct : assocStructs) {
      ASTCDAssociation matched = findMatchedAssociationSrc(assocStruct.getAssociation());
      if (matched != null && getAssocStructByUnmod(astcdClass, matched) == null) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public ASTCDAssociation findMatchedAssociationSrc(ASTCDAssociation association) {
    for (Pair<ASTCDAssociation, ASTCDAssociation> pair : matchedAssocs) {
      if (pair.b.equals(association)) {
        return pair.a;
      }
    }
    return null;
  }

  //CHECKED
  public List<AssocStruct> addedAssocsForClass(ASTCDType astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs) {
      AssocStruct matched = getAssocStructByUnmod(astcdClass, association);
      if (matched != null) {
        list.add(matched);
      }
    }
    return list;
  }

  /**
   * Get two non-abstract subclasses for a given pair.
   *
   * @param pair pair of classes.
   * @return pair of non-abstract subclasses.
   */
  public Pair<ASTCDType, ASTCDType> getClassesForAssoc(Pair<ASTCDType, ASTCDType> pair) {
    ASTCDClass left = null;
    ASTCDClass right = null;
    if (pair.a.getModifier().isAbstract()) {//TODO
      left = minSubClass(pair.a);
    }
    if (pair.b.getModifier().isAbstract()) {
      right = minSubClass(pair.b);
    }
    if (left != null && right != null) {
      return new Pair<>(left, right);
    }
    return null;
  }

  public Pair<Boolean, Boolean> stereotypeChange(ASTCDClass newClass, ASTCDClass oldClass){
    boolean abstractChange = false;
    boolean singletonChange = false;
    if (!newClass.getModifier().isAbstract() && oldClass.getModifier().isAbstract()){
      abstractChange = true;
    }
    if (!newClass.getModifier().getStereotype().contains("singleton") && oldClass.getModifier().getStereotype().contains("singleton")){
      singletonChange = true;
    }
    return new Pair<>(abstractChange, singletonChange);
  }

  public List<Pair<ASTCDInterface, ASTCDInterface>> getMatchedInterfaces() {
    return matchedInterfaces;
  }

  public void setMatchedInterfaces(List<Pair<ASTCDInterface, ASTCDInterface>> matchedInterfaces) {
    this.matchedInterfaces = matchedInterfaces;
  }

  public ASTCDClass getClassForTypeSrc(ASTCDType astcdType){
    if (astcdType instanceof ASTCDClass){
      return (ASTCDClass) astcdType;
    } else {
      return minSubClass(astcdType);
    }
  }

  public OverlappingAssocsDirect computeDirectForType(ASTCDType astcdType, ArrayListMultimap<ASTCDType, AssocStruct> map, ASTCDCompilationUnit compilationUnit) {
    Set<Pair<AssocStruct, AssocStruct>> directOverlappingAssocs = new HashSet<>();
    Set<Pair<AssocStruct, AssocStruct>> directOverlappingNoRelation = new HashSet<>();
    for (AssocStruct assocStruct : map.get(astcdType)){
      if (true) {
        for (AssocStruct assocStruct1 : map.get(astcdType)) {
          if (assocStruct != assocStruct1) {
            if (inInheritanceRelation(assocStruct, assocStruct1, compilationUnit)) {
              directOverlappingAssocs.add(new Pair<>(assocStruct, assocStruct1));
            } else if (!inInheritanceRelation(assocStruct, assocStruct1, compilationUnit)
              && !inInheritanceRelation(assocStruct1, assocStruct, compilationUnit)) {
              directOverlappingNoRelation.add(new Pair<>(assocStruct, assocStruct1));
            }
          }
        }
      }
    }
    return new OverlappingAssocsDirect(directOverlappingAssocs, directOverlappingNoRelation);
  }

}
