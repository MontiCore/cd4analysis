/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDQualifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.matching.matchresult.MatchNode;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.CDMergeUtils;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import java.util.*;

/** Default matching strategies for associations. Provides caching! */
public class DefaultAssociationMatcher extends MatcherBase implements AssociationMatcher {

  private boolean onlyNamedAssociations;
  private boolean failAmbiguousAssociations;

  public DefaultAssociationMatcher(MergeBlackBoard blackBoard) {
    super(blackBoard);
    this.onlyNamedAssociations = blackBoard.getConfig().mergeOnlyNamedAssociations();
    this.failAmbiguousAssociations = blackBoard.getConfig().isFailAmbiguous();
  }

  @Override
  public ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> findMatchingAssociations()
      throws MergingException {

    ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> matches =
        new ASTMatchGraph<ASTCDAssociation, ASTCDDefinition>(getCurrentCDs());
    // We must apply some stronger matching rules if two types share more
    // than one association. In that case, all matches must match with Name
    Map<ASTCDDefinition, List<ASTCDAssociation>> multipleAssociations = new HashMap<>();
    for (int j = 0; j < matches.getParents().size(); j++) {
      multipleAssociations.put(
          matches.getParents().get(j), getMultipleAssoc(matches.getParents().get(j)));
    }
    // Pairwise matching
    ASTCDDefinition cd1, cd2;
    MatchNode<ASTCDAssociation, ASTCDDefinition> node1, node2;
    for (int j = 0; j < getCurrentCDs().size(); j++) {
      cd1 = getCurrentCDs().get(j);
      for (ASTCDAssociation assoc1 : cd1.getCDAssociationsList()) {
        node1 =
            matches.addElement(assoc1, cd1, getCurrentCDHelper().get(j).getCDPackageName(assoc1));
        for (int i = j + 1; i < getCurrentCDs().size(); i++) {
          cd2 = getCurrentCDs().get(i);
          for (ASTCDAssociation assoc2 : cd2.getCDAssociationsList()) {
            // Force unambiguous matching for multi-associations
            if (multipleAssociations.get(cd1).contains(assoc1)
                || multipleAssociations.get(cd2).contains(assoc2)) {
              Set<ASTCDAssociation> alternatives1 = new HashSet<>(multipleAssociations.get(cd1));
              alternatives1.remove(assoc1);
              Set<ASTCDAssociation> alternatives2 = new HashSet<>(multipleAssociations.get(cd2));
              alternatives2.remove(assoc2);
              if (!nameMatch(assoc1, assoc2, true)) {
                if (!match(assoc1, assoc2)) {
                  continue;
                }
                if (alternatives1.stream().anyMatch(alt1 -> match(alt1, assoc2))) {
                  if (failAmbiguousAssociations) {
                    throw new MergingException(
                        "Could not merge due to ambiguous match for "
                            + CD4CodeMill.prettyPrint(assoc2, false)
                            + " in CD "
                            + cd2.getName());
                  } else {
                    continue;
                  }
                }
                if (alternatives2.stream().anyMatch(alt2 -> match(assoc1, alt2))) {
                  if (failAmbiguousAssociations) {
                    throw new MergingException(
                        "Could not merge due to ambiguous match for "
                            + CD4CodeMill.prettyPrint(assoc1, false)
                            + " in CD "
                            + cd1.getName());
                  } else {
                    continue;
                  }
                }
              }
            }
            if (match(assoc1, assoc2)) {
              // Add the entry for the classdiagram
              node2 =
                  matches.addElement(
                      assoc2, cd2, getCurrentCDHelper().get(i).getCDPackageName(assoc2));
              node2.addMatch(node1);
            }
          }
        }
      }
    }
    return matches;
  }

  /**
   * Reports a list of multiple associations, i.e. associations of types with more than one
   * connecting association
   */
  private List<ASTCDAssociation> getMultipleAssoc(ASTCDDefinition astcdDefinition) {
    Map<String, List<ASTCDAssociation>> associationsBetweenTypes = new HashMap<>();
    Iterator<ASTCDAssociation> assocIterator = astcdDefinition.getCDAssociationsList().iterator();
    ASTCDAssociation assoc;
    while (assocIterator.hasNext()) {
      assoc = assocIterator.next();
      if (associationsBetweenTypes.containsKey(
          assoc.getLeftReferenceName().toString()
              + "_"
              + assoc.getRightReferenceName().toString())) {
        associationsBetweenTypes
            .get(
                assoc.getLeftReferenceName().toString()
                    + "_"
                    + assoc.getRightReferenceName().toString())
            .add(assoc);
      } else if (associationsBetweenTypes.containsKey(
          assoc.getRightReferenceName().toString()
              + "_"
              + assoc.getLeftReferenceName().toString())) {
        associationsBetweenTypes
            .get(
                assoc.getRightReferenceName().toString()
                    + "_"
                    + assoc.getLeftReferenceName().toString())
            .add(assoc);
      } else {
        associationsBetweenTypes.put(
            assoc.getLeftReferenceName().toString()
                + "_"
                + assoc.getRightReferenceName().toString(),
            new ArrayList<>());
        associationsBetweenTypes
            .get(
                assoc.getLeftReferenceName().toString()
                    + "_"
                    + assoc.getRightReferenceName().toString())
            .add(assoc);
      }
    }
    List<ASTCDAssociation> multipleAssocs = new ArrayList<>();
    for (String key : associationsBetweenTypes.keySet()) {
      if (associationsBetweenTypes.get(key).size() > 1) {
        log(
            ErrorLevel.INFO,
            "Detected multiple associations between types "
                + key
                + " will force associationname matching for these");
        multipleAssocs.addAll(associationsBetweenTypes.get(key));
      }
    }
    return multipleAssocs;
  }

  @Override
  public boolean match(ASTCDAssociation association1, ASTCDAssociation association2) {
    // Compare associationNames
    boolean sameName = nameMatch(association1, association2, true);
    if (onlyNamedAssociations && !sameName) {
      log(
          ErrorLevel.DEBUG,
          "Association don't match (with option merge only named associations): different or "
              + "unspecified names ",
          association1,
          association2);
      return false;
    }
    boolean explicitRoleMatch = onlyNamedAssociations;

    if (!sameName) {
      // try if one of the names is unspecified
      if (!nameMatch(association1, association2, false)) {
        log(
            ErrorLevel.DEBUG,
            "Association don't match: different names " + (onlyNamedAssociations ? "strict " : " "),
            association1,
            association2);
        return false;
      }
    } else {
      // Ensure non-strict: we already have a name match, thus roles can
      // be checked non-strict
      explicitRoleMatch = false;
    }
    Optional<ASTCDAssociation> alignedAssocation2 =
        CDMergeUtils.tryAlignAssociation(association1, association2);
    if (!alignedAssocation2.isPresent()) {
      log(ErrorLevel.DEBUG, "Association don't match: different types", association1, association2);
      return false;
    }
    // check roles
    if (!rolesMatch(association1, alignedAssocation2.get(), explicitRoleMatch ? 2 : 0)) {
      if (sameName) {
        log(
            ErrorLevel.ERROR,
            "Association with same name but conflicting roles!",
            association1,
            association2);
      } else {
        log(
            ErrorLevel.DEBUG,
            "Association don't match "
                + (explicitRoleMatch ? "strict " : " ")
                + ": different roles ",
            association1,
            association2);
      }

      return false;
    }

    // check qualifier
    if (!qualifierMatch(association1, alignedAssocation2.get())) {
      if (sameName) {
        log(
            ErrorLevel.ERROR,
            "Association with same name but conflitcing qualifier.",
            association1,
            association2);
      } else {
        log(
            ErrorLevel.DEBUG,
            "Association don't match: qualifier conflict",
            association1,
            association2);
      }
      return false;
    }

    // check cardinalities
    if (!cardinalitiesMatch(association1, alignedAssocation2.get())) {
      if (sameName) {
        log(
            ErrorLevel.ERROR,
            "Association with same name but incompatible cardinalities.",
            association1,
            association2);
      } else {
        log(
            ErrorLevel.DEBUG,
            "Association don't match: different cardinalities",
            association1,
            association2);
      }
      return false;
    }
    // check navigation
    // We always assume that directions match as -> doesn't forbid <->
    // explicitly
    if (!navigationDirectionMatch(association1, alignedAssocation2.get())) {
      if (sameName) {
        log(
            ErrorLevel.ERROR,
            "Association with same name but incompatible navigation direction.",
            association1,
            association2);
      } else {
        log(
            ErrorLevel.DEBUG,
            "Association don't match: incompatible navigation direction",
            association1,
            association2);
      }
      return false;
    }

    // All checks passed
    log(ErrorLevel.FINE, "Found matching associations ", association1, association2);
    return true;
  }

  @Override
  public boolean nameMatch(ASTCDAssociation assoc1, ASTCDAssociation assoc2, boolean strict) {
    // association name should be either specified and equal or unspecified
    if (assoc1.isPresentName() && assoc2.isPresentName()) {
      return assoc1.getName().equalsIgnoreCase(assoc2.getName());
    }
    if (strict) {
      // Both names must be specified in strict mode
      return false;
    }
    // No Conflict
    return true;
  }

  /** roles should be equal if specified for both associations */
  @Override
  public boolean rolesMatch(
      ASTCDAssociation association1, ASTCDAssociation association2, final int numRolesToMatch) {

    // Generally we don't allow any role conflict
    if (association1.getLeft().isPresentCDRole()) {
      // LeftRole1 != LeftRole2
      if (association2.getLeft().isPresentCDRole()
          && !association2
              .getLeft()
              .getCDRole()
              .getName()
              .equalsIgnoreCase(association1.getLeft().getCDRole().getName())) {
        log(
            ErrorLevel.FINE,
            "Roles don't match: "
                + association1.getLeft().getCDRole()
                + " / "
                + association2.getLeft().getCDRole(),
            association1,
            association2);
        return false;
      }
    }
    if (association1.getRight().isPresentCDRole()) {
      // RightRole1 != RightRole2
      if (association2.getRight().isPresentCDRole()
          && !association2
              .getRight()
              .getCDRole()
              .getName()
              .equalsIgnoreCase(association1.getRight().getCDRole().getName())) {
        log(
            ErrorLevel.FINE,
            "Roles don't match: "
                + association1.getRight().getCDRole()
                + " / "
                + association2.getRight().getCDRole(),
            association1,
            association2);
        return false;
      }
    }
    // One role must be specified and match
    if (numRolesToMatch == 1
        && !(association1.getLeft().isPresentCDRole() && association2.getLeft().isPresentCDRole()
            || association1.getRight().isPresentCDRole()
                && association2.getRight().isPresentCDRole())) {
      return false;
      // Two role must be specified and match
    } else if (numRolesToMatch == 2
        && !(association1.getLeft().isPresentCDRole()
            && association2.getLeft().isPresentCDRole()
            && association1.getRight().isPresentCDRole()
            && association2.getRight().isPresentCDRole())) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * cardinalities are only allowed to be different when one association is qualified while the
   * other is not
   */
  @Override
  public boolean cardinalitiesMatch(ASTCDAssociation association1, ASTCDAssociation association2) {
    boolean leftCardinalityOK = false;
    boolean rightCardinalityOK = false;

    // We have to check for composition first
    // === COMPOSITION in association1 ==
    // composition A - B
    // association [1] A - B || association [0..1] A - B
    // we expect that the composite is always on the left side, thus the
    // left cardinality can only be 1, 0..1 or unspecified
    if (association1.getCDAssocType().isComposition()
        && !association1.getLeft().isPresentCDCardinality()
        && association2.getLeft().isPresentCDCardinality()) {
      if (association2.getLeft().getCDCardinality().isMult()
          || association2.getLeft().getCDCardinality().isAtLeastOne()) {
        log(
            ErrorLevel.WARNING,
            "Association 1 is composition but specified cardinalities  in Association 2 do not "
                + "match.",
            association1,
            association2);
        return false;
      }
      leftCardinalityOK = true;
    }

    // === COMPOSITION in assoctiation2 ==
    // association [1] A - B || association [0..1] A - B
    // composition A - B
    // we expect that the composite is always on the left side, thus the
    // left cardinality can only be 1, 0..1 or unspecified
    if (association2.getCDAssocType().isComposition()
        && !association2.getLeft().isPresentCDCardinality()
        && association1.getLeft().isPresentCDCardinality()) {
      if (association1.getLeft().getCDCardinality().isMult()
          || association1.getLeft().getCDCardinality().isAtLeastOne()) {
        log(
            ErrorLevel.WARNING,
            "Association 2  is composition but specified cardinalities Association 1  do not "
                + "match.",
            association1,
            association2);
        return false;
      }
      leftCardinalityOK = true;
    }

    // === LEFT QUALIFIED ASSOCIATION in association1 ==
    // association1 A [QUALIFIER] - B
    // association2 A - B [*] || association2 A - B [1..*]
    if (association1.getLeft().isPresentCDQualifier()
        && !association2.getLeft().isPresentCDQualifier()
        && association2.getRight().isPresentCDCardinality()) {
      if (association2.getRight().getCDCardinality().isOpt()
          || association2.getRight().getCDCardinality().isOne()) {
        log(
            ErrorLevel.WARNING,
            "Association 1  is qualified but specified cardinalities in Association 2 do not "
                + "match.",
            association1,
            association2);
        return false;
      }
      rightCardinalityOK = true;
    }

    // === LEFT QUALIFIED ASSOCIATION in association2 ==
    // association2 A - B [*] || association2 A - B [1..*]
    // association2 A [QUALIFIER] - B
    if (association2.getLeft().isPresentCDQualifier()
        && !association1.getLeft().isPresentCDQualifier()
        && association1.getRight().isPresentCDCardinality()) {
      if (association1.getRight().getCDCardinality().isOpt()
          || association1.getRight().getCDCardinality().isOne()) {
        log(
            ErrorLevel.WARNING,
            "Association 2 is qualified but specified cardinalities in Association 1  do not "
                + "match.",
            association1,
            association2);
        return false;
      }
      rightCardinalityOK = true;
    }

    // === RIGHT QUALIFIED ASSOCIATION in association1 ==
    // association1 A - [QUALIFIER] B
    // association2 [*] A - B || association2 [1..*] A - B
    if (association1.getRight().isPresentCDQualifier()
        && !association2.getRight().isPresentCDQualifier()
        && association2.getLeft().isPresentCDCardinality()) {
      if (association2.getLeft().getCDCardinality().isOpt()
          || association2.getLeft().getCDCardinality().isOne()) {
        log(
            ErrorLevel.WARNING,
            "Association 1 is qualified but specified cardinalities 	in Association 2 do not "
                + "match.",
            association1,
            association2);
        return false;
      }
      leftCardinalityOK = true;
    }

    // === RIGHT QUALIFIED ASSOCIATION in association1 ==
    // association1 [*] A - B || association2 [1..*] A - B
    // association2 A - [QUALIFIER] B
    if (association2.getRight().isPresentCDQualifier()
        && !association1.getRight().isPresentCDQualifier()
        && association1.getLeft().isPresentCDCardinality()) {
      if (association1.getLeft().getCDCardinality().isOpt()
          || association1.getLeft().getCDCardinality().isOne()) {
        log(
            ErrorLevel.WARNING,
            "Association 2 is qualified but specified cardinalities in Association 1 do not match.",
            association1,
            association2);
        return false;
      }
      leftCardinalityOK = true;
    }

    // ==== STANDARD CASES ===
    // If specified, cardinalities must match

    if (!leftCardinalityOK) {
      // The standard cases:
      if (association1.getLeft().isPresentCDCardinality()
          && association2.getLeft().isPresentCDCardinality()) {
        leftCardinalityOK =
            (association1.getLeft().getCDCardinality().isMult()
                    == association2.getLeft().getCDCardinality().isMult())
                && (association1.getLeft().getCDCardinality().isOne()
                    == association2.getLeft().getCDCardinality().isOne())
                && (association1.getLeft().getCDCardinality().isAtLeastOne()
                    == association2.getLeft().getCDCardinality().isAtLeastOne())
                && (association1.getLeft().getCDCardinality().isOpt()
                    == association2.getLeft().getCDCardinality().isOpt());

      } else {
        leftCardinalityOK = true;
      }
    }
    if (leftCardinalityOK && !rightCardinalityOK) {
      if (association1.getRight().isPresentCDCardinality()
          && association2.getRight().isPresentCDCardinality()) {
        rightCardinalityOK =
            (association1.getRight().getCDCardinality().isMult()
                    == association2.getRight().getCDCardinality().isMult())
                && (association1.getRight().getCDCardinality().isOne()
                    == association2.getRight().getCDCardinality().isOne())
                && (association1.getRight().getCDCardinality().isAtLeastOne()
                    == association2.getRight().getCDCardinality().isAtLeastOne())
                && (association1.getRight().getCDCardinality().isOpt()
                    == association2.getRight().getCDCardinality().isOpt());

      } else {
        rightCardinalityOK = true;
      }
    }

    return leftCardinalityOK && rightCardinalityOK;
  }

  @Override
  public boolean navigationDirectionMatch(
      ASTCDAssociation association1, ASTCDAssociation association2) {

    // We treat navigation directions as underspecification, thus allowing
    // every match as there is currently no forbidden navigation like "x->"
    // or "<-x"
    return true;
  }

  @Override
  public boolean qualifierMatch(ASTCDAssociation association1, ASTCDAssociation association2) {

    ASTCDQualifier qualifier1;
    ASTCDQualifier qualifier2;

    boolean matchTypeName = false;

    if (association1.getLeft().isPresentCDQualifier()
        && association2.getLeft().isPresentCDQualifier()) {
      qualifier1 = association1.getLeft().getCDQualifier();
      final String qualifier1Name = qualifier1.getName();
      qualifier2 = association2.getLeft().getCDQualifier();
      final String qualifier2Name = qualifier2.getName();
      if (qualifier1.isPresentByAttributeName() && qualifier2.isPresentByAttributeName()) {
        return qualifier1.getName().equals(qualifier2.getName());
      } else if (qualifier1.isPresentByType() && qualifier2.isPresentByType()) {
        return CDMergeUtils.getTypeName(qualifier1.getByType())
            .equals(CDMergeUtils.getTypeName(qualifier2.getByType()));
      } else {
        if (qualifier1.isPresentByAttributeName()
            && qualifier2.isPresentByType()
            && association1.getEnclosingScope() != null) {

          // We allow merging if the name refers to an attribute with
          // the type of the second qualifier
          Optional<CDTypeSymbol> targetClassSymbol =
              association1
                  .getEnclosingScope()
                  .resolveCDType(
                      association1
                          .getRight()
                          .getMCQualifiedType()
                          .getMCQualifiedName()
                          .getBaseName());
          if (targetClassSymbol.isPresent()) {
            Optional<FieldSymbol> attributeSymbol =
                targetClassSymbol.get().getFieldList().stream()
                    .filter(field -> qualifier1Name.equals(field.getName()))
                    .findAny();
            String typeName = "";
            if (attributeSymbol.isPresent() && attributeSymbol.get().getType() != null) {
              typeName = attributeSymbol.get().getType().getTypeInfo().getName();
            }
            if (attributeSymbol.isPresent() && attributeSymbol.get().isPresentAstNode()) {
              typeName =
                  CDMergeUtils.getTypeName((ASTCDAttribute) attributeSymbol.get().getAstNode());
            }
            if (typeName != ""
                && typeName.equals(CDMergeUtils.getTypeName(qualifier2.getByType()))) {
              matchTypeName = true;
            } else {
              log(
                  ErrorLevel.WARNING,
                  "Qualifier refers to an unknown/unresolvable Attribute or unknown Type "
                      + association1.getRightReferenceName()
                      + "."
                      + qualifier1Name,
                  association1);
              return false;
            }
          } else {
            log(
                ErrorLevel.WARNING,
                "Qualifier refers to an unknown/unresolvable Type "
                    + association1.getRightReferenceName(),
                association1);
            return false;
          }

        } else if (qualifier1.isPresentByType()
            && qualifier2.isPresentByAttributeName()
            && association2.getEnclosingScope() != null) {

          // We allow merging if the name refers to an attribute with
          // the type of the second qualifier
          Optional<CDTypeSymbol> targetClassSymbol =
              association2
                  .getEnclosingScope()
                  .resolveCDType(
                      association2
                          .getRight()
                          .getMCQualifiedType()
                          .getMCQualifiedName()
                          .getBaseName());
          if (targetClassSymbol.isPresent()) {
            Optional<FieldSymbol> attributeSymbol =
                targetClassSymbol.get().getFieldList().stream()
                    .filter(field -> qualifier2Name.equals(field.getName()))
                    .findAny();
            String typeName = "";
            if (attributeSymbol.isPresent() && attributeSymbol.get().getType() != null) {
              typeName = attributeSymbol.get().getType().getTypeInfo().getName();
            }
            if (attributeSymbol.isPresent() && attributeSymbol.get().isPresentAstNode()) {
              typeName =
                  CDMergeUtils.getTypeName((ASTCDAttribute) attributeSymbol.get().getAstNode());
            }
            if (typeName != ""
                && typeName.equals(CDMergeUtils.getTypeName(qualifier1.getByType()))) {
              matchTypeName = true;
            } else {
              log(
                  ErrorLevel.WARNING,
                  "Qualifier refers to an unknown/unresolvable Attribute "
                      + association1.getRightReferenceName()
                      + "."
                      + qualifier2Name,
                  association1);
              return false;
            }
          } else {
            log(
                ErrorLevel.WARNING,
                "Qualifier refers to an unknown/unresolvable Type "
                    + association1.getRightReferenceName(),
                association1);
            return false;
          }
        }
      }
    }

    matchTypeName = false;
    if (association1.getRight().isPresentCDQualifier()
        && association2.getRight().isPresentCDQualifier()) {
      qualifier1 = association1.getRight().getCDQualifier();
      final String qualifier1Name = qualifier1.getName();
      qualifier2 = association2.getRight().getCDQualifier();
      final String qualifier2Name = qualifier2.getName();
      if (qualifier1.isPresentByAttributeName() && qualifier2.isPresentByAttributeName()) {
        return qualifier1.getName().equals(qualifier2.getName());
      } else if (qualifier1.isPresentByType() && qualifier2.isPresentByType()) {
        return CDMergeUtils.getTypeName(qualifier1.getByType())
            .equals(CDMergeUtils.getTypeName(qualifier2.getByType()));
      } else {

        if (qualifier1.isPresentByAttributeName()
            && qualifier2.isPresentByType()
            && association1.getEnclosingScope() != null) {

          // We allow merging if the name refers to an attribute with
          // the type of the second qualifierss

          Optional<CDTypeSymbol> targetClassSymbol =
              association1
                  .getEnclosingScope()
                  .resolveCDType(
                      association1
                          .getLeft()
                          .getMCQualifiedType()
                          .getMCQualifiedName()
                          .getBaseName());
          if (targetClassSymbol.isPresent()) {
            Optional<FieldSymbol> attributeSymbol =
                targetClassSymbol.get().getFieldList().stream()
                    .filter(field -> qualifier1Name.equals(field.getName()))
                    .findAny();
            if (attributeSymbol.isPresent()
                && attributeSymbol
                    .get()
                    .getType()
                    .getTypeInfo()
                    .getName()
                    .equals(CDMergeUtils.getTypeName(qualifier2.getByType()))) {
              matchTypeName = true;
            }
          }
          if (!matchTypeName) {
            log(
                ErrorLevel.WARNING,
                "Qualifier refers to an unknown/unresolvable Attribute "
                    + association1.getRightReferenceName(),
                association1);
            return false;
          }
        } else if (qualifier1.isPresentByType()
            && qualifier2.isPresentByAttributeName()
            && association2.getEnclosingScope() != null) {

          // We allow merging if the name refers to an attribute with
          // the type of the second qualifier

          Optional<CDTypeSymbol> targetClassSymbol =
              association2
                  .getEnclosingScope()
                  .resolveCDType(
                      association2
                          .getLeft()
                          .getMCQualifiedType()
                          .getMCQualifiedName()
                          .getBaseName());
          if (targetClassSymbol.isPresent()) {
            Optional<FieldSymbol> attributeSymbol =
                targetClassSymbol.get().getFieldList().stream()
                    .filter(field -> qualifier2Name.equals(field.getName()))
                    .findAny();
            if (attributeSymbol.isPresent()
                && attributeSymbol
                    .get()
                    .getType()
                    .getTypeInfo()
                    .getName()
                    .equals(CDMergeUtils.getTypeName(qualifier1.getByType()))) {
              matchTypeName = true;
            }
          }
          if (!matchTypeName) {
            log(
                ErrorLevel.WARNING,
                "Qualifier refers to an unknown/unresolvable Attribute "
                    + association1.getRightReferenceName(),
                association1);
            return false;
          }
        }
      }
    }
    return true;
  }
}
