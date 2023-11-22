package de.monticore.cddiff.syn2semdiff.odgen;

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
import de.monticore.cddiff.syn2semdiff.datastructures.*;
import de.monticore.cddiff.syndiff.CDAssocDiff;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.matcher.NameTypeMatcher;
import de.monticore.matcher.SrcTgtAssocMatcher;
import de.monticore.matcher.SuperTypeMatcher;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis.ODBasisMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODAttribute;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is a helper class that is accessible from all classes for semantic difference and generation
 * of object diagrams. It contains functions for comparing associations, attributes, classes,
 * generating part of object diagrams. It further contains multiple maps that reduce the complexity
 * of the implementation. The function setMaps() that computes possible associations for each class
 * is also implemented in this class.
 */
public class Syn2SemDiffHelper {

  public Syn2SemDiffHelper() {}

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
   * Map with all subclasses of a class from srcCD. This is used to reduce the complexity for
   * computing the underlying inheritance tree.
   */
  private ArrayListMultimap<ASTCDType, ASTCDClass> srcSubMap;

  /**
   * Map with all subclasses of a class from trgCD. This is used to reduce the complexity for
   * computing the underlying inheritance tree.
   */
  private ArrayListMultimap<ASTCDType, ASTCDClass> tgtSubMap;

  /**
   * Set with all classes that are not instantiatable in srcCD. Those are classes that cannot exist
   * because of overlapping. The second possibility is that the class has an attribute and a
   * relation to the same class, e.g., int age and -> (age) Age.
   */
  private Set<ASTCDType> notInstClassesSrc;

  /**
   * Set with all classes that are not instantiatable in trgCD. Those are classes that cannot exist
   * because of overlapping. The second possibility is that the class has an attribute and a
   * relation to the same class, e.g., int age and -> (age) Age.
   */
  private Set<ASTCDType> notInstClassesTgt;

  /**
   * This is a copy of the srcCD so that it can be accessed from all classes for semantic
   * difference.
   */
  private ASTCDCompilationUnit srcCD;

  /**
   * This is a copy of the trgCD so that it can be accessed from all classes for semantic
   * difference.
   */
  private ASTCDCompilationUnit tgtCD;

  /**
   * Those are the matched classes from the analysis of the syntax. This way some functionalities
   * were moved to this helper class.
   */
  private List<Pair<ASTCDClass, ASTCDType>> matchedClasses;

  /**
   * Those are the matched interfaces from the analysis of the syntax. This way some functionalities
   * were moved to this helper class.
   */
  private List<Pair<ASTCDInterface, ASTCDType>> matchedInterfaces;

  /**
   * Those are the added associations from the analysis of the syntax. This way some functionalities
   * were moved to this helper class.
   */
  private List<ASTCDAssociation> addedAssocs;

  /**
   * Those are the deleted associations from the analysis of the syntax. This way some
   * functionalities were moved to this helper class.
   */
  private List<ASTCDAssociation> deletedAssocs;

  private SrcTgtAssocMatcher matcher;
  private List<CDAssocDiff> diffs;

  // CHECKED
  public boolean isAttContainedInClass(ASTCDAttribute attribute, ASTCDType astcdClass) {
    int indexAttribute = attribute.getMCType().printType().lastIndexOf(".");
    for (ASTCDAttribute att : getAllAttr(astcdClass).b) {
      int indexCurrent = att.getMCType().printType().lastIndexOf(".");
      if (indexCurrent == -1
          && indexAttribute == -1
          && (att.getName().equals(attribute.getName())
              && att.getMCType().printType().equals(attribute.getMCType().printType()))) {
        return true;
      } else if (indexCurrent == -1
          && indexAttribute != -1
          && (att.getName().equals(attribute.getName())
              && att.getMCType()
                  .printType()
                  .equals(attribute.getMCType().printType().substring(indexAttribute + 1)))) {
        return true;
      } else if (indexCurrent != -1
          && indexAttribute == -1
          && (att.getName().equals(attribute.getName())
              && att.getMCType()
                  .printType()
                  .substring(indexCurrent + 1)
                  .equals(attribute.getMCType().printType()))) {
        return true;
      } else if (indexCurrent != -1
          && indexAttribute != -1
          && (att.getName().equals(attribute.getName())
              && att.getMCType()
                  .printType()
                  .substring(indexCurrent + 1)
                  .equals(attribute.getMCType().printType().substring(indexAttribute + 1)))) {
        return true;
      }
    }
    return false;
  }

  public void setDiffs(List<CDAssocDiff> diffs) {
    this.diffs = diffs;
  }

  public List<CDAssocDiff> getDiffs() {
    return diffs;
  }

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

  public void updateSrc(ASTCDType astcdClass) {
    notInstClassesSrc.add(astcdClass);
  }

  public void updateTgt(ASTCDType astcdClass) {
    notInstClassesTgt.add(astcdClass);
  }

  public void setMatchedAssocs(List<Pair<ASTCDAssociation, ASTCDAssociation>> matchedAssocs) {}

  public void setDeletedAssocs(List<ASTCDAssociation> deletedAssocs) {
    this.deletedAssocs = deletedAssocs;
  }

  public void setAddedAssocs(List<ASTCDAssociation> addedAssocs) {
    this.addedAssocs = addedAssocs;
  }

  public List<Pair<ASTCDInterface, ASTCDType>> getMatchedInterfaces() {
    return matchedInterfaces;
  }

  public void setMatchedInterfaces(List<Pair<ASTCDInterface, ASTCDType>> matchedInterfaces) {
    this.matchedInterfaces = matchedInterfaces;
  }

  public boolean isSubclassWithSuper(ASTCDType superClass, ASTCDType subClass) {
    return isSuperOf(
        superClass.getSymbol().getInternalQualifiedName(),
        subClass.getSymbol().getInternalQualifiedName(),
        srcCD);
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
            getConnectedTypes(superAssoc.getAssociation(), srcCD).a,
            getConnectedTypes(subAssoc.getAssociation(), srcCD).a)
        && isSubclassWithSuper(
            getConnectedTypes(superAssoc.getAssociation(), srcCD).b,
            getConnectedTypes(subAssoc.getAssociation(), srcCD).b)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Left)
        && superAssoc.getSide().equals(ClassSide.Right)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && isSubclassWithSuper(
            getConnectedTypes(superAssoc.getAssociation(), srcCD).a,
            getConnectedTypes(subAssoc.getAssociation(), srcCD).b)
        && isSubclassWithSuper(
            getConnectedTypes(superAssoc.getAssociation(), srcCD).b,
            getConnectedTypes(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else if (subAssoc.getSide().equals(ClassSide.Right)
        && superAssoc.getSide().equals(ClassSide.Left)
        && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
        && matchRoleNames(
            superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getRight())
        && matchRoleNames(
            superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getLeft())
        && isSubclassWithSuper(
            getConnectedTypes(superAssoc.getAssociation(), srcCD).a,
            getConnectedTypes(subAssoc.getAssociation(), srcCD).b)
        && isSubclassWithSuper(
            getConnectedTypes(superAssoc.getAssociation(), srcCD).b,
            getConnectedTypes(subAssoc.getAssociation(), srcCD).a)) {
      return true;
    } else
      return subAssoc.getSide().equals(ClassSide.Right)
          && superAssoc.getSide().equals(ClassSide.Right)
          && matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide()))
          && matchRoleNames(
              superAssoc.getAssociation().getLeft(), subAssoc.getAssociation().getLeft())
          && matchRoleNames(
              superAssoc.getAssociation().getRight(), subAssoc.getAssociation().getRight())
          && isSubclassWithSuper(
              getConnectedTypes(superAssoc.getAssociation(), srcCD).a,
              getConnectedTypes(subAssoc.getAssociation(), srcCD).a)
          && isSubclassWithSuper(
              getConnectedTypes(superAssoc.getAssociation(), srcCD).b,
              getConnectedTypes(subAssoc.getAssociation(), srcCD).b);
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
              && Syn2SemDiffHelper.getConnectedTypes(assocStruct.getAssociation(), srcCD).b
                  == astcdClass) {
            list.add(assocStruct.deepClone());
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
              && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
                  || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())
              && Syn2SemDiffHelper.getConnectedTypes(assocStruct.getAssociation(), srcCD).a
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
              && Syn2SemDiffHelper.getConnectedTypes(assocStruct.getAssociation(), tgtCD).b
                  == astcdClass) {
            list.add(assocStruct.deepClone());
          } else if (assocStruct.getSide().equals(ClassSide.Right)
              && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
              && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
                  || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())
              && Syn2SemDiffHelper.getConnectedTypes(assocStruct.getAssociation(), tgtCD).a
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
    for (ASTCDType astcdClass1 : CDDiffUtil.getAllSuperTypes(astcdClass, tgtCD.getCDDefinition())) {
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
    for (ASTCDType astcdClass1 : CDDiffUtil.getAllSuperTypes(astcdClass, srcCD.getCDDefinition())) {
      list.addAll(getOtherAssocFromSuper(astcdClass1));
    }
    return list;
  }

  /**
   * Find matched type in srcCD.
   *
   * @param astcdType type in tgtCD.
   * @return matched type in srcCD.
   */
  public Optional<ASTCDType> findMatchedTypeSrc(ASTCDType astcdType) {
    if (astcdType instanceof ASTCDClass) {
      Optional<ASTCDClass> result = findMatchedSrc((ASTCDClass) astcdType);
      return result.map(Function.identity());
    } else if (astcdType instanceof ASTCDInterface) {
      return findMatchedTypeSrc((ASTCDInterface) astcdType);
    } else {
      return Optional.empty();
    }
  }

  /**
   * Find matched type in tgtCD.
   *
   * @param astcdType type in srcCD.
   * @return matched type in tgtCD.
   */
  public Optional<ASTCDType> findMatchedTypeTgt(ASTCDType astcdType) {
    if (astcdType instanceof ASTCDClass) {
      return findMatchedClass((ASTCDClass) astcdType);
    } else if (astcdType instanceof ASTCDInterface) {
      return findMatchedTypeTgt((ASTCDInterface) astcdType);
    } else {
      return Optional.empty();
    }
  }

  // CHECKED
  public Optional<ASTCDType> findMatchedClass(ASTCDClass astcdClass) {
    for (Pair<ASTCDClass, ASTCDType> pair : matchedClasses) {
      if (pair.a.equals(astcdClass)) {
        return Optional.ofNullable(pair.b);
      }
    }
    return Optional.empty();
  }

  // CHECKED
  public Optional<ASTCDClass> findMatchedSrc(ASTCDClass astcdClass) {
    for (Pair<ASTCDClass, ASTCDType> pair : matchedClasses) {
      if (pair.b.equals(astcdClass)) {
        return Optional.ofNullable(pair.a);
      }
    }
    return Optional.empty();
  }

  public Optional<ASTCDType> findMatchedTypeSrc(ASTCDInterface astcdInterface) {
    for (Pair<ASTCDInterface, ASTCDType> pair : matchedInterfaces) {
      if (pair.b.equals(astcdInterface)) {
        return Optional.ofNullable(pair.a);
      }
    }
    return Optional.empty();
  }

  public Optional<ASTCDType> findMatchedTypeTgt(ASTCDInterface astcdInterface) {
    for (Pair<ASTCDInterface, ASTCDType> pair : matchedInterfaces) {
      if (pair.a.equals(astcdInterface)) {
        return Optional.ofNullable(pair.b);
      }
    }
    return Optional.empty();
  }

  public void setMatchedClasses(List<Pair<ASTCDClass, ASTCDType>> matchedClasses) {
    this.matchedClasses = matchedClasses;
  }

  public List<Pair<ASTCDClass, ASTCDType>> getMatchedClasses() {
    return matchedClasses;
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
    } else if (association.getSide().equals(ClassSide.Left)
        && superAssociation.getSide().equals(ClassSide.Right)
        && matchRoleNames(srcAssoc.getRight(), targetAssoc.getLeft())) {
      return srcAssoc.getRight().getCDRole();
    } else if (association.getSide().equals(ClassSide.Right)
        && superAssociation.getSide().equals(ClassSide.Left)
        && matchRoleNames(srcAssoc.getLeft(), targetAssoc.getRight())) {
      return srcAssoc.getLeft().getCDRole();
    } else {
      return srcAssoc.getLeft().getCDRole();
    }
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
      } else {
        return CD4CodeMill.cDBiDirBuilder().build();
      }
    } else {
      if (superAssociation.getDirection().equals(AssocDirection.RightToLeft)) {
        return CD4CodeMill.cDRightToLeftDirBuilder().build();
      } else {
        return CD4CodeMill.cDBiDirBuilder().build();
      }
    }
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
   * @param tgtType class from trgCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  // CHECKED
  public Optional<ASTCDType> allSubclassesHaveIt(AssocStruct association, ASTCDType tgtType) {
    List<ASTCDClass> subClassesTgt = tgtSubMap.get(tgtType);
    List<ASTCDType> subclassesSrc = getSrcTypes(subClassesTgt);
    for (ASTCDType subClass : subclassesSrc) {
      boolean isContained = false;
      for (AssocStruct assocStruct : srcMap.get(subClass)) {
        if (sameAssociationTypeSrcTgt(assocStruct, association)) {
          isContained = true;
          break;
        }
      }
      if (!isContained) {
        return Optional.ofNullable(subClass);
      }
    }
    return Optional.empty();
  }

  /**
   * Check if all matched subclasses in trgCD of a class from srcCD have the same association or a
   * subassociation.
   *
   * @param association association from srcCD.
   * @param srcType type from srcCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  // CHECKED
  public Optional<ASTCDType> allSubClassesHaveItTgt(AssocStruct association, ASTCDType srcType) {
    List<ASTCDClass> subClassesSrc = srcSubMap.get(srcType);
    List<ASTCDType> subClassesTgt = getTgtTypes(subClassesSrc);
    for (ASTCDType subClass : subClassesTgt) {
      boolean isContained = false;
      for (AssocStruct assocStruct : getAllOtherAssocsTgt(subClass)) {
        if (sameAssociationTypeSrcTgt(association, assocStruct)) {
          isContained = true;
          break;
        }
      }
      if (!isContained) {
        return Optional.ofNullable(subClass);
      }
    }
    return Optional.empty();
  }

  /**
   * Similar to the function above, but the now the classes must be target of the association.
   *
   * @param matchedAssocStruc association from srcCD.
   * @param srcType type from srcCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  // CHECKED
  public Optional<ASTCDType> allSubClassesAreTgtSrcTgt(
      AssocStruct matchedAssocStruc, ASTCDType srcType) {
    List<ASTCDClass> subClasses = srcSubMap.get(srcType);
    List<ASTCDType> subClassesTgt = getTgtTypes(subClasses);
    for (ASTCDType subClass : subClassesTgt) {
      boolean contained = false;
      for (AssocStruct assocStruct : getAllOtherAssocsTgt(subClass)) {
        if (sameAssociationTypeSrcTgt(matchedAssocStruc, assocStruct)) {
          contained = true;
          break;
        }
      }
      if (!contained) {
        return Optional.ofNullable(subClass);
      }
    }
    return Optional.empty();
  }

  /**
   * Check if all matched subclasses in srcCD of a class from trgCD are target of the same
   * association.
   *
   * @param tgtAssoc association from trgCD.
   * @param tgtType type from trgCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  public Optional<ASTCDType> allSubClassesAreTargetTgtSrc(AssocStruct tgtAssoc, ASTCDType tgtType) {
    List<ASTCDClass> subClasses = tgtSubMap.get(tgtType);
    List<ASTCDType> subClassesSrc = getSrcTypes(subClasses);
    for (ASTCDType subClass : subClassesSrc) {
      boolean contained = false;
      for (AssocStruct assocStruct : getAllOtherAssocsSrc(subClass)) {
        if (sameAssociationTypeSrcTgt(assocStruct, tgtAssoc)) {
          contained = true;
          break;
        }
      }
      if (!contained) {
        return Optional.ofNullable(subClass);
      }
    }
    return Optional.empty();
  }

  /**
   * Check if the classes are in an inheritance relation. For this, the matched classes in trgCD of
   * the srcClass are compared with isSuper() to the tgtClass.
   *
   * @param subTypeSrc class from srcCD.
   * @param superTypeTgt class from trgCD.
   * @return true if the condition is fulfilled.
   */
  public boolean compareSrcTgt(ASTCDType subTypeSrc, ASTCDType superTypeTgt) {
    Optional<ASTCDType> typeToMatch = findMatchedTypeSrc(superTypeTgt);
    if (typeToMatch.isPresent()) {
      return isSuperOf(
          typeToMatch.get().getSymbol().getInternalQualifiedName(),
          subTypeSrc.getSymbol().getInternalQualifiedName(),
          (ICD4CodeArtifactScope) srcCD.getEnclosingScope());
    }
    return false;
  }

  /**
   * Check if the classes are in an inheritance relation. For this, the matched classes in srcCD of
   * the tgtClass are compared with isSuper() to the srcClass.
   *
   * @param subTypeTgt class from trgCD.
   * @param superTypeSrc class from srcCD.
   * @return true if the condition is fulfilled.
   */
  public boolean compareTgtSrc(ASTCDType subTypeTgt, ASTCDType superTypeSrc) {
    Optional<ASTCDType> typeToMatch = findMatchedTypeTgt(superTypeSrc);
    if (typeToMatch.isPresent()) {
      return isSuperOf(
          typeToMatch.get().getSymbol().getInternalQualifiedName(),
          subTypeTgt.getSymbol().getInternalQualifiedName(),
          (ICD4CodeArtifactScope) tgtCD.getEnclosingScope());
    }
    return false;
  }

  // CHECKED

  /**
   * Check if the srcType has the given association from srcCD.
   *
   * @param association association from srcCD.
   * @param srcType type from srcCD.
   * @return true, if an association has the same association type.
   */
  public boolean classHasAssociationSrcSrc(AssocStruct association, ASTCDType srcType) {
    for (AssocStruct assocStruct : srcMap.get(srcType)) {
      if (sameAssociationType(assocStruct, association)) {
        return true;
      }
    }
    return false;
  }

  // CHECKED

  /**
   * Check if the srcType has the given association from tgtCD.
   *
   * @param tgtStruct association from tgtCD.
   * @param srcType type from srcCD.
   * @return true, if an association has the same association type.
   */
  public boolean classHasAssociationTgtSrc(AssocStruct tgtStruct, ASTCDType srcType) {
    for (AssocStruct assocStruct1 : srcMap.get(srcType)) {
      if (sameAssociationTypeSrcTgt(assocStruct1, tgtStruct)) {
        return true;
      }
    }
    return false;
  }

  // CHECKED

  /**
   * Check if the tgtType has the given association from srcCD.
   *
   * @param assocStruct association from srcCD.
   * @param tgtType type from tgtCD.
   * @return true, if an association has the same association type.
   */
  public boolean classHasAssociationSrcTgt(AssocStruct assocStruct, ASTCDType tgtType) {
    for (AssocStruct assocStruct1 : tgtMap.get(tgtType)) {
      if (sameAssociationTypeSrcTgt(assocStruct1, assocStruct)) {
        return true;
      }
    }
    return false;
  }

  // CHECKED

  /**
   * Check if the srcType is target of the given association from srcCD.
   *
   * @param association association from srcCD.
   * @param srcType type from srcCD.
   * @return true, if an association has the same association type.
   */
  public boolean classIsTarget(AssocStruct association, ASTCDType srcType) {
    for (AssocStruct assocStruct : getAllOtherAssocsSrc(srcType)) {
      if (sameAssociationType(assocStruct, association)) {
        return true;
      }
    }
    return false;
  }

  // CHECKED

  /**
   * Check if the tgtType is target of the given association from srcCD.
   *
   * @param association association from srcCD.
   * @param tgtType type from tgtCD.
   * @return true, if an association has the same association type.
   */
  public boolean classIsTgtSrcTgt(AssocStruct association, ASTCDType tgtType) {
    for (AssocStruct assocStruct : getAllOtherAssocsTgt(tgtType)) {
      if (sameAssociationTypeSrcTgt(association, assocStruct)) {
        return true;
      }
    }
    return false;
  }

  // CHECKED

  /**
   * Check if the srcType is target of the given association from tgtCD.
   *
   * @param association association from tgtCD.
   * @param srcType type from srcCD.
   * @return true, if an association has the same association type.
   */
  public boolean classIsTargetTgtSrc(AssocStruct association, ASTCDType srcType) {
    for (AssocStruct assocStruct : getAllOtherAssocsSrc(srcType)) {
      if (sameAssociationTypeSrcTgt(assocStruct, association)) {
        return true;
      }
    }
    return false;
  }

  // CHECKED
  public List<ASTCDType> getSrcTypes(List<? extends ASTCDType> types) {
    List<ASTCDType> srcTypes = new ArrayList<>();

    for (ASTCDType astcdType : types) {
      if (astcdType instanceof ASTCDClass) {
        Optional<ASTCDClass> matched = findMatchedSrc((ASTCDClass) astcdType);
        matched.ifPresent(srcTypes::add);
      } else {
        Optional<ASTCDType> matched = findMatchedTypeSrc((ASTCDInterface) astcdType);
        matched.ifPresent(srcTypes::add);
      }
    }
    return srcTypes;
  }

  // CHECKED
  public List<ASTCDType> getTgtTypes(List<? extends ASTCDType> types) {
    List<ASTCDType> tgtTypes = new ArrayList<>();
    for (ASTCDType astcdType : types) {
      if (astcdType instanceof ASTCDClass) {
        Optional<ASTCDType> matched = findMatchedClass((ASTCDClass) astcdType);
        matched.ifPresent(tgtTypes::add);
      } else {
        Optional<ASTCDType> matched = findMatchedTypeTgt((ASTCDInterface) astcdType);
        matched.ifPresent(tgtTypes::add);
      }
    }
    return tgtTypes;
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
      if (sameAssociation(assocStruct.getAssociation(), association, srcCD)) {
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
  public boolean sameAssociation(
      ASTCDAssociation association,
      ASTCDAssociation association2,
      ASTCDCompilationUnit astcdCompilationUnit) {
    Pair<ASTCDCardinality, ASTCDCardinality> cardinalities = getCardinality(association);
    Pair<ASTCDCardinality, ASTCDCardinality> cardinalities2 = getCardinality(association2);
    Pair<ASTCDType, ASTCDType> pair = getConnectedTypes(association, astcdCompilationUnit);
    Pair<ASTCDType, ASTCDType> pair2 = getConnectedTypes(association2, astcdCompilationUnit);
    if (pair.a.equals(pair2.a) && pair.b.equals(pair2.b)) {
      return matchRoleNames(association.getLeft(), association2.getLeft())
          && matchRoleNames(association.getRight(), association2.getRight())
          && Syn2SemDiffHelper.getDirection(association)
              .equals(Syn2SemDiffHelper.getDirection(association2))
          && cardToEnum(cardinalities.a).equals(cardToEnum(cardinalities2.a))
          && cardToEnum(cardinalities.b).equals(cardToEnum(cardinalities2.b));
    }
    return false;
  }

  // CHECKED
  /**
   * Check if the target classes of the two associations are in an inheritance relation
   *
   * @param association base association
   * @param superAssociation association from superclass
   * @return true, if they fulfill the condition
   */
  public boolean inInheritanceRelation(
      AssocStruct association, AssocStruct superAssociation, ASTCDCompilationUnit compilationUnit) {
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

  public boolean inheritanceTgt(AssocStruct assocStruct, AssocStruct assocStruct1) {
    if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct1.getSide().equals(ClassSide.Left)) {
      return compareTgtSrc(
          getConnectedTypes(assocStruct1.getAssociation(), tgtCD).b,
          getConnectedTypes(assocStruct.getAssociation(), srcCD).b);
    } else if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct1.getSide().equals(ClassSide.Right)) {
      return compareTgtSrc(
          getConnectedTypes(assocStruct1.getAssociation(), tgtCD).a,
          getConnectedTypes(assocStruct.getAssociation(), srcCD).b);
    } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct1.getSide().equals(ClassSide.Left)) {
      return compareTgtSrc(
          getConnectedTypes(assocStruct1.getAssociation(), tgtCD).b,
          getConnectedTypes(assocStruct.getAssociation(), srcCD).a);
    } else {
      return compareTgtSrc(
          getConnectedTypes(assocStruct1.getAssociation(), tgtCD).a,
          getConnectedTypes(assocStruct.getAssociation(), srcCD).a);
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
  public Pair<ASTCDType, List<ASTCDAttribute>> getAllAttr(ASTCDType astcdClass) {
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

  public Pair<ASTCDType, List<ASTCDAttribute>> getAllAttrTgt(ASTCDType astcdClass) {
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
  /**
   * Check if the srcAssoc has the same type as the srcAssoc1 - direction, role names and the
   * cardinalities of srcAssoc are sub-intervals of the cardinalities of srcAssoc1.
   *
   * @param srcAssocSub association from srcCD.
   * @param srcAssocSuper association from srcCD.
   * @return true, if the condition is fulfilled.
   */
  public boolean sameAssociationType(AssocStruct srcAssocSub, AssocStruct srcAssocSuper) {
    if ((srcAssocSuper.getSide().equals(ClassSide.Left)
        && srcAssocSub.getSide().equals(ClassSide.Left))) {
      return matchRoleNames(
              srcAssocSuper.getAssociation().getLeft(), srcAssocSub.getAssociation().getLeft())
          && matchRoleNames(
              srcAssocSuper.getAssociation().getRight(), srcAssocSub.getAssociation().getRight())
          && matchDirection(srcAssocSuper, new Pair<>(srcAssocSub, srcAssocSub.getSide()))
          && CDInheritanceHelper.isSuperOf(
              srcAssocSuper.getAssociation().getRightQualifiedName().getQName(),
              srcAssocSub.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              srcAssocSuper.getAssociation().getLeftQualifiedName().getQName(),
              srcAssocSub.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(srcAssocSuper.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getRight().getCDCardinality()),
              cardToEnum(srcAssocSuper.getAssociation().getRight().getCDCardinality()));
    } else if ((srcAssocSuper.getSide().equals(ClassSide.Left)
        && srcAssocSub.getSide().equals(ClassSide.Right))) {
      return matchRoleNames(
              srcAssocSuper.getAssociation().getLeft(), srcAssocSub.getAssociation().getRight())
          && matchRoleNames(
              srcAssocSuper.getAssociation().getRight(), srcAssocSub.getAssociation().getLeft())
          && matchDirection(srcAssocSuper, new Pair<>(srcAssocSub, srcAssocSub.getSide()))
          && CDInheritanceHelper.isSuperOf(
              srcAssocSuper.getAssociation().getRightQualifiedName().getQName(),
              srcAssocSub.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              srcAssocSuper.getAssociation().getLeftQualifiedName().getQName(),
              srcAssocSub.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(srcAssocSuper.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getRight().getCDCardinality()),
              cardToEnum(srcAssocSuper.getAssociation().getLeft().getCDCardinality()));
    } else if (srcAssocSuper.getSide().equals(ClassSide.Right)
        && srcAssocSub.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(
              srcAssocSuper.getAssociation().getLeft(), srcAssocSub.getAssociation().getLeft())
          && matchRoleNames(
              srcAssocSuper.getAssociation().getRight(), srcAssocSub.getAssociation().getRight())
          && matchDirection(srcAssocSuper, new Pair<>(srcAssocSub, srcAssocSub.getSide()))
          && CDInheritanceHelper.isSuperOf(
              srcAssocSuper.getAssociation().getLeftQualifiedName().getQName(),
              srcAssocSub.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              srcAssocSuper.getAssociation().getRightQualifiedName().getQName(),
              srcAssocSub.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(srcAssocSuper.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getRight().getCDCardinality()),
              cardToEnum(srcAssocSuper.getAssociation().getRight().getCDCardinality()));
    } else if (srcAssocSuper.getSide().equals(ClassSide.Right)
        && srcAssocSub.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(
              srcAssocSuper.getAssociation().getLeft(), srcAssocSub.getAssociation().getRight())
          && matchRoleNames(
              srcAssocSuper.getAssociation().getRight(), srcAssocSub.getAssociation().getLeft())
          && matchDirection(srcAssocSuper, new Pair<>(srcAssocSub, srcAssocSub.getSide()))
          && CDInheritanceHelper.isSuperOf(
              srcAssocSuper.getAssociation().getLeftQualifiedName().getQName(),
              srcAssocSub.getAssociation().getRightQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && CDInheritanceHelper.isSuperOf(
              srcAssocSuper.getAssociation().getRightQualifiedName().getQName(),
              srcAssocSub.getAssociation().getLeftQualifiedName().getQName(),
              (ICD4CodeArtifactScope) srcCD.getEnclosingScope())
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(srcAssocSuper.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getRight().getCDCardinality()),
              cardToEnum(srcAssocSuper.getAssociation().getLeft().getCDCardinality()));
    }
    return false;
  }

  // CHECKED

  /**
   * Check if the srcAssoc has the same type as the tgtAssoc - direction, role names and the
   * cardinalities of srcAssoc are sub-intervals of the cardinalities of tgtAssoc.
   *
   * @param srcAssocSub association from srcCD.
   * @param tgtAssocSuper association from tgtCD.
   * @return true, if the condition is fulfilled.
   */
  public boolean sameAssociationTypeSrcTgt(AssocStruct srcAssocSub, AssocStruct tgtAssocSuper) {
    if ((srcAssocSub.getSide().equals(ClassSide.Left)
        && tgtAssocSuper.getSide().equals(ClassSide.Left))) {
      return matchRoleNames(
              srcAssocSub.getAssociation().getLeft(), tgtAssocSuper.getAssociation().getLeft())
          && matchRoleNames(
              srcAssocSub.getAssociation().getRight(), tgtAssocSuper.getAssociation().getRight())
          && matchDirection(srcAssocSub, new Pair<>(tgtAssocSuper, tgtAssocSuper.getSide()))
          && compareSrcTgt(
              getConnectedTypes(srcAssocSub.getAssociation(), srcCD).a,
              getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).a)
          && compareSrcTgt(
              getConnectedTypes(srcAssocSub.getAssociation(), srcCD).b,
              getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).b)
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(tgtAssocSuper.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getRight().getCDCardinality()),
              cardToEnum(tgtAssocSuper.getAssociation().getRight().getCDCardinality()));
    } else if ((srcAssocSub.getSide().equals(ClassSide.Left)
        && tgtAssocSuper.getSide().equals(ClassSide.Right))) {
      return matchRoleNames(
              srcAssocSub.getAssociation().getLeft(), tgtAssocSuper.getAssociation().getRight())
          && matchRoleNames(
              srcAssocSub.getAssociation().getRight(), tgtAssocSuper.getAssociation().getLeft())
          && matchDirection(srcAssocSub, new Pair<>(tgtAssocSuper, tgtAssocSuper.getSide()))
          && compareSrcTgt(
              getConnectedTypes(srcAssocSub.getAssociation(), srcCD).a,
              getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).b)
          && compareSrcTgt(
              getConnectedTypes(srcAssocSub.getAssociation(), srcCD).b,
              getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).a)
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(tgtAssocSuper.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getRight().getCDCardinality()),
              cardToEnum(tgtAssocSuper.getAssociation().getLeft().getCDCardinality()));
    } else if (srcAssocSub.getSide().equals(ClassSide.Right)
        && tgtAssocSuper.getSide().equals(ClassSide.Right)) {
      return matchRoleNames(
              srcAssocSub.getAssociation().getLeft(), tgtAssocSuper.getAssociation().getLeft())
          && matchRoleNames(
              srcAssocSub.getAssociation().getRight(), tgtAssocSuper.getAssociation().getRight())
          && matchDirection(srcAssocSub, new Pair<>(tgtAssocSuper, tgtAssocSuper.getSide()))
          && compareSrcTgt(
              getConnectedTypes(srcAssocSub.getAssociation(), srcCD).a,
              getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).a)
          && compareSrcTgt(
              getConnectedTypes(srcAssocSub.getAssociation(), srcCD).b,
              getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).b)
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(tgtAssocSuper.getAssociation().getLeft().getCDCardinality()))
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getRight().getCDCardinality()),
              cardToEnum(tgtAssocSuper.getAssociation().getRight().getCDCardinality()));
    } else if (srcAssocSub.getSide().equals(ClassSide.Right)
        && tgtAssocSuper.getSide().equals(ClassSide.Left)) {
      return matchRoleNames(
              srcAssocSub.getAssociation().getLeft(), tgtAssocSuper.getAssociation().getRight())
          && matchRoleNames(
              srcAssocSub.getAssociation().getRight(), tgtAssocSuper.getAssociation().getLeft())
          && matchDirection(srcAssocSub, new Pair<>(tgtAssocSuper, tgtAssocSuper.getSide()))
          && compareSrcTgt(
              getConnectedTypes(srcAssocSub.getAssociation(), srcCD).a,
              getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).b)
          && compareSrcTgt(
              getConnectedTypes(srcAssocSub.getAssociation(), srcCD).b,
              getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).a)
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getLeft().getCDCardinality()),
              cardToEnum(tgtAssocSuper.getAssociation().getRight().getCDCardinality()))
          && isContainedIn(
              cardToEnum(srcAssocSub.getAssociation().getRight().getCDCardinality()),
              cardToEnum(tgtAssocSuper.getAssociation().getLeft().getCDCardinality()));
    }
    return false;
  }

  // CHECKED

  // CHECKED

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
    return null;
  }

  /**
   * This is the same function from CDDefinition, but it compares the classes based on the qualified
   * name of the class.
   *
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
   * This is the same function from CDDefinition, but it compares the classes based on the qualified
   * name of the class.
   *
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

  // CHECKED
  /**
   * Compute what associations can be used from a class (associations that were from the class and
   * superAssociations). For each class and each possible association we save the direction and also
   * on which side the class is. Two maps are created - srcMap (for srcCD) and trgMap (for trgCD).
   */
  public void setMaps() {
    srcMap = ArrayListMultimap.create();
    tgtMap = ArrayListMultimap.create();
    List<ASTCDType> srcTypes = new ArrayList<>();
    srcTypes.addAll(srcCD.getCDDefinition().getCDClassesList());
    srcTypes.addAll(srcCD.getCDDefinition().getCDInterfacesList());
    for (ASTCDType astcdClass : srcTypes) {
      for (ASTCDAssociation astcdAssociation : getCDAssociationsListForTypeSrc(astcdClass)) {
        Pair<ASTCDType, ASTCDType> pair = getConnectedTypes(astcdAssociation, getSrcCD());
        if (pair.a == null) {
          continue;
        }
        ASTCDAssociation copyAssoc = astcdAssociation.deepClone(); // create virtual association
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
            .equals(
                astcdClass
                    .getSymbol()
                    .getInternalQualifiedName()))) { // if the class is on the left side
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            getSrcMap()
                .put(
                    astcdClass,
                    new AssocStruct(
                        copyAssoc,
                        AssocDirection.BiDirectional,
                        ClassSide.Left,
                        astcdClass,
                        pair.b));
          } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight()) {
            getSrcMap()
                .put(
                    astcdClass,
                    new AssocStruct(
                        copyAssoc, AssocDirection.LeftToRight, ClassSide.Left, astcdClass, pair.b));
          }
        }
        if ((pair.b
            .getSymbol()
            .getInternalQualifiedName()
            .equals(
                astcdClass
                    .getSymbol()
                    .getInternalQualifiedName()))) { // if the class is on the right side
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            getSrcMap()
                .put(
                    astcdClass,
                    new AssocStruct(
                        copyAssoc,
                        AssocDirection.BiDirectional,
                        ClassSide.Right,
                        astcdClass,
                        pair.a));
          } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()) {
            getSrcMap()
                .put(
                    astcdClass,
                    new AssocStruct(
                        copyAssoc,
                        AssocDirection.RightToLeft,
                        ClassSide.Right,
                        astcdClass,
                        pair.a));
          }
        }
      }
    }

    List<ASTCDType> tgtTypes = new ArrayList<>();
    tgtTypes.addAll(tgtCD.getCDDefinition().getCDClassesList());
    tgtTypes.addAll(tgtCD.getCDDefinition().getCDInterfacesList());
    for (ASTCDType astcdClass : tgtTypes) {
      for (ASTCDAssociation astcdAssociation : getCDAssociationsListForTypeTgt(astcdClass)) {
        Pair<ASTCDType, ASTCDType> pair = getConnectedTypes(astcdAssociation, getTgtCD());
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
            getTgtMap()
                .put(
                    astcdClass,
                    new AssocStruct(
                        copyAssoc,
                        AssocDirection.BiDirectional,
                        ClassSide.Left,
                        astcdClass,
                        pair.b));
          } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableRight()) {
            getTgtMap()
                .put(
                    astcdClass,
                    new AssocStruct(
                        copyAssoc, AssocDirection.LeftToRight, ClassSide.Left, astcdClass, pair.b));
          }
        }
        if ((pair.b
            .getSymbol()
            .getInternalQualifiedName()
            .equals(astcdClass.getSymbol().getInternalQualifiedName()))) {
          if (astcdAssociation.getCDAssocDir().isBidirectional()) {
            getTgtMap()
                .put(
                    astcdClass,
                    new AssocStruct(
                        copyAssoc,
                        AssocDirection.BiDirectional,
                        ClassSide.Right,
                        astcdClass,
                        pair.a));
          } else if (astcdAssociation.getCDAssocDir().isDefinitiveNavigableLeft()) {
            getTgtMap()
                .put(
                    astcdClass,
                    new AssocStruct(
                        copyAssoc,
                        AssocDirection.RightToLeft,
                        ClassSide.Right,
                        astcdClass,
                        pair.a));
          }
        }
      }
    }

    for (ASTCDType astcdClass : srcTypes) { // acquire all associations from supertypes
      Set<ASTCDType> superTypes =
          CDDiffUtil.getAllSuperTypes(astcdClass, getSrcCD().getCDDefinition());
      superTypes.remove(astcdClass);
      for (ASTCDType superClass : superTypes) { // getAllSuperTypes CDDffUtils
        for (ASTCDAssociation association : getCDAssociationsListForTypeSrc(superClass)) {
          Pair<ASTCDType, ASTCDType> pair = getConnectedTypes(association, getSrcCD());
          if (pair.a == null) {
            continue;
          }
          if ((pair.a
              .getSymbol()
              .getInternalQualifiedName()
              .equals(
                  superClass
                      .getSymbol()
                      .getInternalQualifiedName()))) { // if the class is on the left side
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
                                astcdClass
                                    .getSymbol()
                                    .getInternalQualifiedName())) // change the left associated type
                                                                  // to the given one
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
                          copyAssoc,
                          AssocDirection.BiDirectional,
                          ClassSide.Left,
                          true,
                          superClass,
                          pair.b,
                          association));
            } else if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
              getSrcMap()
                  .put(
                      astcdClass,
                      new AssocStruct(
                          copyAssoc,
                          AssocDirection.LeftToRight,
                          ClassSide.Left,
                          true,
                          superClass,
                          pair.b,
                          association));
            }
          }
          if ((pair.b
              .getSymbol()
              .getInternalQualifiedName()
              .equals(
                  superClass
                      .getSymbol()
                      .getInternalQualifiedName()))) { // if the class is on the right side
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
                                astcdClass
                                    .getSymbol()
                                    .getInternalQualifiedName())) // change the right associated
                                                                  // type to the given one
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
                          copyAssoc,
                          AssocDirection.BiDirectional,
                          ClassSide.Right,
                          true,
                          superClass,
                          pair.a,
                          association));
            } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
              getSrcMap()
                  .put(
                      astcdClass,
                      new AssocStruct(
                          copyAssoc,
                          AssocDirection.RightToLeft,
                          ClassSide.Right,
                          true,
                          superClass,
                          pair.a,
                          association));
            }
          }
        }
      }
    }

    for (ASTCDType astcdClass : tgtTypes) { // same procedure for tgtCD
      Set<ASTCDType> superClasses =
          CDDiffUtil.getAllSuperTypes(astcdClass, tgtCD.getCDDefinition());
      superClasses.remove(astcdClass);
      for (ASTCDType superClass : superClasses) {
        for (ASTCDAssociation association : getCDAssociationsListForTypeTgt(superClass)) {
          Pair<ASTCDType, ASTCDType> pair = getConnectedTypes(association, getTgtCD());
          if (pair.a == null) {
            continue;
          }
          if ((pair.a
              .getSymbol()
              .getInternalQualifiedName()
              .equals(superClass.getSymbol().getInternalQualifiedName()))) {
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
              copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
            }
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
              getTgtMap()
                  .put(
                      astcdClass,
                      new AssocStruct(
                          copyAssoc,
                          AssocDirection.BiDirectional,
                          ClassSide.Left,
                          true,
                          superClass,
                          pair.b,
                          association));
            } else if (association.getCDAssocDir().isDefinitiveNavigableRight()) {
              getTgtMap()
                  .put(
                      astcdClass,
                      new AssocStruct(
                          copyAssoc,
                          AssocDirection.LeftToRight,
                          ClassSide.Left,
                          true,
                          superClass,
                          pair.b,
                          association));
            }
          }
          if ((pair.b
              .getSymbol()
              .getInternalQualifiedName()
              .equals(superClass.getSymbol().getInternalQualifiedName()))) {
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
                            MCQualifiedNameFacade.createQualifiedName(
                                astcdClass.getSymbol().getInternalQualifiedName()))
                        .build());
            assocForSubClass.setName(" ");
            if (!assocForSubClass.getLeft().isPresentCDCardinality()) {
              assocForSubClass.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
            }
            if (!assocForSubClass.getRight().isPresentCDCardinality()) {
              assocForSubClass.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
            }
            if (assocForSubClass.getCDAssocType().isComposition()) {
              assocForSubClass.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
            }
            if (association.getCDAssocDir().isBidirectional()) {
              getTgtMap()
                  .put(
                      astcdClass,
                      new AssocStruct(
                          assocForSubClass,
                          AssocDirection.BiDirectional,
                          ClassSide.Right,
                          true,
                          superClass,
                          pair.a,
                          association));
            } else if (association.getCDAssocDir().isDefinitiveNavigableLeft()) {
              getTgtMap()
                  .put(
                      astcdClass,
                      new AssocStruct(
                          assocForSubClass,
                          AssocDirection.RightToLeft,
                          ClassSide.Right,
                          true,
                          superClass,
                          pair.a,
                          association));
            }
          }
        }
      }
    }
    for (ASTCDClass astcdClass :
        srcCD
            .getCDDefinition()
            .getCDClassesList()) { // if a class contains an attribute twice - directly and from a
                                   // superclass
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b; // it is not instantiable
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
    for (ASTCDClass astcdClass :
        srcCD
            .getCDDefinition()
            .getCDClassesList()) { // if a class contains an attribute with the same name as a role
                                   // name
      List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b; // it is not instantiable
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

  /** Compute the subtypes for each type in the diagrams. */
  public void setSubMaps() {
    srcSubMap = ArrayListMultimap.create();
    tgtSubMap = ArrayListMultimap.create();
    for (ASTCDClass astcdClass : srcCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDClass subClass : getSpannedInheritance(srcCD, astcdClass)) {
        srcSubMap.put(astcdClass, subClass);
      }
    }

    for (ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()) {
      for (ASTCDClass subClass : getSpannedInheritance(tgtCD, astcdClass)) {
        tgtSubMap.put(astcdClass, subClass);
      }
    }

    List<ASTCDInterface> interfaces = srcCD.getCDDefinition().getCDInterfacesList();
    for (ASTCDInterface astcdInterface : interfaces) {
      for (ASTCDClass astcdClass : srcCD.getCDDefinition().getCDClassesList()) {
        if (CDInheritanceHelper.isSuperOf(
            astcdInterface.getSymbol().getInternalQualifiedName(),
            astcdClass.getSymbol().getInternalQualifiedName(),
            srcCD)) {
          srcSubMap.put(astcdInterface, astcdClass);
        }
      }
    }
    List<ASTCDInterface> interfacesTgt = tgtCD.getCDDefinition().getCDInterfacesList();
    for (ASTCDInterface astcdInterface : interfacesTgt) {
      for (ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()) {
        if (CDInheritanceHelper.isSuperOf(
            astcdInterface.getSymbol().getInternalQualifiedName(),
            astcdClass.getSymbol().getInternalQualifiedName(),
            tgtCD)) {
          tgtSubMap.put(astcdInterface, astcdClass);
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
            && getConnectedTypes(assocStruct.getAssociation(), srcCD)
                .b
                .getName()
                .equals(roleName1)) {
          return true;
        }
      } else {
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(roleName)
            && getConnectedTypes(assocStruct.getAssociation(), srcCD)
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
    for (AssocStruct assocStruct : tgtMap.get(astcdClass)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(roleName)
            && getConnectedTypes(assocStruct.getAssociation(), tgtCD)
                .b
                .getName()
                .equals(roleName1)) {
          return true;
        }
      } else {
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(roleName)
            && getConnectedTypes(assocStruct.getAssociation(), tgtCD)
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
   * Get the classes that are connected with the association. The function returns null if the
   * associated objects aren't classes.
   *
   * @param association association.
   * @param compilationUnit diagram.
   * @return pair of classes that are connected with the association.
   */
  public static Pair<ASTCDType, ASTCDType> getConnectedTypes(
      ASTCDAssociation association, ASTCDCompilationUnit compilationUnit) {
    Optional<CDTypeSymbol> typeLeft =
        compilationUnit
            .getEnclosingScope()
            .resolveCDTypeDown(association.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> typeRight =
        compilationUnit
            .getEnclosingScope()
            .resolveCDTypeDown(association.getRightQualifiedName().getQName());
    if (typeLeft.isPresent() && typeRight.isPresent()) {
      return new Pair<>(typeLeft.get().getAstNode(), typeRight.get().getAstNode());
    }
    Log.error("Could not resolve types of :" + CD4CodeMill.prettyPrint(association, false));
    return new Pair<>(null, null);
  }

  // CHECKED
  /**
   * Compute the types that extend a given class.
   *
   * @param compilationUnit diagram.
   * @param astcdClass root class for spanned inheritance.
   * @return list of extending types.
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
  /**
   * Check if the first cardinality is contained in the second cardinality.
   *
   * @param cardinality1 first cardinality.
   * @param cardinality2 second cardinality.
   * @return true if first cardinality is contained in the second one.
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

  /**
   * Get the minimal non-abstract subclass(strict subclass) of a given type. The minimal subclass is
   * the subclass with the least amount of attributes and associations(ingoing and outgoing).
   *
   * @param astcdType type.
   * @return minimal subclass.
   */
  public Optional<ASTCDClass> minSubClass(ASTCDType astcdType) {

    List<ASTCDClass> subClasses = srcSubMap.get(astcdType);

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

    return Optional.ofNullable(subclassWithLowestCount);
  }

  public Optional<ASTCDClass> minSubClassTgt(ASTCDType baseClass) {
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

    return Optional.ofNullable(subclassWithLowestCount);
  }

  // CHECKED
  private int getAssociationCount(ASTCDType astcdClass) {
    int count = 0;
    for (AssocStruct assocStruct : srcMap.get(astcdClass)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if ((assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne()
                || assocStruct.getAssociation().getRight().getCDCardinality().isOne())
            && !getConnectedTypes(assocStruct.getAssociation(), srcCD)
                .b
                .getSymbol()
                .getInternalQualifiedName()
                .equals(
                    getConnectedTypes(assocStruct.getAssociation(), srcCD)
                        .a
                        .getSymbol()
                        .getInternalQualifiedName())) {
          count++;
        }
      } else {
        if ((assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
                || assocStruct.getAssociation().getLeft().getCDCardinality().isOne())
            && !getConnectedTypes(assocStruct.getAssociation(), srcCD)
                .b
                .getSymbol()
                .getInternalQualifiedName()
                .equals(
                    getConnectedTypes(assocStruct.getAssociation(), srcCD)
                        .a
                        .getSymbol()
                        .getInternalQualifiedName())) {
          count++;
        }
      }
    }
    return count;
  }

  private int getAssociationCountTgt(ASTCDType astcdClass) {
    int count = 0;
    for (AssocStruct assocStruct : tgtMap.get(astcdClass)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if ((assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne()
                || assocStruct.getAssociation().getRight().getCDCardinality().isOne())
            && !getConnectedTypes(assocStruct.getAssociation(), tgtCD)
                .b
                .getSymbol()
                .getInternalQualifiedName()
                .equals(
                    getConnectedTypes(assocStruct.getAssociation(), tgtCD)
                        .a
                        .getSymbol()
                        .getInternalQualifiedName())) {
          count++;
        }
      } else {
        if ((assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
                || assocStruct.getAssociation().getLeft().getCDCardinality().isOne())
            && !getConnectedTypes(assocStruct.getAssociation(), tgtCD)
                .b
                .getSymbol()
                .getInternalQualifiedName()
                .equals(
                    getConnectedTypes(assocStruct.getAssociation(), tgtCD)
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

  /**
   * Compute the String for <<instanceOf>> stereotype.
   *
   * @param astcdClass class from srcCD.
   * @return List of types as Strings.
   */
  public List<String> getSuperTypes(ASTCDClass astcdClass) {
    List<ASTCDType> typeList =
        new ArrayList<>(CDDiffUtil.getAllSuperTypes(astcdClass, srcCD.getCDDefinition()));
    List<String> typesString = new ArrayList<>();
    for (int i = typeList.size() - 1; i >= 0; i--) {
      String type = typeList.get(i).getSymbol().getInternalQualifiedName();
      typesString.add(type);
    }
    return typesString;
  }

  // CHECKED

  /**
   * Create all attributes for a class.
   *
   * @param astcdClass class from srcCD.
   * @param pair pair if the difference is in an added constant
   * @return List of attributes for object diagram.
   */
  public List<ASTODAttribute> getAttributesOD(
      ASTCDClass astcdClass, Pair<ASTCDAttribute, String> pair) {
    List<ASTCDAttribute> attributes = getAllAttr(astcdClass).b;
    List<ASTODAttribute> odAttributes = new ArrayList<>();
    if (pair != null) {
      odAttributes.add(
          ODBuilder.buildAttr(pair.a.getMCType().printType(), pair.a.getName(), pair.b));
      attributes.remove(pair.a);
    }
    for (ASTCDAttribute attribute : attributes) {
      Pair<Boolean, String> attIsEnum = attIsEnum(attribute);
      if (attIsEnum.a) {
        odAttributes.add(
            ODBuilder.buildAttr(
                attribute.getMCType().printType(), attribute.getName(), attIsEnum.b));
      } else {
        odAttributes.add(
            ODBuilder.buildAttr(attribute.getMCType().printType(), attribute.getName()));
      }
    }
    return odAttributes;
  }

  // CHECKED

  /**
   * Get a class from a diagram based on its name.
   *
   * @param compilationUnit diagram.
   * @param className name of the class.
   * @return found class.
   */
  public static ASTCDClass getCDClass(ASTCDCompilationUnit compilationUnit, String className) {
    for (ASTCDClass astcdClass : compilationUnit.getCDDefinition().getCDClassesList()) {
      if (astcdClass.getSymbol().getInternalQualifiedName().equals(className)) {
        return astcdClass;
      }
    }
    return null; // the function is only used in the generator, so this statement is never reached
  }

  // CHECKED

  /**
   * Check if the directions match in reverse.
   *
   * @param srcStruct association.
   * @param tgtStruct association(we don't look at the side in the struct - the association could be
   *     used as target and not as source).
   * @return true if the directions match.
   */
  public boolean matchDirectionInReverse(
      AssocStruct srcStruct, Pair<AssocStruct, ClassSide> tgtStruct) {
    if (((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Right))
            || (srcStruct.getSide().equals(ClassSide.Right) && tgtStruct.b.equals(ClassSide.Left)))
        && srcStruct.getDirection() == tgtStruct.a.getDirection()) {
      return true;
    } else
      return ((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Left))
              || (srcStruct.getSide().equals(ClassSide.Right)
                  && tgtStruct.b.equals(ClassSide.Right)))
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
    } else
      return ((srcStruct.getSide().equals(ClassSide.Left) && tgtStruct.b.equals(ClassSide.Right))
              || (srcStruct.getSide().equals(ClassSide.Right)
                  && tgtStruct.b.equals(ClassSide.Left)))
          && ((srcStruct.getDirection().equals(AssocDirection.BiDirectional)
                  && tgtStruct.a.getDirection().equals(AssocDirection.BiDirectional))
              || (srcStruct.getDirection().equals(AssocDirection.LeftToRight)
                  && tgtStruct.a.getDirection().equals(AssocDirection.RightToLeft))
              || (srcStruct.getDirection().equals(AssocDirection.RightToLeft)
                  && tgtStruct.a.getDirection().equals(AssocDirection.LeftToRight)));
  }

  // CHECKED
  public boolean sameAssocStruct(AssocStruct srcStruct, AssocStruct tgtStruct) {
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
        && matchRoleNames(struct.getAssociation().getRight(), tgtStruct.getAssociation().getLeft());
  }

  // CHECKED
  public int getClassSize(ASTCDClass astcdClass) {
    int attributeCount = getAllAttr(astcdClass).b.size();
    int associationCount = getAssociationCount(astcdClass);
    int otherAssocsCount = getOtherAssocFromSuper(astcdClass).size();
    return attributeCount + associationCount + otherAssocsCount;
  }

  /**
   * Compare associations. If for a pair of associations one of them is a subassociation and a loop
   * association, the other one is marked so that it won't be look at for generation of object
   * diagrams.
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

  public boolean sameAssociationSpec(ASTCDAssociation association, ASTCDAssociation association2) {
    Pair<ASTCDCardinality, ASTCDCardinality> cardinalities = getCardinality(association2);
    Pair<ASTCDType, ASTCDType> conncected1 = getConnectedTypes(association, srcCD);
    Pair<ASTCDType, ASTCDType> conncected2 = getConnectedTypes(association2, srcCD);
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
    Pair<ASTCDType, ASTCDType> pair = getConnectedTypes(assocStruct.getAssociation(), srcCD);
    return pair.a.equals(pair.b);
  }

  // CHECKED

  /**
   * Delete all associations that use the given type as target.
   *
   * @param astcdType type from srcCD.
   */
  public void deleteOtherSideSrc(ASTCDType astcdType) {
    List<Pair<ASTCDType, List<AssocStruct>>> toDelete = new ArrayList<>();
    for (ASTCDType toCheck : srcMap.keySet()) {
      if (toCheck != astcdType) {
        List<AssocStruct> toDeleteStructs = new ArrayList<>();
        for (AssocStruct struct : srcMap.get(toCheck)) {
          if (struct.getSide().equals(ClassSide.Left)
              && getConnectedTypes(struct.getAssociation(), srcCD).b != null
              && getConnectedTypes(struct.getAssociation(), srcCD).b.equals(astcdType)) {
            toDeleteStructs.add(struct);
          } else if (struct.getSide().equals(ClassSide.Right)
              && getConnectedTypes(struct.getAssociation(), srcCD).a != null
              && getConnectedTypes(struct.getAssociation(), srcCD).a.equals(astcdType)) {
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

  /**
   * Delete all associations that use the given type as target.
   *
   * @param astcdClass type from tgtCD.
   */
  public void deleteOtherSideTgt(ASTCDType astcdClass) {
    List<Pair<ASTCDType, List<AssocStruct>>> toDelete = new ArrayList<>();
    for (ASTCDType toCheck : tgtMap.keySet()) {
      if (toCheck != astcdClass) {
        List<AssocStruct> toDeleteStructs = new ArrayList<>();
        for (AssocStruct struct : tgtMap.get(toCheck)) {
          if (struct.getSide().equals(ClassSide.Left)
              && getConnectedTypes(struct.getAssociation(), tgtCD).b != null
              && getConnectedTypes(struct.getAssociation(), tgtCD).b.equals(astcdClass)) {
            toDeleteStructs.add(struct);
          } else if (struct.getSide().equals(ClassSide.Right)
              && getConnectedTypes(struct.getAssociation(), tgtCD).a != null
              && getConnectedTypes(struct.getAssociation(), tgtCD).a.equals(astcdClass)) {
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

  /**
   * Delete the association from the other associated type.
   *
   * @param assocStruct association from srcCD.
   */
  public void deleteAssocOtherSideSrc(AssocStruct assocStruct) {
    if (assocStruct.getDirection().equals(AssocDirection.BiDirectional)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        for (AssocStruct struct :
            srcMap.get(getConnectedTypes(assocStruct.getAssociation(), srcCD).b)) {
          if ((struct.getUsedAs() == null || struct.getUsedAs().equals(AssocType.SUPER))
              && struct.getSide().equals(ClassSide.Right)
              && getConnectedTypes(struct.getAssociation(), srcCD)
                  .a
                  .equals(getConnectedTypes(assocStruct.getAssociation(), srcCD).a)
              && sameAssocStruct(assocStruct, struct)) {
            srcMap.get(getConnectedTypes(assocStruct.getAssociation(), srcCD).b).remove(struct);
            break;
          }
        }
      } else {
        for (AssocStruct struct :
            srcMap.get(getConnectedTypes(assocStruct.getAssociation(), srcCD).a)) {
          if ((struct.getUsedAs() == null || struct.getUsedAs().equals(AssocType.SUPER))
              && struct.getSide().equals(ClassSide.Left)
              && getConnectedTypes(struct.getAssociation(), srcCD)
                  .b
                  .equals(getConnectedTypes(assocStruct.getAssociation(), srcCD).b)
              && sameAssocStruct(assocStruct, struct)) {
            srcMap.get(getConnectedTypes(assocStruct.getAssociation(), srcCD).a).remove(struct);
            break;
          }
        }
      }
    }
  }

  // CHECKED
  public void deleteAssocOtherSideTgt(AssocStruct assocStruct) {
    if (assocStruct.getDirection().equals(AssocDirection.BiDirectional)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        for (AssocStruct struct :
            tgtMap.get(getConnectedTypes(assocStruct.getAssociation(), tgtCD).b)) {
          if ((struct.getUsedAs() == null || struct.getUsedAs().equals(AssocType.SUPER))
              && struct.getSide().equals(ClassSide.Right)
              && getConnectedTypes(struct.getAssociation(), tgtCD)
                  .a
                  .equals(getConnectedTypes(assocStruct.getAssociation(), tgtCD).a)
              && sameAssocStruct(assocStruct, struct)) {
            tgtMap.get(getConnectedTypes(assocStruct.getAssociation(), tgtCD).b).remove(struct);
            break;
          }
        }
      } else {
        for (AssocStruct struct :
            tgtMap.get(getConnectedTypes(assocStruct.getAssociation(), tgtCD).a)) {
          if ((struct.getUsedAs() == null || struct.getUsedAs().equals(AssocType.SUPER))
              && struct.getSide().equals(ClassSide.Left)
              && getConnectedTypes(struct.getAssociation(), tgtCD)
                  .b
                  .equals(getConnectedTypes(assocStruct.getAssociation(), tgtCD).b)
              && sameAssocStruct(assocStruct, struct)) {
            tgtMap.get(getConnectedTypes(assocStruct.getAssociation(), tgtCD).a).remove(struct);
            break;
          }
        }
      }
    }
  }

  // CHECKED

  /**
   * This function is used when the object diagrams are derived under simple semantics. The function
   * changes the stereotype to contain only the base type of the object without the superclasses.
   *
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
          ((ASTODObject) element)
              .setMCObjectType(
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

  /**
   * Get the cardinalities of an association.
   *
   * @param association association.
   * @return ordered pair of cardinalities.
   */
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
   * Get the matching AssocStructs for a given pair. The id 'unmodifiedAssoc' is used to identify
   * the association in the map.
   *
   * @param srcAssoc source association.
   * @param tgtAssoc target association.
   * @param reversed true if the associations are reversed.
   * @return pair of AssocStructs for both associations.
   */
  public Pair<AssocStruct, AssocStruct> getStructsForAssocDiff(
      ASTCDAssociation srcAssoc, ASTCDAssociation tgtAssoc, boolean reversed) {
    Pair<ASTCDType, ASTCDType> srcCLasses = getConnectedTypes(srcAssoc, srcCD);
    Pair<ASTCDType, ASTCDType> tgtCLasses = getConnectedTypes(tgtAssoc, tgtCD);
    AssocStruct srcStruct = null;
    AssocStruct tgtStruct = null;
    if (getAssocStructByUnmod(srcCLasses.a, srcAssoc).isPresent()) {
      srcStruct = getAssocStructByUnmod(srcCLasses.a, srcAssoc).get();
    } else if (getAssocStructByUnmod(srcCLasses.b, srcAssoc).isPresent()) {
      srcStruct = getAssocStructByUnmod(srcCLasses.b, srcAssoc).get();
    }

    if (!reversed && getAssocStructByUnmodTgt(tgtCLasses.a, tgtAssoc).isPresent()) {
      tgtStruct = getAssocStructByUnmodTgt(tgtCLasses.a, tgtAssoc).get();
    } else if (getAssocStructByUnmodTgt(tgtCLasses.b, tgtAssoc).isPresent()) {
      tgtStruct = getAssocStructByUnmodTgt(tgtCLasses.b, tgtAssoc).get();
    }
    return new Pair<>(srcStruct, tgtStruct);
  }

  /**
   * Get the matching AssocStructs for a given association in srcCD.
   *
   * @param astcdType associated type.
   * @param association unmodified association from srcCD.
   * @return AssocStruct for the association.
   */
  public Optional<AssocStruct> getAssocStructByUnmod(
      ASTCDType astcdType, ASTCDAssociation association) {
    for (AssocStruct struct : srcMap.get(astcdType)) {
      if (sameAssociation(struct.getUnmodifiedAssoc(), association, srcCD)) {
        return Optional.of(struct);
      }
    }
    return Optional.empty();
  }

  /**
   * Get the matching AssocStructs for a given association in tgtCD.
   *
   * @param astcdType associated type.
   * @param association unmodified association from tgtCD.
   * @return AssocStruct for the association.
   */
  public Optional<AssocStruct> getAssocStructByUnmodTgt(
      ASTCDType astcdType, ASTCDAssociation association) {
    for (AssocStruct struct : tgtMap.get(astcdType)) {
      if (sameAssociation(struct.getUnmodifiedAssoc(), association, tgtCD)) {
        return Optional.of(struct);
      }
    }
    return Optional.empty();
  }

  /**
   * Check if an attribute from srcCD is an enum.
   *
   * @param attribute attribute to check.
   * @return pair of boolean and random constant.
   */
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
   *
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
          notInstClassesSrc.add(getConnectedTypes(assocStruct.getAssociation(), srcCD).b);
          srcMap.removeAll(getConnectedTypes(assocStruct.getAssociation(), srcCD).b);
        } else {
          deleteAssocOtherSideSrc(assocStruct);
        }
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
          && CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(role.getName())) {
        if (isOtherSideNeeded(assocStruct)) {
          notInstClassesSrc.add(getConnectedTypes(assocStruct.getAssociation(), srcCD).a);
          srcMap.removeAll(getConnectedTypes(assocStruct.getAssociation(), srcCD).a);
        } else {
          deleteAssocOtherSideSrc(assocStruct);
        }
        iterator.remove();
      }
    }
  }

  public boolean isOtherSideNeeded(AssocStruct assocStruct) {
    if (assocStruct.getSide().equals(ClassSide.Left)
        && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
            || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())) {
      return true;
    }
    if (assocStruct.getSide().equals(ClassSide.Right)
        && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())) {
      return true;
    }
    return false;
  }

  // CHECKED

  /**
   * Check for all compositions if a subcomponent cannot be instantiated. If this is the case, the
   * composite class cannot be instantiated either.
   */
  public void deleteCompositions() {
    for (ASTCDType astcdType : srcMap.keySet()) {
      for (ASTCDAssociation association : getCDAssociationsListForTypeSrc(astcdType)) {
        Pair<ASTCDType, ASTCDType> pair = Syn2SemDiffHelper.getConnectedTypes(association, srcCD);
        Optional<AssocStruct> assocStruct = getAssocStructByUnmod(pair.a, association);
        if (association.getCDAssocType().isComposition() && assocStruct.isPresent()) {
          if (getNotInstClassesSrc().contains(pair.b)
              || (pair.b.getModifier().isAbstract() && minSubClass(pair.b).isEmpty())) {
            updateSrc(pair.a);
            for (ASTCDType subClass : getSrcSubMap().get(pair.a)) {
              getSrcMap().removeAll(subClass);
              updateSrc(subClass);
            }
          }
        }
      }
    }

    for (ASTCDType astcdType : tgtMap.keySet()) {
      for (ASTCDAssociation association : getCDAssociationsListForTypeTgt(astcdType)) {
        Pair<ASTCDType, ASTCDType> pair = Syn2SemDiffHelper.getConnectedTypes(association, tgtCD);
        Optional<AssocStruct> assocStruct = getAssocStructByUnmodTgt(pair.a, association);
        if (association.getCDAssocType().isComposition() && assocStruct.isPresent()) {
          if (getNotInstClassesTgt().contains(pair.b)
              || (pair.b.getModifier().isAbstract() && minSubClassTgt(pair.b).isEmpty())) {
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
  public void deleteAssocsFromTgt(ASTCDType astcdClass, ASTCDRole role) {
    Iterator<AssocStruct> iterator = getTgtMap().get(astcdClass).iterator();
    while (iterator.hasNext()) {
      AssocStruct assocStruct = iterator.next();
      if (assocStruct.getSide().equals(ClassSide.Left)
          && CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(role.getName())) {
        if (isOtherSideNeeded(assocStruct)) {
          notInstClassesTgt.add(getConnectedTypes(assocStruct.getAssociation(), tgtCD).b);
          tgtMap.removeAll(getConnectedTypes(assocStruct.getAssociation(), tgtCD).b);
        } else {
          deleteAssocOtherSideTgt(assocStruct);
        }
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
          && CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(role.getName())) {
        if (isOtherSideNeeded(assocStruct)) {
          notInstClassesTgt.add(getConnectedTypes(assocStruct.getAssociation(), tgtCD).a);
          tgtMap.removeAll(getConnectedTypes(assocStruct.getAssociation(), tgtCD).a);
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
      Optional<AssocStruct> matched = getAssocStructByUnmodTgt(astcdClass, association);
      matched.ifPresent(list::add);
    }
    return list;
  }

  // CHECKED

  /**
   * Search for an association in srcCD that can't be matched with an association in tgtCD.
   *
   * @param assocStructs list of associations without those from addedAssocs.
   * @param tgtType type from tgtCD.
   * @return class from srcCD that has such association.
   */
  public List<Pair<ASTCDClass, AssocStruct>> srcAssocsExist(
      List<AssocStruct> assocStructs, ASTCDType tgtType) {
    List<Pair<ASTCDClass, AssocStruct>> list = new ArrayList<>();
    for (AssocStruct assocStruct : assocStructs) {
      boolean foundMatch = false;
      for (AssocStruct assocStructTgt : tgtMap.get(tgtType)) {
        if (sameAssociationTypeSrcTgt(assocStruct, assocStructTgt)) { // the given pair is a match
          foundMatch = true;
          break;
        } else if (matcher.isMatched(
            assocStruct.getAssociation(),
            assocStructTgt.getAssociation())) { // the given pair is a CDAssocDiff
          foundMatch = true;
          if (!containedInList(
              assocStruct, assocStructTgt)) { // the CDAssocDiff is not already in the list
            diffs.add(
                new CDAssocDiff(
                    assocStruct.getUnmodifiedAssoc(),
                    assocStructTgt.getUnmodifiedAssoc(),
                    srcCD,
                    tgtCD,
                    this));
          }
          break;
        }
      }
      if (!foundMatch) { // if a match for the assocStruct from src is not found in tgt - create a
                         // diff-witness
        ASTCDClass classToUse = null;
        ASTCDType type = findMatchedTypeSrc(tgtType).get();
        if (type instanceof ASTCDClass && !type.getModifier().isAbstract()) {
          classToUse = (ASTCDClass) type;
        } else {
          for (ASTCDClass subClass :
              srcSubMap.get(
                  findMatchedTypeSrc(tgtType)
                      .get())) { // search for a class that doesn't have the association in tgtCD.
            Optional<ASTCDType> subTgt = findMatchedTypeTgt(subClass);
            if (subTgt.isPresent() && !classHasAssociationSrcTgt(assocStruct, subTgt.get())) {
              classToUse = subClass;
              break;
            }
          }
        }
        if (classToUse != null) {
          for (AssocStruct subAssoc : srcMap.get(classToUse)) {
            if (sameAssociationType(subAssoc, assocStruct)) {
              list.add(new Pair<>(classToUse, subAssoc));
              break;
            }
          }
        }
      }
    }
    return list;
  }

  public boolean containedInList(AssocStruct srcStruct, AssocStruct tgtAssocStruct) {
    return diffs.stream()
        .anyMatch(
            obj ->
                obj.getSrcElem() == srcStruct.getUnmodifiedAssoc()
                    && obj.getTgtElem() == tgtAssocStruct.getUnmodifiedAssoc());
  }

  /**
   * Search for an association in tgtCD that can't be matched with an association in srcCD.
   *
   * @param assocStructs list of associations without those from deletedAssocs.
   * @param srcType type from srcCD.
   * @return class from srcCD that doesn't have this association.
   */
  public List<Pair<ASTCDClass, AssocStruct>> tgtAssocsExist(
      List<AssocStruct> assocStructs, ASTCDType srcType) {
    List<Pair<ASTCDClass, AssocStruct>> list = new ArrayList<>();
    for (AssocStruct assocStruct : assocStructs) {
      boolean foundMatch = false;
      for (AssocStruct assocStructSrc : srcMap.get(srcType)) { // 1:1 as srcAssocsExist
        if (sameAssociationTypeSrcTgt(assocStructSrc, assocStruct)) {
          foundMatch = true;

          break;
        } else if (matcher.isMatched(
            assocStructSrc.getAssociation(), assocStruct.getAssociation())) {
          foundMatch = true;
          if (!containedInList(assocStructSrc, assocStruct)) {
            diffs.add(
                new CDAssocDiff(
                    assocStructSrc.getUnmodifiedAssoc(),
                    assocStruct.getUnmodifiedAssoc(),
                    srcCD,
                    tgtCD,
                    this));
          }
          break;
        }
      }
      if (!foundMatch) {
        ASTCDClass classToUse = null;
        if (srcType instanceof ASTCDClass && !srcType.getModifier().isAbstract()) {
          classToUse = (ASTCDClass) srcType;
        } else {
          for (ASTCDClass subClass : tgtSubMap.get(findMatchedTypeTgt(srcType).get())) {
            Optional<ASTCDType> subSrc = findMatchedTypeSrc(subClass);
            if (subSrc.isPresent()
                && subSrc.get() instanceof ASTCDClass
                && !classHasAssociationTgtSrc(assocStruct, subSrc.get())) {
              classToUse = (ASTCDClass) subSrc.get();
              break;
            }
          }
        }
        if (classToUse != null) {
          list.add(new Pair<>(classToUse, assocStruct));
        }
      }
    }
    return list;
  }

  // CHECKED
  public List<AssocStruct> addedAssocsForClass(ASTCDType astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs) {
      Optional<AssocStruct> matched = getAssocStructByUnmod(astcdClass, association);
      matched.ifPresent(list::add);
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
    Optional<ASTCDClass> left = Optional.empty();
    Optional<ASTCDClass> right = Optional.empty();
    if (pair.a.getModifier().isAbstract() || pair.a instanceof ASTCDInterface) {
      left = minSubClass(pair.a);
    }
    if (pair.b.getModifier().isAbstract() || pair.b instanceof ASTCDInterface) {
      right = minSubClass(pair.b);
    }
    if (left.isPresent() && right.isPresent()) {
      return new Pair<>(left.get(), right.get());
    }
    return null;
  }

  /**
   * Check what the changes to the stereotype of a class are.
   *
   * @param newClass class in srcCD.
   * @param oldClass class in tgtCD.
   * @return pair of booleans, first one is true if the class is not abstract anymore, second one is
   *     true if the class is not a singleton anymore.
   */
  public Pair<Boolean, Boolean> stereotypeChange(ASTCDClass newClass, ASTCDClass oldClass) {
    boolean abstractChange = false;
    boolean singletonChange = false;
    if (!newClass.getModifier().isAbstract() && oldClass.getModifier().isAbstract()) {
      abstractChange = true;
    }
    if ((!newClass.getModifier().isPresentStereotype()
            || !newClass.getModifier().getStereotype().contains("singleton"))
        && (oldClass.getModifier().isPresentStereotype()
            && oldClass.getModifier().getStereotype().contains("singleton"))) {
      singletonChange = true;
    }
    return new Pair<>(abstractChange, singletonChange);
  }

  public Optional<ASTCDClass> getClassForTypeSrc(ASTCDType astcdType) {
    if (astcdType instanceof ASTCDClass) {
      return Optional.of((ASTCDClass) astcdType);
    } else {
      return minSubClass(astcdType);
    }
  }

  /**
   * Sort the associations for a given type so that pairs aren't duplicated.
   *
   * @param astcdType type to sort.
   * @param map map search in.
   * @param compilationUnit compilation unit.
   * @return data structure with two list: associations that are in an inheritance relation and
   *     associations aren't in an inheritance relation.
   */
  public OverlappingAssocsDirect computeDirectForType(
      ASTCDType astcdType,
      ArrayListMultimap<ASTCDType, AssocStruct> map,
      ASTCDCompilationUnit compilationUnit) {
    Set<Pair<AssocStruct, AssocStruct>> directOverlappingAssocs = new HashSet<>();
    Set<Pair<AssocStruct, AssocStruct>> directOverlappingNoRelation = new HashSet<>();
    for (AssocStruct assocStruct : map.get(astcdType)) {
      for (AssocStruct assocStruct1 : map.get(astcdType)) {
        if (assocStruct != assocStruct1) {
          if (inInheritanceRelation(assocStruct, assocStruct1, compilationUnit)
              && directOverlappingAssocs.stream()
                  .noneMatch(pair -> pair.a == assocStruct1 && pair.b == assocStruct)) {
            AssocStruct sub = assocStruct;
            AssocStruct sup = assocStruct1;
            Pair<ASTCDType, ASTCDType> pair =
                getConnectedTypes(assocStruct.getAssociation(), compilationUnit);
            Pair<ASTCDType, ASTCDType> pair1 =
                getConnectedTypes(assocStruct1.getAssociation(), compilationUnit);
            if (pair.a == null || pair.b == null || pair1.a == null || pair1.b == null) {
              continue;
            }
            if ((assocStruct.getUsedAs() != null && assocStruct.getUsedAs().equals(AssocType.SUPER))
                || (assocStruct1.getUsedAs() != null
                    && assocStruct1.getUsedAs().equals(AssocType.SUB))) {
              if (assocStruct.getSide().equals(ClassSide.Left)
                  && assocStruct1.getSide().equals(ClassSide.Left)
                  && pair.a.equals(pair1.a)
                  && pair.b.equals(pair1.b)) {
                sub = assocStruct1;
                sup = assocStruct;
                setOtherSideUsage(sub, AssocType.SUB, compilationUnit);
                setOtherSideUsage(sup, AssocType.SUPER, compilationUnit);
              } else if (assocStruct.getSide().equals(ClassSide.Right)
                  && assocStruct1.getSide().equals(ClassSide.Right)
                  && pair.a.equals(pair1.a)
                  && pair.b.equals(pair1.b)) {
                sub = assocStruct1;
                sup = assocStruct;
                setOtherSideUsage(sub, AssocType.SUB, compilationUnit);
                setOtherSideUsage(sup, AssocType.SUPER, compilationUnit);
              } else if (assocStruct.getSide().equals(ClassSide.Left)
                  && assocStruct1.getSide().equals(ClassSide.Right)
                  && pair.a.equals(pair1.b)
                  && pair.b.equals(pair1.a)) {
                sub = assocStruct1;
                sup = assocStruct;
                setOtherSideUsage(sub, AssocType.SUB, compilationUnit);
                setOtherSideUsage(sup, AssocType.SUPER, compilationUnit);
              } else if (assocStruct.getSide().equals(ClassSide.Right)
                  && assocStruct1.getSide().equals(ClassSide.Left)
                  && pair.a.equals(pair1.b)
                  && pair.b.equals(pair1.a)) {
                sub = assocStruct1;
                sup = assocStruct;
                setOtherSideUsage(sub, AssocType.SUB, compilationUnit);
                setOtherSideUsage(sup, AssocType.SUPER, compilationUnit);
              }
            }
            if (assocStruct.getUsedAs() == null) {
              sub.setUsedAs(AssocType.SUB);
            }
            if (assocStruct1.getUsedAs() == null) {
              sup.setUsedAs(AssocType.SUPER);
            }
            directOverlappingAssocs.add(new Pair<>(sub, sup));
          }
        }
      }
    }
    return new OverlappingAssocsDirect(directOverlappingAssocs, directOverlappingNoRelation);
  }

  /**
   * Get the pairs of duplicated associations for a given type
   *
   * @param astcdType type to search for.
   * @param map map to search in.
   * @param compilationUnit compilation unit.
   * @return list of pairs of duplicated associations.
   */
  public List<Pair<AssocStruct, AssocStruct>> getPairsForType(
      ASTCDType astcdType,
      ArrayListMultimap<ASTCDType, AssocStruct> map,
      ASTCDCompilationUnit compilationUnit) {
    List<Pair<AssocStruct, AssocStruct>> list = new ArrayList<>();
    for (AssocStruct assocStruct : map.get(astcdType)) {
      for (AssocStruct assocStruct1 : map.get(astcdType)) {
        if (assocStruct != assocStruct1) {
          Pair<ASTCDType, ASTCDType> pair1 =
              getConnectedTypes(assocStruct.getAssociation(), compilationUnit);
          Pair<ASTCDType, ASTCDType> pair2 =
              getConnectedTypes(assocStruct1.getAssociation(), compilationUnit);
          if (inInheritanceRelation(assocStruct, assocStruct1, compilationUnit)
              && list.stream().noneMatch(pair -> pair.a == assocStruct1 && pair.b == assocStruct)
              && pair1.a != null
              && pair1.b != null
              && pair2.a != null
              && pair2.b != null
              && ((pair1.a.equals(pair2.a) && pair1.b.equals(pair2.b))
                  || (pair1.a.equals(pair2.b) && pair1.b.equals(pair2.a)))) {
            list.add(new Pair<>(assocStruct, assocStruct1));
          }
        }
      }
    }
    return list;
  }

  public void setOtherSideUsage(
      AssocStruct assocStruct, AssocType assocType, ASTCDCompilationUnit compilationUnit) {
    if (assocStruct.getDirection().equals(AssocDirection.BiDirectional)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        for (AssocStruct struct :
            srcMap.get(getConnectedTypes(assocStruct.getAssociation(), compilationUnit).b)) {
          if (struct.getUsedAs() == null
              && struct.getSide().equals(ClassSide.Right)
              && getConnectedTypes(struct.getAssociation(), compilationUnit)
                  .a
                  .equals(getConnectedTypes(assocStruct.getAssociation(), compilationUnit).a)
              && sameAssocStruct(assocStruct, struct)) {
            struct.setUsedAs(assocType);
            break;
          }
        }
      } else {
        for (AssocStruct struct :
            srcMap.get(getConnectedTypes(assocStruct.getAssociation(), compilationUnit).a)) {
          if (struct.getUsedAs() == null
              && struct.getSide().equals(ClassSide.Left)
              && getConnectedTypes(struct.getAssociation(), compilationUnit)
                  .b
                  .equals(getConnectedTypes(assocStruct.getAssociation(), compilationUnit).b)
              && sameAssocStruct(assocStruct, struct)) {
            struct.setUsedAs(assocType);
            break;
          }
        }
      }
    }
  }

  public void filterMatched() {
    matchedClasses.removeIf(
        pair ->
            !pair.a
                .getSymbol()
                .getInternalQualifiedName()
                .equals(pair.b.getSymbol().getInternalQualifiedName()));

    matchedInterfaces.removeIf(
        pair ->
            !pair.a
                .getSymbol()
                .getInternalQualifiedName()
                .equals(pair.b.getSymbol().getInternalQualifiedName()));
  }

  public void setMatcher() {
    NameTypeMatcher nameTypeMatch = new NameTypeMatcher(tgtCD);
    SuperTypeMatcher superTypeMatchNameType = new SuperTypeMatcher(nameTypeMatch, srcCD, tgtCD);
    matcher = new SrcTgtAssocMatcher(superTypeMatchNameType, srcCD, tgtCD);
  }

  public List<Pair<ASTCDClass, List<AssocStruct>>> sortDiffs(
      List<Pair<ASTCDClass, AssocStruct>> input) {
    Map<ASTCDClass, List<AssocStruct>> resultMap = new HashMap<>();

    for (Pair<ASTCDClass, AssocStruct> pair : input) {
      ASTCDClass cdClass = pair.a;
      AssocStruct assocStruct = pair.b;

      resultMap.computeIfAbsent(cdClass, key -> new ArrayList<>()).add(assocStruct);
    }

    return resultMap.entrySet().stream()
        .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  public Optional<Pair<ASTCDClass, List<AssocStruct>>> getPair(
      List<Pair<ASTCDClass, List<AssocStruct>>> list, ASTCDClass astcdClass) {
    for (Pair<ASTCDClass, List<AssocStruct>> pair : list) {
      if (pair.a.equals(astcdClass)) {
        return Optional.of(pair);
      }
    }
    return Optional.empty();
  }

  /**
   * This function is used to treat duplicated associations BEFORE the overlapping associations.
   * This was needed as otherwise overlapping associations would be treated twice and eventually
   * deleted, if there were multiple duplicated and overlapping associations fot the same
   * subAssociation. For both, only isInConflict && inInheritanceRelation is needed, as the other
   * cases should be treated in the other function, but they are left just in case.
   */
  public void findDuplicatedAssocs() {
    Set<ASTCDType> srcToDelete = new HashSet<>();
    Set<Pair<ASTCDType, ASTCDRole>> srcAssocsToDelete = new HashSet<>();
    Set<DeleteStruct> srcAssocsToMergeWithDelete = new HashSet<>();
    Set<ASTCDType> tgtToDelete = new HashSet<>();
    Set<Pair<ASTCDType, ASTCDRole>> tgtAssocsToDelete = new HashSet<>();
    Set<DeleteStruct> tgtAssocsToMergeWithDelete = new HashSet<>();
    for (ASTCDType astcdClass : srcMap.keySet()) {
      List<Pair<AssocStruct, AssocStruct>> sameAssocs = getPairsForType(astcdClass, srcMap, srcCD);
      for (Pair<AssocStruct, AssocStruct> pair : sameAssocs) {
        AssocStruct association = pair.a;
        AssocStruct superAssoc = pair.b;
        if ((sameAssocStruct(association, superAssoc)
                || sameAssocStructInReverse(association, superAssoc))
            && !isAdded(association, superAssoc, astcdClass, srcAssocsToMergeWithDelete)) {
          srcAssocsToMergeWithDelete.add(new DeleteStruct(association, superAssoc, astcdClass));
        } else if (isInConflict(association, superAssoc)
            && inInheritanceRelation(association, superAssoc, getSrcCD())) {
          srcAssocsToMergeWithDelete.add(new DeleteStruct(association, superAssoc, astcdClass));
        } else if (isInConflict(association, superAssoc)
            && !inInheritanceRelation(association, superAssoc, srcCD)
            && !getConnectedTypes(association.getAssociation(), srcCD)
                .equals(getConnectedTypes(superAssoc.getAssociation(), srcCD))) {
          if (areZeroAssocs(association, superAssoc)) {
            srcAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
          } else {
            srcToDelete.add(astcdClass);
          }
        }
      }
    }

    for (ASTCDType astcdClass : tgtMap.keySet()) {
      List<Pair<AssocStruct, AssocStruct>> sameAssocs = getPairsForType(astcdClass, tgtMap, tgtCD);
      for (Pair<AssocStruct, AssocStruct> pair : sameAssocs) {
        AssocStruct association = pair.a;
        AssocStruct superAssoc = pair.b;
        if (isInConflict(association, superAssoc)
            && inInheritanceRelation(association, superAssoc, getTgtCD())) {
          tgtAssocsToMergeWithDelete.add(new DeleteStruct(association, superAssoc, astcdClass));
        } else if (isInConflict(association, superAssoc)
            && !inInheritanceRelation(association, superAssoc, getTgtCD())) {
          if (areZeroAssocs(association, superAssoc)) {
            tgtAssocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
          } else {
            tgtToDelete.add(astcdClass);
          }
        } else if (sameAssocStruct(association, superAssoc)
            || sameAssocStructInReverse(association, superAssoc)) {
          tgtAssocsToMergeWithDelete.add(new DeleteStruct(association, superAssoc, astcdClass));
        }
      }
    }
    for (ASTCDType astcdClass : srcToDelete) {
      updateSrc(astcdClass);
      getSrcMap().removeAll(astcdClass);
      deleteOtherSideSrc(astcdClass);
      for (ASTCDType subClass : getSrcSubMap().get(astcdClass)) {
        updateSrc(subClass);
        getSrcMap().removeAll(subClass);
        deleteOtherSideSrc(subClass);
      }
    }
    for (DeleteStruct pair : srcAssocsToMergeWithDelete) {
      if (!getNotInstClassesSrc().contains(pair.getAstcdClass())) {
        setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
        mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
      }
    }
    for (DeleteStruct pair : srcAssocsToMergeWithDelete) {
      getSrcMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
    }
    for (Pair<ASTCDType, ASTCDRole> pair : srcAssocsToDelete) {
      deleteAssocsFromSrc(pair.a, pair.b);
    }
    for (ASTCDType astcdClass : tgtToDelete) {
      updateTgt(astcdClass);
      getTgtMap().removeAll(astcdClass);
      deleteOtherSideTgt(astcdClass);
      for (ASTCDType subClass : getTgtSubMap().get(astcdClass)) {
        deleteOtherSideTgt(subClass);
        getTgtMap().removeAll(subClass);
      }
    }
    for (DeleteStruct pair : tgtAssocsToMergeWithDelete) {
      if (!getNotInstClassesTgt().contains(pair.getAstcdClass())) {
        setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
        mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
      }
    }
    for (DeleteStruct pair : tgtAssocsToMergeWithDelete) {
      getTgtMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
    }
    for (Pair<ASTCDType, ASTCDRole> pair : tgtAssocsToDelete) {
      deleteAssocsFromTgt(pair.a, pair.b);
    }
  }

  /**
   * Get a non-abstract class for changed type.
   *
   * @param astcdClass class with changed type.
   * @param astcdAttribute attribute with changed type.
   * @return non-abstract class with this attribute.
   */
  public Optional<ASTCDClass> getClassForDiff(
      ASTCDClass astcdClass, ASTCDAttribute astcdAttribute) {
    for (ASTCDClass subClass : srcSubMap.get(astcdClass)) {
      if (!subClass.getModifier().isAbstract() && isAttContainedInClass(astcdAttribute, subClass)) {
        return Optional.of(subClass);
      }
    }
    return Optional.empty();
  }

  /**
   * Check if an attribute is conatined in a class in tgtCD.
   *
   * @param attribute attribute.
   * @param astcdClass class in tgtCD.
   * @return true if attribute is contained in class.
   */
  public boolean isAttContainedInClassTgt(ASTCDAttribute attribute, ASTCDType astcdClass) {
    int indexAttribute = attribute.getMCType().printType().lastIndexOf(".");
    for (ASTCDAttribute att : getAllAttrTgt(astcdClass).b) {
      int indexCurrent = att.getMCType().printType().lastIndexOf(".");
      if (indexCurrent == -1
          && indexAttribute == -1
          && (att.getName().equals(attribute.getName())
              && att.getMCType().printType().equals(attribute.getMCType().printType()))) {
        return true;
      } else if (indexCurrent == -1
          && indexAttribute != -1
          && (att.getName().equals(attribute.getName())
              && att.getMCType()
                  .printType()
                  .equals(attribute.getMCType().printType().substring(indexAttribute + 1)))) {
        return true;
      } else if (indexCurrent != -1
          && indexAttribute == -1
          && (att.getName().equals(attribute.getName())
              && att.getMCType()
                  .printType()
                  .substring(indexCurrent + 1)
                  .equals(attribute.getMCType().printType()))) {
        return true;
      } else if (indexCurrent != -1
          && indexAttribute != -1
          && (att.getName().equals(attribute.getName())
              && att.getMCType()
                  .printType()
                  .substring(indexCurrent + 1)
                  .equals(attribute.getMCType().printType().substring(indexAttribute + 1)))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Delete associations from subclasses in tgtCD.
   *
   * @param assocStruct association to delete.
   * @param astcdType supertype.
   */
  public void deleteAssocFromSubTgt(AssocStruct assocStruct, ASTCDType astcdType) {
    for (ASTCDClass subClass : tgtSubMap.get(astcdType)) {
      Iterator<AssocStruct> iterator = tgtMap.get(subClass).iterator();
      while (iterator.hasNext()) {
        AssocStruct subAssoc = iterator.next();
        if (subAssoc.getSourceAssoc() != null
            && sameAssociation(subAssoc.getSourceAssoc(), assocStruct.getUnmodifiedAssoc(), tgtCD)
            && !getConnectedTypes(subAssoc.getAssociation(), tgtCD)
                .a
                .equals(getConnectedTypes(subAssoc.getAssociation(), tgtCD).b)) {
          iterator.remove();
          deleteAssocOtherSideTgt(subAssoc);
        }
      }
    }
  }

  /**
   * Delete associations from subclasses in srcCD.
   *
   * @param assocStruct association to delete.
   * @param astcdType supertype.
   */
  public void deleteAssocsFromSubSrc(AssocStruct assocStruct, ASTCDType astcdType) {
    for (ASTCDClass subClass : srcSubMap.get(astcdType)) {
      Iterator<AssocStruct> iterator = srcMap.get(subClass).iterator();
      while (iterator.hasNext()) {
        AssocStruct subAssoc = iterator.next();
        if (subAssoc.getSourceAssoc() != null
            && sameAssociation(subAssoc.getSourceAssoc(), assocStruct.getUnmodifiedAssoc(), srcCD)
            && !isLoopStruct(subAssoc)) {
          iterator.remove();
          deleteAssocOtherSideSrc(subAssoc);
        }
      }
    }
  }

  public List<Pair<ASTCDClass, List<ASTCDAttribute>>> transform(
      List<Pair<ASTCDClass, ASTCDAttribute>> list) {
    List<Pair<ASTCDClass, List<ASTCDAttribute>>> result = new ArrayList<>();
    for (Pair<ASTCDClass, ASTCDAttribute> pair : list) {
      result.add(new Pair<>(pair.a, Collections.singletonList(pair.b)));
    }
    return result;
  }

  /**
   * Sort the added and deleted attributes for a given type. This reduces the number of generated
   * diff-witnesses.
   *
   * @param typeDiffStruct type with added and deleted attributes.
   */
  public void sortTypeDiff(TypeDiffStruct typeDiffStruct) {
    List<Pair<ASTCDClass, List<ASTCDAttribute>>> added = new ArrayList<>();
    List<Pair<ASTCDClass, List<ASTCDAttribute>>> deleted = new ArrayList<>();

    if (typeDiffStruct.getAddedAttributes() != null
        && !typeDiffStruct.getAddedAttributes().isEmpty()) {
      added.addAll(typeDiffStruct.getAddedAttributes());
    }
    if (typeDiffStruct.getDeletedAttributes() != null
        && !typeDiffStruct.getDeletedAttributes().isEmpty()) {
      deleted.addAll(typeDiffStruct.getDeletedAttributes());
    }

    Map<ASTCDClass, AddedDeletedAtt> classAttributeMap = new HashMap<>();

    for (Pair<ASTCDClass, List<ASTCDAttribute>> pair : added) {
      ASTCDClass clazz = pair.a;
      List<ASTCDAttribute> attribute = pair.b;

      if (!classAttributeMap.containsKey(clazz)) {
        classAttributeMap.put(clazz, new AddedDeletedAtt());
      }
      classAttributeMap.get(clazz).getAddedAttributes().addAll(attribute);
    }

    for (Pair<ASTCDClass, List<ASTCDAttribute>> pair : deleted) {
      ASTCDClass clazz = pair.a;
      List<ASTCDAttribute> attribute = pair.b;

      if (!classAttributeMap.containsKey(clazz)) {
        classAttributeMap.put(clazz, new AddedDeletedAtt());
      }
      classAttributeMap.get(clazz).getDeletedAttributes().addAll(attribute);
    }

    List<Pair<ASTCDClass, List<ASTCDAttribute>>> addedNew = new ArrayList<>();
    List<Pair<ASTCDClass, List<ASTCDAttribute>>> deletedNew = new ArrayList<>();
    List<Pair<ASTCDClass, AddedDeletedAtt>> addedDeleted = new ArrayList<>();

    // Remove AddedDeletedAtt if no deleted attributes are present
    for (Map.Entry<ASTCDClass, AddedDeletedAtt> astcdClass : classAttributeMap.entrySet()) {
      if (astcdClass.getValue().getDeletedAttributes().isEmpty()) {
        addedNew.add(new Pair<>(astcdClass.getKey(), astcdClass.getValue().getAddedAttributes()));
      } else if (astcdClass.getValue().getAddedAttributes().isEmpty()) {
        deletedNew.add(
            new Pair<>(astcdClass.getKey(), astcdClass.getValue().getDeletedAttributes()));
      } else {
        addedDeleted.add(new Pair<>(astcdClass.getKey(), astcdClass.getValue()));
      }
    }
    typeDiffStruct.setAddedDeletedAttributes(addedDeleted);
    typeDiffStruct.setAddedAttributes(addedNew);
    typeDiffStruct.setDeletedAttributes(deletedNew);
  }
}
