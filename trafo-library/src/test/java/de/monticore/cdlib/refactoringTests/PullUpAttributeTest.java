/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import static org.junit.Assert.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.refactorings.PullUp;
import de.monticore.cdlib.utilities.FileUtility;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class PullUp
 *
 * <p>Created by
 *
 * @author KE
 */
public class PullUpAttributeTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    CD4CodeMill.init();
    ReportManager.ReportManagerFactory factory =
        new ReportManager.ReportManagerFactory() {
          @Override
          public ReportManager provide(String modelName) {
            ReportManager reports = new ReportManager("target/generated-sources");
            TransformationReporter transformationReporter =
                new TransformationReporter(
                    "target/generated-sources",
                    modelName,
                    new ReportingRepository(new ASTNodeIdentHelper()));
            reports.addReportEventHandler(transformationReporter);
            return reports;
          }
        };

    Reporting.init("target/generated-sources", "target/reports", factory);
  }

  /** Test method pullUpAttributes */
  @Test
  public void testPullUpAttribute() throws IOException {

    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule1");
    PullUp refactoring = new PullUp();

    // Perform transformation
    assertTrue(refactoring.pullUpAttributes(utility.getAst()));

    // Check if attribute attribute1 was pulled up
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertTrue(
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDAttributeList()
            .get(0)
            .getModifier()
            .isPublic());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
  }

  /** Test method pullUpAttributes with boolean */
  @Test
  public void testPullUpAttributeBoolean() throws IOException {

    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule1Boolean");
    PullUp refactoring = new PullUp();

    // Perform transformation
    assertTrue(refactoring.pullUpAttributes(utility.getAst()));

    // Check if attribute attribute1 was pulled up
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertTrue(
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDAttributeList()
            .get(0)
            .getModifier()
            .isPublic());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
  }

  /** Test method pullUpAttributes with counterexample */
  @Test
  public void testPullUpAttributeCounterExample() throws IOException {

    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule1CounterExample");
    PullUp refactoring = new PullUp();

    // Perform transformation
    assertFalse(refactoring.pullUpAttributes(utility.getAst()));

    // Attributes should stay in their class
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
  }

  @Test
  public void testPullUpPrivateAttribute() throws IOException {

    FileUtility utility = new FileUtility("cdlib/AAttributePrivate");

    PullUp refactoring = new PullUp();

    ASTCDCompilationUnit oldAST = utility.getAst();

    // Perform transformation
    assertTrue(refactoring.pullUpAttributes(utility.getAst()));

    // Check if attribute attribute1 was pulled up
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());

    assertEquals(
        oldAST.getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0).getName(),
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        oldAST
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType(),
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertTrue(
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDAttributeList()
            .get(0)
            .getModifier()
            .isProtected());
  }

  @Test
  public void testPullUpAttributeCounterexample() throws IOException {

    FileUtility utility = new FileUtility("cdlib/Empty");

    PullUp refactoring = new PullUp();

    // Perform transformation
    assertFalse(refactoring.pullUpAttributes(utility.getAst()));
  }
}
