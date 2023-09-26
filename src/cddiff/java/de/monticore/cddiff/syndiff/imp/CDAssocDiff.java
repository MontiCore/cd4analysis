package de.monticore.cddiff.syndiff.imp;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.syndiff.datastructures.AssocCardinality;
import de.monticore.cddiff.syndiff.datastructures.AssocDirection;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.cddiff.syndiff.interfaces.ICDAssocDiff;
import de.monticore.matcher.MatchingStrategy;
import de.monticore.matcher.NameTypeMatcher;
import de.monticore.matcher.StructureTypeMatcher;
import de.monticore.matcher.SuperTypeMatcher;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isSuperOf;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.*;


public class CDAssocDiff extends CDPrintDiff implements ICDAssocDiff {
  private final ASTCDAssociation srcElem;
  private final ASTCDAssociation tgtElem;
  private final ASTCDCompilationUnit srcCD;
  private final ASTCDCompilationUnit tgtCD;
  ASTCDType srcLeftType, srcRightType, tgtLeftType, tgtRightType;
  private boolean isReversed;
  private List<DiffTypes> baseDiff;
  private boolean mustBeCompared;
  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  NameTypeMatcher nameTypeMatch;
  StructureTypeMatcher structureTypeMatch;
  SuperTypeMatcher superTypeMatch;
  List<MatchingStrategy<ASTCDType>> typeMatchers;
  //Print
  private final CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String
    srcAssocType, srcAssocName, srcAssocLeftCardinality, srcAssocLeftType, srcAssocLeftRole,
    srcAssocDirection,
    srcAssocRightCardinality, srcAssocRightType, srcAssocRightRole, tgtAssoc, srcAssoc, tgtAssocDeleted, srcAssocAdded, assocDiff;
  private String
    tgtAssocType, tgtAssocName, tgtAssocLeftCardinality, tgtAssocLeftType, tgtAssocLeftRole,
    tgtAssocDirection,
    tgtAssocRightCardinality, tgtAssocRightType, tgtAssocRightRole;
  int srcLineOfCode, tgtLineOfCode;
  //Print end

  public CDAssocDiff(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, ASTCDCompilationUnit srcCD, ASTCDCompilationUnit tgtCD) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    this.srcCD = srcCD;
    this.tgtCD = tgtCD;
    this.baseDiff = new ArrayList<>();
    nameTypeMatch = new NameTypeMatcher(tgtCD);
    structureTypeMatch = new StructureTypeMatcher(tgtCD);
    superTypeMatch = new SuperTypeMatcher(nameTypeMatch, srcCD, tgtCD);
    typeMatchers = new ArrayList<>();
    typeMatchers.add(nameTypeMatch);
    //typeMatchers.add(structureTypeMatch);
    typeMatchers.add(superTypeMatch);
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
  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
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

  public AssocStruct findMatchingAssocStructSrc(ASTCDAssociation association, ASTCDClass associatedClass) {
    Pair<ASTCDClass, ASTCDClass> associatedClasses = getConnectedClasses(association, helper.getSrcCD());
    for (AssocStruct assocStruct : helper.getSrcMap().get(associatedClass)) {
      Pair<ASTCDClass, ASTCDClass> structAssociatedClasses = getConnectedClasses(assocStruct.getUnmodifiedAssoc(), helper.getSrcCD());
      if (associatedClasses.a.equals(structAssociatedClasses.a)
        && associatedClasses.b.equals(structAssociatedClasses.b)) {
        return assocStruct;
      }
    }
    mustBeCompared = false;
    return null;
  }

  public AssocStruct findMatchingAssocStructTgt(ASTCDAssociation association, ASTCDClass associatedClass) {
    Pair<ASTCDClass, ASTCDClass> associatedClasses = getConnectedClasses(association, helper.getTgtCD());
    for (AssocStruct assocStruct : helper.getTrgMap().get(associatedClass)) {
      Pair<ASTCDClass, ASTCDClass> structAssociatedClasses = getConnectedClasses(assocStruct.getUnmodifiedAssoc(), helper.getTgtCD());
      if (associatedClasses.a.equals(structAssociatedClasses.a)
        && associatedClasses.b.equals(structAssociatedClasses.b)) {
        return assocStruct;
      }
    }

    return null;
  }
  //Update get.
  //
  // Diff with matched strucs

  /**
   * Find the difference in the cardinalities of an association.
   * Each pair has the association side with the lowest number that is in the
   * new cardinality but not in the old one.
   * @return list with one or two pairs.
   */
  public Pair<ASTCDAssociation, List<Pair<ClassSide, Integer>>> getCardDiff(){
    List<Pair<ClassSide, Integer>> list = new ArrayList<>();
    if (!isReversed){
      //assoc not reversed
      if (!isContainedIn(cardToEnum(getSrcElem().getLeft().getCDCardinality()), cardToEnum(getTgtElem().getLeft().getCDCardinality()))){
        list.add(new Pair<>(ClassSide.Left, findUniqueNumber(getTypeOfCard(getSrcElem().getLeft().getCDCardinality()), getTypeOfCard(getTgtElem().getLeft().getCDCardinality()))));
      }
      if (!isContainedIn(cardToEnum(getSrcElem().getRight().getCDCardinality()), cardToEnum(getTgtElem().getRight().getCDCardinality()))){
        list.add(new Pair<>(ClassSide.Right, findUniqueNumber(getTypeOfCard(getSrcElem().getRight().getCDCardinality()), getTypeOfCard(getTgtElem().getRight().getCDCardinality()))));
      }
    } else {
      if (!isContainedIn(cardToEnum(getSrcElem().getLeft().getCDCardinality()), cardToEnum(getTgtElem().getRight().getCDCardinality()))){
        list.add(new Pair<>(ClassSide.Left, findUniqueNumber(getTypeOfCard(getSrcElem().getLeft().getCDCardinality()), getTypeOfCard(getTgtElem().getRight().getCDCardinality()))));
      }
      if (!isContainedIn(cardToEnum(getSrcElem().getRight().getCDCardinality()), cardToEnum(getTgtElem().getLeft().getCDCardinality()))){
        list.add(new Pair<>(ClassSide.Right, findUniqueNumber(getTypeOfCard(getSrcElem().getRight().getCDCardinality()), getTypeOfCard(getTgtElem().getLeft().getCDCardinality()))));
      }
    }
    return new Pair<>(srcElem, list);
  }

  public boolean isDirectionChanged(){
    if (isReversed){
      if ((getDirection(getSrcElem()).equals(AssocDirection.LeftToRight) && getDirection(getTgtElem()).equals(AssocDirection.RightToLeft))
        || (getDirection(getSrcElem()).equals(AssocDirection.RightToLeft) && getDirection(getTgtElem()).equals(AssocDirection.LeftToRight)
        || (getDirection(getSrcElem()).equals(AssocDirection.BiDirectional)) && getDirection(getTgtElem()).equals(AssocDirection.BiDirectional))){
        return false;
      } else {
        return true;
      }
    } else {
      return !getDirection(getSrcElem()).equals(getDirection(getTgtElem()));
    }
  }

  public Pair<ASTCDAssociation, List<Pair<ClassSide, ASTCDRole>>> getRoleDiff(){
    List<Pair<ClassSide, ASTCDRole>> list = new ArrayList<>();
    if (!isReversed){
      //assoc not reversed
      if (!Objects.equals(getSrcElem().getLeft().getCDRole().getName(), getTgtElem().getLeft().getCDRole().getName())) {
        list.add(new Pair<>(ClassSide.Left, getSrcElem().getLeft().getCDRole()));
      }
      if (!Objects.equals(getSrcElem().getRight().getCDRole().getName(), getTgtElem().getRight().getCDRole().getName())) {
        list.add(new Pair<>(ClassSide.Right, getSrcElem().getRight().getCDRole()));
      }
    } else {
      if (!Objects.equals(getSrcElem().getLeft().getCDRole().getName(), getTgtElem().getRight().getCDRole().getName())) {
        list.add(new Pair<>(ClassSide.Left, getSrcElem().getLeft().getCDRole()));
      }
      if (!Objects.equals(getSrcElem().getRight().getCDRole().getName(), getTgtElem().getLeft().getCDRole().getName())) {
        list.add(new Pair<>(ClassSide.Right, getSrcElem().getRight().getCDRole()));
      }
    }
    return new Pair<>(srcElem, list);
  }

  /**
   * Find the lowest integer that is the first interval but not in the second.
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
      if (interval2.equals(AssocCardinality.One) || interval2.equals(AssocCardinality.Optional)){
        return 2;
      } else {
        return null;
      }
    } else if (interval1.equals(AssocCardinality.Multiple)) {
      if (interval2.equals(AssocCardinality.One) || interval2.equals(AssocCardinality.AtLeastOne)){
        return 0;
      } else if (interval2.equals(AssocCardinality.Optional)){
        return 2;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  public ASTCDClass changedSrc() {
    Pair<ASTCDClass, ASTCDClass> pairNew = getConnectedClasses(getSrcElem(), helper.getSrcCD());
    Pair<ASTCDClass, ASTCDClass> pairOld = getConnectedClasses(getTgtElem(), helper.getTgtCD());
    ASTCDClass leftNew = pairNew.a;
    ASTCDClass leftOld = pairOld.a;
    ASTCDClass rightNew = pairNew.b;
    ASTCDClass rightOld = pairOld.b;
    if (isReversed) {
     leftOld = pairOld.b;
     rightOld = pairOld.a;
    }
      if (srcElem.getCDAssocDir().isDefinitiveNavigableRight()) {
        if (changedToSuper(leftNew, leftOld)) {
          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getSrcCD(), leftNew);
          subclassesA.remove(helper.findMatchedSrc(leftOld));
          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getTgtCD(), leftOld);
          subclassesA.removeAll(helper.getSrcClasses(inheritance));
          subclassesA.add(leftNew);
          for (ASTCDClass subclass : subclassesA) {
            if (!subclass.getModifier().isAbstract()) {
              if (helper.findMatchedClass(subclass) != null
                && !helper.classHasAssociationTgt(tgtElem, helper.findMatchedClass(subclass))) {
                return subclass;
              }
            }
          }
        } else if (changedToSub(leftNew, leftOld)
          && !(tgtElem.getRight().getCDCardinality().isOpt() || tgtElem.getRight().getCDCardinality().isMult())) {
          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getTgtCD(), leftOld);
          subclassesA.remove(helper.findMatchedClass(leftNew));
          List<ASTCDClass> subClassesASrc = helper.getSrcClasses(subclassesA);
          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getSrcCD(), leftNew);
          subClassesASrc.removeAll(inheritance);
          for (ASTCDClass subclass : subClassesASrc) {
            ASTCDClass matchedClass = helper.findMatchedClass(subclass);
            if (matchedClass != null
              && !matchedClass.getModifier().isAbstract()
              && !helper.classHasAssociation(srcElem, matchedClass)) {
              return helper.findMatchedSrc(subclass);
            }
          }
        }
      } else if (srcElem.getCDAssocDir().isDefinitiveNavigableLeft()) {
        if (changedToSuper(rightNew, rightOld)) {
          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getSrcCD(), rightNew);
          subclassesA.remove(helper.findMatchedSrc(rightOld));
          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getTgtCD(), rightOld);
          subclassesA.removeAll(helper.getSrcClasses(inheritance));
          subclassesA.add(rightNew);
          for (ASTCDClass subclass : subclassesA) {
            if (!subclass.getModifier().isAbstract()) {
              //search with isSubAssociation() - done
              if (helper.findMatchedClass(subclass) != null
                && !helper.classHasAssociationTgt(tgtElem, helper.findMatchedClass(subclass))) {
                return subclass;
              }
            }
          }
        } else if (changedToSub(rightNew, rightOld)
          && !(tgtElem.getLeft().getCDCardinality().isOpt() || tgtElem.getLeft().getCDCardinality().isMult())) {
          //check if th matched class of the old one is abstract - done
          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getTgtCD(), rightOld);
          subclassesA.remove(helper.findMatchedClass(rightNew));
          List<ASTCDClass> subClassesASrc = helper.getSrcClasses(subclassesA);
          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getSrcCD(), rightNew);
          subClassesASrc.removeAll(inheritance);
          for (ASTCDClass subclass : subClassesASrc) {
            ASTCDClass matchedClass = helper.findMatchedClass(subclass);
            if (matchedClass != null
              && !matchedClass.getModifier().isAbstract()
              && !helper.classHasAssociation(srcElem, matchedClass)) {
              return helper.findMatchedSrc(subclass);
            }
          }
        }
    }
    return null;
  }
  public ASTCDClass changedTgt() {
    Pair<ASTCDClass, ASTCDClass> pairNew = getConnectedClasses(getSrcElem(), helper.getSrcCD());
    Pair<ASTCDClass, ASTCDClass> pairOld = getConnectedClasses(getTgtElem(), helper.getTgtCD());
    ASTCDClass leftNew = pairNew.a;
    ASTCDClass leftOld = pairOld.a;
    ASTCDClass rightNew = pairNew.b;
    ASTCDClass rightOld = pairOld.b;
    if (isReversed) {
      leftOld = pairOld.b;
      rightOld = pairOld.a;
    }
      if (srcElem.getCDAssocDir().isDefinitiveNavigableRight()) {
        if (changedToSuper(rightNew, rightOld)) {
          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getSrcCD(), rightNew);
          subclassesA.remove(helper.findMatchedSrc(rightOld));
          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getTgtCD(), rightOld);
          subclassesA.removeAll(helper.getSrcClasses(inheritance));
          subclassesA.add(rightNew);
          for (ASTCDClass subclass : subclassesA) {
            if (!subclass.getModifier().isAbstract()) {
              if (helper.findMatchedClass(subclass) != null
                && !helper.classIsTargetTgt(tgtElem, helper.findMatchedClass(subclass))) {
                return subclass;
              }
            }
          }
        } else if (changedToSub(rightNew, rightOld)
          && !(tgtElem.getLeft().getCDCardinality().isOpt() || tgtElem.getLeft().getCDCardinality().isMult())) {
          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getTgtCD(), rightOld);
          subclassesA.remove(helper.findMatchedClass(rightNew));
          List<ASTCDClass> subClassesASrc = helper.getSrcClasses(subclassesA);
          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getSrcCD(), rightNew);
          subClassesASrc.removeAll(inheritance);
          for (ASTCDClass subclass : subClassesASrc) {
            ASTCDClass matchedClass = helper.findMatchedSrc(subclass);
            if (matchedClass != null
              && srcElem.getCDAssocDir().isBidirectional() && tgtElem.getCDAssocDir().isBidirectional()
              && !matchedClass.getModifier().isAbstract()
              && !helper.classHasAssociationTgt(tgtElem, matchedClass)) {
              return helper.findMatchedSrc(subclass);
            }
            if (matchedClass != null
              && !matchedClass.getModifier().isAbstract()
              && !helper.classIsTarget(srcElem, matchedClass)) {
              return helper.findMatchedSrc(subclass);
            }
          }
        }
      } else if (srcElem.getCDAssocDir().isDefinitiveNavigableLeft()) {
        if (changedToSuper(leftNew, leftOld)) {
          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getSrcCD(), leftNew);
          subclassesA.remove(helper.findMatchedSrc(leftOld));
          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getTgtCD(), leftOld);
          subclassesA.removeAll(helper.getSrcClasses(inheritance));
          subclassesA.add(leftNew);
          for (ASTCDClass subclass : subclassesA) {
            if (!subclass.getModifier().isAbstract()) {
              if (helper.findMatchedClass(subclass) != null
                && !helper.classIsTargetTgt(tgtElem, helper.findMatchedClass(subclass))) {
                return subclass;
              }
            }
          }
        } else if (changedToSub(leftNew, leftOld)
          && !(tgtElem.getRight().getCDCardinality().isOpt() || tgtElem.getRight().getCDCardinality().isMult())) {
          List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getTgtCD(), leftOld);
          subclassesA.remove(helper.findMatchedClass(leftNew));
          List<ASTCDClass> subClassesASrc = helper.getSrcClasses(subclassesA);
          List<ASTCDClass> inheritance = getSpannedInheritance(helper.getSrcCD(), leftNew);
          subClassesASrc.removeAll(inheritance);
          for (ASTCDClass subclass : subClassesASrc) {
            ASTCDClass matchedClass = helper.findMatchedClass(subclass);
            if (matchedClass != null
              && !matchedClass.getModifier().isAbstract()
              && !helper.classIsTarget(srcElem, matchedClass)) {
              return helper.findMatchedSrc(subclass);
            }
          }
        }
      }
    return null;
  }

  public boolean changedToSuper(ASTCDClass classNew, ASTCDClass classOld){
    return isSuperOf(classNew.getSymbol().getInternalQualifiedName().replace(".", "_"), classOld.getSymbol().getInternalQualifiedName().replace(".", "_"), (ICD4CodeArtifactScope) helper.getSrcCD().getEnclosingScope());
  }

  public boolean changedToSub(ASTCDClass classNew, ASTCDClass classOld){
    return isSuperOf(classOld.getSymbol().getInternalQualifiedName().replace(".", "_"), classNew.getSymbol().getInternalQualifiedName().replace(".", "_"), (ICD4CodeArtifactScope) helper.getSrcCD().getEnclosingScope());
  }

  /*--------------------------------------------------------------------*/

  private void assocDiff(ASTCDAssociation srcAssoc, ASTCDAssociation tgtAssoc, List<MatchingStrategy<ASTCDType>> typeMatchers) {

    // Association Type
    Optional<ASTCDAssocType> srcAssocType = Optional.of(srcAssoc.getCDAssocType());
    Optional<ASTCDAssocType> tgtAssocType = Optional.of(tgtAssoc.getCDAssocType());
    CDNodeDiff<ASTCDAssocType, ASTCDAssocType> assocType = new CDNodeDiff<>(srcAssocType, tgtAssocType);
    this.srcAssocType = getColorCode(assocType) + pp.prettyprint(srcAssocType.get()) + RESET;
    this.tgtAssocType = getColorCode(assocType) + pp.prettyprint(tgtAssocType.get()) + RESET;

    // Name
    Optional<ASTCDAssociation> srcName = (srcAssoc.isPresentName()) ? Optional.of(srcAssoc) : Optional.empty();
    Optional<ASTCDAssociation> tgtName = (tgtAssoc.isPresentName()) ? Optional.of(tgtAssoc) : Optional.empty();
    CDNodeDiff<ASTCDAssociation, ASTCDAssociation> assocName = new CDNodeDiff<>(null, srcName, tgtName);

    if(srcName.isPresent() && tgtName.isPresent()) {
      if (!srcName.get().getName().equals(tgtName.get().getName())) {
        assocName = new CDNodeDiff<>(Actions.CHANGED, srcName, tgtName);
      }
    }
    if(srcName.isPresent() && tgtName.isEmpty()) {
      assocName = new CDNodeDiff<>(Actions.ADDED, srcName, tgtName);
    }
    if(srcName.isEmpty() && tgtName.isPresent()) {
      assocName = new CDNodeDiff<>(Actions.REMOVED, srcName, tgtName);
    }

    if(srcName.isPresent()) {
      this.srcAssocName = getColorCode(assocName) + srcName.get().getName() + RESET;
    }
    if(tgtName.isPresent()) {
      this.tgtAssocName = getColorCode(assocName) + tgtName.get().getName() + RESET;
    }

    if (assocName.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_NAME)) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_NAME);
      }
    }

    // Association Direction
    Optional<ASTCDAssocDir> srcAssocDir = Optional.of(srcAssoc.getCDAssocDir());
    Optional<ASTCDAssocDir> tgtAssocDir = Optional.of(tgtAssoc.getCDAssocDir());
    CDNodeDiff<ASTCDAssocDir, ASTCDAssocDir> assocDiffDir = new CDNodeDiff<>(srcAssocDir, tgtAssocDir);

    if (assocDiffDir.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_DIRECTION)){
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_DIRECTION);
      }
    }

    srcAssocDirection = getColorCode(assocDiffDir) + pp.prettyprint(srcAssocDir.get()) + RESET;
    tgtAssocDirection = getColorCode(assocDiffDir) + pp.prettyprint(tgtAssocDir.get()) + RESET;

    //Differences in the sides
    Optional<CDTypeSymbol> srcLeftSymbol = srcCD.getEnclosingScope().resolveCDTypeDown(srcAssoc.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> srcRightSymbol = srcCD.getEnclosingScope().resolveCDTypeDown(srcAssoc.getRightQualifiedName().getQName());
    Optional<CDTypeSymbol> tgtLeftSymbol = tgtCD.getEnclosingScope().resolveCDTypeDown(tgtAssoc.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> tgtRightSymbol = tgtCD.getEnclosingScope().resolveCDTypeDown(tgtAssoc.getRightQualifiedName().getQName());
      srcLeftSymbol.ifPresent(cdTypeSymbol -> this.srcLeftType = cdTypeSymbol.getAstNode());
      srcRightSymbol.ifPresent(cdTypeSymbol -> this.srcRightType = cdTypeSymbol.getAstNode());
      tgtLeftSymbol.ifPresent(cdTypeSymbol -> this.tgtLeftType = cdTypeSymbol.getAstNode());
      tgtRightSymbol.ifPresent(cdTypeSymbol -> this.tgtRightType = cdTypeSymbol.getAstNode());


    for(MatchingStrategy<ASTCDType> typeMatcher : typeMatchers) {
      if(typeMatcher.isMatched(srcLeftType,tgtLeftType) || typeMatcher.isMatched(srcRightType,tgtRightType)) {
        isReversed = false;
        getAssocSideDiff(srcAssoc.getLeft(), tgtAssoc.getLeft());
        if(baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)){
          if(srcAssoc.getCDAssocDir().isDefinitiveNavigableRight() && !srcAssoc.getCDAssocDir().isBidirectional()){
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if(!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)){
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
            }
          }
          if(srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft() && !srcAssoc.getCDAssocDir().isBidirectional()){
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if(!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)){
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
            }
          }
        }
        getAssocSideDiff(srcAssoc.getRight(), tgtAssoc.getRight());
        if(baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)){
          if(srcAssoc.getCDAssocDir().isDefinitiveNavigableRight() && !srcAssoc.getCDAssocDir().isBidirectional()){
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if(!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)){
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
            }
          }
          if(srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft() && !srcAssoc.getCDAssocDir().isBidirectional()){
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if(!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)){
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
            }
          }
        }
      }
      if(typeMatcher.isMatched(srcLeftType,tgtRightType) || typeMatcher.isMatched(srcRightType,tgtLeftType)) {
        isReversed = true;
        getAssocSideDiff(srcAssoc.getLeft(), tgtAssoc.getRight());
        if (baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight() && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
            }
          }
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft() && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
            }
          }
        }
        getAssocSideDiff(srcAssoc.getRight(), tgtAssoc.getLeft());
        if (baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)) {
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableRight() && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_TARGET_CLASS);
            }
          }
          if (srcAssoc.getCDAssocDir().isDefinitiveNavigableLeft() && !srcAssoc.getCDAssocDir().isBidirectional()) {
            baseDiff.remove(DiffTypes.CHANGED_ASSOCIATION_CLASS);
            if (!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS)) {
              baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_SOURCE_CLASS);
            }
          }
        }
      }
    }

    srcLineOfCode = srcAssoc.get_SourcePositionStart().getLine();
    tgtLineOfCode = tgtAssoc.get_SourcePositionStart().getLine();
  }

  public void getAssocSideDiff(ASTCDAssocSide srcAssocSide, ASTCDAssocSide tgtAssocSide) {

    // Cardinality
    Optional<ASTCDCardinality> srcAssocCardinality = (srcAssocSide.isPresentCDCardinality()) ? Optional.of(srcAssocSide.getCDCardinality()) : Optional.empty();
    Optional<ASTCDCardinality> tgtAssocCardinality = (tgtAssocSide.isPresentCDCardinality()) ? Optional.of(tgtAssocSide.getCDCardinality()) : Optional.empty();
    CDNodeDiff<ASTCDCardinality, ASTCDCardinality> assocCardinality = new CDNodeDiff<>(srcAssocCardinality, tgtAssocCardinality);

    if (assocCardinality.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY)){
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_CARDINALITY);
      }
    }

    // QualifiedType
    Optional<ASTMCQualifiedName> srcAssocType = Optional.of(srcAssocSide.getMCQualifiedType().getMCQualifiedName());
    Optional<ASTMCQualifiedName> tgtAssocType = Optional.of(tgtAssocSide.getMCQualifiedType().getMCQualifiedName());
    CDNodeDiff<ASTMCQualifiedName, ASTMCQualifiedName> typeDiff = new CDNodeDiff<>(srcAssocType, tgtAssocType);

    if (typeDiff.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_CLASS)){
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_CLASS);
      }
    }

    // CDRole
    Optional<ASTCDRole> srcAssocRole = (srcAssocSide.isPresentCDRole()) ? Optional.of(srcAssocSide.getCDRole()) : Optional.empty();
    Optional<ASTCDRole> tgtAssocRole = (tgtAssocSide.isPresentCDRole()) ? Optional.of(tgtAssocSide.getCDRole()) : Optional.empty();
    CDNodeDiff<ASTCDRole, ASTCDRole> assocRole = new CDNodeDiff<>(srcAssocRole, tgtAssocRole);

    if (assocRole.checkForAction()) {
      if(!baseDiff.contains(DiffTypes.CHANGED_ASSOCIATION_ROLE)){
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_ROLE);
      }
    }

    if (tgtAssocSide.isLeft()) {
      tgtAssocCardinality.ifPresent(astCardinality -> tgtAssocLeftCardinality = getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
      tgtAssocLeftType = getColorCode(typeDiff) + pp.prettyprint(tgtAssocType.get()) + RESET;
      tgtAssocRole.ifPresent(role -> tgtAssocLeftRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
    } else {
      tgtAssocCardinality.ifPresent(astCardinality -> tgtAssocRightCardinality = getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
      tgtAssocRightType = getColorCode(typeDiff) + pp.prettyprint(tgtAssocType.get()) + RESET;
      tgtAssocRole.ifPresent(role -> tgtAssocRightRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
    }
    if (srcAssocSide.isLeft()) {
      srcAssocCardinality.ifPresent(astCardinality -> srcAssocLeftCardinality = getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
      srcAssocLeftType = getColorCode(typeDiff) + pp.prettyprint(srcAssocType.get()) + RESET;
      srcAssocRole.ifPresent(role -> srcAssocLeftRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
    } else {
      srcAssocCardinality.ifPresent(astCardinality -> srcAssocRightCardinality = getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
      srcAssocRightType = getColorCode(typeDiff) + pp.prettyprint(srcAssocType.get()) + RESET;
      srcAssocRole.ifPresent(role -> srcAssocRightRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
    }

  }

  private void setStrings() {

    // Build Source String
    srcAssoc = "//new, L: " + srcLineOfCode + System.lineSeparator() +
      insertSpaceBetweenStrings(
        Arrays.asList(srcAssocType, srcAssocName, srcAssocLeftCardinality, srcAssocLeftType, srcAssocLeftRole, srcAssocDirection, srcAssocRightRole, srcAssocRightType, srcAssocRightCardinality));

    // Build Target String
    tgtAssoc = "//old, L: " + tgtLineOfCode + System.lineSeparator() +
      insertSpaceBetweenStrings(
        Arrays.asList(tgtAssocType, tgtAssocName, tgtAssocLeftCardinality, tgtAssocLeftType, tgtAssocLeftRole, tgtAssocDirection, tgtAssocRightRole, tgtAssocRightType, tgtAssocRightCardinality));

    srcAssocAdded =  "//added association, L: " + srcLineOfCode + System.lineSeparator() +
      insertSpaceBetweenStringsAndGreen(
        Arrays.asList(srcAssocType, srcAssocName, srcAssocLeftCardinality, srcAssocLeftType, srcAssocLeftRole, srcAssocDirection, srcAssocRightRole, srcAssocRightType, srcAssocRightCardinality)) + System.lineSeparator();

    // Build Target String
    tgtAssocDeleted = "//removed association, L: " + tgtLineOfCode + System.lineSeparator() +
      insertSpaceBetweenStringsAndRed(
        Arrays.asList(tgtAssocType, tgtAssocName, tgtAssocLeftCardinality, tgtAssocLeftType, tgtAssocLeftRole, tgtAssocDirection, tgtAssocRightRole, tgtAssocRightType, tgtAssocRightCardinality)) + System.lineSeparator();

    // Build Assoc Diff
    assocDiff = "//changed association" + System.lineSeparator() + srcAssoc + System.lineSeparator() + tgtAssoc + System.lineSeparator();

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

  public String printSrcAssoc() {
    return srcAssoc;
  }

  public String printAddedAssoc() {
    return srcAssocAdded;
  }

  public String printDeletedAssoc() {
    return tgtAssocDeleted;
  }

  public String printTgtAssoc() { return tgtAssoc; }

  public String printDiffAssoc() { return assocDiff; }
}
