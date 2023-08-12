package de.monticore.cddiff.syndiff.imp;

import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cddiff.syndiff.datastructures.AssocStruct;
import de.monticore.cddiff.syndiff.interfaces.ICDAssocDiff;
import de.monticore.cddiff.syndiff.datastructures.AssocCardinality;
import de.monticore.cddiff.syndiff.datastructures.AssocDirection;
import de.monticore.cddiff.syndiff.datastructures.ClassSide;
import de.monticore.matcher.MatchingStrategy;
import de.monticore.prettyprint.IndentPrinter;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isSuperOf;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.*;


public class CDAssocDiff implements ICDAssocDiff {
  private final ASTCDAssociation srcElem;
  private final ASTCDAssociation tgtElem;
  private boolean isReversed;

  private ASTCDCompilationUnit srcCD;
  private ASTCDCompilationUnit tgtCD;

  private List<DiffTypes> baseDiff;
  protected MatchingStrategy<ASTCDAssociation> assocMatcher;
  protected MatchingStrategy<ASTCDType> typeMatcher;

  //use them to check if a search has been made
  private AssocStruct srcStruc;
  private AssocStruct tgtStruc;
  private boolean mustBeCompared;

  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

  //Printer help functions and strings
  private final CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());
  protected static final String CHANGED_ASSOCIATION = "\u001B[33m";
  final String RESET = "\u001B[0m";
  String stringSrcAssoc;
  String stringTgtAssoc;
  //What if both sides are changed
  String srcAssocName;
  String tgtAssocName;
  String srcTypeLeft;
  String srcTypeRight;
  String tgtTypeLeft;
  String tgtTypeRight;
  String srcRoleNameLeft;
  String srcRoleNameRight;
  String tgtRoleNameLeft;
  String tgtRoleNameRight;
  String differenceInWords;
  String srcDirection;
  String tgtDirection;
  String srcCardinalityLeft;
  String srcCardinalityRight;
  String tgtCardinalityLeft;
  String tgtCardinalityRight;

  public CDAssocDiff(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, boolean isReversed) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    this.isReversed = isReversed;
    buildingInterpretation();
  }

  public boolean isReversed() {
    return isReversed;
  }

  public void setReversed(boolean reversed) {
    isReversed = reversed;
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
  //Update get..Diff with matched strucs

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
        if (pair.a.getName().equals(pairOld.a.getName())
          && !pair.b.getName().equals(pairOld.b.getName())){
          return new Pair<>(getSrcElem(), pair.b);
        } else if (pair.a.getName().equals(pairOld.b.getName())
          && !pair.b.getName().equals(pairOld.a.getName())) {
          return new Pair<>(getSrcElem(), pair.b);
        } else if (pair.b.getName().equals(pairOld.b.getName())
          && !pair.a.getName().equals(pairOld.a.getName())) {
          return new Pair<>(getSrcElem(), pair.a);
        } else if (pair.b.getName().equals(pairOld.a.getName())
          && !pair.a.getName().equals(pairOld.b.getName())) {
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

  public ASTCDAssociation findChangedAssocs(ASTCDAssociation srcAssoc, ASTCDAssociation tgtAssoc) {
    boolean changedRoleName = false;
    boolean changedLeftMultiplicity = false;
    boolean changedRightMultiplicity = false;
    this.stringSrcAssoc = printer.prettyprint(srcAssoc);
    this.stringTgtAssoc = printer.prettyprint(tgtAssoc);
    srcAssocName = srcAssoc.getName();
    tgtAssocName = tgtAssoc.getName();
    srcTypeLeft = srcAssoc.getLeftQualifiedName().getQName();
    srcTypeRight = srcAssoc.getRightQualifiedName().getQName();
    tgtTypeLeft = tgtAssoc.getLeftQualifiedName().getQName();
    tgtTypeRight = tgtAssoc.getRightQualifiedName().getQName();
    srcDirection = srcAssoc.getCDAssocDir().toString();
    tgtDirection = tgtAssoc.getCDAssocDir().toString();

    if (assocMatcher.isMatched(srcAssoc, tgtAssoc)) {

      if(getTypes(srcAssoc.getLeftQualifiedName().getQName(),tgtAssoc.getRightQualifiedName().getQName())
      || getTypes(srcAssoc.getRightQualifiedName().getQName(), tgtAssoc.getLeftQualifiedName().getQName())){
        isReversed = true;
      }

      if(getTypes(srcAssoc.getLeftQualifiedName().getQName(),tgtAssoc.getLeftQualifiedName().getQName())
      || getTypes(srcAssoc.getRightQualifiedName().getQName(),tgtAssoc.getRightQualifiedName().getQName())){
        if(!srcAssoc.getLeft().getCDRole().getName().equals(tgtAssoc.getLeft().getCDRole().getName())){
          changedRoleName = true;
          srcRoleNameLeft = CHANGED_ASSOCIATION + printer.prettyprint(srcAssoc.getLeft().getCDRole()) + RESET;
          tgtRoleNameLeft = CHANGED_ASSOCIATION + printer.prettyprint(tgtAssoc.getLeft().getCDRole()) + RESET;
        }

        if(!srcAssoc.getLeft().getCDCardinality().equals(tgtAssoc.getLeft().getCDCardinality())){
          changedLeftMultiplicity = true;
          srcCardinalityLeft = CHANGED_ASSOCIATION + printer.prettyprint(srcAssoc.getLeft().getCDCardinality()) + RESET;
          tgtCardinalityLeft = CHANGED_ASSOCIATION + printer.prettyprint(tgtAssoc.getLeft().getCDCardinality()) + RESET;
        }

        if(!srcAssoc.getRight().getCDRole().getName().equals(tgtAssoc.getRight().getCDRole().getName())){
          changedRoleName = true;
          srcRoleNameRight = CHANGED_ASSOCIATION + printer.prettyprint(srcAssoc.getRight().getCDRole()) + RESET;
          tgtRoleNameRight = CHANGED_ASSOCIATION + printer.prettyprint(tgtAssoc.getRight().getCDRole()) + RESET;
        }

        if(!srcAssoc.getRight().getCDCardinality().equals(tgtAssoc.getRight().getCDCardinality())){
          changedRightMultiplicity = true;
          srcCardinalityRight = CHANGED_ASSOCIATION + printer.prettyprint(srcAssoc.getRight().getCDCardinality()) + RESET;
          tgtCardinalityRight = CHANGED_ASSOCIATION + printer.prettyprint(tgtAssoc.getRight().getCDCardinality()) + RESET;
        }
      }

      if (isReversed) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_DIRECTION);
        if (!srcAssoc.getLeft().getCDRole().getName().equals(tgtAssoc.getRight().getCDRole().getName())){
          changedRoleName = true;
          srcRoleNameLeft = CHANGED_ASSOCIATION + printer.prettyprint(srcAssoc.getLeft().getCDRole()) + RESET;
          tgtRoleNameLeft = CHANGED_ASSOCIATION + printer.prettyprint(tgtAssoc.getRight().getCDRole()) + RESET;
        }

        if(!srcAssoc.getLeft().getCDCardinality().equals(tgtAssoc.getRight().getCDCardinality())){
          changedLeftMultiplicity = true;
          srcCardinalityLeft = CHANGED_ASSOCIATION + printer.prettyprint(srcAssoc.getLeft().getCDCardinality()) + RESET;
          tgtCardinalityRight = CHANGED_ASSOCIATION + printer.prettyprint(tgtAssoc.getRight().getCDCardinality()) + RESET;
        }

        if(!srcAssoc.getRight().getCDRole().getName().equals(tgtAssoc.getLeft().getCDRole().getName())) {
          changedRoleName = true;
          srcRoleNameRight = CHANGED_ASSOCIATION + printer.prettyprint(srcAssoc.getRight().getCDRole()) + RESET;
          tgtRoleNameRight = CHANGED_ASSOCIATION + printer.prettyprint(tgtAssoc.getLeft().getCDRole()) + RESET;
        }

        if(!srcAssoc.getRight().getCDCardinality().equals(tgtAssoc.getLeft().getCDCardinality())){
          changedRightMultiplicity = true;
          srcCardinalityRight = CHANGED_ASSOCIATION + printer.prettyprint(srcAssoc.getRight().getCDCardinality()) + RESET;
          tgtCardinalityLeft = CHANGED_ASSOCIATION + printer.prettyprint(tgtAssoc.getLeft().getCDCardinality()) + RESET;
        }
      }

      if (changedRoleName) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_ROLE);
      }

      if (changedLeftMultiplicity) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_LEFT_MULTIPLICITY);
      }

      if (changedRightMultiplicity) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_RIGHT_MULTIPLICITY);
      }

    }

    if (changedRoleName || changedLeftMultiplicity || changedRightMultiplicity) {
      return srcAssoc;
    }

    return null;
  }

  //TODO CompilationUnit??
  protected boolean getTypes(String srcElem, String tgt) {
    Optional<CDTypeSymbol> srcTypeSymbol = srcCD.getEnclosingScope().resolveCDTypeDown(srcElem);
    Optional<CDTypeSymbol> tgtTypeSymbol = tgtCD.getEnclosingScope().resolveCDTypeDown(tgt);

    if (srcTypeSymbol.isPresent() && tgtTypeSymbol.isPresent()) {
      ASTCDType srcType = srcTypeSymbol.get().getAstNode();
      ASTCDType tgtType = tgtTypeSymbol.get().getAstNode();
      return typeMatcher.isMatched(tgtType, srcType);
    }
    return false;
  }

  private void buildingInterpretation() {

    this.stringSrcAssoc = buildStrings(Arrays.asList(srcAssocName, srcCardinalityLeft, srcTypeLeft, srcRoleNameLeft, srcDirection, srcRoleNameRight, srcTypeRight, srcCardinalityRight));

    this.stringTgtAssoc = buildStrings(Arrays.asList(tgtAssocName, tgtCardinalityLeft, tgtTypeLeft, tgtRoleNameLeft, tgtDirection, tgtRoleNameRight, tgtTypeRight, tgtCardinalityRight));

    this.differenceInWords = "Difference in words: ";
  }

  @Override
  public String buildStrings(List<String> stringList) {
    return buildStrings(stringList) + ";";
  }

  public String printHelperSrcCD() {
    return stringSrcAssoc;
  }

  public String printHelperTgtCD() {
    return stringTgtAssoc;
  }
}
