/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.validation;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.log.MergePhase;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssociationChecker extends ModelValidatorBase {

  private AssociationChecker(MergeBlackBoard blackboard) {
    super(blackboard);
  }

  public static class Builder extends ModelValidatorBuilder {

    @Override
    protected ModelValidator buildModelValidator(MergeBlackBoard blackboard) {
      return new AssociationChecker(blackboard);
    }
  }

  // FIXME Shouldnt this be covered by CoCos?

  /** Registers a design issue if there are two compositions with the same target type */
  protected void checkCompositionDesignIssues(ASTCDDefinition cd) {
    List<ASTCDAssociation> resAssocs = cd.getCDAssociationsList();
    ASTCDAssociation assoc1;
    ASTCDAssociation assoc2;
    for (int i = 0; i < resAssocs.size() - 1; i++) {
      assoc1 = resAssocs.get(i);
      for (int j = i + 1; j < resAssocs.size(); j++) {
        assoc2 = resAssocs.get(j);
        // we expect that the composite is always on the left side
        if (assoc1.getCDAssocType().isComposition()
            && assoc2.getCDAssocType().isComposition()
            && assoc1
                .getRightReferenceName()
                .toString()
                .equals(assoc2.getRightReferenceName().toString())) {
          if (assoc1.isPresentName() && assoc2.isPresentName()) {
            getBlackBoard()
                .addLog(
                    ErrorLevel.DESIGN_ISSUE,
                    "Compositions '"
                        + assoc1.getName()
                        + "' and '"
                        + assoc2.getName()
                        + "' have the same right target type '"
                        + assoc1.getRightReferenceName().toString()
                        + "'.",
                    MergePhase.VALIDATION,
                    assoc1,
                    assoc2);
          } else {
            getBlackBoard()
                .addLog(
                    ErrorLevel.DESIGN_ISSUE,
                    "There are two compositions which have the same right target type '"
                        + assoc1.getRightReferenceName().toString()
                        + "'.",
                    MergePhase.VALIDATION,
                    assoc1,
                    assoc2);
          }
        }
      }
    }
  }

  /** All associations between two types must have identical names */
  protected void checkUniqueAssociationNames(ASTCDDefinition cd) {
    Map<String, ASTCDAssociation> namedAssociations = new HashMap<>();
    ASTCDAssociation other;
    for (ASTCDAssociation assoc :
        getBlackBoard().getIntermediateMergedCD().getCDDefinition().getCDAssociationsList()) {
      if (assoc.isPresentName()) {
        if (namedAssociations.containsKey(assoc.getName())) {
          other = namedAssociations.get(assoc.getName());
          if (other
                  .getLeftReferenceName()
                  .toString()
                  .equalsIgnoreCase(assoc.getLeftReferenceName().toString())
              && other
                  .getRightReferenceName()
                  .toString()
                  .equalsIgnoreCase(assoc.getRightReferenceName().toString())) {
            getBlackBoard()
                .addLog(
                    ErrorLevel.ERROR,
                    "There are two associations between the same types which have an identical name. "
                        + "However, association names must be unique for two associated types",
                    MergePhase.VALIDATION,
                    assoc,
                    other);
          }
        } else {
          namedAssociations.put(assoc.getName(), assoc);
        }
      }
    }
  }

  /**
   * Registers a warning if the navigation over a role is ambiguous
   *
   * @throws MergingFailedException
   */
  // FIXME Need to be reviewed!
  protected void checkRolesConflictWithTypes(ASTCDDefinition cd) {
    List<ASTCDAssociation> resAssocs = cd.getCDAssociationsList();
    ASTCDAssociation assoc1;
    String leftRefName1;
    String rightRefName1;
    String leftRole1;
    String rightRole1;
    ASTCDAssociation assoc2;
    String leftRefName2;
    String rightRefName2;
    String leftRole2;
    String rightRole2;

    for (int i = 0; i < resAssocs.size() - 1; i++) {
      assoc1 = resAssocs.get(i);
      leftRefName1 = assoc1.getLeftReferenceName().toString().toLowerCase();
      rightRefName1 = assoc1.getRightReferenceName().toString().toLowerCase();
      leftRole1 =
          (assoc1.getLeft().isPresentCDRole())
              ? assoc1.getLeft().getCDRole().getName().toLowerCase()
              : leftRefName1;
      rightRole1 =
          (assoc1.getRight().isPresentCDRole())
              ? assoc1.getRight().getCDRole().getName().toLowerCase()
              : rightRefName1;
      for (int j = i + 1; j < resAssocs.size(); j++) {
        assoc2 = resAssocs.get(j);

        leftRefName2 = assoc2.getLeftReferenceName().toString().toLowerCase();
        rightRefName2 = assoc2.getRightReferenceName().toString().toLowerCase();

        leftRole2 =
            (assoc2.getLeft().isPresentCDRole())
                ? assoc2.getLeft().getCDRole().getName().toLowerCase()
                : leftRefName2;
        rightRole2 =
            (assoc2.getRight().isPresentCDRole())
                ? assoc2.getRight().getCDRole().getName().toLowerCase()
                : rightRefName2;

        if (leftRole1.equals(leftRole2)
            && !leftRefName1.equals(leftRefName2)
            && rightRefName1.equals(rightRefName2)) {
          getBlackBoard()
              .addLog(
                  ErrorLevel.WARNING,
                  "Navigation over '" + rightRefName1 + "." + leftRole1 + "' is ambiguous.",
                  MergePhase.VALIDATION,
                  assoc1,
                  assoc2);
        } else if (rightRole1.equals(rightRole2)
            && !rightRefName1.equals(rightRefName2)
            && leftRefName1.equals(leftRefName2)) {
          getBlackBoard()
              .addLog(
                  ErrorLevel.WARNING,
                  "Navigation over '" + leftRefName1 + "." + rightRole1 + "' is ambiguous.",
                  MergePhase.VALIDATION,
                  assoc1,
                  assoc2);
        } else if (leftRole1.equals(rightRole2)
            && !leftRefName1.equals(rightRefName2)
            && rightRefName1.equals(leftRefName2)) {
          getBlackBoard()
              .addLog(
                  ErrorLevel.WARNING,
                  "Navigation over '" + rightRefName1 + "." + leftRole1 + "' is ambiguous.",
                  MergePhase.VALIDATION,
                  assoc1,
                  assoc2);
        } else if (rightRole1.equals(leftRole2)
            && !rightRefName1.equals(leftRefName2)
            && leftRefName1.equals(rightRefName2)) {
          getBlackBoard()
              .addLog(
                  ErrorLevel.WARNING,
                  "Navigation over '" + leftRefName1 + "." + rightRole1 + "' is ambiguous.",
                  MergePhase.VALIDATION,
                  assoc1,
                  assoc2);
        }
      }
    }
  }

  @Override
  public void apply(ASTCDDefinition classDiagram) {
    checkUniqueAssociationNames(classDiagram);
    checkRolesConflictWithTypes(classDiagram);
    checkCompositionDesignIssues(classDiagram);
  }
}
