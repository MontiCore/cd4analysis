/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.config.CDMergeConfig;
import de.monticore.cdmerge.config.MergeParameter;
import de.monticore.cdmerge.exceptions.MergingException;
import de.monticore.cdmerge.matching.DefaultCDMatcher;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import de.monticore.cdmerge.matching.matchresult.MatchNode;
import de.monticore.cdmerge.merging.mergeresult.MergeBlackBoard;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class DefaulCDMatcherTest extends BaseTest {

  public static final String INPUT_MODEL_A = "General/university/Staff.cd";

  public static final String INPUT_MODEL_B = "General/university/Teaching.cd";

  private DefaultCDMatcher testant;

  private TypeMatcher typeMatcher;

  private AttributeMatcher attributeMatcher;

  private AssociationMatcher associationMatcher;

  private List<ASTCDDefinition> inputCds;

  @Before
  public void initTest() throws IOException {
    CDMergeConfig.Builder b = new CDMergeConfig.Builder(false);
    b.withParam(MergeParameter.MODEL_PATH, MODEL_PATH)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_A)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_B)
        .withParam(MergeParameter.OUTPUT_PATH, "C:\\TEMP")
        .withParam(MergeParameter.MERGE_HETEROGENOUS_TYPES);

    MergeBlackBoard blackBoard = new MergeBlackBoard(b.build());
    ASTCDCompilationUnit cd1 = loadModel(MODEL_PATH + "/" + INPUT_MODEL_A);
    ASTCDCompilationUnit cd2 = loadModel(MODEL_PATH + "/" + INPUT_MODEL_B);
    blackBoard.initOrReset(cd1, cd2, Optional.of("TESTRESULT"));
    this.inputCds = new ArrayList<ASTCDDefinition>();
    this.inputCds.add(blackBoard.getCurrentInputCd1().getCDDefinition());
    this.inputCds.add(blackBoard.getCurrentInputCd2().getCDDefinition());
    this.typeMatcher = new DefaultTypeMatcher(blackBoard);
    this.attributeMatcher = new DefaultAttributeMatcher(blackBoard);
    this.associationMatcher = new DefaultAssociationMatcher(blackBoard);
    this.testant =
        new DefaultCDMatcher(blackBoard, typeMatcher, attributeMatcher, associationMatcher);
  }

  @Test
  public void testFindTypes() {
    ASTMatchGraph<ASTCDType, ASTCDDefinition> result = this.testant.findMatchingTypes();
    List<MatchNode<ASTCDType, ASTCDDefinition>> nodes;
    nodes = result.findNodes(t -> t.getElement().getName().equals("Person"));
    for (MatchNode<ASTCDType, ASTCDDefinition> node : nodes) {
      assertTrue(node.getMatchedElements().size() == 1);
    }
    nodes = result.findNodes(t -> t.getElement().getName().equals("Room"));
    for (MatchNode<ASTCDType, ASTCDDefinition> node : nodes) {
      assertTrue(node.getMatchedElements().size() == 1);
    }
    nodes = result.findNodes(t -> t.getElement().getName().equals("Student"));
    for (MatchNode<ASTCDType, ASTCDDefinition> node : nodes) {
      assertTrue(node.getMatchedElements().size() == 0);
    }
    nodes = result.findNodes(t -> t.getElement().getName().equals("CourseOfStudy"));
    for (MatchNode<ASTCDType, ASTCDDefinition> node : nodes) {
      assertTrue(node.getMatchedElements().size() == 0);
    }
  }

  @Test
  public void testFindAttributes() {
    ASTMatchGraph<ASTCDAttribute, ASTCDClass> result =
        this.testant.findMatchingAttributes("Employee");
    List<MatchNode<ASTCDAttribute, ASTCDClass>> nodes;
    nodes = result.findNodes(t -> t.getElement().getName().equals("emplNumber"));
    for (MatchNode<ASTCDAttribute, ASTCDClass> node : nodes) {
      assertTrue(node.getMatchedElements().size() == 1);
      assertTrue(node.getParent().getName().equals("Employee"));
    }
  }

  @Test
  public void testFindAssociations() throws MergingException {
    ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> result =
        this.testant.findMatchingAssociations();
    List<MatchNode<ASTCDAssociation, ASTCDDefinition>> nodes;

    nodes =
        result.findNodes(t -> t.getElement().getRightQualifiedName().getBaseName().equals("Room"));
    assertEquals("Find asscociations with right reference 'Room'", 5, nodes.size());

    nodes =
        result.findNodes(t -> t.getElement().getLeftQualifiedName().getBaseName().equals("Room"));
    assertEquals("Find asscociations with left reference 'Room'", 1, nodes.size());
    assertEquals(
        "Find asscociations with left reference 'Room'",
        1,
        nodes.get(0).getMatchedElements().size());

    nodes =
        result.findNodes(t -> t.getElement().getLeftQualifiedName().toString().equals("Person"));
    assertEquals("Find asscociations with left reference 'Person'", 2, nodes.size());

    // FIXME Fails in Maven
    // assertEquals("Find asscociations with left reference 'Person'", 1,
    // nodes.get(0).getMatchedElements().size());

    nodes =
        result.findNodes(
            t -> t.getElement().getRightQualifiedName().getBaseName().equals("Person"));
    assertEquals("Find asscociations with right reference 'Person'", 1, nodes.size());
    assertEquals(
        "Find asscociations with right reference 'Person'",
        1,
        nodes.get(0).getMatchedElements().size());

    nodes =
        result.findNodes(
            t -> t.getElement().getLeftQualifiedName().getBaseName().equals("Department"));
    assertEquals("Find asscociations with left reference 'Department'", 2, nodes.size());
    assertEquals(
        "Find asscociations with left reference 'Department'",
        0,
        nodes.get(0).getMatchedElements().size());
  }
}
