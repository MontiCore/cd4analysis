package de.monticore.cdconcretization;

import static de.monticore.cdconformance.CDConfParameter.*;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.CDConformanceChecker;
import de.monticore.cdconformance.inc.association.*;
import de.monticore.cdconformance.inc.type.CompTypeIncStrategy;
import de.monticore.cdconformance.inc.type.EqTypeIncStrategy;
import de.monticore.cdconformance.inc.type.STTypeIncStrategy;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdmatcher.MatchCDAssocsGreedy;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.tf.odrulegeneration._ast.ASTAssociation;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultAssocIncCompleter implements IIncarnationCompleter<ASTAssociation> {

  protected ASTCDCompilationUnit rcd;
  protected ASTCDCompilationUnit ccd;

  protected String mapping;
  protected CompAssocIncStrategy compAssocIncStrategy;
  protected CompTypeIncStrategy compTypeIncStrategy;
  protected ConcretizationHelper helper;
  protected boolean intersectCardinality = false;

  public DefaultAssocIncCompleter(
      ASTCDCompilationUnit conCD, ASTCDCompilationUnit refCD, String mapping) {
    this.rcd = refCD;
    this.ccd = conCD;
    this.mapping = mapping;

    compTypeIncStrategy = new CompTypeIncStrategy(refCD, mapping);
    compTypeIncStrategy.addIncStrategy(new STTypeIncStrategy(refCD, mapping));
    compTypeIncStrategy.addIncStrategy(new EqTypeIncStrategy(refCD, mapping));

    compAssocIncStrategy = new CompAssocIncStrategy(refCD, mapping);
    compAssocIncStrategy.addIncStrategy(new STNamedAssocIncStrategy(refCD, mapping));
    compAssocIncStrategy.addIncStrategy(new EqNameAssocIncStrategy(refCD, mapping));
    compAssocIncStrategy.addIncStrategy(
        new RolePrefixInNavDirIncStrategy(compTypeIncStrategy, conCD, refCD));
    compAssocIncStrategy.addIncStrategy(
        new RolePrefixIfPresentIncStrategy(compTypeIncStrategy, conCD, refCD));

    this.helper = new ConcretizationHelper(ccd, rcd, compTypeIncStrategy, compAssocIncStrategy);
  }

  @Override
  public void completeIncarnations() throws CompletionException {
    // First: complete the incarnations, so add stuff to the underspecified incarnation
    // or do nothing to the over-specified incarnation

    // Iterate through all concrete associations
    for (ASTCDAssociation cAssoc : ccd.getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation rAssoc : rcd.getCDDefinition().getCDAssociationsList()) {
        // Check if the concrete association is an incarnation of the reference association
        if (compAssocIncStrategy.isMatched(cAssoc, rAssoc)) {
          handleAssociation(cAssoc, rAssoc);
        }
      }
    }

    // Second:
    identifyAndAddMissingAssociations();

    // Third: Conformance check
    CDConformanceChecker checker =
        new CDConformanceChecker(
            Set.of(
                STEREOTYPE_MAPPING,
                NAME_MAPPING,
                SRC_TARGET_ASSOC_MAPPING,
                INHERITANCE,
                ALLOW_CARD_RESTRICTION));

    if (!checker.checkConformance(ccd, rcd, mapping)) {
      throw new CompletionException("The association completion result is not conform");
    }
  }

  private void handleAssociation(ASTCDAssociation cAssoc, ASTCDAssociation rAssoc)
      throws CompletionException {

    // Extract the left and right types of the concrete association
    ASTCDType cLeftType = helper.getAssocLeftType(ccd, cAssoc);
    ASTCDType cRightType = helper.getAssocRightType(ccd, cAssoc);

    // Extract the left and right types of the reference association
    ASTCDType rLeftType = helper.getAssocLeftType(rcd, rAssoc);
    ASTCDType rRightType = helper.getAssocRightType(rcd, rAssoc);

    // Get all supertypes of the left type and right type of the concrete association
    Set<ASTCDType> cLeftSuperTypes = CDDiffUtil.getAllSuperTypes(cLeftType, ccd.getCDDefinition());
    Set<ASTCDType> cRightSuperTypes =
        CDDiffUtil.getAllSuperTypes(rRightType, ccd.getCDDefinition());

    // Determine if the concrete association matches the reference association in the standard
    // direction.
    // A match occurs if the left types match and the right types match, considering supertypes as
    // well.
    boolean match =
        (compTypeIncStrategy.isMatched(cLeftType, rLeftType)
                || cLeftSuperTypes.stream()
                    .anyMatch(sLeftType -> compTypeIncStrategy.isMatched(sLeftType, rLeftType)))
            && (compTypeIncStrategy.isMatched(cRightType, rRightType)
                || cRightSuperTypes.stream()
                    .anyMatch(sRightType -> compTypeIncStrategy.isMatched(sRightType, rRightType)));

    // Determine if the concrete association matches the reference association in the reverse
    // direction.
    // A match in reverse occurs if the left type of the concrete association matches the right type
    // of the reference, and vice versa.
    boolean matchInReverse =
        (compTypeIncStrategy.isMatched(cLeftType, rRightType)
                || cLeftSuperTypes.stream()
                    .anyMatch(sLeftType -> compTypeIncStrategy.isMatched(sLeftType, rRightType)))
            && (compTypeIncStrategy.isMatched(cRightType, rLeftType)
                || cRightSuperTypes.stream()
                    .anyMatch(sRightType -> compTypeIncStrategy.isMatched(sRightType, rLeftType)));

    // If a match is found in either direction, proceed to complete the association names.
    if (match || matchInReverse) {
      completeAssociationNames(cAssoc, rAssoc);
    } else {
      // If no match is found, throw an exception as the associations could not be completed.
      throw new CompletionException("Associations could not be completed.");
    }

    // Check for potential role name conflicts if a match is found in both directions.
    // If the role name on one side of the association matches the role name on the opposite side of
    // the reference association, the match is invalidated.
    if (match && matchInReverse) {
      if ((cAssoc.getRight().isPresentCDRole()
              && rAssoc.getLeft().isPresentCDRole()
              && cAssoc
                  .getRight()
                  .getCDRole()
                  .getName()
                  .equals(rAssoc.getLeft().getCDRole().getName()))
          || (cAssoc.getLeft().isPresentCDRole()
              && rAssoc.getRight().isPresentCDRole()
              && cAssoc
                  .getLeft()
                  .getCDRole()
                  .getName()
                  .equals(rAssoc.getRight().getCDRole().getName()))) {
        match = false;
      }
    }

    // If the match is still valid, complete the association properties for the same sides.
    if (match) {
      // Complete association
      completeAssocNavigability(cAssoc, rAssoc);
      completeAssocCardinality(cAssoc.getLeft(), rAssoc.getLeft());
      completeAssocCardinality(cAssoc.getRight(), rAssoc.getRight());
      completeAssociationRoleNames(cAssoc.getLeft(), rAssoc.getLeft());
      completeAssociationRoleNames(cAssoc.getRight(), rAssoc.getRight());
    } else {
      // If the match is in reverse, complete the association properties for alternating sides.
      completeAssocNavigabilityReverse(cAssoc, rAssoc);
      completeAssocCardinality(cAssoc.getLeft(), rAssoc.getRight());
      completeAssocCardinality(cAssoc.getRight(), rAssoc.getLeft());
      completeAssociationRoleNames(cAssoc.getLeft(), rAssoc.getRight());
      completeAssociationRoleNames(cAssoc.getRight(), rAssoc.getLeft());
    }

    // Handle potential role name conflicts in a post-processing step.
    renameRoleIfConflicting(cAssoc);
  }

  private void renameRoleIfConflicting(ASTCDAssociation assoc) throws CompletionException {
    /* Wenn es eine andere Assoziation mit gleichem Rollennamen gibt
           und der Typ auf der gegenüberliegenden Seite gleich / Subtyp / Supertyp ist,
           dann ändere den entsprechenden Rollennamen für assoc!
    */
    boolean renamed = false;

    // Check and rename conflicts on the right side
    if (assoc.getRight().isPresentCDRole()) {
      renamed =
          checkAndRenameConflict(
              assoc,
              assoc.getRight().getCDRole().getName(),
              assoc.getLeftQualifiedName().getQName(),
              assoc.getRightQualifiedName().getQName(),
              true);
    }

    // Check and rename conflicts on the left side
    if (assoc.getLeft().isPresentCDRole()) {
      renamed =
          checkAndRenameConflict(
              assoc,
              assoc.getLeft().getCDRole().getName(),
              assoc.getRightQualifiedName().getQName(),
              assoc.getLeftQualifiedName().getQName(),
              false);
    }
  }

  private boolean checkAndRenameConflict(
      ASTCDAssociation assoc,
      String roleName,
      String oppositeQName,
      String currentQName,
      boolean isRightSide)
      throws CompletionException {

    boolean renamed = false;

    ASTCDType oppositeType = helper.getAssocTypeByQName(ccd, oppositeQName);

    // Create a set to store the full hierarchy (all supertypes and subtypes) of the opposite type.
    Set<ASTCDType> typeFullHierarchy = new HashSet<>();
    typeFullHierarchy.add(oppositeType);
    typeFullHierarchy.addAll(CDDiffUtil.getAllSuperTypes(oppositeType, ccd.getCDDefinition()));
    typeFullHierarchy.addAll(CDDiffUtil.getAllStrictSubTypes(oppositeType, ccd.getCDDefinition()));

    // Iterate over all associations in the current class diagram to detect potential conflicts.
    for (ASTCDAssociation otherAssoc : ccd.getCDDefinition().getCDAssociationsList()) {
      if (otherAssoc == assoc) continue; // Skip the current association itself.

      // Resolve the type on the opposite side of the other association being compared.

      boolean rename = false;

      // Check right role and left type of the other association
      if (otherAssoc.getRight().isPresentCDRole()) {
        ASTCDType leftType = helper.getAssocLeftType(ccd, otherAssoc);
        String rightRole = otherAssoc.getRight().getCDRole().getName();
        rename = roleName.equals(rightRole) && typeFullHierarchy.contains(leftType);
      }

      // Check left role and right type of the other association
      if (otherAssoc.getLeft().isPresentCDRole()) {
        ASTCDType rightType = helper.getAssocRightType(ccd, otherAssoc);
        String leftRole = otherAssoc.getLeft().getCDRole().getName();
        rename = roleName.equals(leftRole) && typeFullHierarchy.contains(rightType);
      }

      // Check if the role name matches and if the types are either the same or within the same type
      // hierarchy.
      if (rename) {

        // Create a new role name by appending the name of the current type to the original role
        // name.
        String newRoleName = roleName + "_" + currentQName;

        // Set the new role name on the correct side (left or right) of the current association.
        if (isRightSide) {
          assoc.getRight().getCDRole().setName(newRoleName);
        } else {
          assoc.getLeft().getCDRole().setName(newRoleName);
        }

        renamed = true;
      }
    }

    return renamed;
  }

  private void completeAssocNavigability(ASTCDAssociation cAssoc, ASTCDAssociation rAssoc) {
    // if cAssoc has complementary navigation it becomes bidirectional, else copy navigation of
    // rAssoc
    if ((cAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && rAssoc.getCDAssocDir().isDefinitiveNavigableLeft())
        || (cAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && rAssoc.getCDAssocDir().isDefinitiveNavigableRight())
        || rAssoc.getCDAssocDir().isBidirectional()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDBiDirBuilder().build());
    } else if (rAssoc.getCDAssocDir().isDefinitiveNavigableRight()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDLeftToRightDirBuilder().build());
    } else if (rAssoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDRightToLeftDirBuilder().build());
    }
    // else
    // unspecified or overspecifiedf by cAssoc, so do nothing
  }

  private void completeAssocNavigabilityReverse(ASTCDAssociation cAssoc, ASTCDAssociation rAssoc) {
    // if cAssoc has complementary navigation it becomes bidirectional, else copy navigation of
    // rAssoc
    if ((cAssoc.getCDAssocDir().isDefinitiveNavigableRight()
            && rAssoc.getCDAssocDir().isDefinitiveNavigableRight())
        || (cAssoc.getCDAssocDir().isDefinitiveNavigableLeft()
            && rAssoc.getCDAssocDir().isDefinitiveNavigableLeft())
        || rAssoc.getCDAssocDir().isBidirectional()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDBiDirBuilder().build());
    } else if (rAssoc.getCDAssocDir().isDefinitiveNavigableRight()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDRightToLeftDirBuilder().build());
    } else if (rAssoc.getCDAssocDir().isDefinitiveNavigableLeft()) {
      cAssoc.setCDAssocDir(CD4CodeMill.cDLeftToRightDirBuilder().build());
    }
    // else
    // unspecified or overspecifiedf by cAssoc, so do nothing
  }

  private void completeAssocCardinality(ASTCDAssocSide cAssocSide, ASTCDAssocSide rAssocSide)
      throws CompletionException {
    if (!cAssocSide.isPresentCDCardinality() && rAssocSide.isPresentCDCardinality()) {
      cAssocSide.setCDCardinality(rAssocSide.getCDCardinality());
    } else if (cAssocSide.isPresentCDCardinality()
        && rAssocSide.isPresentCDCardinality()
        && !cAssocSide.getCDCardinality().deepEquals(rAssocSide.getCDCardinality())) {
      if (!intersectCardinality) {
        throw new CompletionException("Unequal cardinalities");
      } else {
        // todo: schnitt von cardinalitäten
      }
    }
  }

  private void completeAssociationRoleNames(ASTCDAssocSide cAssocSide, ASTCDAssocSide rAssocSide) {
    if (!cAssocSide.isPresentCDRole() && rAssocSide.isPresentCDRole()) {
      cAssocSide.setCDRole(rAssocSide.getCDRole());
    }
  }

  private void completeAssociationNames(ASTCDAssociation cAssoc, ASTCDAssociation rAssoc) {
    // Check and complete association name
    if (!cAssoc.isPresentName() && rAssoc.isPresentName()) {
      if (ccd.getCDDefinition().getCDAssociationsList().stream()
          .noneMatch(
              assoc -> (assoc.isPresentName() && assoc.getName().equals(rAssoc.getName())))) {
        cAssoc.setName(rAssoc.getName());
      }
    }
  }

  public void identifyAndAddMissingAssociations() throws CompletionException {
    CDDiffUtil.refreshSymbolTable(ccd);

    // Iterate over all associations in the reference class diagram
    for (ASTCDAssociation rAssoc : rcd.getCDDefinition().getCDAssociationsList()) {
      MatchingStrategy<ASTCDAssociation> greedyMatching =
          new MatchCDAssocsGreedy(compTypeIncStrategy, ccd, rcd);

      // Find all associations in the concrete class diagram that match the reference association
      Set<ASTCDAssociation> assocIncarnations =
          ccd.getCDDefinition().getCDAssociationsList().stream()
              .filter(cAssoc -> compAssocIncStrategy.isMatched(cAssoc, rAssoc))
              .collect(Collectors.toSet());

      // Find associations that match greedily , but ensure that they don't match more than one
      // element
      Set<ASTCDAssociation> assocGreedyMatches =
          ccd.getCDDefinition().getCDAssociationsList().stream()
              .filter(
                  cAssoc ->
                      greedyMatching.isMatched(cAssoc, rAssoc)
                          && greedyMatching.getMatchedElements(cAssoc).size() < 2)
              .collect(Collectors.toSet());

      // Resolve the left and right types of the reference association in the reference class
      // diagram
      ASTCDType rLeftType = helper.getAssocLeftType(rcd, rAssoc);
      ASTCDType rRightType = helper.getAssocRightType(rcd, rAssoc);

      // Collect all type incarnations in the concrete class diagram that match the left and right
      // types
      Set<ASTCDType> rLeftTypeIncarnations =
          helper.getCDTypes(ccd).stream()
              .filter(type -> compTypeIncStrategy.isMatched(type, rLeftType))
              .collect(Collectors.toSet());

      Set<ASTCDType> rRightTypeIncarnations =
          helper.getCDTypes(ccd).stream()
              .filter(type -> compTypeIncStrategy.isMatched(type, rRightType))
              .collect(Collectors.toSet());

      // Initialize sets to track which type incarnations still need processing
      Set<ASTCDType> leftTypeInc2Process = new HashSet<>(rLeftTypeIncarnations);
      Set<ASTCDType> rightTypeInc2Process = new HashSet<>(rRightTypeIncarnations);

      // Process the type incarnations to find and handle matching associations
      // First, process left-type incarnations against right-type incarnations
      processTypeIncarnations(
          rLeftTypeIncarnations,
          rRightTypeIncarnations,
          leftTypeInc2Process,
          assocIncarnations,
          assocGreedyMatches,
          ccd.getCDDefinition(),
          rAssoc);

      // Then, process right-type incarnations against left-type incarnations
      processTypeIncarnations(
          rRightTypeIncarnations,
          rLeftTypeIncarnations,
          rightTypeInc2Process,
          assocIncarnations,
          assocGreedyMatches,
          ccd.getCDDefinition(),
          rAssoc);

      CDDiffUtil.refreshSymbolTable(ccd);

      // Finally, process any remaining type incarnations that still need to be handled
      // Process the remaining left-type incarnations against right-type incarnations
      processTypeInc2Process(leftTypeInc2Process, rRightTypeIncarnations, rAssoc, true);

      // Process the remaining right-type incarnations against left-type incarnations
      processTypeInc2Process(rightTypeInc2Process, rLeftTypeIncarnations, rAssoc, false);
    }
  }

  private void processTypeIncarnations(
      Set<ASTCDType> rTypeIncarnation,
      Set<ASTCDType> rOppositeTypeIncarnations,
      Set<ASTCDType> typeInc2Process,
      Set<ASTCDAssociation> assocIncarnations,
      Set<ASTCDAssociation> assocGreedyMatches,
      ASTCDDefinition cd,
      ASTCDAssociation rAssoc)
      throws CompletionException {

    // Iterate over each type incarnation in rTypeIncarnation
    for (ASTCDType typeInc : rTypeIncarnation) {
      // Retrieve all supertypes for the current type incarnation from the cd
      Set<ASTCDType> superTypes = CDDiffUtil.getAllSuperTypes(typeInc, cd);

      // First, attempt to find a match among the specific association incarnations
      Optional<ASTCDAssociation> match =
          processAssociations(superTypes, rOppositeTypeIncarnations, assocIncarnations, cd);

      // If a match is found, remove the current type incarnation from the set to be processed and
      // continue
      if (match.isPresent()) {
        typeInc2Process.remove(typeInc);
        continue;
      }

      // If no match is found in specific incarnations, try matching against the greedy matches
      match = processAssociations(superTypes, rOppositeTypeIncarnations, assocGreedyMatches, cd);

      // If a match is found among the greedy matches, remove the current type incarnation from the
      // set to be processed
      // and handle the association accordingly
      if (match.isPresent()) {
        typeInc2Process.remove(typeInc);
        handleAssociation(match.get(), rAssoc);
      }
    }
  }

  private Optional<ASTCDAssociation> processAssociations(
      Set<ASTCDType> superTypes,
      Set<ASTCDType> oppositeTypeIncarnations,
      Set<ASTCDAssociation> associations,
      ASTCDDefinition cd)
      throws CompletionException {
    for (ASTCDAssociation assoc : associations) {
      // Check if there is a match with the left side of the association
      if (checkAssociationMatch(superTypes, oppositeTypeIncarnations, assoc, cd)) {
        return Optional.of(assoc); // Match found, return the association
      }
    }
    return Optional.empty(); // No match found, return empty
  }

  private void processTypeInc2Process(
      Set<ASTCDType> typeInc2Process,
      Set<ASTCDType> otherTypeIncs,
      ASTCDAssociation rAssoc,
      boolean isLeftToRight) {

    // Iterate over each type incarnation in typeInc2Process
    for (ASTCDType typeInc : typeInc2Process) {
      // For each type incarnation in typeInc2Process, iterate over each type incarnation in
      // otherTypeIncs
      for (ASTCDType otherTypeInc : otherTypeIncs) {
        ASTCDAssociation association = rAssoc.deepClone();

        // Set the qualified names for the association's left and right types based on the direction
        if (isLeftToRight) {
          // Set the left type to the current type incarnation and the right type to the other type
          // incarnation
          association
              .getLeft()
              .setMCQualifiedType(
                  CD4CodeMill.mCQualifiedTypeBuilder()
                      .setMCQualifiedName(
                          MCQualifiedNameFacade.createQualifiedName(
                              typeInc.getSymbol().getInternalQualifiedName()))
                      .build());
          association
              .getRight()
              .setMCQualifiedType(
                  CD4CodeMill.mCQualifiedTypeBuilder()
                      .setMCQualifiedName(
                          MCQualifiedNameFacade.createQualifiedName(
                              otherTypeInc.getSymbol().getInternalQualifiedName()))
                      .build());

          // If the right type does not have a role name, set it to the type incarnation's name
          if (association.getRight().isPresentCDRole()) {
            // If a role name is already present, append the type incarnation's name to it
            association
                .getRight()
                .getCDRole()
                .setName(
                    association.getRight().getCDRole().getName() + "_" + otherTypeInc.getName());
          }
          if (association.getLeft().isPresentCDRole()) {
            association
                .getLeft()
                .getCDRole()
                .setName(association.getLeft().getCDRole().getName() + "_" + typeInc.getName());
          }

        } else {
          // Set the left type to the other type incarnation and the right type to the current type
          // incarnation
          association
              .getLeft()
              .setMCQualifiedType(
                  CD4CodeMill.mCQualifiedTypeBuilder()
                      .setMCQualifiedName(
                          MCQualifiedNameFacade.createQualifiedName(
                              otherTypeInc.getSymbol().getInternalQualifiedName()))
                      .build());
          association
              .getRight()
              .setMCQualifiedType(
                  CD4CodeMill.mCQualifiedTypeBuilder()
                      .setMCQualifiedName(
                          MCQualifiedNameFacade.createQualifiedName(
                              typeInc.getSymbol().getInternalQualifiedName()))
                      .build());

          if (association.getLeft().isPresentCDRole()) {
            association
                .getLeft()
                .getCDRole()
                .setName(
                    association.getLeft().getCDRole().getName() + "_" + otherTypeInc.getName());
          }
          if (association.getRight().isPresentCDRole()) {
            association
                .getRight()
                .getCDRole()
                .setName(association.getRight().getCDRole().getName() + "_" + typeInc.getName());
          }
        }

        if (ccd.getCDDefinition().getCDAssociationsList().stream()
            .noneMatch(a -> a.deepEquals(association))) {
          ccd.getCDDefinition().addCDElement(association);
        }
      }
    }
  }

  private boolean checkAssociationMatch(
      Set<ASTCDType> superTypes,
      Set<ASTCDType> oppositeTypeIncarnations,
      ASTCDAssociation assoc,
      ASTCDDefinition cd)
      throws CompletionException {

    boolean fail = false;

    if (superTypes.stream()
        .anyMatch(
            superType ->
                // Compare the qualified name of the supertype with the qualified name of the
                // left side of the association
                superType
                    .getSymbol()
                    .getInternalQualifiedName()
                    .contains(assoc.getLeftQualifiedName().getQName()))
    // additionally, check if any supertype of the opposite types matches the right side of
    // the association
    ) {
      if (oppositeTypeIncarnations.stream()
          // For each type in the oppositeTypeIncarnations set, get all its supertypes
          .flatMap(oType -> CDDiffUtil.getAllSuperTypes(oType, cd).stream())
          .anyMatch(
              oSuperType ->
                  // Compare the qualified name of the supertype with the qualified name of the
                  // right side of the association
                  oSuperType
                      .getSymbol()
                      .getInternalQualifiedName()
                      .contains(assoc.getRightQualifiedName().getQName()))) {
        return true;
      }
      fail = true;
    }
    // Same logic, but this time we check from right to left
    if (superTypes.stream()
        .anyMatch(
            superType ->
                // Compare the qualified name of the supertype with the qualified name of the
                // right side of the association
                superType
                    .getSymbol()
                    .getInternalQualifiedName()
                    .contains(assoc.getRightQualifiedName().getQName()))
    // Additionally, check if any supertype of the opposite types matches the left side of the
    // association
    ) {
      if (oppositeTypeIncarnations.stream()
          // For each type in the oppositeTypeIncarnations set, get all its supertypes
          .flatMap(oType -> CDDiffUtil.getAllSuperTypes(oType, cd).stream())
          .anyMatch(
              oSuperType ->
                  // Compare the qualified name of the supertype with the qualified name of the
                  // left side of the association
                  oSuperType
                      .getSymbol()
                      .getInternalQualifiedName()
                      .contains(assoc.getLeftQualifiedName().getQName()))) {
        return true;
      }
      fail = true;
    }
    if (fail) {
      throw new CompletionException(
          "Something went wrong when identifying missing association incarnations.");
    }
    return false;
  }

  protected void setIntersectCardinality(Boolean b) {
    this.intersectCardinality = b;
  }

  protected Boolean getIntersectCardinality() {
    return intersectCardinality;
  }

  public CompAssocIncStrategy getCompAssocIncStrategy() {
    return this.compAssocIncStrategy;
  }
}
