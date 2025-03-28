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
import de.monticore.cdmatcher.MatchCDTypesToSubTypes;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.tf.odrulegeneration._ast.ASTAssociation;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultAssocIncCompleter implements IIncarnationCompleter<ASTAssociation> {

  private static final String LOG_NAME = DefaultAssocIncCompleter.class.getName();

  protected ASTCDCompilationUnit rcd;
  protected ASTCDCompilationUnit ccd;

  protected String mapping;
  protected CompAssocIncStrategy compAssocIncStrategy;
  protected CompTypeIncStrategy compTypeIncStrategy;
  protected CompTypeIncStrategy typeIncStrategyMatchingSubTypes;
  protected ConcretizationHelper helper;
  protected boolean intersectCardinality = false;

  private boolean greedyMatcherEnabled = true; // TODO remove! only for testing

  public DefaultAssocIncCompleter(
      ASTCDCompilationUnit conCD, ASTCDCompilationUnit refCD, String mapping) {
    this.rcd = refCD;
    this.ccd = conCD;
    this.mapping = mapping;

    // TODO use same config params as for conformance checker

    compTypeIncStrategy = new CompTypeIncStrategy(refCD, mapping);
    compTypeIncStrategy.addIncStrategy(new STTypeIncStrategy(refCD, mapping));
    compTypeIncStrategy.addIncStrategy(new EqTypeIncStrategy(refCD, mapping));

    typeIncStrategyMatchingSubTypes = new CompTypeIncStrategy(refCD, mapping);
    typeIncStrategyMatchingSubTypes.addIncStrategy(compTypeIncStrategy);
    typeIncStrategyMatchingSubTypes.addIncStrategy(new MatchCDTypesToSubTypes(compTypeIncStrategy, conCD, refCD));

    compAssocIncStrategy = new CompAssocIncStrategy(refCD, mapping);
    compAssocIncStrategy.addIncStrategy(new STNamedAssocIncStrategy(refCD, mapping));
    compAssocIncStrategy.addIncStrategy(new EqNameAssocIncStrategy(refCD, mapping));
    compAssocIncStrategy.addIncStrategy(
        new RolePrefixInNavDirIncStrategy(typeIncStrategyMatchingSubTypes, conCD, refCD));
    compAssocIncStrategy.addIncStrategy(
        new RolePrefixIfPresentIncStrategy(typeIncStrategyMatchingSubTypes, conCD, refCD));

    this.helper = new ConcretizationHelper(ccd, rcd, compTypeIncStrategy, compAssocIncStrategy);
  }

  @Override
  public void completeIncarnations() throws CompletionException {
    // First: complete the incarnations, so add stuff to the underspecified incarnation
    // or do nothing to the over-specified incarnation

    Log.debug("=== START completing existing associations ===", LOG_NAME);
    // Iterate through all concrete associations
    for (ASTCDAssociation cAssoc : ccd.getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation rAssoc : rcd.getCDDefinition().getCDAssociationsList()) {
        // Check if the concrete association is an incarnation of the reference association
        if (compAssocIncStrategy.isMatched(cAssoc, rAssoc)) {
          Log.debug("Found match for assoc: " + CD4CodeMill.prettyPrint(cAssoc, false), LOG_NAME);
          handleAssociation(cAssoc, rAssoc);
        }
      }
    }
    Log.debug("=== DONE completing existing associations ===", LOG_NAME);

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
        (typeIncStrategyMatchingSubTypes.isMatched(cLeftType, rLeftType)
                || cLeftSuperTypes.stream()
                    .anyMatch(sLeftType -> typeIncStrategyMatchingSubTypes.isMatched(sLeftType, rLeftType)))
            && (typeIncStrategyMatchingSubTypes.isMatched(cRightType, rRightType)
                || cRightSuperTypes.stream()
                    .anyMatch(sRightType -> typeIncStrategyMatchingSubTypes.isMatched(sRightType, rRightType)));

    // Determine if the concrete association matches the reference association in the reverse
    // direction.
    // A match in reverse occurs if the left type of the concrete association matches the right type
    // of the reference, and vice versa.
    boolean matchInReverse =
        (typeIncStrategyMatchingSubTypes.isMatched(cLeftType, rRightType)
                || cLeftSuperTypes.stream()
                    .anyMatch(sLeftType -> typeIncStrategyMatchingSubTypes.isMatched(sLeftType, rRightType)))
            && (typeIncStrategyMatchingSubTypes.isMatched(cRightType, rLeftType)
                || cRightSuperTypes.stream()
                    .anyMatch(sRightType -> typeIncStrategyMatchingSubTypes.isMatched(sRightType, rLeftType)));

    if (!match && !matchInReverse) {
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

    completeAssociation(cAssoc, rAssoc, match);
  }

  /***
   * Completes the properties of the concrete association based on the reference association.
   *
   * @param cAssoc The concrete association to complete
   * @param rAssoc The reference association to use for completion
   * @param matchInSameDirection A boolean indicating whether the associations match in the same
   *                             direction (left-left, right-right) or reverse (left-right,
   *                             right-left).
   * @throws CompletionException
   */
  private void completeAssociation(ASTCDAssociation cAssoc, ASTCDAssociation rAssoc, boolean matchInSameDirection) throws CompletionException {
    completeAssociationName(cAssoc, rAssoc);

    if (matchInSameDirection) {
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

    // Handle potential role name conflicts in a post-processing step
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

  private void completeAssociationName(ASTCDAssociation cAssoc, ASTCDAssociation rAssoc) {
    // Check and complete association name
    if (!cAssoc.isPresentName() && rAssoc.isPresentName()) {
      if (ccd.getCDDefinition().getCDAssociationsList().stream()
          .noneMatch(
              assoc -> (assoc.isPresentName() && assoc.getName().equals(rAssoc.getName())))) {
        cAssoc.setName(rAssoc.getName());
      }
    }
  }

  /**
   * Adds missing associations to the concrete class diagram based on the associations in the
   * reference class diagram.
   *
   * @throws CompletionException
   */
  public void identifyAndAddMissingAssociations() throws CompletionException {
    Log.debug("=== START finding missing associations ===", LOG_NAME);
    CDDiffUtil.refreshSymbolTable(ccd);

    // Iterate over all associations in the reference class diagram
    for (ASTCDAssociation rAssoc : rcd.getCDDefinition().getCDAssociationsList()) {
      Log.debug("Finding matches for assoc: " + CD4CodeMill.prettyPrint(rAssoc, false), LOG_NAME);

      MatchingStrategy<ASTCDAssociation> greedyMatching =
          new MatchCDAssocsGreedy(typeIncStrategyMatchingSubTypes, ccd, rcd);

      // Find all associations in the concrete class diagram that match the reference association
      Set<ASTCDAssociation> assocIncarnations =
          ccd.getCDDefinition().getCDAssociationsList().stream()
              .filter(cAssoc -> compAssocIncStrategy.isMatched(cAssoc, rAssoc))
              .collect(Collectors.toSet());

      Log.debug("Found normal matches: " + assocIncarnations.stream()
              .map(a -> CD4CodeMill.prettyPrint(a, false))
              .collect(Collectors.toList()), LOG_NAME);

      // Find associations that match greedily , but ensure that they don't match more than one
      // element
      Set<ASTCDAssociation> assocGreedyMatches =
          ccd.getCDDefinition().getCDAssociationsList().stream()
              .filter(
                  cAssoc ->
                      greedyMatching.isMatched(cAssoc, rAssoc)
                          && greedyMatching.getMatchedElements(cAssoc).size() < 2)
              .collect(Collectors.toSet());

      Log.debug("Found greedy matches: " + assocGreedyMatches.stream()
              .map(a -> CD4CodeMill.prettyPrint(a, false))
              .collect(Collectors.toList()), LOG_NAME);

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

      Log.debug("DONE finding matches for assoc: " + CD4CodeMill.prettyPrint(rAssoc, false), LOG_NAME);
      Log.debug("remaining left type incarnations: " + leftTypeInc2Process.stream().map(ASTCDType::getName).collect(Collectors.toList()), LOG_NAME);
      Log.debug("remaining right type incarnations: " + rightTypeInc2Process.stream().map(ASTCDType::getName).collect(Collectors.toList()), LOG_NAME);

      // Finally, process any remaining type incarnations that still need to be handled
      // Process the remaining left-type incarnations against right-type incarnations
      addAssociationIncarnations(leftTypeInc2Process, rRightTypeIncarnations, rAssoc);

      // Process the remaining right-type incarnations against left-type incarnations
      addAssociationIncarnations(rLeftTypeIncarnations, rightTypeInc2Process, rAssoc);
      Log.debug("=== DONE processing assoc: " + CD4CodeMill.prettyPrint(rAssoc, false), LOG_NAME);
    }
  }

  /**
   * Process the type incarnations to find and handle matching associations.
   *    -> so we still try to find more matches than we found in the step before.
   *
   * @param rTypeIncarnation all incarnation of the one side of the association
   * @param rOppositeTypeIncarnations all incarnation of the other side of the association
   * @param typeInc2Process Subset of 'rTypeIncarnation' (?) that still needs an incarnation of the reference association (??)
   * @param assocIncarnations "normal" matches from the association matching strategy -> NOTE: these were processed before in step 4 by handleAssociation(...)
   * @param assocGreedyMatches greedy matches for the reference association to process
   * @param cd the CONCRETE class diagram
   * @param rAssoc the reference association to process
   * @throws CompletionException
   */
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
          findAssociationToAnyOppositeTypeInc(superTypes, rOppositeTypeIncarnations, assocIncarnations, cd);

      // If a match is found, remove the current type incarnation from the set to be processed and
      // continue
      if (match.isPresent()) {
        typeInc2Process.remove(typeInc);
        // NOTE: we do not call "handleAssociation" here, because we already processed the match in the step before
        Log.debug("Found normal match for type inc: " + typeInc.getName(), LOG_NAME);
        Log.debug("reference assoc: " + CD4CodeMill.prettyPrint(rAssoc, false), LOG_NAME);
        Log.debug("concrete assoc: " + CD4CodeMill.prettyPrint(match.get(), false), LOG_NAME);
        continue;
      }

      if (greedyMatcherEnabled) {
        // If no match is found in specific incarnations, try matching against the greedy matches
        match = findAssociationToAnyOppositeTypeInc(superTypes, rOppositeTypeIncarnations, assocGreedyMatches, cd);

        // If a match is found among the greedy matches, remove the current type incarnation from the
        // set to be processed
        // and handle the association accordingly
        if (match.isPresent()) {
          typeInc2Process.remove(typeInc);
          Log.debug("Found GREEDY match for type inc: " + typeInc.getName(), LOG_NAME);
          Log.debug("reference assoc: " + CD4CodeMill.prettyPrint(rAssoc, false), LOG_NAME);
          Log.debug("concrete assoc: " + CD4CodeMill.prettyPrint(match.get(), false), LOG_NAME);
          Log.debug("completing greedy match", LOG_NAME);
          handleAssociation(match.get(), rAssoc);
        }
      }
    }
  }

  /**
   * Finds any association in the given set that "associates" one of the superTypes with one if the
   * "oppositeTypeIncarnations".
   *
   * @param superTypes all concrete super types of the "one side" of the association
   * @param oppositeTypeIncarnations all incarnations of the type on the "other side" of the association
   * @param associations the set of concrete associations to check for a match
   * @param cd the concrete CD
   * @return an association from the given set that connect one of the superTypes with one of the
   * oppositeTypeIncarnations.
   * @throws CompletionException
   */
  private Optional<ASTCDAssociation> findAssociationToAnyOppositeTypeInc(
      Set<ASTCDType> superTypes,
      Set<ASTCDType> oppositeTypeIncarnations,
      Set<ASTCDAssociation> associations,
      ASTCDDefinition cd)
      throws CompletionException {

    // For each type in the oppositeTypeIncarnations set we also check the super types
    Set<ASTCDType> oppositeTypeIncarnationSuperTypes = oppositeTypeIncarnations.stream()
            .flatMap(oType -> CDDiffUtil.getAllSuperTypes(oType, cd).stream())
            .collect(Collectors.toSet());

    for (ASTCDAssociation assoc : associations) {
      // Check if the association "assoc" relates any of the superTypes with any of the oppositeTypeIncarnations (or its super types) (??)
      if (checkAssociationMatchesTypes(superTypes, oppositeTypeIncarnationSuperTypes, assoc)) {
        return Optional.of(assoc); // Match found, return the association
      }
    }
    return Optional.empty(); // No match found, return empty
  }

  /**
   * Creates incarnations of a reference association for the given sets of left and right type
   * incarnations.
   * For each pair of left and right type incarnations, a new association is created.
   *
   * @param leftTypesIncs the left type incarnations missing an association
   * @param rightTypeIncs the right type incarnations missing an association
   * @param referenceAssociation the reference association for which we want to create missing incarnations
   */
  private void addAssociationIncarnations(
      Set<ASTCDType> leftTypesIncs,
      Set<ASTCDType> rightTypeIncs,
      ASTCDAssociation referenceAssociation) {

    for (ASTCDType leftTypeInc : leftTypesIncs) {
      for (ASTCDType rightTypeInc : rightTypeIncs) {
        ASTCDAssociation association = referenceAssociation.deepClone();

        // Set the left and right types of the association based on the type incarnations
        association.getLeft().setMCQualifiedType(
                CD4CodeMill.mCQualifiedTypeBuilder()
                        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(
                                leftTypeInc.getSymbol().getInternalQualifiedName()))
                        .build());
        association.getRight().setMCQualifiedType(
                CD4CodeMill.mCQualifiedTypeBuilder()
                        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(
                                rightTypeInc.getSymbol().getInternalQualifiedName()))
                        .build());

        // If the right type does not have a role name, it is implicitly the type incarnation's
        // name (first character lowercase) anyway.
        if (association.getRight().isPresentCDRole()) {
          // If a role name is already present, append the type incarnation's name to it
          association
                  .getRight()
                  .getCDRole()
                  .setName(
                          association.getRight().getCDRole().getName() + "_" + rightTypeInc.getName());
        }
        if (association.getLeft().isPresentCDRole()) {
          association
                  .getLeft()
                  .getCDRole()
                  .setName(association.getLeft().getCDRole().getName() + "_" + leftTypeInc.getName());
        }

        // Only add it if it is not already present. This can happen if the same type incarnation
        // occurs in the "leftType2Process" set while processing "left types" missing an association
        // and after that in the "allLeftTypeIncs" set while processing the "right types" missing
        // an association.
        if (ccd.getCDDefinition().getCDAssociationsList().stream()
            .noneMatch(a -> a.deepEquals(association))) {
          ccd.getCDDefinition().addCDElement(association);
        }
      }
    }
  }

  /**
   * Checks if the given concrete association relates any of the types from set A with any of the
   * types from set B.
   *
   * @param typesSideA the set (A) of types to check for on one side of the association
   * @param typesSideB the set (B) of types to check for on the other side of the association
   * @param assoc the CONCRETE association to check for a match
   * @return true if the association relates any of the types from set A with any of the types from
   * @throws CompletionException
   */
  private boolean checkAssociationMatchesTypes(
      Set<ASTCDType> typesSideA,
      Set<ASTCDType> typesSideB,
      ASTCDAssociation assoc)
      throws CompletionException {

    boolean fail = false;

    if (typesSideA.stream()
        .anyMatch(
            superType ->
                // Compare the qualified name of the supertype with the qualified name of the
                // left side of the association
                superType
                    .getSymbol()
                    .getInternalQualifiedName()
                    .contains(assoc.getLeftQualifiedName().getQName())) // TODO why contains?? - if because CD names can differ -> remove first part
    // additionally, check if any supertype of the opposite types matches the right side of
    // the association
    ) {
      if (typesSideB.stream()
          .anyMatch(
              oSuperType ->
                  // Compare the qualified name of the supertype with the qualified name of the
                  // right side of the association
                  oSuperType
                      .getSymbol()
                      .getInternalQualifiedName()
                      .contains(assoc.getRightQualifiedName().getQName()))) { // TODO why contains?? - if because CD names can differ -> remove first part
        return true;
      }
      fail = true; // TODO analyze what this fail means!
    }
    // Same logic, but this time we check from right to left
    if (typesSideA.stream()
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
      if (typesSideB.stream()
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
}
