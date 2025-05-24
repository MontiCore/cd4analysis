/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaulCDMatcherTest extends BaseTest {

  public static final String INPUT_MODEL_A = "General/university/Staff.cd";

  public static final String INPUT_MODEL_B = "General/university/Teaching.cd";

  private DefaultCDMatcher testant;

  private TypeMatcher typeMatcher;

  private AttributeMatcher attributeMatcher;

  private AssociationMatcher associationMatcher;

  private List<ASTCDDefinition> inputCds;

  @BeforeEach
  public void initTest() throws IOException {
    CDMergeConfig.Builder b = new CDMergeConfig.Builder(false);
    b.withParam(MergeParameter.MODEL_PATH, MODEL_PATH)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_A)
        .withParam(MergeParameter.INPUT_MODELS, INPUT_MODEL_B)
        .withParam(MergeParameter.OUTPUT_PATH, "C:\\TEMP")
        .withParam(MergeParameter.MERGE_HETEROGENEOUS_TYPES);

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
      assertEquals(1, node.getMatchedElements().size());
    }
    nodes = result.findNodes(t -> t.getElement().getName().equals("Room"));
    for (MatchNode<ASTCDType, ASTCDDefinition> node : nodes) {
      assertEquals(1, node.getMatchedElements().size());
    }
    nodes = result.findNodes(t -> t.getElement().getName().equals("Student"));
    for (MatchNode<ASTCDType, ASTCDDefinition> node : nodes) {
      assertEquals(0, node.getMatchedElements().size());
    }
    nodes = result.findNodes(t -> t.getElement().getName().equals("CourseOfStudy"));
    for (MatchNode<ASTCDType, ASTCDDefinition> node : nodes) {
      assertEquals(0, node.getMatchedElements().size());
    }
  }

  @Test
  public void testFindAttributes() {
    ASTMatchGraph<ASTCDAttribute, ASTCDClass> result =
        this.testant.findMatchingAttributes("Employee");
    List<MatchNode<ASTCDAttribute, ASTCDClass>> nodes;
    nodes = result.findNodes(t -> t.getElement().getName().equals("emplNumber"));
    for (MatchNode<ASTCDAttribute, ASTCDClass> node : nodes) {
      assertEquals(1, node.getMatchedElements().size());
      assertEquals("Employee", node.getParent().getName());
    }
  }

  @Test
  public void testFindAssociations() throws MergingException {
    ASTMatchGraph<ASTCDAssociation, ASTCDDefinition> result =
        this.testant.findMatchingAssociations();
    List<MatchNode<ASTCDAssociation, ASTCDDefinition>> nodes;

    nodes =
        result.findNodes(t -> t.getElement().getRightQualifiedName().getBaseName().equals("Room"));
    assertEquals(5, nodes.size(), "Find asscociations with right reference 'Room'");

    nodes =
        result.findNodes(t -> t.getElement().getLeftQualifiedName().getBaseName().equals("Room"));
    assertEquals(1, nodes.size(), "Find asscociations with left reference 'Room'");
    assertEquals(
        1,
        nodes.get(0).getMatchedElements().size(),
        "Find asscociations with left reference 'Room'");

    nodes =
        result.findNodes(t -> t.getElement().getLeftQualifiedName().toString().equals("Person"));
    assertEquals(2, nodes.size(), "Find asscociations with left reference 'Person'");

    // FIXME Fails in Maven
    // assertEquals("Find asscociations with left reference 'Person'", 1,
    // nodes.get(0).getMatchedElements().size());

    nodes =
        result.findNodes(
            t -> t.getElement().getRightQualifiedName().getBaseName().equals("Person"));
    assertEquals(1, nodes.size(), "Find asscociations with right reference 'Person'");
    assertEquals(
        1,
        nodes.get(0).getMatchedElements().size(),
        "Find asscociations with right reference 'Person'");

    nodes =
        result.findNodes(
            t -> t.getElement().getLeftQualifiedName().getBaseName().equals("Department"));
    assertEquals(2, nodes.size(), "Find asscociations with left reference 'Department'");
    assertEquals(
        0,
        nodes.get(0).getMatchedElements().size(),
        "Find asscociations with left reference 'Department'");
  }
}
