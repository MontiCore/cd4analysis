package de.monticore.cddiff.syn2semdiff.odgen;

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
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdmatcher.MatchCDAssocsBySrcNameAndTgtRole;
import de.monticore.cdmatcher.MatchCDTypesByName;
import de.monticore.cdmatcher.MatchCDTypesToSuperTypes;
import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis.ODBasisMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTODElement;
import de.monticore.odbasis._ast.ASTODObject;
import de.se_rwth.commons.logging.Log;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.monticore.cddiff.ow2cw.CDAssociationHelper.matchRoleNames;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.getAllSuper;
import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isSuperOf;

/**
 * This is a helper class that is accessible from all classes for semantic difference and generation
 * of object diagrams. It contains functions for comparing associations, attributes, classes,
 * generating part of object diagrams. It further contains multiple maps that reduce the complexity
 * of the implementation. The function setMaps() that computes possible associations for each class
 * is also implemented in this class.
 */
public class Syn2SemDiffHelper {

  public Syn2SemDiffHelper() {}

  private final ODBuilder ODBuilder = new ODBuilder();
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

  private MatchCDAssocsBySrcNameAndTgtRole matcher;
  private List<CDAssocDiff> diffs;
  private List<MatchingStrategy> matchingStrategies;

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
  public void setMatchedClasses(List<Pair<ASTCDClass, ASTCDType>> matchedClasses) {
    this.matchedClasses = matchedClasses;
  }

  public List<Pair<ASTCDClass, ASTCDType>> getMatchedClasses() {
    return matchedClasses;
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

  public void setMatchingStrategies(List<MatchingStrategy> matchingStrategies) {
    this.matchingStrategies = matchingStrategies;
  }

  public boolean isSubclassWithSuper(ASTCDType superClass, ASTCDType subClass) {
    return isSuperOf(
        superClass.getSymbol().getInternalQualifiedName(),
        subClass.getSymbol().getInternalQualifiedName(),
        srcCD);
  }

  /**
   * Get all needed associations from the src/tgtMap that use the given class as target. The
   * associations are strictly unidirectional. Needed associations - the cardinality must be at
   * least one.
   *
   * @param astcdClass target class
   * @param isSource for srcMap if true, for tgtMap if false
   * @return list of associations
   */
  public List<AssocStruct> getOtherAssocs(ASTCDType astcdClass, boolean isSource) {
    List<AssocStruct> list = new ArrayList<>();
    ArrayListMultimap<ASTCDType, AssocStruct> map = isSource ? srcMap : tgtMap;

    for (ASTCDType classToCheck : map.keySet()) {
      if (classToCheck != astcdClass) {
        for (AssocStruct assocStruct : map.get(classToCheck)) {
          Pair<ASTCDType, ASTCDType> connectedTypes;
          connectedTypes = Syn2SemDiffHelper.getConnectedTypes(assocStruct.getAssociation(), isSource ? srcCD : tgtCD);

          if (assocStruct.getSide().equals(ClassSide.Left)
            && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
            && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
            || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne())
            && connectedTypes.b == astcdClass) {
            list.add(assocStruct.deepClone());
          } else if (assocStruct.getSide().equals(ClassSide.Right)
            && !assocStruct.getDirection().equals(AssocDirection.BiDirectional)
            && (assocStruct.getAssociation().getRight().getCDCardinality().isOne()
            || assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne())
            && connectedTypes.a == astcdClass) {
            list.add(assocStruct.deepClone());
          }
        }
      }
    }
    return list;
  }

  /**
   * Get all needed associations (including superclasses) from the src/tgtMap that use the given class
   * as target. The associations are strictly unidirectional. Needed associations - the cardinality
   * must be at least one. 'Subassociations' might be included.
   *
   * @param astcdClass target class
   * @return list of associations
   */
  public List<AssocStruct> getAllOtherAssocs(ASTCDType astcdClass, boolean isSource) {
    List<AssocStruct> list = new ArrayList<>();
    Set<ASTCDType> superTypes = CDDiffUtil.getAllSuperTypes(astcdClass, isSource ? srcCD.getCDDefinition() : tgtCD.getCDDefinition());

    for (ASTCDType astcdClass1 : superTypes) {
      list.addAll(getOtherAssocs(astcdClass1, isSource));
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
      Optional<ASTCDType> result = findMatchedSrc((ASTCDClass) astcdType);
      return result;
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

  public Optional<ASTCDType> findMatchedClass(ASTCDClass astcdClass) {
    for (Pair<ASTCDClass, ASTCDType> pair : matchedClasses) {
      if (pair.a.equals(astcdClass)) {
        return Optional.ofNullable(pair.b);
      }
    }
    return Optional.empty();
  }

  public Optional<ASTCDType> findMatchedSrc(ASTCDClass astcdClass) {
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

  /**
   * Check if the associations allow 0 objects from target class
   *
   * @param association association
   * @param superAssociation association
   * @return true if the condition is fulfilled
   */
  public static boolean areZeroAssocs(AssocStruct association, AssocStruct superAssociation) {
    ASTCDCardinality assocCardinality = association.getSide().equals(ClassSide.Left)
      ? association.getAssociation().getRight().getCDCardinality()
      : association.getAssociation().getLeft().getCDCardinality();

    ASTCDCardinality superAssocCardinality = superAssociation.getSide().equals(ClassSide.Left)
      ? superAssociation.getAssociation().getRight().getCDCardinality()
      : superAssociation.getAssociation().getLeft().getCDCardinality();

    return (assocCardinality.isMult() || assocCardinality.isOpt())
      && (superAssocCardinality.isMult() || superAssocCardinality.isOpt());
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
   * Similar to the function above, but the now the classes must be the target of the association.
   *
   * @param matchedAssocStruc association from srcCD.
   * @param srcType type from srcCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  public Optional<ASTCDType> allSubClassesAreTgtSrcTgt(
      AssocStruct matchedAssocStruc, ASTCDType srcType) {
    List<ASTCDClass> subClasses = srcSubMap.get(srcType);
    List<ASTCDType> subClassesTgt = getTypes(subClasses, false);
    for (ASTCDType subClass : subClassesTgt) {
      boolean contained = false;
      for (AssocStruct assocStruct : getAllOtherAssocs(subClass, false)) {
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
   * Check if all matched subclasses in tgtCD/srcCD of a class from srcCD/tgtCD have the same association or a
   * subassociation.
   *
   * @param association association from srcCD/tgtCD.
   * @param type type from srcCD/tgtCD.
   * @return null if condition is fulfilled, else the class that violates the condition.
   */
  public Optional<ASTCDType> allSubClassesHaveIt(AssocStruct association, ASTCDType type, boolean isSource) {
    List<ASTCDClass> subClasses = isSource ? tgtSubMap.get(type) : srcSubMap.get(type);
    List<ASTCDType> subTypes = getTypes(subClasses, isSource);

    for (ASTCDType subClass : subTypes) {
      boolean isContained = false;
      List<AssocStruct> assocList = isSource ? srcMap.get(subClass) : getAllOtherAssocs(subClass, false);

      for (AssocStruct assocStruct : assocList) {
        if (isSource) {
          if (sameAssociationTypeSrcTgt(assocStruct, association)) {
            isContained = true;
            break;
          }
        } else {
          if (sameAssociationTypeSrcTgt(association, assocStruct)) {
            isContained = true;
            break;
          }
        }
      }

      if (!isContained) {
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
    List<ASTCDType> subClassesSrc = getTypes(subClasses, true);
    for (ASTCDType subClass : subClassesSrc) {
      boolean contained = false;
      for (AssocStruct assocStruct : getAllOtherAssocs(subClass, true)) {
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
   * Check if the classes are in an inheritance relation. For this, the matched classes in srcCD/tgtCD of
   * the tgtClass/srcClass are compared with isSuper() to the srcClass.
   *
   * @param subType class from srcCD/trgCD.
   * @param superType class from tgtCD/srcCD.
   * @return true if the condition is fulfilled.
   */
  public boolean compareTypes(ASTCDType subType, ASTCDType superType, boolean isSource) {
    Optional<ASTCDType> typeToMatch = isSource ? findMatchedTypeSrc(superType) : findMatchedTypeTgt(superType);

    return typeToMatch.filter(astcdType -> isSuperOf(
      astcdType.getSymbol().getInternalQualifiedName(),
      subType.getSymbol().getInternalQualifiedName(),
      (ICD4CodeArtifactScope) (isSource ? srcCD.getEnclosingScope() : tgtCD.getEnclosingScope())
    )).isPresent();
  }

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

  /**
   * Check if the srcType is target of the given association from srcCD.
   *
   * @param association association from srcCD.
   * @param srcType type from srcCD.
   * @return true, if an association has the same association type.
   */
  public boolean classIsTarget(AssocStruct association, ASTCDType srcType) {
    for (AssocStruct assocStruct : getAllOtherAssocs(srcType, true)) {
      if (sameAssociationType(assocStruct, association)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if the tgtType is target of the given association from srcCD.
   *
   * @param association association from srcCD.
   * @param tgtType type from tgtCD.
   * @return true, if an association has the same association type.
   */
  public boolean classIsTgtSrcTgt(AssocStruct association, ASTCDType tgtType) {
    for (AssocStruct assocStruct : getAllOtherAssocs(tgtType, false)) {
      if (sameAssociationTypeSrcTgt(association, assocStruct)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if the srcType is target of the given association from tgtCD.
   *
   * @param association association from tgtCD.
   * @param srcType type from srcCD.
   * @return true, if an association has the same association type.
   */
  public boolean classIsTargetTgtSrc(AssocStruct association, ASTCDType srcType) {
    for (AssocStruct assocStruct : getAllOtherAssocs(srcType, true)) {
      if (sameAssociationTypeSrcTgt(assocStruct, association)) {
        return true;
      }
    }
    return false;
  }

  public List<ASTCDType> getTypes(List<? extends ASTCDType> types, boolean isSource) {
    List<ASTCDType> resultTypes = new ArrayList<>();

    for (ASTCDType astcdType : types) {
      if (astcdType instanceof ASTCDClass) {
        Optional<ASTCDType> matched = isSource
          ? findMatchedSrc((ASTCDClass) astcdType)
          : findMatchedClass((ASTCDClass) astcdType);
        matched.ifPresent(resultTypes::add);
      } else if (astcdType instanceof ASTCDInterface) {
        Optional<ASTCDType> matched = isSource
          ? findMatchedTypeSrc((ASTCDInterface) astcdType)
          : findMatchedTypeTgt((ASTCDInterface) astcdType);
        matched.ifPresent(resultTypes::add);
      }
    }
    return resultTypes;
  }

  /**
   * Check if two associations are exactly the same.
   *
   * @param association association.
   * @param association2 association.
   * @return true if the condition is fulfilled.
   */
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
      return compareTypes(
          getConnectedTypes(assocStruct1.getAssociation(), tgtCD).b,
          getConnectedTypes(assocStruct.getAssociation(), srcCD).b,
        false);
    } else if (assocStruct.getSide().equals(ClassSide.Left)
        && assocStruct1.getSide().equals(ClassSide.Right)) {
      return compareTypes(
          getConnectedTypes(assocStruct1.getAssociation(), tgtCD).a,
          getConnectedTypes(assocStruct.getAssociation(), srcCD).b,
        false);
    } else if (assocStruct.getSide().equals(ClassSide.Right)
        && assocStruct1.getSide().equals(ClassSide.Left)) {
      return compareTypes(
          getConnectedTypes(assocStruct1.getAssociation(), tgtCD).b,
          getConnectedTypes(assocStruct.getAssociation(), srcCD).a,
        false);
    } else {
      return compareTypes(
          getConnectedTypes(assocStruct1.getAssociation(), tgtCD).a,
          getConnectedTypes(assocStruct.getAssociation(), srcCD).a,
        false);
    }
  }

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

  /**
   * Check if the srcAssoc has the same type as the srcAssoc1 - direction, role names and the
   * cardinalities of srcAssoc are sub-intervals of the cardinalities of srcAssoc1.
   *
   * @param srcAssocSub association from srcCD.
   * @param srcAssocSuper association from srcCD.
   * @return true, if the condition is fulfilled.
   */
  public boolean sameAssociationType(AssocStruct srcAssocSub, AssocStruct srcAssocSuper) {
    if (srcAssocSuper.getSide().equals(srcAssocSub.getSide())) {
      // Case 1: Same side (Left-Left or Right-Right)
      return compareAssociations(srcAssocSuper, srcAssocSub, true);
    } else {
      // Case 2: Different sides (Left-Right or Right-Left)
      return compareAssociations(srcAssocSuper, srcAssocSub, false);
    }
  }

  private boolean compareAssociations(AssocStruct superAssoc, AssocStruct subAssoc, boolean sameSides) {
    // Compare role names, cardinality, and direction for both sides (left and right)
    return matchRoleNames(superAssoc.getAssociation().getLeft(), sameSides ? subAssoc.getAssociation().getLeft() : subAssoc.getAssociation().getRight()) &&
      matchRoleNames(superAssoc.getAssociation().getRight(), sameSides ? subAssoc.getAssociation().getRight() : subAssoc.getAssociation().getLeft()) &&
      matchDirection(superAssoc, new Pair<>(subAssoc, subAssoc.getSide())) &&
      CDInheritanceHelper.isSuperOf(superAssoc.getAssociation().getLeftQualifiedName().getQName(),
        sameSides ? subAssoc.getAssociation().getLeftQualifiedName().getQName() : subAssoc.getAssociation().getRightQualifiedName().getQName(),
        (ICD4CodeArtifactScope) srcCD.getEnclosingScope()) &&
      CDInheritanceHelper.isSuperOf(superAssoc.getAssociation().getRightQualifiedName().getQName(),
        sameSides ? subAssoc.getAssociation().getRightQualifiedName().getQName() : subAssoc.getAssociation().getLeftQualifiedName().getQName(),
        (ICD4CodeArtifactScope) srcCD.getEnclosingScope()) &&
      isContainedIn(cardToEnum(subAssoc.getAssociation().getLeft().getCDCardinality()),
        cardToEnum(sameSides ? superAssoc.getAssociation().getLeft().getCDCardinality() : superAssoc.getAssociation().getRight().getCDCardinality())) &&
      isContainedIn(cardToEnum(subAssoc.getAssociation().getRight().getCDCardinality()),
        cardToEnum(sameSides ? superAssoc.getAssociation().getRight().getCDCardinality() : superAssoc.getAssociation().getLeft().getCDCardinality()));
  }

  /**
   * Check if the srcAssoc has the same type as the tgtAssoc - direction, role names and the
   * cardinalities of srcAssoc are sub-intervals of the cardinalities of tgtAssoc.
   *
   * @param srcAssocSub association from srcCD.
   * @param tgtAssocSuper association from tgtCD.
   * @return true, if the condition is fulfilled.
   */
  public boolean sameAssociationTypeSrcTgt(AssocStruct srcAssocSub, AssocStruct tgtAssocSuper) {
    boolean isLeftLeft = srcAssocSub.getSide().equals(ClassSide.Left) && tgtAssocSuper.getSide().equals(ClassSide.Left);
    boolean isLeftRight = srcAssocSub.getSide().equals(ClassSide.Left) && tgtAssocSuper.getSide().equals(ClassSide.Right);
    boolean isRightRight = srcAssocSub.getSide().equals(ClassSide.Right) && tgtAssocSuper.getSide().equals(ClassSide.Right);
    boolean isRightLeft = srcAssocSub.getSide().equals(ClassSide.Right) && tgtAssocSuper.getSide().equals(ClassSide.Left);

    if (isLeftLeft || isRightRight) {
      return matchRoleNames(srcAssocSub.getAssociation().getLeft(), tgtAssocSuper.getAssociation().getLeft())
        && matchRoleNames(srcAssocSub.getAssociation().getRight(), tgtAssocSuper.getAssociation().getRight())
        && matchDirection(srcAssocSub, new Pair<>(tgtAssocSuper, tgtAssocSuper.getSide()))
        && compareTypes(getConnectedTypes(srcAssocSub.getAssociation(), srcCD).a, getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).a, true)
        && compareTypes(getConnectedTypes(srcAssocSub.getAssociation(), srcCD).b, getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).b, true)
        && isContainedIn(cardToEnum(srcAssocSub.getAssociation().getLeft().getCDCardinality()), cardToEnum(tgtAssocSuper.getAssociation().getLeft().getCDCardinality()))
        && isContainedIn(cardToEnum(srcAssocSub.getAssociation().getRight().getCDCardinality()), cardToEnum(tgtAssocSuper.getAssociation().getRight().getCDCardinality()));
    } else if (isLeftRight || isRightLeft) {
      return matchRoleNames(srcAssocSub.getAssociation().getLeft(), tgtAssocSuper.getAssociation().getRight())
        && matchRoleNames(srcAssocSub.getAssociation().getRight(), tgtAssocSuper.getAssociation().getLeft())
        && matchDirection(srcAssocSub, new Pair<>(tgtAssocSuper, tgtAssocSuper.getSide()))
        && compareTypes(getConnectedTypes(srcAssocSub.getAssociation(), srcCD).a, getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).b, true)
        && compareTypes(getConnectedTypes(srcAssocSub.getAssociation(), srcCD).b, getConnectedTypes(tgtAssocSuper.getAssociation(), tgtCD).a, true)
        && isContainedIn(cardToEnum(srcAssocSub.getAssociation().getLeft().getCDCardinality()), cardToEnum(tgtAssocSuper.getAssociation().getRight().getCDCardinality()))
        && isContainedIn(cardToEnum(srcAssocSub.getAssociation().getRight().getCDCardinality()), cardToEnum(tgtAssocSuper.getAssociation().getLeft().getCDCardinality()));
    }

    return false;
  }

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
   * @param type class from srcCD/tgtCD to get related associations.
   * @return list of associations.
   */
  public List<ASTCDAssociation> getCDAssociationsListForType(ASTCDType type, boolean isSource) {
    List<ASTCDAssociation> result = new ArrayList<>();
    List<ASTCDAssociation> associationsList = isSource
      ? srcCD.getCDDefinition().getCDAssociationsList()
      : tgtCD.getCDDefinition().getCDAssociationsList();

    for (ASTCDAssociation association : associationsList) {
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
      for (ASTCDAssociation astcdAssociation : getCDAssociationsListForType(astcdClass, true)) {
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
      for (ASTCDAssociation astcdAssociation : getCDAssociationsListForType(astcdClass, false)) {
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
        for (ASTCDAssociation association : getCDAssociationsListForType(superClass, true)) {
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
        for (ASTCDAssociation association : getCDAssociationsListForType(superClass, false)) {
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
        if (sameRoleNameAndClass(attribute.getName(), astcdClass, true)) {
          notInstClassesSrc.add(astcdClass);
          break;
        }
      }
    }
    for (ASTCDClass astcdClass : tgtCD.getCDDefinition().getCDClassesList()) {
      List<ASTCDAttribute> attributes = getAllAttrTgt(astcdClass).b;
      for (ASTCDAttribute attribute : attributes) {
        if (sameRoleNameAndClass(attribute.getName(), astcdClass, false)) {
          notInstClassesTgt.add(astcdClass);
          break;
        }
      }
    }
  }
  // TODO: Replace with new functions
  public void setMap() {
    initializeMaps();
    checkForDuplicateAttributes();
    initializeNotInstClasses();
  }

  public void initializeMaps() {
    srcMap = ArrayListMultimap.create();
    tgtMap = ArrayListMultimap.create();

    // Process source types
    List<ASTCDType> srcTypes = new ArrayList<>();
    srcTypes.addAll(srcCD.getCDDefinition().getCDClassesList());
    srcTypes.addAll(srcCD.getCDDefinition().getCDInterfacesList());
    processAssociations(srcCD, srcTypes, srcMap, true);

    // Process target types
    List<ASTCDType> tgtTypes = new ArrayList<>();
    tgtTypes.addAll(tgtCD.getCDDefinition().getCDClassesList());
    tgtTypes.addAll(tgtCD.getCDDefinition().getCDInterfacesList());
    processAssociations(tgtCD, tgtTypes, tgtMap, false);
  }

  private void processAssociations(ASTCDCompilationUnit cdUnit, List<ASTCDType> types, ArrayListMultimap<ASTCDType, AssocStruct> map, boolean isSource) {
    for (ASTCDType astcdClass : types) {
      for (ASTCDAssociation astcdAssociation : getCDAssociationsListForType(astcdClass, isSource)) {
        Pair<ASTCDType, ASTCDType> pair = getConnectedTypes(astcdAssociation, cdUnit);
        if (pair.a == null) {
          continue;
        }
        ASTCDAssociation copyAssoc = prepareAssociationCopy(astcdAssociation);

        // Process Left Side
        if (pair.a.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName())) {
          handleAssocStruct(copyAssoc, astcdAssociation, map, astcdClass, pair.b, ClassSide.Left);
        }

        // Process Right Side
        if (pair.b.getSymbol().getInternalQualifiedName().equals(astcdClass.getSymbol().getInternalQualifiedName())) {
          handleAssocStruct(copyAssoc, astcdAssociation, map, astcdClass, pair.a, ClassSide.Right);
        }
      }
    }
  }

  // Method to prepare a deep clone of the association with default values
  private ASTCDAssociation prepareAssociationCopy(ASTCDAssociation association) {
    ASTCDAssociation copyAssoc = association.deepClone();
    copyAssoc.setName(" ");

    // Ensure cardinalities are set
    if (!copyAssoc.getLeft().isPresentCDCardinality()) {
      copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
    }
    if (!copyAssoc.getRight().isPresentCDCardinality()) {
      copyAssoc.getRight().setCDCardinality(CD4CodeMill.cDCardMultBuilder().build());
    }

    // Handle composition association
    if (copyAssoc.getCDAssocType().isComposition()) {
      copyAssoc.getLeft().setCDCardinality(CD4CodeMill.cDCardOneBuilder().build());
    }

    // Set role names
    copyAssoc.getLeft().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(association.getLeft())).build());
    copyAssoc.getRight().setCDRole(CD4CodeMill.cDRoleBuilder().setName(CDDiffUtil.inferRole(association.getRight())).build());

    return copyAssoc;
  }

  // Handle AssocStruct creation and add it to the map
  private void handleAssocStruct(ASTCDAssociation copyAssoc, ASTCDAssociation originalAssoc, ArrayListMultimap<ASTCDType, AssocStruct> map,
                                 ASTCDType astcdClass, ASTCDType connectedType, ClassSide side) {
    if (originalAssoc.getCDAssocDir().isBidirectional()) {
      map.put(astcdClass, new AssocStruct(copyAssoc, AssocDirection.BiDirectional, side, astcdClass, connectedType));
    } else if ((side == ClassSide.Left && originalAssoc.getCDAssocDir().isDefinitiveNavigableRight()) ||
      (side == ClassSide.Right && originalAssoc.getCDAssocDir().isDefinitiveNavigableLeft())) {
      AssocDirection direction = (side == ClassSide.Left) ? AssocDirection.LeftToRight : AssocDirection.RightToLeft;
      map.put(astcdClass, new AssocStruct(copyAssoc, direction, side, astcdClass, connectedType));
    }
  }

  // Helper method to process classes and check for duplicate attributes
  private void processClassesForDuplicateAttributes(ASTCDCompilationUnit cdUnit, Set<ASTCDType> notInstClasses, Function<ASTCDClass, Pair<?, List<ASTCDAttribute>>> getAttrFunction) {
    for (ASTCDClass astcdClass : cdUnit.getCDDefinition().getCDClassesList()) {
      List<ASTCDAttribute> attributes = getAttrFunction.apply(astcdClass).b;  // Extract attributes

      for (ASTCDAttribute attribute : attributes) {
        for (ASTCDAttribute attribute1 : attributes) {
          if (attribute != attribute1
            && attribute.getName().equals(attribute1.getName())
            && !attribute.getMCType().printType().equals(attribute1.getMCType().printType())) {
            notInstClasses.add(astcdClass);  // Add the class to the set if conditions are met
            break;
          }
        }
      }
    }
  }

  public void checkForDuplicateAttributes() {
    // Process srcCD classes
    processClassesForDuplicateAttributes(srcCD, notInstClassesSrc, this::getAllAttr);

    // Process tgtCD classes
    processClassesForDuplicateAttributes(tgtCD, notInstClassesTgt, this::getAllAttrTgt);
  }


  // Helper method to process classes and attributes for source or target CD
  private void processClassesForNotInst(ASTCDCompilationUnit cdUnit, Set<ASTCDType> notInstClasses, Function<ASTCDClass, Pair<?, List<ASTCDAttribute>>> getAttrFunction) {
    for (ASTCDClass astcdClass : cdUnit.getCDDefinition().getCDClassesList()) {
      List<ASTCDAttribute> attributes = getAttrFunction.apply(astcdClass).b;  // Extract attributes

      for (ASTCDAttribute attribute : attributes) {
        for (ASTCDAttribute attribute1 : attributes) {
          if (attribute != attribute1
            && attribute.getName().equals(attribute1.getName())
            && !attribute.getMCType().printType().equals(attribute1.getMCType().printType())) {
            notInstClasses.add(astcdClass);  // Add the class to the set if conditions are met
            break;
          }
        }
      }
    }
  }

  public void initializeNotInstClasses() {
    // Process srcCD classes
    processClassesForNotInst(srcCD, notInstClassesSrc, this::getAllAttr);

    // Process tgtCD classes
    processClassesForNotInst(tgtCD, notInstClassesTgt, this::getAllAttrTgt);
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

  // Helper method to process interfaces and classes for superclass relationship
  private void processInterfacesAndClasses(ASTCDCompilationUnit cdUnit, ArrayListMultimap<ASTCDType, ASTCDClass> subMap) {
    List<ASTCDInterface> interfaces = cdUnit.getCDDefinition().getCDInterfacesList();
    List<ASTCDClass> classes = cdUnit.getCDDefinition().getCDClassesList();

    for (ASTCDInterface astcdInterface : interfaces) {
      for (ASTCDClass astcdClass : classes) {
        if (CDInheritanceHelper.isSuperOf(
          astcdInterface.getSymbol().getInternalQualifiedName(),
          astcdClass.getSymbol().getInternalQualifiedName(),
          cdUnit)) {
          subMap.put(astcdInterface, astcdClass);  // Add to the map if inheritance relation exists
        }
      }
    }
  }

  public void processSuperClassRelations() {
    // Process srcCD interfaces and classes
    processInterfacesAndClasses(srcCD, srcSubMap);

    // Process tgtCD interfaces and classes
    processInterfacesAndClasses(tgtCD, tgtSubMap);
  }

  private boolean sameRoleNameAndClass(String roleName, ASTCDClass astcdClass, boolean isSource) {
    String roleName1 = roleName.substring(0, 1).toUpperCase() + roleName.substring(1);

    // Determine which map and CD to use based on whether it's source or target
    ArrayListMultimap<ASTCDType, AssocStruct> mapToUse = isSource ? srcMap : tgtMap;
    ASTCDCompilationUnit cdToUse = isSource ? srcCD : tgtCD;

    for (AssocStruct assocStruct : mapToUse.get(astcdClass)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getRight()).equals(roleName)
          && getConnectedTypes(assocStruct.getAssociation(), cdToUse)
          .b
          .getName()
          .equals(roleName1)) {
          return true;
        }
      } else {
        if (CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(roleName)
          && getConnectedTypes(assocStruct.getAssociation(), cdToUse)
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

  /**
   * Get the minimal non-abstract subclass(strict subclass) of a given type. The minimal subclass is
   * the subclass with the least amount of attributes and associations(ingoing and outgoing).
   *
   * @param baseClass type.
   * @param isSource boolean to determine if the source or target CD should be used.
   * @return minimal subclass.
   */
  public Optional<ASTCDClass> minSubClass(ASTCDType baseClass, boolean isSource) {
    ArrayListMultimap<ASTCDType, ASTCDClass> subMap = isSource ? srcSubMap : tgtSubMap;
    Set<ASTCDType> notInstClasses = isSource ? notInstClassesSrc : notInstClassesTgt;

    List<ASTCDClass> subClasses = subMap.get(baseClass);
    int lowestCount = Integer.MAX_VALUE;
    ASTCDClass subclassWithLowestCount = null;

    for (ASTCDClass subclass : subClasses) {
      if (!subclass.getModifier().isAbstract() && !notInstClasses.contains(subclass)) {
        int attributeCount = getAllAttr(subclass).b.size();
        int associationCount = getAssociationCount(subclass, isSource);
        int otherAssocsCount = getAllOtherAssocs(subclass, isSource).size();
        int totalCount = attributeCount + associationCount + otherAssocsCount;

        if (totalCount < lowestCount) {
          lowestCount = totalCount;
          subclassWithLowestCount = subclass;
        }
      }
    }

    return Optional.ofNullable(subclassWithLowestCount);
  }

  public int getAssociationCount(ASTCDType astcdClass, boolean isSource) {
    int count = 0;
    // Determine which map and CD to use based on whether it's source or target
    ArrayListMultimap<ASTCDType, AssocStruct> mapToUse = isSource ? srcMap : tgtMap;
    ASTCDCompilationUnit cdToUse = isSource ? srcCD : tgtCD;

    for (AssocStruct assocStruct : mapToUse.get(astcdClass)) {
      if (assocStruct.getSide().equals(ClassSide.Left)) {
        if ((assocStruct.getAssociation().getRight().getCDCardinality().isAtLeastOne()
          || assocStruct.getAssociation().getRight().getCDCardinality().isOne())
          && !getConnectedTypes(assocStruct.getAssociation(), cdToUse)
          .b
          .getSymbol()
          .getInternalQualifiedName()
          .equals(
            getConnectedTypes(assocStruct.getAssociation(), cdToUse)
              .a
              .getSymbol()
              .getInternalQualifiedName())) {
          count++;
        }
      } else {
        if ((assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne()
          || assocStruct.getAssociation().getLeft().getCDCardinality().isOne())
          && !getConnectedTypes(assocStruct.getAssociation(), cdToUse)
          .b
          .getSymbol()
          .getInternalQualifiedName()
          .equals(
            getConnectedTypes(assocStruct.getAssociation(), cdToUse)
              .a
              .getSymbol()
              .getInternalQualifiedName())) {
          count++;
        }
      }
    }
    return count;
  }

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

  public boolean sameAssocStructInReverse(AssocStruct struct, AssocStruct tgtStruct) {
    return CDDiffUtil.inferRole(struct.getAssociation().getLeft())
            .equals(CDDiffUtil.inferRole(tgtStruct.getAssociation().getRight()))
        && CDDiffUtil.inferRole(struct.getAssociation().getRight())
            .equals(CDDiffUtil.inferRole(tgtStruct.getAssociation().getLeft()))
        && matchDirectionInReverse(struct, new Pair<>(tgtStruct, tgtStruct.getSide()))
        && matchRoleNames(struct.getAssociation().getLeft(), tgtStruct.getAssociation().getRight())
        && matchRoleNames(struct.getAssociation().getRight(), tgtStruct.getAssociation().getLeft());
  }

  /**
   * Compare associations. If for a pair of associations one of them is a subassociation and a loop
   * association, the other one is marked so that it won't be looked at for generation of object
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

  public boolean isLoopStruct(AssocStruct assocStruct) {
    Pair<ASTCDType, ASTCDType> pair = getConnectedTypes(assocStruct.getAssociation(), srcCD);
    return pair.a.equals(pair.b);
  }

  /**
   * Delete all associations that use the given type as target.
   *
   * @param astcdType type from srcCD/tgtCD.
   * @param isSource boolean to determine if the source or target CD should be used.
   */
  public void deleteOtherSide(ASTCDType astcdType, boolean isSource) {
    // Select the map and CD to use based on whether it's source or target
    ArrayListMultimap<ASTCDType, AssocStruct> mapToUse = isSource ? srcMap : tgtMap;
    ASTCDCompilationUnit cdToUse = isSource ? srcCD : tgtCD;

    List<Pair<ASTCDType, List<AssocStruct>>> toDelete = new ArrayList<>();

    for (ASTCDType toCheck : mapToUse.keySet()) {
      if (toCheck != astcdType) {
        List<AssocStruct> toDeleteStructs = new ArrayList<>();
        for (AssocStruct struct : mapToUse.get(toCheck)) {
          if (struct.getSide().equals(ClassSide.Left)
            && getConnectedTypes(struct.getAssociation(), cdToUse).b != null
            && getConnectedTypes(struct.getAssociation(), cdToUse).b.equals(astcdType)) {
            toDeleteStructs.add(struct);
          } else if (struct.getSide().equals(ClassSide.Right)
            && getConnectedTypes(struct.getAssociation(), cdToUse).a != null
            && getConnectedTypes(struct.getAssociation(), cdToUse).a.equals(astcdType)) {
            toDeleteStructs.add(struct);
          }
        }
        toDelete.add(new Pair<>(toCheck, toDeleteStructs));
      }
    }

    // Remove the structures from the map
    for (Pair<ASTCDType, List<AssocStruct>> pair : toDelete) {
      for (AssocStruct struct : pair.b) {
        mapToUse.get(pair.a).remove(struct);
      }
    }
  }

  /**
   * Delete the association from the other associated type.
   *
   * @param assocStruct association from srcCD/tgtCD.
   * @param isSource boolean to determine if the source or target CD should be used.
   */
  public void deleteAssocOtherSide(AssocStruct assocStruct, boolean isSource) {
    if (assocStruct.getDirection().equals(AssocDirection.BiDirectional)) {
      // Select the appropriate map and CD based on the isSource flag
      ArrayListMultimap<ASTCDType, AssocStruct> mapToUse = isSource ? srcMap : tgtMap;
      ASTCDCompilationUnit cdToUse = isSource ? srcCD : tgtCD;

      if (assocStruct.getSide().equals(ClassSide.Left)) {
        // Get the associated types on the right side
        ASTCDType connectedTypeB = getConnectedTypes(assocStruct.getAssociation(), cdToUse).b;
        for (AssocStruct struct : mapToUse.get(connectedTypeB)) {
          if ((struct.getUsedAs() == null || struct.getUsedAs().equals(AssocType.SUPER))
            && struct.getSide().equals(ClassSide.Right)
            && getConnectedTypes(struct.getAssociation(), cdToUse)
            .a
            .equals(getConnectedTypes(assocStruct.getAssociation(), cdToUse).a)
            && sameAssocStruct(assocStruct, struct)) {
            // Remove the matching association struct
            mapToUse.get(connectedTypeB).remove(struct);
            break;
          }
        }
      } else {
        // Get the associated types on the left side
        ASTCDType connectedTypeA = getConnectedTypes(assocStruct.getAssociation(), cdToUse).a;
        for (AssocStruct struct : mapToUse.get(connectedTypeA)) {
          if ((struct.getUsedAs() == null || struct.getUsedAs().equals(AssocType.SUPER))
            && struct.getSide().equals(ClassSide.Left)
            && getConnectedTypes(struct.getAssociation(), cdToUse)
            .b
            .equals(getConnectedTypes(assocStruct.getAssociation(), cdToUse).b)
            && sameAssocStruct(assocStruct, struct)) {
            // Remove the matching association struct
            mapToUse.get(connectedTypeA).remove(struct);
            break;
          }
        }
      }
    }
  }

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
    if (getAssocStructByUnmod(srcCLasses.a, srcAssoc, true).isPresent()) {
      srcStruct = getAssocStructByUnmod(srcCLasses.a, srcAssoc, true).get();
    } else if (getAssocStructByUnmod(srcCLasses.b, srcAssoc, true).isPresent()) {
      srcStruct = getAssocStructByUnmod(srcCLasses.b, srcAssoc, true).get();
    }

    if (!reversed && getAssocStructByUnmod(tgtCLasses.a, tgtAssoc, false).isPresent()) {
      tgtStruct = getAssocStructByUnmod(tgtCLasses.a, tgtAssoc, false).get();
    } else if (getAssocStructByUnmod(tgtCLasses.b, tgtAssoc, false).isPresent()) {
      tgtStruct = getAssocStructByUnmod(tgtCLasses.b, tgtAssoc, false).get();
    }
    return new Pair<>(srcStruct, tgtStruct);
  }

  /**
   * Get the matching AssocStructs for a given association in srcCD/tgtCD.
   *
   * @param astcdType associated type.
   * @param association unmodified association from srcCD/tgtCD.
   * @param isSource boolean to determine if the source or target CD should be used.
   * @return AssocStruct for the association.
   */
  public Optional<AssocStruct> getAssocStructByUnmod(ASTCDType astcdType, ASTCDAssociation association, boolean isSource) {
    // Select the appropriate map and CD based on the isSource flag
    ArrayListMultimap<ASTCDType, AssocStruct> mapToUse = isSource ? srcMap : tgtMap;
    ASTCDCompilationUnit cdToUse = isSource ? srcCD : tgtCD;

    // Iterate through the AssocStructs for the given ASTCDType
    for (AssocStruct struct : mapToUse.get(astcdType)) {
      if (sameAssociation(struct.getUnmodifiedAssoc(), association, cdToUse)) {
        return Optional.of(struct);
      }
    }
    return Optional.empty();
  }

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
          deleteAssocOtherSide(assocStruct, true);
        }
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
          && CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(role.getName())) {
        if (isOtherSideNeeded(assocStruct)) {
          notInstClassesSrc.add(getConnectedTypes(assocStruct.getAssociation(), srcCD).a);
          srcMap.removeAll(getConnectedTypes(assocStruct.getAssociation(), srcCD).a);
        } else {
          deleteAssocOtherSide(assocStruct, true);
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
      return assocStruct.getSide().equals(ClassSide.Right)
              && (assocStruct.getAssociation().getLeft().getCDCardinality().isOne()
              || assocStruct.getAssociation().getLeft().getCDCardinality().isAtLeastOne());
  }

  /**
   * Check for all compositions if a subcomponent cannot be instantiated. If this is the case, the
   * composite class cannot be instantiated either.
   */
  public void deleteCompositions() {
    for (ASTCDType astcdType : srcMap.keySet()) {
      for (ASTCDAssociation association : getCDAssociationsListForType(astcdType, true)) {
        Pair<ASTCDType, ASTCDType> pair = Syn2SemDiffHelper.getConnectedTypes(association, srcCD);
        Optional<AssocStruct> assocStruct = getAssocStructByUnmod(pair.a, association, true);
        if (association.getCDAssocType().isComposition() && assocStruct.isPresent()) {
          if (getNotInstClassesSrc().contains(pair.b)
              || (pair.b.getModifier().isAbstract() && minSubClass(pair.b, true).isEmpty())) {
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
      for (ASTCDAssociation association : getCDAssociationsListForType(astcdType, false)) {
        Pair<ASTCDType, ASTCDType> pair = Syn2SemDiffHelper.getConnectedTypes(association, tgtCD);
        Optional<AssocStruct> assocStruct = getAssocStructByUnmod(pair.a, association, false);
        if (association.getCDAssocType().isComposition() && assocStruct.isPresent()) {
          if (getNotInstClassesTgt().contains(pair.b)
              || (pair.b.getModifier().isAbstract() && minSubClass(pair.b, false).isEmpty())) {
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
          deleteAssocOtherSide(assocStruct, false);
        }
        iterator.remove();
      }
      if (assocStruct.getSide().equals(ClassSide.Right)
          && CDDiffUtil.inferRole(assocStruct.getAssociation().getLeft()).equals(role.getName())) {
        if (isOtherSideNeeded(assocStruct)) {
          notInstClassesTgt.add(getConnectedTypes(assocStruct.getAssociation(), tgtCD).a);
          tgtMap.removeAll(getConnectedTypes(assocStruct.getAssociation(), tgtCD).a);
        } else {
          deleteAssocOtherSide(assocStruct, false);
        }
        iterator.remove();
      }
    }
  }

  public List<AssocStruct> deletedAssocsForClass(ASTCDType astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDAssociation association : deletedAssocs) {
      Optional<AssocStruct> matched = getAssocStructByUnmod(astcdClass, association, false);
      matched.ifPresent(list::add);
    }
    return list;
  }

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
                    this,
                  matchingStrategies));
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

  // TODO: Check if can be merged with srcAssocsExist
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
                    this,
                  matchingStrategies));
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

  public List<Pair<ASTCDClass, AssocStruct>> assocsExist(
    List<AssocStruct> assocStructs, ASTCDType astcdType, boolean isSource) {
    ArrayListMultimap<ASTCDType, AssocStruct> mapToUse = isSource ? srcMap : tgtMap;
    ArrayListMultimap<ASTCDType, ASTCDClass> subMap = isSource ? srcSubMap : tgtSubMap;
    List<Pair<ASTCDClass, AssocStruct>> list = new ArrayList<>();
    for (AssocStruct assocStruct : assocStructs) {
      boolean foundMatch = false;
      for (AssocStruct assocStructTgt : mapToUse.get(astcdType)) {
        if (sameAssociationTypeSrcTgt(assocStruct, assocStructTgt)) { // the given pair is a match
          foundMatch = true;
          break;
        } else if (matcher.isMatched(// TODO: here must be one more change
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
                this,
                matchingStrategies));
          }
          break;
        }
      }
      if (!foundMatch) { // if a match for the assocStruct from src is not found in tgt - create a
        // diff-witness
        ASTCDClass classToUse = null;
        ASTCDType type = isSource? astcdType : findMatchedTypeSrc(astcdType).get();
        if (type instanceof ASTCDClass && !type.getModifier().isAbstract()) {
          classToUse = (ASTCDClass) type;
        } else {
          for (ASTCDClass subClass :
            subMap.get(
              findMatchedTypeSrc(astcdType)
                .get())) { // search for a class that doesn't have the association in tgtCD.
            Optional<ASTCDType> subTgt = isSource? findMatchedTypeTgt(subClass) : findMatchedTypeSrc(subClass);
            if (subTgt.isPresent()
              && ((isSource && !classHasAssociationSrcTgt(assocStruct, subTgt.get()))
            || (!isSource && !classHasAssociationTgtSrc(assocStruct, subTgt.get()))) ) {
              classToUse = subClass;
              break;
            }
          }
        }
        if (classToUse != null) {
          if (isSource) {
            for (AssocStruct subAssoc : mapToUse.get(classToUse)) {
              if (sameAssociationType(subAssoc, assocStruct)) {
                list.add(new Pair<>(classToUse, subAssoc));
                break;
              }
            }
          } else {
            list.add(new Pair<>(classToUse, assocStruct));
          }
        }
      }
    }
    return list;
  }

  public List<AssocStruct> addedAssocsForClass(ASTCDType astcdClass) {
    List<AssocStruct> list = new ArrayList<>();
    for (ASTCDAssociation association : addedAssocs) {
      Optional<AssocStruct> matched = getAssocStructByUnmod(astcdClass, association, true);
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
      left = minSubClass(pair.a, true);
    }
    if (pair.b.getModifier().isAbstract() || pair.b instanceof ASTCDInterface) {
      right = minSubClass(pair.b, true);
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

  public OverlappingAssocsDirect computeDirectForTypeNew(
    ASTCDType astcdType,
    ArrayListMultimap<ASTCDType, AssocStruct> map,
    ASTCDCompilationUnit compilationUnit) {

    Set<Pair<AssocStruct, AssocStruct>> directOverlappingAssocs = new HashSet<>();
    Set<Pair<AssocStruct, AssocStruct>> directOverlappingNoRelation = new HashSet<>();

    List<AssocStruct> assocStructs = map.get(astcdType);
    for (AssocStruct assoc1 : assocStructs) {
      for (AssocStruct assoc2 : assocStructs) {
        if (assoc1 != assoc2 && !pairExists(directOverlappingAssocs, assoc1, assoc2)) {
          if (inInheritanceRelation(assoc1, assoc2, compilationUnit)) {
            Pair<ASTCDType, ASTCDType> types1 = getConnectedTypes(assoc1.getAssociation(), compilationUnit);
            Pair<ASTCDType, ASTCDType> types2 = getConnectedTypes(assoc2.getAssociation(), compilationUnit);

            if (areTypesValid(types1, types2)) {
              AssocStruct sub = assoc1;
              AssocStruct sup = assoc2;

              if (isSwappable(assoc1, assoc2, types1, types2)) {
                sub = assoc2;
                sup = assoc1;
              }
              updateAssocUsage(sub, sup, compilationUnit);

              directOverlappingAssocs.add(new Pair<>(sub, sup));
            }
          }
        }
      }
    }
    return new OverlappingAssocsDirect(directOverlappingAssocs, directOverlappingNoRelation);
  }

  private boolean pairExists(Set<Pair<AssocStruct, AssocStruct>> set, AssocStruct a, AssocStruct b) {
    return set.stream().anyMatch(pair -> (pair.a == b && pair.b == a));
  }

  private boolean areTypesValid(Pair<ASTCDType, ASTCDType> pair1, Pair<ASTCDType, ASTCDType> pair2) {
    return pair1.a != null && pair1.b != null && pair2.a != null && pair2.b != null;
  }

  private boolean isSwappable(AssocStruct assoc1, AssocStruct assoc2, Pair<ASTCDType, ASTCDType> pair1, Pair<ASTCDType, ASTCDType> pair2) {
    return assoc1.getSide().equals(ClassSide.Left) && assoc2.getSide().equals(ClassSide.Left) && pair1.a.equals(pair2.a) && pair1.b.equals(pair2.b)
      || assoc1.getSide().equals(ClassSide.Right) && assoc2.getSide().equals(ClassSide.Right) && pair1.a.equals(pair2.a) && pair1.b.equals(pair2.b)
      || assoc1.getSide().equals(ClassSide.Left) && assoc2.getSide().equals(ClassSide.Right) && pair1.a.equals(pair2.b) && pair1.b.equals(pair2.a)
      || assoc1.getSide().equals(ClassSide.Right) && assoc2.getSide().equals(ClassSide.Left) && pair1.a.equals(pair2.b) && pair1.b.equals(pair2.a);
  }

  private void updateAssocUsage(AssocStruct sub, AssocStruct sup, ASTCDCompilationUnit compilationUnit) {
    setOtherSideUsage(sub, AssocType.SUB, compilationUnit);
    setOtherSideUsage(sup, AssocType.SUPER, compilationUnit);
    if (sub.getUsedAs() == null) {
      sub.setUsedAs(AssocType.SUB);
    }
    if (sup.getUsedAs() == null) {
      sup.setUsedAs(AssocType.SUPER);
    }
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
    MatchCDTypesByName nameTypeMatch = new MatchCDTypesByName(tgtCD);
    MatchCDTypesToSuperTypes superTypeMatchNameType =
        new MatchCDTypesToSuperTypes(nameTypeMatch, srcCD, tgtCD);
    matcher = new MatchCDAssocsBySrcNameAndTgtRole(superTypeMatchNameType, srcCD, tgtCD);
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

    // Process source associations
    processAssociationMap(srcMap, srcCD, srcAssocsToMergeWithDelete, srcAssocsToDelete, srcToDelete, true);

    // Process target associations
    processAssociationMap(tgtMap, tgtCD, tgtAssocsToMergeWithDelete, tgtAssocsToDelete, tgtToDelete, false);

    // Handle deletions and merges for src
    handleDeletionsAndMerges(srcToDelete, srcAssocsToMergeWithDelete, srcAssocsToDelete, true);

    // Handle deletions and merges for tgt
    handleDeletionsAndMerges(tgtToDelete, tgtAssocsToMergeWithDelete, tgtAssocsToDelete, false);
  }

  // Helper method to process associations
  private void processAssociationMap(ArrayListMultimap<ASTCDType, AssocStruct> assocMap, ASTCDCompilationUnit cdType,
                                     Set<DeleteStruct> assocsToMergeWithDelete,
                                     Set<Pair<ASTCDType, ASTCDRole>> assocsToDelete,
                                     Set<ASTCDType> toDelete,
                                     boolean isSrc) {
    for (ASTCDType astcdClass : assocMap.keySet()) {
      List<Pair<AssocStruct, AssocStruct>> sameAssocs = getPairsForType(astcdClass, assocMap, cdType);
      for (Pair<AssocStruct, AssocStruct> pair : sameAssocs) {
        AssocStruct association = pair.a;
        AssocStruct superAssoc = pair.b;
        if (isMergable(association, superAssoc, astcdClass, assocsToMergeWithDelete, isSrc)) {
          assocsToMergeWithDelete.add(new DeleteStruct(association, superAssoc, astcdClass));
        } else if (isDeletable(association, superAssoc, isSrc)) {
          if (areZeroAssocs(association, superAssoc)) {
            assocsToDelete.add(new Pair<>(astcdClass, getConflict(association, superAssoc)));
          } else {
            toDelete.add(astcdClass);
          }
        }
      }
    }
  }

  // Determine if association is mergable
  private boolean isMergable(AssocStruct association, AssocStruct superAssoc, ASTCDType astcdClass,
                             Set<DeleteStruct> assocsToMergeWithDelete, boolean isSrc) {
    return (sameAssocStruct(association, superAssoc)
      || sameAssocStructInReverse(association, superAssoc))
      && !isAdded(association, superAssoc, astcdClass, assocsToMergeWithDelete)
      || isInConflict(association, superAssoc)
      && inInheritanceRelation(association, superAssoc, isSrc? srcCD : tgtCD);
  }

  // Determine if association is deletable
  private boolean isDeletable(AssocStruct association, AssocStruct superAssoc,
                              boolean isSrc) {
    return isInConflict(association, superAssoc)
      && !inInheritanceRelation(association, superAssoc, isSrc? srcCD : tgtCD)
      && !getConnectedTypes(association.getAssociation(), isSrc? srcCD : tgtCD)
      .equals(getConnectedTypes(superAssoc.getAssociation(), isSrc? srcCD : tgtCD));
  }

  // Handle deletions and merges for src or tgt
  private void handleDeletionsAndMerges(Set<ASTCDType> toDelete,
                                        Set<DeleteStruct> assocsToMergeWithDelete,
                                        Set<Pair<ASTCDType, ASTCDRole>> assocsToDelete,
                                        boolean isSrc) {
    for (ASTCDType astcdClass : toDelete) {
      if (isSrc) {
        updateSrc(astcdClass);
        deleteClassAndSubclasses(astcdClass, true);
      } else {
        updateTgt(astcdClass);
        deleteClassAndSubclasses(astcdClass, false);
      }
    }
    for (DeleteStruct pair : assocsToMergeWithDelete) {
      if ((isSrc && !getNotInstClassesSrc().contains(pair.getAstcdClass()))
        || (!isSrc && !getNotInstClassesTgt().contains(pair.getAstcdClass()))) {
        setBiDirRoleName(pair.getAssociation(), pair.getSuperAssoc());
        mergeAssocs(pair.getAssociation(), pair.getSuperAssoc());
      }
    }
    removeAssociations(assocsToMergeWithDelete, isSrc);
    deleteAssociations(assocsToDelete, isSrc);
  }

  // Delete a class and its subclasses
  private void deleteClassAndSubclasses(ASTCDType astcdClass, boolean isSrc) {
    if (isSrc) {
      getSrcMap().removeAll(astcdClass);
      deleteOtherSide(astcdClass, true);
      for (ASTCDType subClass : getSrcSubMap().get(astcdClass)) {
        getSrcMap().removeAll(subClass);
        deleteOtherSide(subClass, true);
      }
    } else {
      getTgtMap().removeAll(astcdClass);
      deleteOtherSide(astcdClass, false);
      for (ASTCDType subClass : getTgtSubMap().get(astcdClass)) {
        getTgtMap().removeAll(subClass);
        deleteOtherSide(subClass, false);
      }
    }
  }

  // Remove associations
  private void removeAssociations(Set<DeleteStruct> assocsToMergeWithDelete, boolean isSrc) {
    for (DeleteStruct pair : assocsToMergeWithDelete) {
      if (isSrc) {
        getSrcMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
      } else {
        getTgtMap().remove(pair.getAstcdClass(), pair.getSuperAssoc());
      }
    }
  }

  // Delete associations
  private void deleteAssociations(Set<Pair<ASTCDType, ASTCDRole>> assocsToDelete, boolean isSrc) {
    for (Pair<ASTCDType, ASTCDRole> pair : assocsToDelete) {
      if (isSrc) {
        deleteAssocsFromSrc(pair.a, pair.b);
      } else {
        deleteAssocsFromTgt(pair.a, pair.b);
      }
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

  // TODO: Merge those two
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
          deleteAssocOtherSide(subAssoc, false);
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
          deleteAssocOtherSide(subAssoc, true);
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
