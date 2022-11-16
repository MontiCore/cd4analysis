/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cdmerge.matching.strategies;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableList;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.matching.matchresult.Match;
import de.monticore.cdmerge.matching.matchresult.MatchNode;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

/** A Unit Test for the default association matcher */
public class DefaultAssociationMatcherTest extends BaseTest {

  public static final String INPUT_MODEL_PATH_A =
      "src/cdmergetest/resources/class_diagrams" + "/General/university/Staff.cd";

  public static final String INPUT_MODEL_PATH_B =
      "src/cdmergetest/resources/class_diagrams" + "/General/university/Teaching.cd";

  public static final String INPUT_MODEL_A = "General/university/Staff.cd";

  public static final String INPUT_MODEL_B = "General/university/Teaching.cd";

  private AssociationMatcher testant;

  public DefaultAssociationMatcherTest() {}

  private void checkAssociationMatch(
      ASTCDAssociation association1, ASTCDAssociation association2, boolean named) {
    // The reference names should match
    String lr1 = association1.getLeftReferenceName().toString();
    String rr1 = association1.getRightReferenceName().toString();
    String lr2 = association2.getLeftReferenceName().toString();
    String rr2 = association2.getRightReferenceName().toString();
    assertTrue(lr1.equals(lr2) || lr1.equals(rr2));
    assertTrue(rr1.equals(lr2) || rr1.equals(rr2));

    // If named associations where searched the match found should contain named
    // associations
    if (named) {
      assertTrue(association1.isPresentName());
      assertTrue(association2.isPresentName());
      assertTrue(association1.getName().equals(association2.getName()));
    }
  }

  private void checkRoleMatch(
      ASTCDAssociation association1, ASTCDAssociation association2, final int numRolesToMatch) {
    if (association1.getLeft().isPresentCDRole()) {
      // LeftRole1 != LeftRole2
      if (association2.getLeft().isPresentCDRole()) {
        assertTrue(
            association1
                .getLeft()
                .getCDRole()
                .getName()
                .equalsIgnoreCase(association2.getLeft().getCDRole().getName()));
      }
    }
    if (association1.getRight().isPresentCDRole()) {
      // RightRole1 != RightRole2
      if (association2.getRight().isPresentCDRole()) {
        assertTrue(
            association1
                .getRight()
                .getCDRole()
                .getName()
                .equalsIgnoreCase(association2.getRight().getCDRole().getName()));
      }
    }
  }

  private void checkMatches(
      ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> matchingAssociations, boolean named) {
    // Retrieve parents
    ImmutableList<ASTCDDefinition> parents = matchingAssociations.getParents();

    // Check result
    for (ASTCDDefinition astcdDefinition : parents) {
      List<MatchNode<ASTCDAssociation, ASTCDDefinition>> associations =
          matchingAssociations.getAllNodesForParent(astcdDefinition);
      for (MatchNode<ASTCDAssociation, ASTCDDefinition> matchNode : associations) {

        // Assert that all matched nodes are part of the class diagram
        assertTrue(astcdDefinition.getCDAssociationsList().contains(matchNode.getElement()));

        // Check if the found matched elements are matches
        for (Match<ASTCDAssociation, ASTCDDefinition> match : matchNode.getMatches()) {

          // The associations should lie in different class diagrams
          assertFalse(match.getNode1().getParent().equals(match.getNode2().getParent()));

          // Check if the associations match
          ASTCDAssociation association1 = match.getNode1().getElement();
          ASTCDAssociation association2 = match.getNode2().getElement();
          checkAssociationMatch(association1, association2, named);
        }
      }
      // Check if the size is set correctly
      assertTrue(associations.size() == astcdDefinition.getCDAssociationsList().size());
    }
  }

  @Test
  public void findMatchingAssociationsTest() throws IOException {
    CDMergeConfig.Builder b = new CDMergeConfig.Builder(false);
    b.withParam(MergeParameter.MODEL_PATH, MODEL_PATH)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_A)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_B)
        .withParam(MergeParameter.OUTPUT_PATH, "C:\\TEMP")
        .withParam(MergeParameter.MERGE_HETEROGENOUS_TYPES);
    MergeBlackBoard blackBoard = new MergeBlackBoard(b.build());
    // FIXME USCHOEN!
    blackBoard.initOrReset(
        blackBoard.getConfig().getInputCDs().get(0),
        blackBoard.getConfig().getInputCDs().get(1),
        Optional.empty());
    this.testant = new DefaultAssociationMatcher(blackBoard);
    // Match associations without regarding the naming
    ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> matchingAssociations =
        testant.findMatchingAssociations();
    // Check found matches
    checkMatches(matchingAssociations, false);

    // Check matches with only named associations
    b = new CDMergeConfig.Builder(false);
    b.withParam(MergeParameter.MODEL_PATH, MODEL_PATH)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_A)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_B)
        .withParam(MergeParameter.OUTPUT_PATH, "C:\\TEMP")
        .withParam(MergeParameter.MERGE_HETEROGENOUS_TYPES)
        .withParam(MergeParameter.MERGE_ONLY_NAMED_ASSOCIATIONS);
    blackBoard = new MergeBlackBoard(b.build());
    blackBoard.initOrReset(
        blackBoard.getConfig().getInputCDs().get(0),
        blackBoard.getConfig().getInputCDs().get(1),
        Optional.empty());
    this.testant = new DefaultAssociationMatcher(blackBoard);
    matchingAssociations = testant.findMatchingAssociations();
    // Check found matches
    checkMatches(matchingAssociations, true);
    // check if all matches are named

  }

  @Test
  public void matchTest() throws IOException {
    CDMergeConfig.Builder b = new CDMergeConfig.Builder(false);
    b.withParam(MergeParameter.MODEL_PATH, MODEL_PATH)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_A)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_B)
        .withParam(MergeParameter.OUTPUT_PATH, "C:\\TEMP")
        .withParam(MergeParameter.MERGE_HETEROGENOUS_TYPES);
    MergeBlackBoard blackBoard = new MergeBlackBoard(b.build());
    blackBoard.initOrReset(
        blackBoard.getConfig().getInputCDs().get(0),
        blackBoard.getConfig().getInputCDs().get(1),
        Optional.empty());
    this.testant = new DefaultAssociationMatcher(blackBoard);
    // Parse input for association matching
    ASTCDCompilationUnit cd1 = loadModel(INPUT_MODEL_PATH_A);
    ASTCDCompilationUnit cd2 = loadModel(INPUT_MODEL_PATH_B);

    List<ASTCDAssociation> associations1 = cd1.getCDDefinition().getCDAssociationsList();
    List<ASTCDAssociation> associations2 = cd2.getCDDefinition().getCDAssociationsList();

    // Find all without regarding named associations and check them
    int matchesFound = 0;
    for (ASTCDAssociation association1 : associations1) {
      for (ASTCDAssociation association2 : associations2) {
        if (testant.match(association1, association2)) {
          checkAssociationMatch(association1, association2, false);
          matchesFound++;
        }
      }
    }

    // Check if the correct number of matches was found
    assertEquals(1, matchesFound);
  }

  @Test
  public void rolesMatchTest() throws IOException {
    CDMergeConfig.Builder b = new CDMergeConfig.Builder(false);
    b.withParam(MergeParameter.MODEL_PATH, MODEL_PATH)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_A)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_B)
        .withParam(MergeParameter.OUTPUT_PATH, "C:\\TEMP")
        .withParam(MergeParameter.MERGE_HETEROGENOUS_TYPES);
    MergeBlackBoard blackBoard = new MergeBlackBoard(b.build());
    this.testant = new DefaultAssociationMatcher(blackBoard);
    blackBoard.initOrReset(
        blackBoard.getConfig().getInputCDs().get(0),
        blackBoard.getConfig().getInputCDs().get(1),
        Optional.empty());
    // Parse input for association matching
    ASTCDCompilationUnit cd1 =
        loadModel("src/cdmergetest/resources/class_diagrams/Association/rolesWithAssocName/A.cd");
    ASTCDCompilationUnit cd2 =
        loadModel("src/cdmergetest/resources/class_diagrams/Association/rolesWithAssocName/B.cd");
    List<ASTCDAssociation> associations1 = cd1.getCDDefinition().getCDAssociationsList();
    List<ASTCDAssociation> associations2 = cd2.getCDDefinition().getCDAssociationsList();

    int matchesFound = 0;
    for (ASTCDAssociation association1 : associations1) {
      for (ASTCDAssociation association2 : associations2) {
        if (testant.rolesMatch(association1, association2, 0)) {
          checkRoleMatch(association1, association2, 0);
          matchesFound++;
        }
      }
    }
    assertEquals(1, matchesFound);
  }
}
