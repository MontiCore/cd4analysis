package de.monticore.cddiff.syndiff.imp;

import de.monticore.ast.ASTNode;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.interfaces.ICDAssocDiff;
import de.monticore.cddiff.syndiff.datastructures.AssocCardinality;
import de.monticore.cddiff.syndiff.datastructures.AssocDirection;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isSuperOf;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.*;


public class CDAssocDiff extends CDDiffHelper implements ICDAssocDiff {
  private final ASTCDAssociation srcElem;
  private final ASTCDAssociation tgtElem;
  private boolean isReversed;
  private List<DiffTypes> baseDiff;

  //use them to check if a search has been made
  private AssocStruct srcStruc;
  private AssocStruct tgtStruc;
  private boolean mustBeCompared;
  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();
  //Print
  private final CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  private String
    tgtAssocType,tgtAssocName,tgtAssocLeftCardinality,tgtAssocLeftType,tgtAssocLeftRole,
    tgtAssocDirection,
    tgtAssocRightCardinality,tgtAssocRightType,tgtAssocRightRole;
  private String
    srcAssocType,srcAssocName,srcAssocLeftCardinality,srcAssocLeftType,srcAssocLeftRole,
    srcAssocDirection,
    srcAssocRightCardinality,srcAssocRightType,srcAssocRightRole,tgtAssoc,srcAssoc;
  //Print end

  public CDAssocDiff(ASTCDAssociation srcElem, ASTCDAssociation tgtElem) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;

    assoDiff(tgtElem, srcElem);

    for (CDNodeDiff<?, ?> diff : diffList) {
      if (diff.checkForAction() && diff.getDiff().isPresent()) {
        diffTypesList.add(diff.getDiff().get());
      }
    }

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

  @Override
  public String roleDiff() {
    ASTCDAssociation newAssoc = getSrcElem();
    ASTCDAssociation oldAssoc = getTgtElem();
    StringBuilder diff = new StringBuilder("");
    if (!newAssoc
        .getLeftQualifiedName()
        .getQName()
        .equals(oldAssoc.getLeftQualifiedName().getQName())) {
      diff.append(
          "\nLeft role changed from "
              + oldAssoc.getLeftQualifiedName().getQName()
              + " to "
              + newAssoc.getLeftQualifiedName().getQName());
    }
    if (!newAssoc
        .getRightQualifiedName()
        .getQName()
        .equals(oldAssoc.getRightQualifiedName().getQName())) {
      diff.append(
          "\nLeft role changed from "
              + oldAssoc.getLeftQualifiedName().getQName()
              + " to "
              + newAssoc.getLeftQualifiedName().getQName());
    }
    return diff.toString();
  }

  @Override
  public String dirDiff() {
    return "Direction changed from "
        + getDirection(getTgtElem()).toString()
        + " to "
        + getDirection(getSrcElem());
  }

  @Override
  public String cardDiff() {
    ASTCDAssociation newAssoc = getSrcElem();
    ASTCDAssociation oldAssoc = getTgtElem();
    StringBuilder cardinalityChanges = new StringBuilder();

    if (oldAssoc.getLeft().isPresentCDCardinality()
        && newAssoc.getLeft().isPresentCDCardinality()) {
      ASTCDCardinality card0 = oldAssoc.getLeft().getCDCardinality(); // old
      ASTCDCardinality card1 = newAssoc.getLeft().getCDCardinality(); // new

      if (!card0.equals(card1)) {
        cardinalityChanges
            .append("Source cardinality changed from ")
            .append(getTypeOfCard(oldAssoc.getLeft().getCDCardinality()))
            .append(" to ")
            .append(getTypeOfCard(newAssoc.getLeft().getCDCardinality()))
            .append(" ");
      }
    } else if (oldAssoc.getLeft().isPresentCDCardinality()
        && !newAssoc.getLeft().isPresentCDCardinality()) {
      cardinalityChanges
          .append("Source cardinality changed from ")
          .append(getTypeOfCard(oldAssoc.getLeft().getCDCardinality()))
          .append(" to 1 ");
    } else if (!oldAssoc.getLeft().isPresentCDCardinality()
        && newAssoc.getLeft().isPresentCDCardinality()) {
      cardinalityChanges
          .append("Source cardinality changed from 1 to ")
          .append(getTypeOfCard(newAssoc.getLeft().getCDCardinality()))
          .append(" ");
    }

    if (oldAssoc.getRight().isPresentCDCardinality()
        && newAssoc.getRight().isPresentCDCardinality()) {

      if (!oldAssoc.getRight().getCDCardinality().equals(newAssoc.getRight().getCDCardinality())) {
        cardinalityChanges
            .append("Target cardinality changed from ")
            .append(getTypeOfCard(oldAssoc.getRight().getCDCardinality()))
            .append(" to ")
            .append(getTypeOfCard(newAssoc.getRight().getCDCardinality()))
            .append(" ");
      }
    } else if (oldAssoc.getRight().isPresentCDCardinality()
        && !newAssoc.getRight().isPresentCDCardinality()) {
      cardinalityChanges
          .append("Target cardinality changed from ")
          .append(getTypeOfCard(oldAssoc.getRight().getCDCardinality()))
          .append(" to 1 ");
    } else if (!oldAssoc.getRight().isPresentCDCardinality()
        && newAssoc.getRight().isPresentCDCardinality()) {
      cardinalityChanges
          .append("Target cardinality changed from 1 to ")
          .append(getTypeOfCard(newAssoc.getRight().getCDCardinality()))
          .append(" ");
    }

    if (cardinalityChanges.length() == 0) {
      return "Cardinalities remain the same";
    }

    return "Cardinality changes: " + cardinalityChanges.toString().trim();
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

  //TODO: check if somewhere the comparisson should be with unmatchedAssoc
  //TODO: if no match is found for src, we don't look anymore at this
  //if no match in tgt is found, we just add this assoc to diff
  public AssocStruct findMatchingAssocStructSrc(
    ASTCDAssociation association, ASTCDClass associatedClass) {
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

  public AssocStruct findMatchingAssocStructTgt(
    ASTCDAssociation association, ASTCDClass associatedClass) {
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
    //TODO: check if a class now can be instatiated without anothe one
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

  /**
   * CHeck if the old association allowed 0 objects
   * @param association association
   * @return true if condition is fulfilled
   */
  public boolean allowsZeroObjects(AssocStruct association){
    if (association.getSide().equals(ClassSide.Left)) {
      return (association.getAssociation().getRight().getCDCardinality().isMult() || association.getAssociation().getRight().getCDCardinality().isOpt());
    } else {
      return (association.getAssociation().getLeft().getCDCardinality().isMult() || association.getAssociation().getLeft().getCDCardinality().isOpt());
    }
  }

  public Pair<ASTCDAssociation, List<Pair<ClassSide, ASTCDRole>>> getRoleDiff(){
    List<Pair<ClassSide, ASTCDRole>> list = new ArrayList<>();
    if (!isReversed){
      //assoc not reversed
      if (!Objects.equals(getSrcElem().getLeft().getCDRole(), getTgtElem().getLeft().getCDRole())) {
        list.add(new Pair<>(ClassSide.Left, getSrcElem().getLeft().getCDRole()));
      }
      if (!Objects.equals(getSrcElem().getRight().getCDRole(), getTgtElem().getRight().getCDRole())) {
        list.add(new Pair<>(ClassSide.Right, getSrcElem().getRight().getCDRole()));
      }
    } else {
      if (!Objects.equals(getSrcElem().getLeft().getCDRole(), getTgtElem().getRight().getCDRole())) {
        list.add(new Pair<>(ClassSide.Left, getSrcElem().getLeft().getCDRole()));
      }
      if (!Objects.equals(getSrcElem().getRight().getCDRole(), getTgtElem().getLeft().getCDRole())) {
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

  public Pair<ASTCDAssociation, ASTCDClass> getChangedTgtClass(ASTCDCompilationUnit compilationUnit){
    if (changedTgtClass(compilationUnit)){
      Pair<ASTCDClass, ASTCDClass> pair = Syn2SemDiffHelper.getConnectedClasses(getSrcElem(), compilationUnit);
      if (getSrcElem().getCDAssocDir().isBidirectional()){
        Pair<ASTCDClass, ASTCDClass> pairOld = getConnectedClasses(getTgtElem(), compilationUnit);
        if (pair.a.getSymbol().getInternalQualifiedName().equals(pairOld.a.getSymbol().getInternalQualifiedName())
          && !pair.b.getSymbol().getInternalQualifiedName().equals(pairOld.b.getSymbol().getInternalQualifiedName())){
          return new Pair<>(getSrcElem(), pair.b);
        } else if (pair.a.getSymbol().getInternalQualifiedName().equals(pairOld.b.getSymbol().getInternalQualifiedName())
          && !pair.b.getSymbol().getInternalQualifiedName().equals(pairOld.a.getSymbol().getInternalQualifiedName())) {
          return new Pair<>(getSrcElem(), pair.b);
        } else if (pair.b.getSymbol().getInternalQualifiedName().equals(pairOld.b.getSymbol().getInternalQualifiedName())
          && !pair.a.getName().equals(pairOld.a.getSymbol().getInternalQualifiedName())) {
          return new Pair<>(getSrcElem(), pair.a);
        } else if (pair.b.getSymbol().getInternalQualifiedName().equals(pairOld.a.getSymbol().getInternalQualifiedName())
          && !pair.a.getSymbol().getInternalQualifiedName().equals(pairOld.b.getSymbol().getInternalQualifiedName())) {
          return new Pair<>(getSrcElem(), pair.a);
        }
      } else if (getSrcElem().getCDAssocDir().isDefinitiveNavigableRight()){
        return new Pair<>(getSrcElem(), pair.b);
      } else if (getSrcElem().getCDAssocDir().isDefinitiveNavigableLeft()) {
        return new Pair<>(getSrcElem(), pair.a);
      }
    }
    return null;
  }

  public Pair<ASTCDAssociation, ASTCDClass> getChangedTgtClass(){
    //wrong idea
    //check if the new class is abstract
    //if it is, check if there is a class of the super, that doesn't have the old one as super
    ASTCDClass superClass = getChangedTgtClass(helper.getSrcCD()).b;
    if (superClass.getModifier().isAbstract()){
      Pair<Boolean, ASTCDClass> subClass = hasOtherSubclasses(superClass, null);
      if (subClass.a){
        return new Pair<>(getSrcElem(), subClass.b);// diff might not be minimal!!!
      }
    } else {
      return new Pair<>(getSrcElem(), superClass);
    }
    return null;
  }

  public Pair<Boolean, ASTCDClass> hasOtherSubclasses(ASTCDClass superClass, ASTCDClass subClass) {
    List<ASTCDClass> subclassesA = getSpannedInheritance(helper.getSrcCD(), superClass);

    for (ASTCDClass subclass : subclassesA) {
      if (subclass != subClass && !isSuperOf(subClass.getName(), subclass.getName(), helper.getSrcCD())) {
        return new Pair<>(true, subclass);
      }
    }

    return null;
  }

  public boolean isOldSuperOfNew(){
    return false;
  }

  public Pair<Boolean, ASTCDClass> isCardZero(){
    if (!isReversed){
      if (getConnectedClasses(getSrcElem(), Syn2SemDiffHelper.getInstance().getSrcCD()).a.getSymbol().getInternalQualifiedName()
        .equals(getConnectedClasses(getTgtElem(), Syn2SemDiffHelper.getInstance().getTgtCD()).a.getSymbol().getInternalQualifiedName())){
        return new Pair<>((getTgtElem().getLeft().getCDCardinality().isOpt()
          || getTgtElem().getLeft().getCDCardinality().isMult()), getConnectedClasses(getSrcElem(), Syn2SemDiffHelper.getInstance().getSrcCD()).b);
      } else {
        return new Pair<>((getTgtElem().getRight().getCDCardinality().isOpt()
          || getTgtElem().getRight().getCDCardinality().isMult()), getConnectedClasses(getSrcElem(), Syn2SemDiffHelper.getInstance().getSrcCD()).a);
      }
    } else {
      if (getConnectedClasses(getSrcElem(), Syn2SemDiffHelper.getInstance().getSrcCD()).a.getSymbol().getInternalQualifiedName()
        .equals(getConnectedClasses(getTgtElem(), Syn2SemDiffHelper.getInstance().getTgtCD()).b.getSymbol().getInternalQualifiedName())){
        return new Pair<>((getTgtElem().getRight().getCDCardinality().isOpt()
          || getTgtElem().getRight().getCDCardinality().isMult()), getConnectedClasses(getSrcElem(), Syn2SemDiffHelper.getInstance().getSrcCD()).a);
      } else {
        return new Pair<>((getTgtElem().getLeft().getCDCardinality().isOpt()
          || getTgtElem().getLeft().getCDCardinality().isMult()), getConnectedClasses(getSrcElem(), Syn2SemDiffHelper.getInstance().getSrcCD()).b);
      }
    }
  }
  public boolean changedTgtClass(ASTCDCompilationUnit compilationUnit){
    Pair<ASTCDClass, ASTCDClass> pairNew = getConnectedClasses(getSrcElem(), compilationUnit);
    Pair<ASTCDClass, ASTCDClass> pairOld = getConnectedClasses(getTgtElem(), compilationUnit);
    if (pairNew.a.getName().equals(pairOld.a.getName())
      && !pairNew.b.getName().equals(pairOld.b.getName())){
      return isSuperOf(pairNew.b.getName(), pairOld.b.getName(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
    } else if (pairNew.a.getName().equals(pairOld.b.getName())
      && !pairNew.b.getName().equals(pairOld.a.getName())) {
      return isSuperOf(pairNew.b.getName(), pairOld.a.getName(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
    } else if (pairNew.b.getName().equals(pairOld.b.getName())
      && !pairNew.a.getName().equals(pairOld.a.getName())) {
      return isSuperOf(pairNew.a.getName(), pairOld.a.getName(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
    } else if (pairNew.b.getName().equals(pairOld.a.getName())
      && !pairNew.a.getName().equals(pairOld.b.getName())) {
      return isSuperOf(pairNew.a.getName(), pairOld.b.getName(), (ICD4CodeArtifactScope) compilationUnit.getEnclosingScope());
    }
    return false;
  }

  /*--------------------------------------------------------------------*/

  private void assoDiff(ASTCDAssociation tgtAssoc, ASTCDAssociation srcAssoc) {

    List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();
    diffType.append("The difference type is: ");

    // Association Type
    Optional<ASTCDAssocType> srcAssocType = Optional.of(srcAssoc.getCDAssocType());
    Optional<ASTCDAssocType> tgtAssocType = Optional.of(tgtAssoc.getCDAssocType());
    CDNodeDiff<ASTCDAssocType, ASTCDAssocType> assocType = new CDNodeDiff<>(tgtAssocType, srcAssocType);

    if (assocType.checkForAction()) {
      diffs.add(assocType);
      if (assocType.getDiff().isPresent()) {
        diffType
          .append("Type")
          .append(": ")
          .append(assocType.getDiff().get())
          .append(", ");
      }
    }
    this.srcAssocType = getColorCode(assocType) + pp.prettyprint(srcAssocType.get()) + RESET;
    this.tgtAssocType = getColorCode(assocType) + pp.prettyprint(tgtAssocType.get()) + RESET;

    // Association Direction
    Optional<ASTCDAssocDir> srcAssocDir = Optional.of(srcAssoc.getCDAssocDir());
    Optional<ASTCDAssocDir> tgtAssocDir = Optional.of(tgtAssoc.getCDAssocDir());

    CDNodeDiff<ASTCDAssocDir, ASTCDAssocDir> assocDirDiff = new CDNodeDiff<>(tgtAssocDir, srcAssocDir);

    if (assocDirDiff.checkForAction()) {
      diffs.add(assocDirDiff);
      if (assocDirDiff.getDiff().isPresent()) {
        diffType
          .append("Direction")
          .append(": ")
          .append(assocDirDiff.getDiff().get())
          .append(", ");
      }
    }

    // Check if direction '->' was changed to '<-' or if direction '<-' was changed to '->'.
    // If yes, then add weight for calculating the smallest diff for each side combination.
    int weightDirection = 0;
    if ((tgtAssocDir.get().isDefinitiveNavigableRight()
      && !tgtAssocDir.get().isDefinitiveNavigableLeft())
      && (!srcAssocDir.get().isDefinitiveNavigableRight()
      && srcAssocDir.get().isDefinitiveNavigableLeft())
      || (!tgtAssocDir.get().isDefinitiveNavigableRight()
      && tgtAssocDir.get().isDefinitiveNavigableLeft())
      && (srcAssocDir.get().isDefinitiveNavigableRight()
      && !srcAssocDir.get().isDefinitiveNavigableLeft())) {
      isReversed = true;
      weightDirection = 1;
    }

    List<CDNodeDiff<?,?>> tmpOriginalDir = new ArrayList<>();
    tmpOriginalDir.addAll(getAssocSideDiff(tgtAssoc.getLeft(), srcAssoc.getLeft(), false));
    tmpOriginalDir.addAll(getAssocSideDiff(tgtAssoc.getRight(), srcAssoc.getRight(), false));

    List<CDNodeDiff<?,?>> tmpReverseDir = new ArrayList<>();
    tmpReverseDir.addAll(getAssocSideDiff(tgtAssoc.getLeft(), srcAssoc.getRight(), false));
    tmpReverseDir.addAll(getAssocSideDiff(tgtAssoc.getRight(), srcAssoc.getLeft(), false));

    // Here, we calculate with '<=' because:
    // If we have the assoc1: [1] A (a) -> [*] B (b)
    // and assoc2: [*] A (c) -> [1] C (b),
    // we match assoc1 and assoc2, but tmpOriginal.size() = 4, tmpReverse.size() = 4, weightDirection = 0,
    // so we have to get into the first 'if', but 4 + 0 = 4, so we add the =
    if ((tmpOriginalDir.size() + weightDirection) <= tmpReverseDir.size()) {
      diffs.addAll(tmpOriginalDir);

      getAssocSideDiff(tgtAssoc.getLeft(), srcAssoc.getLeft(), true);
      getAssocSideDiff(tgtAssoc.getRight(), srcAssoc.getRight(), true);

      tgtAssocDirection = getColorCode(assocDirDiff) + pp.prettyprint(tgtAssocDir.get()) + RESET;
      srcAssocDirection = getColorCode(assocDirDiff) + pp.prettyprint(srcAssocDir.get()) + RESET;

      isReversed = false;
    } else {
      diffs.addAll(tmpReverseDir);

      getAssocSideDiff(tgtAssoc.getLeft(), srcAssoc.getRight(), true);
      getAssocSideDiff(tgtAssoc.getRight(), srcAssoc.getLeft(), true);

      if (isReversed) {
        tgtAssocDirection = pp.prettyprint(tgtAssocDir.get());
        srcAssocDirection = pp.prettyprint(srcAssocDir.get());
      } else {
        tgtAssocDirection = getColorCode(assocDirDiff) + pp.prettyprint(tgtAssocDir.get()) + RESET;
        srcAssocDirection = getColorCode(assocDirDiff) + pp.prettyprint(srcAssocDir.get()) + RESET;
      }
    }
    this.diffList = diffs;
  }

  private List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> getAssocSideDiff(ASTCDAssocSide srcAssocSide, ASTCDAssocSide tgtAssocSide, boolean readyForPrinting) {

    List<CDNodeDiff<? extends ASTNode, ? extends ASTNode>> diffs = new ArrayList<>();

    // Cardinality
    Optional<ASTCDCardinality> srcAssocCardinality = (srcAssocSide.isPresentCDCardinality()) ? Optional.of(srcAssocSide.getCDCardinality()) : Optional.empty();
    Optional<ASTCDCardinality> tgtAssocCardinality = (tgtAssocSide.isPresentCDCardinality()) ? Optional.of(tgtAssocSide.getCDCardinality()) : Optional.empty();

    CDNodeDiff<ASTCDCardinality, ASTCDCardinality> assocCardinality = new CDNodeDiff<>(tgtAssocCardinality, srcAssocCardinality);

    if (assocCardinality.checkForAction()) {
      diffs.add(assocCardinality);
      if (assocCardinality.getDiff().isPresent() && readyForPrinting) {
        diffType.append(tgtAssocSide.isLeft() ? "Left " : "Right ");
        diffType
          .append("Cardinality")
          .append(": ")
          .append(assocCardinality.getDiff().get())
          .append(", ");
      }
    }

    // QualifiedType
    Optional<ASTMCQualifiedName> srcAssocType = Optional.of(srcAssocSide.getMCQualifiedType().getMCQualifiedName());
    Optional<ASTMCQualifiedName> tgtAssocType = Optional.of(tgtAssocSide.getMCQualifiedType().getMCQualifiedName());

    CDNodeDiff<ASTMCQualifiedName, ASTMCQualifiedName> type = new CDNodeDiff<>(tgtAssocType, srcAssocType);

    if (type.checkForAction()) {
      diffs.add(type);
      if (type.getDiff().isPresent() && readyForPrinting) {
        diffType.append(tgtAssocSide.isLeft() ? "Left " : "Right ");
        diffType.append("Name").append(": ").append(type.getDiff().get()).append(", ");
      }
    }

    // CDRole
    Optional<ASTCDRole> tgtAssocRole = (tgtAssocSide.isPresentCDRole()) ? Optional.of(tgtAssocSide.getCDRole()) : Optional.empty();
    Optional<ASTCDRole> srcAssocRole = (srcAssocSide.isPresentCDRole()) ? Optional.of(srcAssocSide.getCDRole()) : Optional.empty();

    CDNodeDiff<ASTCDRole, ASTCDRole> assocRole = new CDNodeDiff<>(tgtAssocRole, srcAssocRole);

    if (assocRole.checkForAction()) {
      diffs.add(assocRole);
      if (assocRole.getDiff().isPresent() && readyForPrinting) {
        diffType.append(tgtAssocSide.isLeft() ? "Left " : "Right ");
        diffType
          .append("Role")
          .append(": ")
          .append(assocRole.getDiff().get())
          .append(", ");
      }
    }

    if (readyForPrinting) {
      if (tgtAssocSide.isLeft()) {
        tgtAssocCardinality.ifPresent(astCardinality -> tgtAssocLeftCardinality = getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
        tgtAssocLeftType = getColorCode(type) + pp.prettyprint(tgtAssocType.get()) + RESET;
        tgtAssocRole.ifPresent(role -> tgtAssocLeftRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
      } else {
        tgtAssocCardinality.ifPresent(astCardinality -> tgtAssocRightCardinality = getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
        tgtAssocRightType = getColorCode(type) + pp.prettyprint(tgtAssocType.get()) + RESET;
        tgtAssocRole.ifPresent(role -> tgtAssocRightRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
      }
      if (srcAssocSide.isLeft()) {
        srcAssocCardinality.ifPresent(astCardinality -> srcAssocLeftCardinality = getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
        srcAssocLeftType = getColorCode(type) + pp.prettyprint(srcAssocType.get()) + RESET;
        srcAssocRole.ifPresent(role -> srcAssocLeftRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
      } else {
        srcAssocCardinality.ifPresent(astCardinality -> srcAssocRightCardinality = getColorCode(assocCardinality) + pp.prettyprint(astCardinality) + RESET);
        srcAssocRightType = getColorCode(type) + pp.prettyprint(srcAssocType.get()) + RESET;
        srcAssocRole.ifPresent(role -> srcAssocRightRole = getColorCode(assocRole) + pp.prettyprint(role) + RESET);
      }
    }

    //Result from function getAssocSideDiff()!
    return diffs;
  }

  private void setStrings() {
    tgtAssocName = srcElem.getName();
    srcAssocName = tgtElem.getName();

    // Build Source String
    srcAssoc =
      insertSpaceBetweenStrings(
        Arrays.asList(srcAssocType, srcAssocName, srcAssocLeftCardinality, srcAssocLeftType, srcAssocLeftRole, srcAssocDirection, srcAssocRightRole, srcAssocRightType, srcAssocRightCardinality));

    // Build Target String
    tgtAssoc =
      insertSpaceBetweenStrings(
        Arrays.asList(tgtAssocType, tgtAssocName, tgtAssocLeftCardinality, tgtAssocLeftType, tgtAssocLeftRole, tgtAssocDirection, tgtAssocRightRole, tgtAssocRightType, tgtAssocRightCardinality));

  }

  @Override
  public String insertSpaceBetweenStrings(List<String> stringList) {
    return super.insertSpaceBetweenStrings(stringList) + ";";
  }

  public String printCD2() {
    return srcAssoc;
  }

  public String printCD1() {
    return tgtAssoc;
  }


}
