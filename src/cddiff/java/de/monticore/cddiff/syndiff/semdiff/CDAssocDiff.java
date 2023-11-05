package de.monticore.cddiff.syndiff.semdiff;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isSuperOf;
import static de.monticore.cddiff.cdsyntax2semdiff.odgen.Syn2SemDiffHelper.*;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.cdsyntax2semdiff.odgen.Syn2SemDiffHelper;
import de.monticore.cddiff.cdsyntax2semdiff.datastructures.AssocCardinality;
import de.monticore.cddiff.cdsyntax2semdiff.datastructures.AssocDirection;
import de.monticore.cddiff.cdsyntax2semdiff.datastructures.AssocStruct;
import de.monticore.cddiff.cdsyntax2semdiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.interfaces.ICDAssocDiff;
import de.monticore.matcher.MatchingStrategy;
import de.monticore.matcher.NameTypeMatcher;
import de.monticore.matcher.StructureTypeMatcher;
import de.monticore.matcher.SuperTypeMatcher;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.*;

/**
 * This class computes the differences between two ASTCDAssociation nodes. It analyzes the
 * role names, cardinalities, direction, and associated classes.
 */
public class CDAssocDiff extends SyntaxDiffHelper implements ICDAssocDiff {
  private final ASTCDAssociation srcElem;
  private final ASTCDAssociation tgtElem;
  private final ASTCDCompilationUnit srcCD;
  private final ASTCDCompilationUnit tgtCD;
  ASTCDType srcLeftType, srcRightType, tgtLeftType, tgtRightType;
  private boolean isReversed;
  private List<DiffTypes> baseDiff;
  List<ASTCDType> srcCDTypes;
  private Syn2SemDiffHelper helper;
  private Pair<ASTCDAssocSide, ASTCDAssocSide> srcSide;
  private Pair<ASTCDAssocSide, ASTCDAssocSide> tgtSide;

  private AssocStruct srcStruct;
  private AssocStruct tgtStruct;

  NameTypeMatcher nameTypeMatch;
  StructureTypeMatcher structureTypeMatch;
  SuperTypeMatcher superTypeMatchStructure;
  SuperTypeMatcher superTypeMatchName;
  List<MatchingStrategy<ASTCDType>> typeMatchers;
  // Print
  private final CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String srcAssocType,
      srcAssocName,
      srcAssocLeftCardinality,
      srcAssocLeftType,
      srcAssocLeftRole,
      srcAssocDirection,
      srcAssocRightCardinality,
      srcAssocRightType,
      srcAssocRightRole,
      tgtAssoc,
      srcAssoc,
      tgtAssocDeleted,
      srcAssocAdded,
      assocDiff;
  private String tgtAssocType,
      tgtAssocName,
      tgtAssocLeftCardinality,
      tgtAssocLeftType,
      tgtAssocLeftRole,
      tgtAssocDirection,
      tgtAssocRightCardinality,
      tgtAssocRightType,
      tgtAssocRightRole;
  int srcLineOfCode, tgtLineOfCode;
  // Print end

  public CDAssocDiff(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD, Syn2SemDiffHelper helper) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    this.helper = helper;
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    this.baseDiff = new ArrayList<>();
    nameTypeMatch = new NameTypeMatcher(tgtCD);
    structureTypeMatch = new StructureTypeMatcher(tgtCD);
    superTypeMatchName = new SuperTypeMatcher(nameTypeMatch, srcCD, tgtCD);
    superTypeMatchStructure = new SuperTypeMatcher(structureTypeMatch, srcCD, tgtCD);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatchStructure);
    typeMatchers.add(superTypeMatchName);
    srcCDTypes = new ArrayList<>();
    srcCDTypes.add(
        srcCD
            .getEnclosingScope()
            .resolveCDTypeDown(srcElem.getLeftQualifiedName().getQName())
            .get()
            .getAstNode());
    srcCDTypes.add(
        srcCD
            .getEnclosingScope()
            .resolveCDTypeDown(srcElem.getRightQualifiedName().getQName())
            .get()
            .getAstNode());
    assocDiff(srcElem, tgtElem, typeMatchers);
    setStrings();
  }

  @Override
  public ASTCDAssociation getSrcElem() {
    return srcElem;
  }

  @Override
  public ASTCDAssociation getTgtElem() {
    return tgtElem;
  }

  @Override
  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }

  @Override
  public void setStructs() {
    Pair<AssocStruct, AssocStruct> pair = helper.getStructsForAssocDiff(srcElem, tgtElem);
    srcStruct = pair.a;
    tgtStruct = pair.b;
    srcSide = new Pair<>(pair.a.getAssociation().getLeft(), pair.a.getAssociation().getRight());
    tgtSide = new Pair<>(pair.b.getAssociation().getLeft(), pair.b.getAssociation().getRight());
  }

  private AssocCardinality getTypeOfCard(ASTCDCardinality cardinality) {
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
  /**
   * Find the difference in the cardinalities of an association. Each pair has the association side
   * with the lowest number that is in the new cardinality but not in the old one.
   *
   * @return list with one or two pairs.
   */
  @Override
  public Pair<ASTCDAssociation, List<Pair<ClassSide, Integer>>> getCardDiff() {
    List<Pair<ClassSide, Integer>> list = new ArrayList<>();
    if (!isReversed) {
      // assoc not reversed
      if (!isContainedIn(
          cardToEnum(srcSide.a.getCDCardinality()), cardToEnum(tgtSide.a.getCDCardinality()))) {
        list.add(
            new Pair<>(
                ClassSide.Left,
                findUniqueNumber(
                    getTypeOfCard(srcSide.a.getCDCardinality()),
                    getTypeOfCard(tgtSide.a.getCDCardinality()))));
      }
      if (!isContainedIn(
          cardToEnum(srcSide.b.getCDCardinality()), cardToEnum(tgtSide.b.getCDCardinality()))) {
        list.add(
            new Pair<>(
                ClassSide.Right,
                findUniqueNumber(
                    getTypeOfCard(srcSide.b.getCDCardinality()),
                    getTypeOfCard(tgtSide.b.getCDCardinality()))));
      }
    } else {
      if (!isContainedIn(
          cardToEnum(srcSide.a.getCDCardinality()), cardToEnum(tgtSide.b.getCDCardinality()))) {
        list.add(
            new Pair<>(
                ClassSide.Left,
                findUniqueNumber(
                    getTypeOfCard(srcSide.a.getCDCardinality()),
                    getTypeOfCard(tgtSide.b.getCDCardinality()))));
      }
      if (!isContainedIn(
          cardToEnum(srcSide.b.getCDCardinality()), cardToEnum(tgtSide.a.getCDCardinality()))) {
        list.add(
            new Pair<>(
                ClassSide.Right,
                findUniqueNumber(
                    getTypeOfCard(srcSide.b.getCDCardinality()),
                    getTypeOfCard(tgtSide.a.getCDCardinality()))));
      }
    }
    return new Pair<>(srcElem, list);
  }

  // CHECKED
  @Override
  public boolean isDirectionChanged() {
    if (isReversed) {
        return (!getDirection(srcStruct.getAssociation()).equals(AssocDirection.LeftToRight)
                || !getDirection(tgtStruct.getAssociation()).equals(AssocDirection.RightToLeft))
                && ((!getDirection(srcStruct.getAssociation()).equals(AssocDirection.RightToLeft)
                || !getDirection(tgtStruct.getAssociation()).equals(AssocDirection.LeftToRight))
                && ((!getDirection(srcStruct.getAssociation()).equals(AssocDirection.BiDirectional))
                || !getDirection(tgtStruct.getAssociation())
                .equals(AssocDirection.BiDirectional)));
    } else {
      return !getDirection(srcStruct.getAssociation())
          .equals(getDirection(tgtStruct.getAssociation()));
    }
  }

  // CHECKED
  @Override
  public Pair<ASTCDAssociation, List<Pair<ClassSide, ASTCDRole>>> getRoleDiff() {
    List<Pair<ClassSide, ASTCDRole>> list = new ArrayList<>();
    if (!isReversed) {
      // assoc not reversed
      if (!Objects.equals(srcSide.a.getCDRole().getName(), tgtSide.a.getCDRole().getName())) {
        list.add(new Pair<>(ClassSide.Left, srcSide.a.getCDRole()));
      }
      if (!Objects.equals(srcSide.b.getCDRole().getName(), tgtSide.b.getCDRole().getName())) {
        list.add(new Pair<>(ClassSide.Right, srcSide.b.getCDRole()));
      }
    } else {
      if (!Objects.equals(srcSide.a.getCDRole().getName(), tgtSide.b.getCDRole().getName())) {
        list.add(new Pair<>(ClassSide.Left, srcSide.a.getCDRole()));
      }
      if (!Objects.equals(srcSide.b.getCDRole().getName(), tgtSide.a.getCDRole().getName())) {
        list.add(new Pair<>(ClassSide.Right, srcSide.b.getCDRole()));
      }
    }
    return new Pair<>(srcElem, list);
  }

  // CHECKED
  /**
   * Find the lowest integer that is the first interval but not in the second.
   *
   * @param interval1 new cardinality
   * @param interval2 old cardinality
   * @return integer representing the difference.
   */
  public static Integer findUniqueNumber(AssocCardinality interval1, AssocCardinality interval2) {

    if (interval1.equals(AssocCardinality.One)) {
      return 0;
    } else if (interval1.equals(AssocCardinality.Optional)) {
      if (interval2.equals(AssocCardinality.AtLeastOne) || interval2.equals(AssocCardinality.One)) {
        return 0;
      } else {
        return null;
      }
    } else if (interval1.equals(AssocCardinality.AtLeastOne)) {
      if (interval2.equals(AssocCardinality.One) || interval2.equals(AssocCardinality.Optional)) {
        return 2;
      } else {
        return null;
      }
    } else if (interval1.equals(AssocCardinality.Multiple)) {
      if (interval2.equals(AssocCardinality.One) || interval2.equals(AssocCardinality.AtLeastOne)) {
        return 0;
      } else if (interval2.equals(AssocCardinality.Optional)) {
        return 2;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  // CHECKED
  public ASTCDType changedSrc() {
    Pair<ASTCDType, ASTCDType> pairNew = getConnectedTypes(getSrcElem(), helper.getSrcCD());
    Pair<ASTCDType, ASTCDType> pairOld = getConnectedTypes(getTgtElem(), helper.getTgtCD());
    ASTCDType leftNew = pairNew.a;
    ASTCDType leftOld = pairOld.a;
    ASTCDType rightNew = pairNew.b;
    ASTCDType rightOld = pairOld.b;
    if (isReversed) {
      leftOld = pairOld.b;
      rightOld = pairOld.a;
    }
    if (srcStruct.getAssociation().getCDAssocDir().isDefinitiveNavigableRight()) {
      //        if (changedToSuper(leftNew, leftOld)) {
      //          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getSrcCD(), leftNew);
      //          subclassesA.remove(helper.findMatchedSrc(leftOld));
      //          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getTgtCD(), leftOld);
      //          subclassesA.removeAll(helper.getSrcClasses(inheritance));
      //          subclassesA.add(leftNew);
      //          for (ASTCDClass subclass : subclassesA) {
      //            if (!subclass.getModifier().isAbstract()) {
      //              if (helper.findMatchedClass(subclass) != null
      //                && !helper.classHasAssociationTgtTgt(tgtStruct,
      // helper.findMatchedClass(subclass))) {
      //                return subclass;
      //              }
      //            }
      //          }
      //        }
      if (!(tgtSide.b.getCDCardinality().isOpt() || tgtSide.b.getCDCardinality().isMult())) {
        List<ASTCDClass> subclassesA = helper.getTgtSubMap().get(leftOld);
        Optional<ASTCDType> matchedTypeTgt = helper.findMatchedTypeTgt(leftNew);
        matchedTypeTgt.ifPresent(astcdType -> subclassesA.removeIf(subclass -> subclass.equals(astcdType)));
        //subclassesA.remove(helper.findMatchedTypeTgt(leftNew).get());
        List<ASTCDType> subClassesASrc = helper.getSrcTypes(subclassesA);
        List<ASTCDClass> inheritance = helper.getSrcSubMap().get(leftNew);
        subClassesASrc.removeAll(inheritance);
        for (ASTCDType subclass : subClassesASrc) {
          if (!subclass.getModifier().isAbstract()
              && !(helper.classHasAssociationSrcSrc(srcStruct, subclass)
              || helper.classHasAssociationTgtSrc(tgtStruct, subclass))) {
            return subclass;
          }
        }
      }
    } else if (srcStruct.getAssociation().getCDAssocDir().isDefinitiveNavigableLeft()) {
      //        if (changedToSuper(rightNew, rightOld)) {
      //          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getSrcCD(), rightNew);
      //          subclassesA.remove(helper.findMatchedSrc(rightOld));
      //          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getTgtCD(), rightOld);
      //          subclassesA.removeAll(helper.getSrcClasses(inheritance));
      //          subclassesA.add(rightNew);
      //          for (ASTCDClass subclass : subclassesA) {
      //            if (!subclass.getModifier().isAbstract()) {
      //              //search with isSubAssociation() - done
      //              if (helper.findMatchedClass(subclass) != null
      //                && !helper.classHasAssociationTgtTgt(tgtStruct,
      // helper.findMatchedClass(subclass))) {
      //                return subclass;
      //              }
      //            }
      //          }
      //        }
      if (!(tgtSide.a.getCDCardinality().isOpt() || tgtSide.a.getCDCardinality().isMult())) {
        // check if th matched class of the old one is abstract - done
        List<ASTCDClass> subclassesA = helper.getTgtSubMap().get(rightOld);
        Optional<ASTCDType> matchedTypeTgt = helper.findMatchedTypeTgt(rightNew);
        matchedTypeTgt.ifPresent(astcdType -> subclassesA.removeIf(subclass -> subclass.equals(astcdType)));
        //subclassesA.remove(helper.findMatchedTypeTgt(rightNew).get());
        List<ASTCDType> subClassesASrc = helper.getSrcTypes(subclassesA);
        List<ASTCDClass> inheritance = helper.getSrcSubMap().get(rightNew);
        subClassesASrc.removeAll(inheritance);
        for (ASTCDType subclass : subClassesASrc) {
          if (!subclass.getModifier().isAbstract()
              && !(helper.classHasAssociationSrcSrc(srcStruct, subclass)
              || helper.classHasAssociationTgtSrc(tgtStruct, subclass))) {
            return subclass;
          }
        }
      }
    }
    return null;
  }

  // CHECKED
  public ASTCDType changedTgt() {
    Pair<ASTCDType, ASTCDType> pairNew = getConnectedTypes(getSrcElem(), helper.getSrcCD());
    Pair<ASTCDType, ASTCDType> pairOld = getConnectedTypes(getTgtElem(), helper.getTgtCD());
    ASTCDType leftNew = pairNew.a;
    ASTCDType leftOld = pairOld.a;
    ASTCDType rightNew = pairNew.b;
    ASTCDType rightOld = pairOld.b;
    if (isReversed) {
      leftOld = pairOld.b;
      rightOld = pairOld.a;
    }
    if (srcStruct.getAssociation().getCDAssocDir().isDefinitiveNavigableRight()) {
      //        if (changedToSuper(rightNew, rightOld)) {
      //          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getSrcCD(), rightNew);
      //          subclassesA.remove(helper.findMatchedSrc(rightOld));
      //          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getTgtCD(), rightOld);
      //          subclassesA.removeAll(helper.getSrcClasses(inheritance));
      //          subclassesA.add(rightNew);
      //          for (ASTCDClass subclass : subclassesA) {
      //            if (!subclass.getModifier().isAbstract()) {
      //              if (helper.findMatchedClass(subclass) != null
      //                && !helper.classIsTgtTgtTgt(tgtStruct, helper.findMatchedClass(subclass))) {
      //                return subclass;
      //              }
      //            }
      //          }
      //        }
      if (!(tgtSide.a.getCDCardinality().isOpt() || tgtSide.a.getCDCardinality().isMult())) {
        List<ASTCDClass> subclassesA = helper.getTgtSubMap().get(rightOld);
        Optional<ASTCDType> matchedTypeTgt = helper.findMatchedTypeTgt(rightNew);
        matchedTypeTgt.ifPresent(astcdType -> subclassesA.removeIf(subclass -> subclass.equals(astcdType)));
        //subclassesA.remove(helper.findMatchedTypeTgt(rightNew).get());
        List<ASTCDType> subClassesASrc = helper.getSrcTypes(subclassesA);
        List<ASTCDClass> inheritance = helper.getSrcSubMap().get(rightNew);
        subClassesASrc.removeAll(inheritance);
        for (ASTCDType subclass : subClassesASrc) {
//          Optional<ASTCDType> matchedClass = helper.findMatchedTypeTgt(subclass);
//          if (matchedClass.isPresent()
//              && srcStruct.getAssociation().getCDAssocDir().isBidirectional()
//              && tgtStruct.getAssociation().getCDAssocDir().isBidirectional()
//              && !matchedClass.get().getModifier().isAbstract()
//              && !helper.classHasAssociationTgtTgt(tgtStruct, matchedClass.get())) {
//            return subclass;
//          }
          if (!subclass.getModifier().isAbstract()
            && !(helper.classIsTarget(srcStruct, subclass)
            || helper.classIsTargetTgtSrc(tgtStruct, subclass))) {
            return subclass;
          }
        }
      }
    } else if (srcStruct.getAssociation().getCDAssocDir().isDefinitiveNavigableLeft()) {
      //        if (changedToSuper(leftNew, leftOld)) {
      //          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getSrcCD(), leftNew);
      //          subclassesA.remove(helper.findMatchedSrc(leftOld));
      //          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getTgtCD(), leftOld);
      //          subclassesA.removeAll(helper.getSrcClasses(inheritance));
      //          subclassesA.add(leftNew);
      //          for (ASTCDClass subclass : subclassesA) {
      //            if (!subclass.getModifier().isAbstract()) {
      //              if (helper.findMatchedClass(subclass) != null
      //                && !helper.classIsTgtTgtTgt(tgtStruct, helper.findMatchedClass(subclass))) {
      //                return subclass;
      //              }
      //            }
      //          }
      //        }
      if (!(tgtSide.b.getCDCardinality().isOpt() || tgtSide.b.getCDCardinality().isMult())) {
        List<ASTCDClass> subclassesA = helper.getTgtSubMap().get(leftOld);
        Optional<ASTCDType> matchedTypeTgt = helper.findMatchedTypeTgt(leftNew);
        matchedTypeTgt.ifPresent(astcdType -> subclassesA.removeIf(subclass -> subclass.equals(astcdType)));
        List<ASTCDType> subClassesASrc = helper.getSrcTypes(subclassesA);
        List<ASTCDClass> inheritance = helper.getSrcSubMap().get(leftNew);
        subClassesASrc.removeAll(inheritance);
        for (ASTCDType subclass : subClassesASrc) {
          if (!subclass.getModifier().isAbstract()
            && !(helper.classIsTarget(srcStruct, subclass)
            || helper.classIsTargetTgtSrc(tgtStruct, subclass))) {
            return subclass;
          }
        }
      }
    }
    return null;
  }

  public boolean changedToSuper(ASTCDClass classNew, ASTCDClass classOld) {
    return isSuperOf(
        classNew.getSymbol().getInternalQualifiedName().replace(".", "_"),
        classOld.getSymbol().getInternalQualifiedName().replace(".", "_"),
        (ICD4CodeArtifactScope) helper.getSrcCD().getEnclosingScope());
  }

  public boolean changedToSub(ASTCDClass classNew, ASTCDClass classOld) {
    return isSuperOf(
        classOld.getSymbol().getInternalQualifiedName().replace(".", "_"),
        classNew.getSymbol().getInternalQualifiedName().replace(".", "_"),
        (ICD4CodeArtifactScope) helper.getSrcCD().getEnclosingScope());
  }

  /*--------------------------------------------------------------------*/

  /**
   * Computes and stores differences between two ASTCDAssociation nodes. This method analyzes the
   * association type, name, direction, and associated classes.
   *
   * @param srcAssoc The source ASTCDAssociation.
   * @param tgtAssoc The target ASTCDAssociation.
   * @param typeMatchers List of matching strategies for CD types.
   */
  private void assocDiff(
      ASTCDAssociation srcAssoc,
      ASTCDAssociation tgtAssoc,
      List<MatchingStrategy<ASTCDType>> typeMatchers) {

    // Association Type
    Optional<ASTCDAssocType> srcAssocType = Optional.of(srcAssoc.getCDAssocType());
    Optional<ASTCDAssocType> tgtAssocType = Optional.of(tgtAssoc.getCDAssocType());
    CDNodeDiff<ASTCDAssocType, ASTCDAssocType> assocType =
        new CDNodeDiff<>(srcAssocType, tgtAssocType);
    this.srcAssocType = getColorCode(assocType) + pp.prettyprint(srcAssocType.get()) + RESET;
    this.tgtAssocType = getColorCode(assocType) + pp.prettyprint(tgtAssocType.get()) + RESET;

    // Name
    Optional<ASTCDAssociation> srcName =
        (srcAssoc.isPresentName()) ? Optional.of(srcAssoc) : Optional.empty();
    Optional<ASTCDAssociation> tgtName =
        (tgtAssoc.isPresentName()) ? Optional.of(tgtAssoc) : Optional.empty();
    CDNodeDiff<ASTCDAssociation, ASTCDAssociation> assocName =
        new CDNodeDiff<>(null, srcName, tgtName);

    if (srcName.isPresent() && tgtName.isPresent()) {
      if (!srcName.get().getName().equals(tgtName.get().getName())) {
        assocName = new CDNodeDiff<>(Actions.CHANGED, srcName, tgtName);
      }
    }
    if (srcName.isPresent() && tgtName.isEmpty()) {
      assocName = new CDNodeDiff<>(Actions.ADDED, srcName, tgtName);
    }
    if (srcName.isEmpty() && tgtName.isPresent()) {
      assocName = new CDNodeDiff<>(Actions.REMOVED, srcName, tgtName);
    }

    if (srcName.isPresent()) {
      this.srcAssocName = getColorCode(assocName) + srcName.get().getName() + RESET;
    }
    if (tgtName.isPresent()) {
      this.tgtAssocName = getColorCode(assocName) + tgtName.get().getName() + RESET;
    }

    if (assocName.checkForAction()) {
      if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_NAME)) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_NAME);
      }
    }

    // Association Direction
    Optional<ASTCDAssocDir> srcAssocDir = Optional.of(srcAssoc.getCDAssocDir());
    Optional<ASTCDAssocDir> tgtAssocDir = Optional.of(tgtAssoc.getCDAssocDir());
    CDNodeDiff<ASTCDAssocDir, ASTCDAssocDir> assocDiffDir =
        new CDNodeDiff<>(srcAssocDir, tgtAssocDir);

    if (assocDiffDir.checkForAction()) {
      if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_DIRECTION)) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_DIRECTION);
      }
    }

    srcAssocDirection = getColorCode(assocDiffDir) + pp.prettyprint(srcAssocDir.get()) + RESET;
    tgtAssocDirection = getColorCode(assocDiffDir) + pp.prettyprint(tgtAssocDir.get()) + RESET;

    // Differences in the sides
    Optional<CDTypeSymbol> srcLeftSymbol =
        srcCD.getEnclosingScope().resolveCDTypeDown(srcAssoc.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> srcRightSymbol =
        srcCD.getEnclosingScope().resolveCDTypeDown(srcAssoc.getRightQualifiedName().getQName());
    Optional<CDTypeSymbol> tgtLeftSymbol =
        tgtCD.getEnclosingScope().resolveCDTypeDown(tgtAssoc.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> tgtRightSymbol =
        tgtCD.getEnclosingScope().resolveCDTypeDown(tgtAssoc.getRightQualifiedName().getQName());
    srcLeftSymbol.ifPresent(cdTypeSymbol -> this.srcLeftType = cdTypeSymbol.getAstNode());
    srcRightSymbol.ifPresent(cdTypeSymbol -> this.srcRightType = cdTypeSymbol.getAstNode());
    tgtLeftSymbol.ifPresent(cdTypeSymbol -> this.tgtLeftType = cdTypeSymbol.getAstNode());
    tgtRightSymbol.ifPresent(cdTypeSymbol -> this.tgtRightType = cdTypeSymbol.getAstNode());

    Map<ASTCDType, ASTCDType> computedMatchingMapTypes =
        computeMatchingMapTypes(srcCDTypes, srcCD, tgtCD);

    for(Map.Entry<ASTCDType, ASTCDType> entry : computedMatchingMapTypes.entrySet()) {
      if( (entry.getKey().equals(srcLeftType) && entry.getValue().equals(tgtLeftType)) ||
        (entry.getKey().equals(srcRightType) && entry.getValue().equals(tgtRightType)) ) {
        isReversed = false;
        getAssocSideDiff(srcAssoc.getLeft(), tgtAssoc.getLeft());
        if (baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
            }
          }
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
            }
          }
        }
        getAssocSideDiff(srcAssoc.getRight(), tgtAssoc.getRight());
        if (baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
            }
          }
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
            }
          }
        }
      }
    }

    /*if (computedMatchingMapTypes.get(srcLeftType).equals(tgtLeftType)
        || computedMatchingMapTypes.get(srcRightType).equals(tgtRightType)) {
      isReversed = false;
      getAssocSideDiff(srcAssoc.getLeft(), tgtAssoc.getLeft());
      if (baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
        if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
          baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
          if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
            baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
          }
        }
        if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
          baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
          if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
            baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
          }
        }
      }
      getAssocSideDiff(srcAssoc.getRight(), tgtAssoc.getRight());
      if (baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
        if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
          baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
          if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
            baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
          }
        }
        if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
          baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
          if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
            baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
          }
        }
      }
    }*/

    for(Map.Entry<ASTCDType, ASTCDType> entry : computedMatchingMapTypes.entrySet()) {
      if( (entry.getKey().equals(srcLeftType) && entry.getValue().equals(tgtRightType)) ||
        (entry.getKey().equals(srcRightType) && entry.getValue().equals(tgtLeftType)) ) {
        isReversed = true;
        getAssocSideDiff(srcAssoc.getLeft(), tgtAssoc.getRight());
        if (baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
            }
          }
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
            }
          }
        }
        getAssocSideDiff(srcAssoc.getRight(), tgtAssoc.getLeft());
        if (baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
            }
          }
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
            }
          }
        }
      }
    }

    /*if (computedMatchingMapTypes.get(srcLeftType).equals(tgtRightType)
        || computedMatchingMapTypes.get(srcRightType).equals(tgtLeftType)) {
      isReversed = true;
      getAssocSideDiff(srcAssoc.getLeft(), tgtAssoc.getRight());
      if (baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
        if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
          baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
          if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
            baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
          }
        }
        if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
          baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
          if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
            baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
          }
        }
      }
      getAssocSideDiff(srcAssoc.getRight(), tgtAssoc.getLeft());
      if (baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
        if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
          baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
          if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
            baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
          }
        }
        if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && !srcAssoc.getCDAssocDir().isBidirectional()) {
          baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
          if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
            baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
          }
        }
      }
    }*/

    srcLineOfCode = srcAssoc.get_SourcePositionStart().getLine();
    tgtLineOfCode = tgtAssoc.get_SourcePositionStart().getLine();
  }

  /**
   * Computes and stores differences between two ASTCDAssocSide nodes. This method analyzes the
   * cardinality, qualified type, and role of an association side.
   *
   * @param srcAssocSide The source ASTCDAssocSide.
   * @param tgtAssocSide The target ASTCDAssocSide.
   */
  public void getAssocSideDiff(ASTCDAssocSide srcAssocSide, ASTCDAssocSide tgtAssocSide) {

    // Cardinality
    Optional<ASTCDCardinality> srcAssocCardinality =
        (srcAssocSide.isPresentCDCardinality())
            ? Optional.of(srcAssocSide.getCDCardinality())
            : Optional.empty();
    Optional<ASTCDCardinality> tgtAssocCardinality =
        (tgtAssocSide.isPresentCDCardinality())
            ? Optional.of(tgtAssocSide.getCDCardinality())
            : Optional.empty();
    CDNodeDiff<ASTCDCardinality, ASTCDCardinality> assocCardinality =
        new CDNodeDiff<>(srcAssocCardinality, tgtAssocCardinality);

    if (assocCardinality.checkForAction()) {
      if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY)) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY);
      }
    }

    // QualifiedType
    Optional<ASTMCQualifiedName> srcAssocType =
        Optional.of(srcAssocSide.getMCQualifiedType().getMCQualifiedName());
    Optional<ASTMCQualifiedName> tgtAssocType =
        Optional.of(tgtAssocSide.getMCQualifiedType().getMCQualifiedName());
    CDNodeDiff<ASTMCQualifiedName, ASTMCQualifiedName> typeDiff =
        new CDNodeDiff<>(srcAssocType, tgtAssocType);

    if (typeDiff.checkForAction()) {
      if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_CLASS);
      }
    }

    // CDRole
    Optional<ASTCDRole> srcAssocRole =
        (srcAssocSide.isPresentCDRole()) ? Optional.of(srcAssocSide.getCDRole()) : Optional.empty();
    Optional<ASTCDRole> tgtAssocRole =
        (tgtAssocSide.isPresentCDRole()) ? Optional.of(tgtAssocSide.getCDRole()) : Optional.empty();
    CDNodeDiff<ASTCDRole, ASTCDRole> assocRole = new CDNodeDiff<>(srcAssocRole, tgtAssocRole);

    if (assocRole.checkForAction()) {
      if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_ROLE)) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_ROLE);
      }
    }

    if (tgtAssocSide.isLeft()) {
      tgtAssocCardinality.ifPresent(
          astCardinality ->
              tgtAssocLeftCardinality =
                  getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
      tgtAssocLeftType = getColorCode(typeDiff) + pp.prettyprint(tgtAssocType.get()) + RESET;
      tgtAssocRole.ifPresent(
          role -> tgtAssocLeftRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
    } else {
      tgtAssocCardinality.ifPresent(
          astCardinality ->
              tgtAssocRightCardinality =
                  getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
      tgtAssocRightType = getColorCode(typeDiff) + pp.prettyprint(tgtAssocType.get()) + RESET;
      tgtAssocRole.ifPresent(
          role -> tgtAssocRightRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
    }
    if (srcAssocSide.isLeft()) {
      srcAssocCardinality.ifPresent(
          astCardinality ->
              srcAssocLeftCardinality =
                  getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
      srcAssocLeftType = getColorCode(typeDiff) + pp.prettyprint(srcAssocType.get()) + RESET;
      srcAssocRole.ifPresent(
          role -> srcAssocLeftRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
    } else {
      srcAssocCardinality.ifPresent(
          astCardinality ->
              srcAssocRightCardinality =
                  getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
      srcAssocRightType = getColorCode(typeDiff) + pp.prettyprint(srcAssocType.get()) + RESET;
      srcAssocRole.ifPresent(
          role -> srcAssocRightRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
    }
  }

  /**
   * Constructs various strings representing associations and their differences. This method builds
   * strings for the source association, target association, added association (green), removed
   * association (red), and the association differences.
   */
  private void setStrings() {

    // Build Source String
    srcAssoc =
        "\t"
            + "//new, L: "
            + srcLineOfCode
            + System.lineSeparator()
            + "\t"
            + insertSpaceBetweenStrings(
                Arrays.asList(
                    srcAssocType,
                    srcAssocName,
                    srcAssocLeftCardinality,
                    srcAssocLeftType,
                    srcAssocLeftRole,
                    srcAssocDirection,
                    srcAssocRightRole,
                    srcAssocRightType,
                    srcAssocRightCardinality));

    // Build Target String
    tgtAssoc =
        "\t"
            + "//old, L: "
            + tgtLineOfCode
            + System.lineSeparator()
            + "\t"
            + insertSpaceBetweenStrings(
                Arrays.asList(
                    tgtAssocType,
                    tgtAssocName,
                    tgtAssocLeftCardinality,
                    tgtAssocLeftType,
                    tgtAssocLeftRole,
                    tgtAssocDirection,
                    tgtAssocRightRole,
                    tgtAssocRightType,
                    tgtAssocRightCardinality));

    srcAssocAdded =
        "//added association, L: "
            + srcLineOfCode
            + System.lineSeparator()
            + insertSpaceBetweenStringsAndGreen(
                Arrays.asList(
                    srcAssocType,
                    srcAssocName,
                    srcAssocLeftCardinality,
                    srcAssocLeftType,
                    srcAssocLeftRole,
                    srcAssocDirection,
                    srcAssocRightRole,
                    srcAssocRightType,
                    srcAssocRightCardinality))
            + System.lineSeparator();

    // Build Target String
    tgtAssocDeleted =
        "//deleted association, L: "
            + tgtLineOfCode
            + System.lineSeparator()
            + insertSpaceBetweenStringsAndRed(
                Arrays.asList(
                    tgtAssocType,
                    tgtAssocName,
                    tgtAssocLeftCardinality,
                    tgtAssocLeftType,
                    tgtAssocLeftRole,
                    tgtAssocDirection,
                    tgtAssocRightRole,
                    tgtAssocRightType,
                    tgtAssocRightCardinality))
            + System.lineSeparator();

    // Build Assoc Diff
    assocDiff =
        "//changed association"
            + System.lineSeparator()
            + srcAssoc
            + System.lineSeparator()
            + tgtAssoc
            + System.lineSeparator();
  }

  @Override
  public String insertSpaceBetweenStrings(List<String> stringList) {
    return super.insertSpaceBetweenStrings(stringList) + ";";
  }

  @Override
  public String insertSpaceBetweenStringsAndGreen(List<String> stringList) {
    return super.insertSpaceBetweenStringsAndGreen(stringList) + COLOR_ADD + "; ";
  }

  @Override
  public String insertSpaceBetweenStringsAndRed(List<String> stringList) {
    return super.insertSpaceBetweenStringsAndRed(stringList) + COLOR_DELETE + "; ";
  }

  /**
   * Returns the source association string representation.
   *
   * @return The source association string.
   */
  public String printSrcAssoc() {
    return srcAssoc;
  }

  /**
   * Returns the added association string representation.
   *
   * @return The added association string.
   */
  public String printAddedAssoc() {
    return srcAssocAdded;
  }

  /**
   * Returns the deleted association string representation.
   *
   * @return The deleted association string.
   */
  public String printDeletedAssoc() {
    return tgtAssocDeleted;
  }

  /**
   * Returns the target association string representation.
   *
   * @return The target association string.
   */
  public String printTgtAssoc() {
    return tgtAssoc;
  }

  /**
   * Returns the changed association string representation.
   *
   * @return The changed association string.
   */
  public String printDiffAssoc() {
    return assocDiff;
  }
}
