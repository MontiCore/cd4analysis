/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.cd;

import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cdconcretization.association.AssocMatchDirection;
import de.monticore.cdconcretization.association.AssociationMatch;
import de.monticore.cdconcretization.association.IAssociationCompleter;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdmatcher.ExternalCandidatesMatchingStrategy;
import de.monticore.cdmatcher.MatchCDAssocsGreedy;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adds missing associations to the concrete class diagram based on the associations in the
 * reference class diagram.
 */
public class MissingAssociationsCDCompleter extends AbstractCDCompleter {
  
  private static final String LOG_NAME = MissingAssociationsCDCompleter.class.getName();
  
  private final IAssociationCompleter assocDetailsCompleter;
  
  private boolean greedyMatcherEnabled = true; // TODO remove! only for testing
  
  public MissingAssociationsCDCompleter(IAssociationCompleter assocDetailsCompleter) {
    this.assocDetailsCompleter = assocDetailsCompleter;
  }
  
  @Override
  public void complete(ASTCDCompilationUnit ccd, ASTCDCompilationUnit rcd,
      CDCompletionContext context) throws CompletionException {
    Log.debug("=== START finding missing associations ===", LOG_NAME);
    CDDiffUtil.refreshSymbolTable(ccd);
    
    // Iterate over all associations in the reference class diagram
    for (ASTCDAssociation rAssoc : rcd.getCDDefinition().getCDAssociationsList()) {
      Log.debug("Finding matches for assoc: " + CD4CodeMill.prettyPrint(rAssoc, false), LOG_NAME);
      
      ExternalCandidatesMatchingStrategy<ASTCDAssociation> greedyMatching = new MatchCDAssocsGreedy(
          context.getTypeIncStrategyMatchingSubTypes(), ccd, rcd);
      
      // Find all associations in the concrete class diagram that match the reference association
      Set<ASTCDAssociation> assocIncarnations = ccd.getCDDefinition().getCDAssociationsList()
          .stream().filter(cAssoc -> context.getAssociationIncStrategy().isMatched(cAssoc, rAssoc))
          .collect(Collectors.toSet());
      
      Log.debug("Found normal matches: " + assocIncarnations.stream().map(a -> CD4CodeMill
          .prettyPrint(a, false)).collect(Collectors.toList()), LOG_NAME);
      
      // Find associations that match greedily , but ensure that they don't match more than one
      // element
      Set<ASTCDAssociation> assocGreedyMatches = ccd.getCDDefinition().getCDAssociationsList()
          .stream().filter(cAssoc -> greedyMatching.isMatched(cAssoc, rAssoc) && greedyMatching
              .getMatchedElements(cAssoc).size() < 2).collect(Collectors.toSet());
      
      Log.debug("Found greedy matches: " + assocGreedyMatches.stream().map(a -> CD4CodeMill
          .prettyPrint(a, false)).collect(Collectors.toList()), LOG_NAME);
      
      // Resolve the left and right types of the reference association in the reference class
      // diagram
      ASTCDType rLeftType = ConcretizationHelper.getAssocLeftType(rcd, rAssoc);
      ASTCDType rRightType = ConcretizationHelper.getAssocRightType(rcd, rAssoc);
      
      // Collect all type incarnations in the concrete class diagram that match the left and right
      // types
      Set<ASTCDType> rLeftTypeIncarnations = ConcretizationHelper.getCDTypes(ccd).stream().filter(
          type -> context.getTypeIncStrategy().isMatched(type, rLeftType)).collect(Collectors
              .toSet());
      
      Set<ASTCDType> rRightTypeIncarnations = ConcretizationHelper.getCDTypes(ccd).stream().filter(
          type -> context.getTypeIncStrategy().isMatched(type, rRightType)).collect(Collectors
              .toSet());
      
      // Initialize sets to track which type incarnations still need processing
      Set<ASTCDType> leftTypeInc2Process = new HashSet<>(rLeftTypeIncarnations);
      Set<ASTCDType> rightTypeInc2Process = new HashSet<>(rRightTypeIncarnations);
      
      // Process the type incarnations to find and handle matching associations
      // First, process left-type incarnations against right-type incarnations
      processTypeIncarnations(rLeftTypeIncarnations, rRightTypeIncarnations, leftTypeInc2Process,
          assocIncarnations, assocGreedyMatches, ccd.getCDDefinition(), rAssoc, true);
      
      // Then, process right-type incarnations against left-type incarnations
      processTypeIncarnations(rRightTypeIncarnations, rLeftTypeIncarnations, rightTypeInc2Process,
          assocIncarnations, assocGreedyMatches, ccd.getCDDefinition(), rAssoc, false);
      
      CDDiffUtil.refreshSymbolTable(ccd);
      
      Log.debug("DONE finding matches for assoc: " + CD4CodeMill.prettyPrint(rAssoc, false),
          LOG_NAME);
      Log.debug("remaining left type incarnations: " + leftTypeInc2Process.stream().map(
          ASTCDType::getName).collect(Collectors.toList()), LOG_NAME);
      Log.debug("remaining right type incarnations: " + rightTypeInc2Process.stream().map(
          ASTCDType::getName).collect(Collectors.toList()), LOG_NAME);
      
      // Finally, process any remaining type incarnations that still need to be handled
      // Process the remaining left-type incarnations against right-type incarnations
      addAssociationIncarnations(ccd, leftTypeInc2Process, rRightTypeIncarnations, rAssoc);
      
      // Process the remaining right-type incarnations against left-type incarnations
      addAssociationIncarnations(ccd, rLeftTypeIncarnations, rightTypeInc2Process, rAssoc);
      Log.debug("=== DONE processing assoc: " + CD4CodeMill.prettyPrint(rAssoc, false), LOG_NAME);
    }
    super.complete(ccd, rcd, context);
  }
  
  /**
   * Process the type incarnations to find and handle matching associations. -> so we still try to
   * find more matches than we found in the step before.
   *
   * @param rTypeIncarnation all incarnation of the one side of the association
   * @param rOppositeTypeIncarnations all incarnation of the other side of the association
   * @param typeInc2Process Subset of 'rTypeIncarnation' (?) that still needs an incarnation of the
   * reference association (??)
   * @param assocIncarnations "normal" matches from the association matching strategy -> NOTE: these
   * were processed before in step 4 by handleAssociation(...)
   * @param assocGreedyMatches greedy matches for the reference association to process
   * @param cd the CONCRETE class diagram
   * @param rAssoc the reference association to process
   * @param leftToRight indicates if we process left types against right types (true) or vice versa
   * (false). This indicates whether the 'typeInc2Process' set contains left or right type
   * incarnations.
   * @throws CompletionException
   */
  private void processTypeIncarnations(Set<ASTCDType> rTypeIncarnation,
      Set<ASTCDType> rOppositeTypeIncarnations, Set<ASTCDType> typeInc2Process,
      Set<ASTCDAssociation> assocIncarnations, Set<ASTCDAssociation> assocGreedyMatches,
      ASTCDDefinition cd, ASTCDAssociation rAssoc, boolean leftToRight) throws CompletionException {
    
    // Iterate over each type incarnation in rTypeIncarnation
    for (ASTCDType typeInc : rTypeIncarnation) {
      // Retrieve all supertypes for the current type incarnation from the cd
      Set<ASTCDType> superTypes = CDDiffUtil.getAllSuperTypes(typeInc, cd);
      
      // First, attempt to find a match among the specific association incarnations
      Optional<AssociationMatch> match = findAssociationToAnyOppositeTypeInc(superTypes,
          rOppositeTypeIncarnations, assocIncarnations, cd, leftToRight);
      
      // If a match is found, remove the current type incarnation from the set to be processed and
      // continue
      if (match.isPresent()) {
        typeInc2Process.remove(typeInc);
        // NOTE: we do not call "handleAssociation" here, because we already processed the match in
        // the step before
        Log.debug("Found normal match for type inc: " + typeInc.getName(), LOG_NAME);
        Log.debug("reference assoc: " + CD4CodeMill.prettyPrint(rAssoc, false), LOG_NAME);
        Log.debug("concrete assoc: " + CD4CodeMill.prettyPrint(match.get().getAssociation(), false),
            LOG_NAME);
        continue;
      }
      
      if (greedyMatcherEnabled) {
        // If no match is found in specific incarnations, try matching against the greedy matches
        match = findAssociationToAnyOppositeTypeInc(superTypes, rOppositeTypeIncarnations,
            assocGreedyMatches, cd, leftToRight);
        
        // If a match is found among the greedy matches, remove the current type incarnation from
        // the
        // set to be processed
        // and handle the association accordingly
        if (match.isPresent()) {
          typeInc2Process.remove(typeInc);
          Log.debug("Found GREEDY match for type inc: " + typeInc.getName(), LOG_NAME);
          Log.debug("reference assoc: " + CD4CodeMill.prettyPrint(rAssoc, false), LOG_NAME);
          Log.debug("concrete assoc: " + CD4CodeMill.prettyPrint(match.get().getAssociation(),
              false), LOG_NAME);
          Log.debug("completing greedy match", LOG_NAME);
          // instead of handleAssociation(...) we call completeAssociation(...) here because we
          // already
          // know in what direction the match is
          assocDetailsCompleter.completeAssociation(match.get().getAssociation(), rAssoc, match
              .get().getMatchDirection());
        }
      }
    }
  }
  
  /**
   * Finds any association in the given set that "associates" one of the superTypes with one if the
   * "oppositeTypeIncarnations".
   *
   * @param superTypes all concrete super types of the "one side" of the association
   * @param oppositeTypeIncarnations all incarnations of the type on the "other side" of the
   * association
   * @param associations the set of concrete associations to check for a match
   * @param cd the concrete CD
   * @param leftToRight indicates if we process left types against right types (true) or vice versa
   * (false).
   * @return an association from the given set that connect one of the superTypes with one of the
   * oppositeTypeIncarnations.
   * @throws CompletionException
   */
  private Optional<AssociationMatch> findAssociationToAnyOppositeTypeInc(Set<ASTCDType> superTypes,
      Set<ASTCDType> oppositeTypeIncarnations, Set<ASTCDAssociation> associations,
      ASTCDDefinition cd, boolean leftToRight) throws CompletionException {
    
    // For each type in the oppositeTypeIncarnations set we also check the super types
    Set<ASTCDType> oppositeTypeIncarnationSuperTypes = oppositeTypeIncarnations.stream().flatMap(
        oType -> CDDiffUtil.getAllSuperTypes(oType, cd).stream()).collect(Collectors.toSet());
    
    for (ASTCDAssociation assoc : associations) {
      // Check if the association "assoc" relates any of the superTypes with any of the
      // oppositeTypeIncarnations (or its super types) (??)
      Optional<AssocMatchDirection> matchDirection = checkAssociationMatchesTypes(superTypes,
          oppositeTypeIncarnationSuperTypes, assoc, leftToRight);
      if (matchDirection.isPresent()) {
        // Match found, return the association
        return Optional.of(new AssociationMatch(assoc, matchDirection.get()));
      }
    }
    return Optional.empty(); // No match found, return empty
  }
  
  /**
   * Creates incarnations of a reference association for the given sets of left and right type
   * incarnations. For each pair of left and right type incarnations, a new association is created.
   *
   * @param leftTypesIncs the left type incarnations missing an association
   * @param rightTypeIncs the right type incarnations missing an association
   * @param referenceAssociation the reference association for which we want to create missing
   * incarnations
   */
  private void addAssociationIncarnations(ASTCDCompilationUnit concreteCD,
      Set<ASTCDType> leftTypesIncs, Set<ASTCDType> rightTypeIncs,
      ASTCDAssociation referenceAssociation) {
    
    for (ASTCDType leftTypeInc : leftTypesIncs) {
      for (ASTCDType rightTypeInc : rightTypeIncs) {
        ASTCDAssociation association = referenceAssociation.deepClone();
        
        // Set the left and right types of the association based on the type incarnations
        association.getLeft().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(leftTypeInc.getSymbol()
                .getInternalQualifiedName())).build());
        association.getRight().setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder()
            .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(rightTypeInc.getSymbol()
                .getInternalQualifiedName())).build());
        
        // If the right type does not have a role name, it is implicitly the type incarnation's
        // name (first character lowercase) anyway.
        if (association.getRight().isPresentCDRole()) {
          // If a role name is already present, append the type incarnation's name to it
          association.getRight().getCDRole().setName(association.getRight().getCDRole().getName()
              + "_" + rightTypeInc.getName());
        }
        if (association.getLeft().isPresentCDRole()) {
          association.getLeft().getCDRole().setName(association.getLeft().getCDRole().getName()
              + "_" + leftTypeInc.getName());
        }
        
        // Only add it if it is not already present. This can happen if the same type incarnation
        // occurs in the "leftType2Process" set while processing "left types" missing an association
        // and after that in the "allLeftTypeIncs" set while processing the "right types" missing
        // an association.
        if (concreteCD.getCDDefinition().getCDAssociationsList().stream().noneMatch(a -> a
            .deepEquals(association))) {
          concreteCD.getCDDefinition().addCDElement(association);
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
   * @param leftToRight indicates if we process left types against right types (true) or vice versa
   * (false).
   * @return if the association relates any of the types from set A with any of the types from set
   * B, return the direction of the match (same or reverse), otherwise return empty.
   * @throws CompletionException
   */
  private Optional<AssocMatchDirection> checkAssociationMatchesTypes(Set<ASTCDType> typesSideA,
      Set<ASTCDType> typesSideB, ASTCDAssociation assoc, boolean leftToRight)
      throws CompletionException {
    
    boolean fail = false;
    
    if (typesSideA.stream().anyMatch(superType ->
    // Compare the qualified name of the supertype with the qualified name of the
    // left side of the association
    superType.getSymbol().getInternalQualifiedName().contains(assoc.getLeftQualifiedName()
        .getQName())) // TODO why contains?? - if because CD names can differ ->
    // remove first part
    // additionally, check if any supertype of the opposite types matches the right side of
    // the association
    ) {
      if (typesSideB.stream().anyMatch(oSuperType ->
      // Compare the qualified name of the supertype with the qualified name of the
      // right side of the association
      oSuperType.getSymbol().getInternalQualifiedName().contains(assoc.getRightQualifiedName()
          .getQName()))) { // TODO why contains?? - if because CD names can
        // differ -> remove first part
        return Optional.of(leftToRight ? AssocMatchDirection.SAME_DIRECTION
            : AssocMatchDirection.REVERSE_DIRECTION);
      }
      fail = true; // TODO analyze what this fail means!
    }
    // Same logic, but this time we check from right to left
    if (typesSideA.stream().anyMatch(superType ->
    // Compare the qualified name of the supertype with the qualified name of the
    // right side of the association
    superType.getSymbol().getInternalQualifiedName().contains(assoc.getRightQualifiedName()
        .getQName()))
    // Additionally, check if any supertype of the opposite types matches the left side of the
    // association
    ) {
      if (typesSideB.stream().anyMatch(oSuperType ->
      // Compare the qualified name of the supertype with the qualified name of the
      // left side of the association
      oSuperType.getSymbol().getInternalQualifiedName().contains(assoc.getLeftQualifiedName()
          .getQName()))) {
        return Optional.of(leftToRight ? AssocMatchDirection.REVERSE_DIRECTION
            : AssocMatchDirection.SAME_DIRECTION);
      }
      fail = true;
    }
    if (fail) {
      throw new CompletionException(
          "Something went wrong when identifying missing association incarnations.");
    }
    return Optional.empty();
  }
  
}
