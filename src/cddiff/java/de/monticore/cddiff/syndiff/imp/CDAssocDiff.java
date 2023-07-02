package de.monticore.cddiff.syndiff.imp;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdassociation._ast.ASTCDRole;
import de.monticore.cddiff.syndiff.DiffTypes;
import de.monticore.cddiff.syndiff.ICDAssocDiff;
import de.monticore.matcher.MatchingStrategy;
import edu.mit.csail.sdg.alloy4.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CDAssocDiff implements ICDAssocDiff {
  private final ASTCDAssociation srcAssoc;
  private final ASTCDAssociation tgtAssoc;
  private List<DiffTypes> baseDiff;
  protected MatchingStrategy<ASTCDAssociation> assocMatcher;

  @Override
  public ASTCDAssociation getSrcAssoc() {
    return srcAssoc;
  }

  @Override
  public ASTCDAssociation getTgtAssoc() {
    return tgtAssoc;
  }

  @Override
  public List<DiffTypes> getBaseDiff() {
    return baseDiff;
  }

  @Override
  public void setBaseDiff(List<DiffTypes> baseDiff) {
    this.baseDiff = baseDiff;
  }

  protected CDAssocDiff(ASTCDAssociation srcAssoc, ASTCDAssociation tgtAssoc) {
    this.srcAssoc = srcAssoc;
    this.tgtAssoc = tgtAssoc;
  }

  @Override
  public String roleDiff() {
    ASTCDAssociation newAssoc = getSrcAssoc();
    ASTCDAssociation oldAssoc = getTgtAssoc();
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
    return "Direction changed from "
        + getDirection(getTgtAssoc()).toString()
        + " to "
        + getDirection(getSrcAssoc());
  }

  public AssocDirection getDirection(ASTCDAssociation association) {
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
    ASTCDAssociation newAssoc = getSrcAssoc();
    ASTCDAssociation oldAssoc = getTgtAssoc();
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
   * Find the difference in the cardinalities of an association. Each pair has the association side
   * with the lowest number that is in the new cardinality but not in the old one.
   *
   * @return list with one or two pairs.
   */
  public List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> getCardDiff() {
    List<Pair<ASTCDAssociation, Pair<ClassSide, Integer>>> list = new ArrayList<>();
    if (getSrcAssoc()
            .getLeftQualifiedName()
            .getQName()
            .equals(getSrcAssoc().getLeftQualifiedName().getQName())
        && getSrcAssoc()
            .getRightQualifiedName()
            .getQName()
            .equals(getTgtAssoc().getRightQualifiedName().getQName())) {
      // assoc not reversed
      if (!getSrcAssoc()
          .getLeft()
          .getCDCardinality()
          .equals(getTgtAssoc().getLeft().getCDCardinality())) {
        list.add(
            new Pair<>(
                getSrcAssoc(),
                new Pair<>(
                    ClassSide.Left,
                    findUniqueNumber(
                        getTypeOfCard(getSrcAssoc().getLeft().getCDCardinality()),
                        getTypeOfCard(getTgtAssoc().getLeft().getCDCardinality())))));
      }
      if (!getSrcAssoc()
          .getRight()
          .getCDCardinality()
          .equals(getTgtAssoc().getRight().getCDCardinality())) {
        list.add(
            new Pair<>(
                getSrcAssoc(),
                new Pair<>(
                    ClassSide.Right,
                    findUniqueNumber(
                        getTypeOfCard(getSrcAssoc().getRight().getCDCardinality()),
                        getTypeOfCard(getTgtAssoc().getRight().getCDCardinality())))));
      }
    } else {
      if (!getSrcAssoc()
          .getLeft()
          .getCDCardinality()
          .equals(getTgtAssoc().getRight().getCDCardinality())) {
        list.add(
            new Pair<>(
                getSrcAssoc(),
                new Pair<>(
                    ClassSide.Left,
                    findUniqueNumber(
                        getTypeOfCard(getSrcAssoc().getLeft().getCDCardinality()),
                        getTypeOfCard(getTgtAssoc().getRight().getCDCardinality())))));
      }
      if (!getSrcAssoc()
          .getRight()
          .getCDCardinality()
          .equals(getTgtAssoc().getLeft().getCDCardinality())) {
        list.add(
            new Pair<>(
                getSrcAssoc(),
                new Pair<>(
                    ClassSide.Right,
                    findUniqueNumber(
                        getTypeOfCard(getSrcAssoc().getRight().getCDCardinality()),
                        getTypeOfCard(getTgtAssoc().getLeft().getCDCardinality())))));
      }
    }
    return list;
  }

  public List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> getRoleDiff() {
    List<Pair<ASTCDAssociation, Pair<ClassSide, ASTCDRole>>> list = new ArrayList<>();
    if (getSrcAssoc()
            .getLeftQualifiedName()
            .getQName()
            .equals(getSrcAssoc().getLeftQualifiedName().getQName())
        && getSrcAssoc()
            .getRightQualifiedName()
            .getQName()
            .equals(getTgtAssoc().getRightQualifiedName().getQName())) {
      // assoc not reversed
      if (!Objects.equals(
          getSrcAssoc().getLeft().getCDRole(), getTgtAssoc().getLeft().getCDRole())) {
        list.add(
            new Pair<>(
                getSrcAssoc(), new Pair<>(ClassSide.Left, getSrcAssoc().getLeft().getCDRole())));
      }
      if (!Objects.equals(
          getSrcAssoc().getRight().getCDRole(), getTgtAssoc().getRight().getCDRole())) {
        list.add(
            new Pair<>(
                getSrcAssoc(), new Pair<>(ClassSide.Right, getSrcAssoc().getRight().getCDRole())));
      }
    } else {
      if (!Objects.equals(
          getSrcAssoc().getLeft().getCDRole(), getTgtAssoc().getRight().getCDRole())) {
        list.add(
            new Pair<>(
                getSrcAssoc(), new Pair<>(ClassSide.Left, getSrcAssoc().getLeft().getCDRole())));
      }
      if (!Objects.equals(
          getSrcAssoc().getRight().getCDRole(), getTgtAssoc().getLeft().getCDRole())) {
        list.add(
            new Pair<>(
                getSrcAssoc(), new Pair<>(ClassSide.Right, getSrcAssoc().getRight().getCDRole())));
      }
    }
    return list;
  }

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

  public ASTCDAssociation findChangedAssocs(ASTCDAssociation srcAssoc, ASTCDAssociation tgtAssoc) {
    boolean changedRoleName = false;
    boolean changedDirection = false;
    boolean changedMultiplicity = false;
    if (assocMatcher.isMatched(srcAssoc, tgtAssoc)) {
      if (srcAssoc
              .getLeftQualifiedName()
              .getQName()
              .equals(tgtAssoc.getLeftQualifiedName().getQName())
          || srcAssoc
              .getRightQualifiedName()
              .getQName()
              .equals(tgtAssoc.getRightQualifiedName().getQName())) {
        if (!srcAssoc
                .getLeft()
                .getCDRole()
                .getName()
                .equals(tgtAssoc.getLeft().getCDRole().getName())
            || !srcAssoc
                .getRight()
                .getCDRole()
                .getName()
                .equals(tgtAssoc.getRight().getCDRole().getName())) {
          changedRoleName = true;
        }
      }

      if (srcAssoc
              .getLeftQualifiedName()
              .getQName()
              .equals(tgtAssoc.getRightQualifiedName().getQName())
          || srcAssoc
              .getRightQualifiedName()
              .getQName()
              .equals(tgtAssoc.getLeftQualifiedName().getQName())) {
        if (!srcAssoc
                .getLeft()
                .getCDRole()
                .getName()
                .equals(tgtAssoc.getRight().getCDRole().getName())
            || !srcAssoc
                .getRight()
                .getCDRole()
                .getName()
                .equals(tgtAssoc.getLeft().getCDRole().getName())) {
          changedRoleName = true;
        }
      }

      if (changedRoleName) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_ROLE);
      }

      if (!srcAssoc.getCDAssocDir().equals(tgtAssoc.getCDAssocDir())) {
        changedDirection = true;
      }

      if (changedDirection) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_DIRECTION);
      }

      if (srcAssoc
              .getLeftQualifiedName()
              .getQName()
              .equals(tgtAssoc.getLeftQualifiedName().getQName())
          || srcAssoc
              .getRightQualifiedName()
              .getQName()
              .equals(tgtAssoc.getRightQualifiedName().getQName())) {
        if (!srcAssoc.getLeft().getCDCardinality().equals(tgtAssoc.getLeft().getCDCardinality())
            || !srcAssoc
                .getRight()
                .getCDCardinality()
                .equals(tgtAssoc.getRight().getCDCardinality())) {
          changedMultiplicity = true;
        }
      }

      if (srcAssoc
              .getLeftQualifiedName()
              .getQName()
              .equals(tgtAssoc.getRightQualifiedName().getQName())
          || srcAssoc
              .getRightQualifiedName()
              .getQName()
              .equals(tgtAssoc.getLeftQualifiedName().getQName())) {
        if (!srcAssoc.getLeft().getCDCardinality().equals(tgtAssoc.getRight().getCDCardinality())
            || !srcAssoc
                .getRight()
                .getCDCardinality()
                .equals(tgtAssoc.getLeft().getCDCardinality())) {
          changedMultiplicity = true;
        }
      }

      if (changedMultiplicity) {
        baseDiff.add(DiffTypes.CHANGED_ASSOCIATION_MULTIPLICITY);
      }
    }

    if (changedRoleName || changedDirection || changedMultiplicity) {
      return srcAssoc;
    }
    return null;
  }
}
