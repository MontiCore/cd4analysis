/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.cd;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.CompletionException;
import de.monticore.cdconcretization.ConcretizationHelper;
import de.monticore.cdconcretization.association.AssocMatchDirection;
import de.monticore.cdconcretization.association.IAssociationCompleter;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cdmatcher.BooleanMatchingStrategy;
import de.se_rwth.commons.logging.Log;

import java.util.Set;

public class ExistingAssociationsCDCompleter extends AbstractCDCompleter {

  private static final String LOG_NAME = ExistingAssociationsCDCompleter.class.getName();

  private final IAssociationCompleter assocDetailsCompleter;

  public ExistingAssociationsCDCompleter(IAssociationCompleter assocDetailsCompleter) {
    this.assocDetailsCompleter = assocDetailsCompleter;
  }

  @Override
  public void complete(ASTCDCompilationUnit ccd, ASTCDCompilationUnit rcd,
      CDCompletionContext context) throws CompletionException {
    // First: complete the incarnations, so add stuff to the underspecified incarnation
    // or do nothing to the over-specified incarnation

    Log.debug("=== START completing existing associations ===", LOG_NAME);
    // Iterate through all concrete associations
    for (ASTCDAssociation cAssoc : ccd.getCDDefinition().getCDAssociationsList()) {
      for (ASTCDAssociation rAssoc : rcd.getCDDefinition().getCDAssociationsList()) {
        // Check if the concrete association is an incarnation of the reference association
        if (context.getAssociationIncStrategy().isMatched(cAssoc, rAssoc)) {
          Log.debug("Found match for assoc: " + CD4CodeMill.prettyPrint(cAssoc, false), LOG_NAME);
          AssocMatchDirection matchDirection = determineMatchDirection(cAssoc, rAssoc, context);
          assocDetailsCompleter.completeAssociation(cAssoc, rAssoc, matchDirection);
        }
      }
    }
    Log.debug("=== DONE completing existing associations ===", LOG_NAME);
    super.complete(ccd, rcd, context);
  }

  /**
   * Checks in what direction the concrete association matches the reference association. If the
   * direction cannot be determined, an exception is thrown.
   *
   * @param cAssoc the concrete association
   * @param rAssoc the reference association
   * @return
   * @throws CompletionException if the match direction cannot be determined.
   */
  private AssocMatchDirection determineMatchDirection(ASTCDAssociation cAssoc,
      ASTCDAssociation rAssoc, CDCompletionContext context) throws CompletionException {
    ASTCDCompilationUnit ccd = context.getConcreteCD();
    ASTCDCompilationUnit rcd = context.getReferenceCD();
    // Extract the left and right types of the concrete association
    ASTCDType cLeftType = ConcretizationHelper.getAssocLeftType(ccd, cAssoc);
    ASTCDType cRightType = ConcretizationHelper.getAssocRightType(ccd, cAssoc);

    // Extract the left and right types of the reference association
    ASTCDType rLeftType = ConcretizationHelper.getAssocLeftType(rcd, rAssoc);
    ASTCDType rRightType = ConcretizationHelper.getAssocRightType(rcd, rAssoc);

    // Get all supertypes of the left type and right type of the concrete association
    Set<ASTCDType> cLeftSuperTypes = CDDiffUtil.getAllSuperTypes(cLeftType, ccd.getCDDefinition());
    Set<ASTCDType> cRightSuperTypes = CDDiffUtil.getAllSuperTypes(rRightType, ccd
        .getCDDefinition());

    BooleanMatchingStrategy<ASTCDType> typeIncStrategyMatchingSubTypes = context
        .getTypeIncStrategyMatchingSubTypes();
    // Determine if the concrete association matches the reference association in the standard
    // direction.
    // A match occurs if the left types match and the right types match, considering supertypes as
    // well.
    boolean match = (typeIncStrategyMatchingSubTypes.isMatched(cLeftType, rLeftType)
        || cLeftSuperTypes.stream().anyMatch(sLeftType -> typeIncStrategyMatchingSubTypes.isMatched(
            sLeftType, rLeftType))) && (typeIncStrategyMatchingSubTypes.isMatched(cRightType,
                rRightType) || cRightSuperTypes.stream().anyMatch(
                    sRightType -> typeIncStrategyMatchingSubTypes.isMatched(sRightType,
                        rRightType)));

    // Determine if the concrete association matches the reference association in the reverse
    // direction.
    // A match in reverse occurs if the left type of the concrete association matches the right type
    // of the reference, and vice versa.
    boolean matchInReverse = (typeIncStrategyMatchingSubTypes.isMatched(cLeftType, rRightType)
        || cLeftSuperTypes.stream().anyMatch(sLeftType -> typeIncStrategyMatchingSubTypes.isMatched(
            sLeftType, rRightType))) && (typeIncStrategyMatchingSubTypes.isMatched(cRightType,
                rLeftType) || cRightSuperTypes.stream().anyMatch(
                    sRightType -> typeIncStrategyMatchingSubTypes.isMatched(sRightType,
                        rLeftType)));

    if (!match && !matchInReverse) {
      // If no match is found, throw an exception as the associations could not be completed.
      throw new CompletionException("Associations could not be completed.");
    }

    // Check for potential role name conflicts if a match is found in both directions.
    // If the role name on one side of the association matches the role name on the opposite side of
    // the reference association, the match is invalidated.
    if (match && matchInReverse) {
      if ((cAssoc.getRight().isPresentCDRole() && rAssoc.getLeft().isPresentCDRole() && cAssoc
          .getRight().getCDRole().getName().equals(rAssoc.getLeft().getCDRole().getName()))
          || (cAssoc.getLeft().isPresentCDRole() && rAssoc.getRight().isPresentCDRole() && cAssoc
              .getLeft().getCDRole().getName().equals(rAssoc.getRight().getCDRole().getName()))) {
        match = false;
      }
    }

    return match ? AssocMatchDirection.SAME_DIRECTION : AssocMatchDirection.REVERSE_DIRECTION;
  }

}
