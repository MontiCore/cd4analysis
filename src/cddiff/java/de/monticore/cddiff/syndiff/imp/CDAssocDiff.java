package de.monticore.cddiff.syndiff.imp;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cddiff.ow2cw.CDAssociationHelper;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDAssocDiff;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDAssociationWrapperCardinality;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CDAssocDiff implements ICDAssocDiff {
  private final ASTCDAssociation elem1;
  private final ASTCDAssociation elem2;
  private List<DiffTypes> baseDiff;

  @Override
  public ASTCDAssociation getElem1() {
    return elem1;
  }

  @Override
  public ASTCDAssociation getElem2() {
    return elem2;
  }

  @Override
  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }

  @Override
  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
  }

  protected CDAssocDiff(ASTCDAssociation elem1, ASTCDAssociation elem2) {
    this.elem1 = elem1;
    this.elem2 = elem2;
  }

  @Override
  public String roleDiff() {
    ASTCDAssociation newAssoc = getElem1();
    ASTCDAssociation oldAssoc = getElem2();
    StringBuilder diff = new StringBuilder(new String());
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
    return "Direction changed from " + getDirection(getElem2()).toString() + " to " + getDirection(getElem1());

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
    ASTCDAssociation newAssoc = getElem1();
    ASTCDAssociation oldAssoc = getElem2();
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
    if (getElem1().getLeftQualifiedName().getQName().equals(getElem1().getLeftQualifiedName().getQName())
      && getElem1().getRightQualifiedName().getQName().equals(getElem2().getRightQualifiedName().getQName())){
      //assoc not reversed
      if (!getElem1().getLeft().getCDCardinality().equals(getElem2().getLeft().getCDCardinality())){
        list.add(new Pair<>(getElem1(), new Pair<>(ClassSide.Left, findUniqueNumber(getTypeOfCard(getElem1().getLeft().getCDCardinality()), getTypeOfCard(getElem2().getLeft().getCDCardinality())))));
      }
      if (!getElem1().getRight().getCDCardinality().equals(getElem2().getRight().getCDCardinality())){
        list.add(new Pair<>(getElem1(), new Pair<>(ClassSide.Right, findUniqueNumber(getTypeOfCard(getElem1().getRight().getCDCardinality()), getTypeOfCard(getElem2().getRight().getCDCardinality())))));
      }
    } else {
      if (!getElem1().getLeft().getCDCardinality().equals(getElem2().getRight().getCDCardinality())){
        list.add(new Pair<>(getElem1(), new Pair<>(ClassSide.Left, findUniqueNumber(getTypeOfCard(getElem1().getLeft().getCDCardinality()), getTypeOfCard(getElem2().getRight().getCDCardinality())))));
      }
      if (!getElem1().getRight().getCDCardinality().equals(getElem2().getLeft().getCDCardinality())){
        list.add(new Pair<>(getElem1(), new Pair<>(ClassSide.Right, findUniqueNumber(getTypeOfCard(getElem1().getRight().getCDCardinality()), getTypeOfCard(getElem2().getLeft().getCDCardinality())))));
      }
    }
    return list;
  }

  public List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> getRoleDiff(){
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> list = new ArrayList<>();
    if (getElem1().getLeftQualifiedName().getQName().equals(getElem1().getLeftQualifiedName().getQName())
      && getElem1().getRightQualifiedName().getQName().equals(getElem2().getRightQualifiedName().getQName())){
      //assoc not reversed
      if (!Objects.equals(getElem1().getLeft().getCDRole(), getElem2().getLeft().getCDRole())) {
        list.add(new Pair<>(getElem1(), new Pair<>(ClassSide.Left, getElem1().getLeft().getCDRole())));
      }
      if (!Objects.equals(getElem1().getRight().getCDRole(), getElem2().getRight().getCDRole())) {
        list.add(new Pair<>(getElem1(), new Pair<>(ClassSide.Right, getElem1().getRight().getCDRole())));
      }
    } else {
      if (!Objects.equals(getElem1().getLeft().getCDRole(), getElem2().getRight().getCDRole())) {
        list.add(new Pair<>(getElem1(), new Pair<>(ClassSide.Left, getElem1().getLeft().getCDRole())));
      }
      if (!Objects.equals(getElem1().getRight().getCDRole(), getElem2().getLeft().getCDRole())) {
        list.add(new Pair<>(getElem1(), new Pair<>(ClassSide.Right, getElem1().getRight().getCDRole())));
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
}
