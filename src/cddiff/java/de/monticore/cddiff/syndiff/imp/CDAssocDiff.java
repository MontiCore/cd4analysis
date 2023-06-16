package de.monticore.cddiff.syndiff.imp;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDAssocDiff;
import java.util.List;
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
    ASTCDAssociation newAssoc = getElem1();
    ASTCDAssociation oldAssoc = getElem2();
    // return "Direction changed from " + getElem2().getDirection().toString() + " to " +
    // getElem1().getDirection().toString();
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

  private String getTypeOfCard(ASTCDCardinality cardinality) {
    if (cardinality.toCardinality().getLowerBoundLit().equals(Optional.empty())) {
      // [*]
      return "[*]";
    } else if (!cardinality.toCardinality().getLowerBoundLit().equals(Optional.empty())
        & cardinality.toCardinality().getUpperBoundLit().equals(Optional.empty())) {
      // [x..*]
      return "[" + cardinality.toCardinality().getLowerBound() + "..*]";
    } else if (!cardinality.toCardinality().getLowerBoundLit().equals(Optional.empty())
        & !cardinality.toCardinality().getUpperBoundLit().equals(Optional.empty())) {
      // [x..y]
      return "["
          + cardinality.toCardinality().getLowerBound()
          + ".."
          + cardinality.getUpperBound()
          + "]";
    }
    return null;
  }
}
