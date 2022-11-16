/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.CDUtils;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._ast.ASTModifierBuilder;
import java.util.Optional;

/**
 * Default Strategy for merging two associations Merges two associations, checks the matching status
 * with provided AssociationMatcher, possible logs to the MathResult via MergingBlackBoard
 */
public class DefaultAssociationMergeStrategy extends MergerBase
    implements AssociationMergeStrategy {

  public DefaultAssociationMergeStrategy(MergeBlackBoard mergingBlackBoard) {
    super(mergingBlackBoard, MergePhase.ASSOCIATION_MERGING);
  }

  @Override
  public Optional<ASTCDAssociation> mergeAssociation(
      ASTCDAssociation association1, ASTCDAssociation association2) {

    // We take the orientation of the first association, this ensures that Left and
    // Right point to same references
    Optional<ASTCDAssociation> alignedAssoc2 =
        CDUtils.tryAlignAssociation(association1, association2);

    if (!alignedAssoc2.isPresent()) {
      log(
          ErrorLevel.ERROR,
          "Association do not seem to match, Unable to align Associations!",
          association1,
          association2);
      return Optional.empty();
    }
    association2 = alignedAssoc2.get();

    ASTCDAssociationBuilder mergedAssociationBuilder = CD4CodeMill.cDAssociationBuilder();

    // ============
    // === NAME ===
    // ============
    // names are either equal or unspecified for the associations
    if (association1.isPresentName()) {
      mergedAssociationBuilder.setName(association1.getName());
    } else if (association2.isPresentName()) {
      mergedAssociationBuilder.setName(association2.getName());
    }

    // ================
    // === MODIFIER ===
    // ================
    Optional<ASTModifier> modifier =
        mergeModifier(association1.getModifier(), association2.getModifier());
    if (modifier.isPresent()) {
      mergedAssociationBuilder.setModifier(modifier.get());
    }

    // ===================
    // === COMPOSITION ===
    // ===================
    if (association1.getCDAssocType().isComposition()
        || association2.getCDAssocType().isComposition()) {
      mergedAssociationBuilder.setCDAssocType(CD4CodeMill.cDAssocTypeCompBuilder().build());
    } else {
      mergedAssociationBuilder.setCDAssocType(CD4CodeMill.cDAssocTypeAssocBuilder().build());
    }

    // ===============
    // === DERIVED ===
    // ================
    if (association1.getModifier().isDerived() && association2.getModifier().isDerived()) {
      mergedAssociationBuilder.setModifier(new ASTModifierBuilder().setDerived(true).build());

    } else if (association1.getModifier().isDerived() || association2.getModifier().isDerived()) {
      mergedAssociationBuilder.setModifier(new ASTModifierBuilder().build());
      log(
          ErrorLevel.DESIGN_ISSUE,
          "Association '"
              + FormatAssocName(mergedAssociationBuilder)
              + "' between '"
              + association1.getLeftReferenceName().toString()
              + "' and '"
              + association1.getRightReferenceName().toString()
              + "' is now not derived anymore.",
          association1,
          association2);
    } else {
      mergedAssociationBuilder.setModifier(new ASTModifierBuilder().build());
    }

    ASTCDAssocSide assoc1Left = association1.getLeft();
    ASTCDAssocSide assoc1Right = association1.getRight();
    ASTCDAssocSide assoc2Left = association2.getLeft();
    ASTCDAssocSide assoc2Right = association2.getRight();
    ASTCDAssocLeftSide mergedLeft =
        CD4CodeMill.cDAssocLeftSideBuilder()
            .setModifier(new ASTModifierBuilder().build())
            .setMCQualifiedType(assoc1Left.getMCQualifiedType())
            .build();
    ASTCDAssocRightSide mergedRight =
        CD4CodeMill.cDAssocRightSideBuilder()
            .setModifier(new ASTModifierBuilder().build())
            .setMCQualifiedType(assoc1Right.getMCQualifiedType())
            .build();

    // ==================
    // === NAVIGATION ===
    // ==================
    // Fixme: Paramater to not merge -> and <- to bidirectional association??
    if ((association1.getCDAssocDir().isBidirectional()
            || association2.getCDAssocDir().isBidirectional())
        || (association1.getCDAssocDir().isDefinitiveNavigableLeft()
            && association2.getCDAssocDir().isDefinitiveNavigableRight())
        || (association1.getCDAssocDir().isDefinitiveNavigableRight()
            && association2.getCDAssocDir().isDefinitiveNavigableLeft())) {
      // A <-> B
      mergedAssociationBuilder.setCDAssocDir(CD4CodeMill.cDBiDirBuilder().build());
    } else if (association1.getCDAssocDir().isDefinitiveNavigableLeft()
        || association2.getCDAssocDir().isDefinitiveNavigableLeft()) {
      // A <- B
      mergedAssociationBuilder.setCDAssocDir(CD4CodeMill.cDRightToLeftDirBuilder().build());
    } else if (association1.getCDAssocDir().isDefinitiveNavigableRight()
        || association2.getCDAssocDir().isDefinitiveNavigableRight()) {
      // A -> B
      mergedAssociationBuilder.setCDAssocDir(CD4CodeMill.cDLeftToRightDirBuilder().build());
    } else {
      // A -- B
      mergedAssociationBuilder.setCDAssocDir(CD4CodeMill.cDUnspecifiedDirBuilder().build());
    }

    // =============
    // === ROLES ===
    // =============
    // roles are equal or unspecified for the associations
    if (assoc1Left.isPresentCDRole()) {
      mergedLeft.setCDRole(assoc1Left.getCDRole());
    } else if (assoc2Left.isPresentCDRole()) {
      mergedLeft.setCDRole(assoc2Left.getCDRole());
    }

    if (assoc1Right.isPresentCDRole()) {
      mergedRight.setCDRole(assoc1Right.getCDRole());
    } else if (assoc2Right.isPresentCDRole()) {
      mergedRight.setCDRole(assoc2Right.getCDRole());
    }

    // ===================
    // === CARDINALITIES ===
    // ===================
    if (assoc1Left.isPresentCDCardinality()) {
      mergedLeft.setCDCardinality(assoc1Left.getCDCardinality());
    } else if (assoc2Left.isPresentCDCardinality()) {
      mergedLeft.setCDCardinality(assoc2Left.getCDCardinality());
    }
    if (assoc1Right.isPresentCDCardinality()) {
      mergedRight.setCDCardinality(assoc1Right.getCDCardinality());
    } else if (assoc2Right.isPresentCDCardinality()) {
      mergedRight.setCDCardinality(assoc2Right.getCDCardinality());
    }

    // ===============
    // === ORDERED ===
    // ===============
    if (assoc1Left.isPresentCDOrdered() || assoc2Left.isPresentCDOrdered()) {
      mergedLeft.setCDOrdered(CD4CodeMill.cDOrderedBuilder().build());
    }
    if (assoc1Right.isPresentCDOrdered() || assoc2Right.isPresentCDOrdered()) {
      mergedRight.setCDOrdered(CD4CodeMill.cDOrderedBuilder().build());
    }

    // ======================
    // === LEFT QUALIFIER ===
    // ======================
    Optional<ASTCDQualifier> leftQualifier =
        MergeQualifier(
            association1,
            association2,
            assoc1Left,
            assoc2Left,
            FormatAssocName(mergedAssociationBuilder),
            true);
    if (leftQualifier.isPresent()) {
      mergedLeft.setCDQualifier(leftQualifier.get());
    } else {
      mergedLeft.setCDQualifierAbsent();
    }

    // =======================
    // === RIGHT QUALIFIER ===
    // =======================
    Optional<ASTCDQualifier> rightQualifier =
        MergeQualifier(
            association1,
            association2,
            assoc1Right,
            assoc2Right,
            FormatAssocName(mergedAssociationBuilder),
            false);
    if (rightQualifier.isPresent()) {
      mergedRight.setCDQualifier(rightQualifier.get());
    } else {
      mergedRight.setCDQualifierAbsent();
    }

    mergedAssociationBuilder.setLeft(mergedLeft);
    mergedAssociationBuilder.setRight(mergedRight);
    ASTCDAssociation mergedAssociation = mergedAssociationBuilder.build();

    mergeComments(mergedAssociation, association1, association2);

    log(
        ErrorLevel.FINE,
        "Merged association: "
            + CDUtils.prettyPrintInline((ASTCDAssociationNode) mergedAssociation),
        association1,
        association2);

    return Optional.of(mergedAssociation);
  }

  private String FormatAssocName(ASTCDAssociationBuilder mergedBuilder) {
    if (mergedBuilder.isPresentName()) {
      return " '" + mergedBuilder.getName() + "' ";
    }
    return " ";
  }

  private Optional<ASTCDQualifier> MergeQualifier(
      ASTCDAssociation association1,
      ASTCDAssociation association2,
      ASTCDAssocSide assocSide1,
      ASTCDAssocSide assocSide2,
      String associationName,
      boolean isLeftSide) {

    ASTCDQualifier mergedQualifier = null;
    String leftType = association1.getLeft().getName();
    String rightType = association1.getRight().getName();

    if (assocSide1.isPresentCDQualifier() && !assocSide2.isPresentCDQualifier()) {
      mergedQualifier = assocSide1.getCDQualifier();
      log(
          ErrorLevel.WARNING,
          "Qualified association"
              + associationName
              + "between "
              + leftType
              + " and "
              + rightType
              + " has been merged with an ordinary association.",
          association1,
          association2);

    } else if (!assocSide1.isPresentCDQualifier() && assocSide2.isPresentCDQualifier()) {
      mergedQualifier = assocSide2.getCDQualifier();
      log(
          ErrorLevel.WARNING,
          "Qualified association"
              + associationName
              + "between "
              + leftType
              + " and "
              + rightType
              + " has been merged with an ordinary association.",
          association1,
          association2);
    } else if (assocSide1.isPresentCDQualifier() && assocSide2.isPresentCDQualifier()) {
      // set qualifier to most concrete type
      if (assocSide1.getCDQualifier().isPresentByAttributeName()) {
        if (assocSide2.getCDQualifier().isPresentByAttributeName()) {
          if ((assocSide1
              .getCDQualifier()
              .getByAttributeName()
              .equals(assocSide2.getCDQualifier().getByAttributeName()))) {
            mergedQualifier = assocSide1.getCDQualifier();
          } else {
            logError(
                "Named Left Qualifier mismatch in Qualified association"
                    + associationName
                    + "between "
                    + leftType
                    + " and "
                    + rightType
                    + "! Attribute Qualifier 1 '"
                    + assocSide1.getCDQualifier().getName()
                    + "' Attribute Qualifier 2 '"
                    + assocSide2.getCDQualifier().getName()
                    + "'",
                association1,
                association2);
            // We cannot merge the associations
            return Optional.empty();
          }
        } else {
          // Check if the types are compatible
          Optional<ASTCDAttribute> qualifiedAttribute =
              getBlackBoard()
                  .getASTCDHelperInputCD1()
                  .getAttributeFromClass(
                      assocSide1.getCDQualifier().getName(),
                      (isLeftSide
                          ? association1.getRightQualifiedName().getBaseName()
                          : association1.getLeftQualifiedName().getBaseName()));
          if (qualifiedAttribute.isPresent()) {
            if (CDUtils.getTypeName(qualifiedAttribute.get().getMCType())
                .equalsIgnoreCase(assocSide2.getCDQualifier().getName())) {
              logWarning(
                  "Type-Qualifier '"
                      + assocSide2.getCDQualifier().getName()
                      + "' in association 2 is replaced by concrete named attribute qualifier in "
                      + "association 1 '"
                      + assocSide1.getCDQualifier().getName()
                      + "' of same type.",
                  association1,
                  association2);
              mergedQualifier = assocSide1.getCDQualifier();
            } else {
              logError(
                  "Left Qualifier Type mismatch in Qualified association"
                      + associationName
                      + "between "
                      + leftType
                      + " and "
                      + rightType
                      + "! Attribute Qualifier 1 '"
                      + assocSide1.getCDQualifier().getByAttributeName()
                      + " of Type "
                      + assocSide1.getCDQualifier().getByAttributeNameSymbol().getType().toString()
                      + " doesnt't match "
                      + "' Type of Attribute Qualifier 2 '"
                      + assocSide2.getCDQualifier().getName()
                      + "'",
                  association1,
                  association2);
              // We cannot merge the associations
              return Optional.empty();
            }
          }
        }
      } else {
        if (assocSide2.getCDQualifier().isPresentByAttributeName()) {
          // Check if the types are compatible
          // Check if the types are compatible
          Optional<ASTCDAttribute> qualifiedAttribute =
              getBlackBoard()
                  .getASTCDHelperInputCD2()
                  .getAttributeFromClass(
                      assocSide2.getCDQualifier().getName(),
                      (isLeftSide
                          ? association2.getRightQualifiedName().getBaseName()
                          : association2.getLeftQualifiedName().getBaseName()));
          if (qualifiedAttribute.isPresent()) {
            if (CDUtils.getTypeName(qualifiedAttribute.get().getMCType())
                .equalsIgnoreCase(assocSide1.getCDQualifier().getName())) {
              logWarning(
                  "Type-Qualifier '"
                      + assocSide1.getCDQualifier().getName()
                      + "' in association 1 is replaced by concrete named attribute qualifier in "
                      + "association 2 '"
                      + assocSide2.getCDQualifier().getName()
                      + "' of same type.",
                  association1,
                  association2);
              mergedQualifier = assocSide2.getCDQualifier();
            } else {
              logError(
                  "Left Qualifier Type mismatch in Qualified association"
                      + associationName
                      + "between "
                      + leftType
                      + " and "
                      + rightType
                      + "'! Type of Attribute Qualifier 1 '"
                      + assocSide1.getCDQualifier().getName()
                      + "' doesn't match Attribute Qualifier 2 '"
                      + assocSide2.getCDQualifier().getName()
                      + " of Type "
                      + CDUtils.getTypeName(qualifiedAttribute.get()),
                  association1,
                  association2);
              // We cannot merge the associations
              return Optional.empty();
            }
          }
        } else if (!assocSide1
            .getCDQualifier()
            .getName()
            .equalsIgnoreCase(assocSide1.getCDQualifier().getName())) {
          logError(
              "Left Qualifier Type mismatch in Qualified association"
                  + associationName
                  + "between "
                  + leftType
                  + " and "
                  + rightType
                  + "! Type of Qualifier 1 '"
                  + assocSide1.getCDQualifier().getName()
                  + " doesnt't match "
                  + "' Type of Attribute Qualifier 2 '"
                  + assocSide2.getCDQualifier().getName()
                  + "'",
              association1,
              association2);
          // We cannot merge the associations
          return Optional.empty();
        }

        // either association2 is also via type or it has a specific and matching
        // name
        mergedQualifier = assocSide2.getCDQualifier();
      }
    }
    return Optional.ofNullable(mergedQualifier);
  }
}
