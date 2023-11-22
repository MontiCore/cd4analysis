/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.merging.strategies;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.log.ErrorLevel;
import de.monticore.cdmerge.matching.matchresult.CDMatch;
import de.monticore.cdmerge.matching.matchresult.MatchNode;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import de.monticore.cdmerge.util.CDMergeUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Merges the association lists of two class diagrams by uniting them and/or considering
 * precedences; similar associations are merged into one association in the result
 */
public class DefaultAssociationMerger extends AssociationMerger {

  public DefaultAssociationMerger(
      MergeBlackBoard mergeBlackBoard, AssociationMergeStrategy mergeStrategy) {
    super(mergeBlackBoard, mergeStrategy);
  }

  // Performs the merger of all Associations of the provided input CDs
  public void mergeAssociations(ASTCDDefinition cd1, ASTCDDefinition cd2, CDMatch matchResult) {

    Iterator<List<MatchNode<ASTCDAssociation, ASTCDDefinition>>> matchIterator =
        matchResult.getMatchedAssociations().getMatchNodeIterator();

    // We want to have all Associations in the merged CD. Some of them will
    // be merged and removed from this list during the merging process.
    List<ASTCDAssociation> notMergedAssociationsCd1 = new ArrayList<>();
    List<ASTCDAssociation> notMergedAssociationsCd2 = new ArrayList<>();
    notMergedAssociationsCd1.addAll(cd1.getCDAssociationsList());
    notMergedAssociationsCd2.addAll(cd2.getCDAssociationsList());
    Optional<String> cdPackageName = Optional.empty();

    int ambiguousMatches;
    while (matchIterator.hasNext()) {
      for (MatchNode<ASTCDAssociation, ASTCDDefinition> association1 : matchIterator.next()) {
        // We assume that the packages match and hence we take the package declaration
        // of first element
        cdPackageName = association1.getPackage();
        ambiguousMatches = 0;
        // First Check if we have a unique association match
        List<MatchNode<ASTCDAssociation, ASTCDDefinition>> matchingAssocsInCD2 =
            association1.getMatchedNodes(cd2);
        // Check if we find a unique match in the ambiguous matches
        if (matchingAssocsInCD2.size() > 1) {
          List<MatchNode<ASTCDAssociation, ASTCDDefinition>> matches = new ArrayList<>();
          for (MatchNode<ASTCDAssociation, ASTCDDefinition> association2 : matchingAssocsInCD2) {
            /*
             * Matching Phase already checked both rolenames and both association names but
             * still have two matches. This case can only happen, if one CD contains one
             * association with same name and one association with identical both roles or
             * one of the associations have no names/roles
             */
            if (getBlackBoard().getConfig().mergeOnlyNamedAssociations()) {
              ambiguousMatches++;
              logWarning(
                  "Ambiguous association  match, cd '"
                      + cd1.getName()
                      + "' and cd '"
                      + cd2.getName()
                      + "' "
                      + ambiguousMatches
                      + "/"
                      + matchingAssocsInCD2.size()
                      + " :",
                  association1.getElement(),
                  association2.getElement());
            } else {
              // We check if find a unique match by matching one
              // role or the associationname
              if (association1.getElement().isPresentName()
                  && association2.getElement().isPresentName()
                  && association1
                      .getElement()
                      .getName()
                      .equalsIgnoreCase(association2.getElement().getName())) {
                matches.add(association2);
              } else {
                Optional<ASTCDAssociation> alignedAssocation2 =
                    CDMergeUtils.tryAlignAssociation(
                        association1.getElement(), association2.getElement());
                if (alignedAssocation2.isPresent()) {
                  if (association1.getElement().getLeft().isPresentCDRole()
                      && alignedAssocation2.get().getLeft().isPresentCDRole()) {
                    if (association1
                        .getElement()
                        .getLeft()
                        .getCDRole()
                        .getName()
                        .equalsIgnoreCase(
                            alignedAssocation2.get().getLeft().getCDRole().getName())) {
                      if (!association1.getElement().getRight().isPresentCDRole()
                          || !alignedAssocation2.get().getRight().isPresentCDRole()
                          || association1
                              .getElement()
                              .getRight()
                              .getCDRole()
                              .getName()
                              .equalsIgnoreCase(
                                  alignedAssocation2.get().getRight().getCDRole().getName())) {
                        matches.add(association2);
                      }
                    }
                  } else if (association1.getElement().getRight().isPresentCDRole()
                      && alignedAssocation2.get().getRight().isPresentCDRole()) {
                    if (association1
                        .getElement()
                        .getRight()
                        .getCDRole()
                        .getName()
                        .equalsIgnoreCase(
                            alignedAssocation2.get().getRight().getCDRole().getName())) {
                      if (association1
                          .getElement()
                          .getRight()
                          .getCDRole()
                          .getName()
                          .equalsIgnoreCase(
                              alignedAssocation2.get().getRight().getCDRole().getName())) {
                        matches.add(association2);
                      }
                    }
                  }
                }
              }
            }
          }
          if (matches.size() == 1) {
            matchingAssocsInCD2 = matches;
          } else {
            ambiguousMatches = 0;
            for (MatchNode<ASTCDAssociation, ASTCDDefinition> assoc : matchingAssocsInCD2) {
              ambiguousMatches++;
              logWarning(
                  "Ambiguous association match, cd '"
                      + cd1.getName()
                      + "' and cd '"
                      + cd2.getName()
                      + "' "
                      + ambiguousMatches
                      + "/"
                      + matchingAssocsInCD2.size()
                      + " :",
                  association1.getElement(),
                  assoc.getElement());
            }
          }
        }
        if (matchingAssocsInCD2.size() == 1) {

          // Check precedences first
          if (getConfig()
              .getPrecedences()
              .hasPrecedence(
                  association1.getElement(), matchingAssocsInCD2.get(0).getElement(), cd1, cd2)) {
            log(
                ErrorLevel.INFO,
                "Association has precedence and will not be merged with matching association",
                association1.getElement(),
                matchingAssocsInCD2.get(0).getElement());
            getBlackBoard()
                .addMergedAssociation(Optional.of(association1.getElement()), cdPackageName);

          } else if (getConfig()
              .getPrecedences()
              .hasPrecedence(
                  matchingAssocsInCD2.get(0).getElement(), association1.getElement(), cd2, cd1)) {
            log(
                ErrorLevel.INFO,
                "Association has precedence and will not be merged with matching association",
                matchingAssocsInCD2.get(0).getElement(),
                association1.getElement());
            getBlackBoard()
                .addMergedAssociation(
                    Optional.of(matchingAssocsInCD2.get(0).getElement()), cdPackageName);
          } else {
            // Check precedence Types - we are not allowed to add an
            // association PType -> X as it modifies the PType
            if (getConfig()
                .getPrecedences()
                .hasConflictWithPrecedenceType(
                    association1.getElement(), matchingAssocsInCD2.get(0).getElement(), cd1)) {
              log(
                  ErrorLevel.INFO,
                  "Merging Association would cause a modification to precedenced reference type. "
                      + "Association  will not be merged",
                  association1.getElement(),
                  matchingAssocsInCD2.get(0).getElement());
              getBlackBoard()
                  .addMergedAssociation(Optional.of(association1.getElement()), cdPackageName);
              getBlackBoard()
                  .addMergedAssociation(
                      Optional.of(matchingAssocsInCD2.get(0).getElement()), cdPackageName);
            } else if (getConfig()
                .getPrecedences()
                .hasConflictWithPrecedenceType(
                    matchingAssocsInCD2.get(0).getElement(), association1.getElement(), cd2)) {
              log(
                  ErrorLevel.INFO,
                  "Merging Association would cause a modification to precedenced reference type. Association  will not be merged",
                  association1.getElement(),
                  matchingAssocsInCD2.get(0).getElement());

            } else {
              // Merge and add To Result
              getBlackBoard()
                  .addMergedAssociation(
                      this.mergeStrategy.mergeAssociation(
                          association1.getElement(), matchingAssocsInCD2.get(0).getElement()),
                      cdPackageName);
              // Don't keep the source associations
              notMergedAssociationsCd1.remove(association1.getElement());
              notMergedAssociationsCd2.remove(matchingAssocsInCD2.get(0).getElement());
            }
          }
        }
      }
    }
    // Add all remaining, not merged associations to the merged CD
    notMergedAssociationsCd1.forEach(assoc -> getBlackBoard().addCDElementFromCD1(assoc));
    notMergedAssociationsCd2.forEach(assoc -> getBlackBoard().addCDElementFromCD2(assoc));
  }
}
