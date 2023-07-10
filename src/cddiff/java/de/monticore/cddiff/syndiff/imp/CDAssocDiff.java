package de.monticore.cddiff.syndiff.imp;

import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDAssocDiff;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.monticore.cddiff.ow2cw.CDInheritanceHelper.isSuperOf;
import static de.monticore.cddiff.syndiff.imp.Syn2SemDiffHelper.*;

public class CDAssocDiff implements ICDAssocDiff {
  private final ASTCDAssociation srcElem;
  private final ASTCDAssociation tgtElem;
  private boolean isReversed;
  private List<DiffTypes> baseDiff;

  private Syn2SemDiffHelper helper = Syn2SemDiffHelper.getInstance();

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

  public CDAssocDiff(ASTCDAssociation srcElem, ASTCDAssociation tgtElem, boolean isReversed) {
    this.srcElem = srcElem;
    this.tgtElem = tgtElem;
    this.isReversed = isReversed;
  }

  public boolean isReversed() {
    return isReversed;
  }

  public void setReversed(boolean reversed) {
    isReversed = reversed;
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
    return "Direction changed from " + getDirection(getTgtElem()).toString() + " to " + getDirection(getSrcElem());

  }

  public AssocDirection getDirection(ASTCDAssociation association){
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

  /**
   * Find the difference in the cardinalities of an association.
   * Each pair has the association side with the lowest number that is in the
   * new cardinality but not in the old one.
   * @return list with one or two pairs.
   */
  public List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> getCardDiff(){
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list = new ArrayList<>();
    if (!isReversed){
      //assoc not reversed
      if (!isContainedIn(cardToEnum(getSrcElem().getLeft().getCDCardinality()), cardToEnum(getTgtElem().getLeft().getCDCardinality()))){
        list.add(new Pair<>(getSrcElem(), new Pair<>(ClassSide.Left, findUniqueNumber(getTypeOfCard(getSrcElem().getLeft().getCDCardinality()), getTypeOfCard(getTgtElem().getLeft().getCDCardinality())))));
      }
      if (!isContainedIn(cardToEnum(getSrcElem().getRight().getCDCardinality()), cardToEnum(getTgtElem().getRight().getCDCardinality()))){
        list.add(new Pair<>(getSrcElem(), new Pair<>(ClassSide.Right, findUniqueNumber(getTypeOfCard(getSrcElem().getRight().getCDCardinality()), getTypeOfCard(getTgtElem().getRight().getCDCardinality())))));
      }
    } else {
      if (!isContainedIn(cardToEnum(getSrcElem().getLeft().getCDCardinality()), cardToEnum(getTgtElem().getRight().getCDCardinality()))){
        list.add(new Pair<>(getSrcElem(), new Pair<>(ClassSide.Left, findUniqueNumber(getTypeOfCard(getSrcElem().getLeft().getCDCardinality()), getTypeOfCard(getTgtElem().getRight().getCDCardinality())))));
      }
      if (!isContainedIn(cardToEnum(getSrcElem().getRight().getCDCardinality()), cardToEnum(getTgtElem().getLeft().getCDCardinality()))){
        list.add(new Pair<>(getSrcElem(), new Pair<>(ClassSide.Right, findUniqueNumber(getTypeOfCard(getSrcElem().getRight().getCDCardinality()), getTypeOfCard(getTgtElem().getLeft().getCDCardinality())))));
      }
    }
    return list;
  }

  public boolean isDirectionChanged(){
    return getDirection(getSrcElem()).equals(getDirection(getTgtElem()));
  }

  public List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> getRoleDiff(){
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> list = new ArrayList<>();
    if (!isReversed){
      //assoc not reversed
      if (!Objects.equals(getSrcElem().getLeft().getCDRole(), getTgtElem().getLeft().getCDRole())) {
        list.add(new Pair<>(getSrcElem(), new Pair<>(ClassSide.Left, getSrcElem().getLeft().getCDRole())));
      }
      if (!Objects.equals(getSrcElem().getRight().getCDRole(), getTgtElem().getRight().getCDRole())) {
        list.add(new Pair<>(getSrcElem(), new Pair<>(ClassSide.Right, getSrcElem().getRight().getCDRole())));
      }
    } else {
      if (!Objects.equals(getSrcElem().getLeft().getCDRole(), getTgtElem().getRight().getCDRole())) {
        list.add(new Pair<>(getSrcElem(), new Pair<>(ClassSide.Left, getSrcElem().getLeft().getCDRole())));
      }
      if (!Objects.equals(getSrcElem().getRight().getCDRole(), getTgtElem().getLeft().getCDRole())) {
        list.add(new Pair<>(getSrcElem(), new Pair<>(ClassSide.Right, getSrcElem().getRight().getCDRole())));
      }
    }
    return list;
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
}
