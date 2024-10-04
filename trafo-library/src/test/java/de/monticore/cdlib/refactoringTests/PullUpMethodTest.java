/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import static org.junit.Assert.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
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
public class PullUpMethodTest {

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

  /** Test method pullUpMethods */
  // Test the method pullUpMethod in the Refactoring class
  @Test
  public void testPullUpMethod() throws IOException {

    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule1Methods");
    PullUp refactoring = new PullUp();

    // Perform transformation
    assertTrue(refactoring.pullUpMethods(utility.getAst()));

    // Check if method is added
    assertEquals(
        "getAttribute",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertTrue(
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0))
            .getModifier()
            .isPublic());
    assertEquals(
        "String",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0))
            .getMCReturnType()
            .printType());
    assertEquals(
        0,
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0))
            .getCDParameterList()
            .size());

    // Check if methods are deleted
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
  }

  /** Test method pullUpMethods with counterexample */
  // Test the method pullUpMethod in the Refactoring class
  @Test
  public void testPullUpMethodCounterExample() throws IOException {

    FileUtility utility =
        new FileUtility("cdlib/EvaluationCDs/EvaluationRule1MethodCounterExample");
    PullUp refactoring = new PullUp();

    // Perform transformation
    assertFalse(refactoring.pullUpMethods(utility.getAst()));

    // Check if methods are not deleted
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(
        "getAttribute",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(1)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertTrue(
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(1)
                    .getCDMethodList()
                    .get(0))
            .getModifier()
            .isPublic());
    assertEquals(
        "String",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(1)
                    .getCDMethodList()
                    .get(0))
            .getMCReturnType()
            .printType());
    assertEquals(
        0,
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(1)
                    .getCDMethodList()
                    .get(0))
            .getCDParameterList()
            .size());

    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    assertEquals(
        "getAttribute",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(2)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertTrue(
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(2)
                    .getCDMethodList()
                    .get(0))
            .getModifier()
            .isPublic());
    assertEquals(
        "String",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(2)
                    .getCDMethodList()
                    .get(0))
            .getMCReturnType()
            .printType());
    assertEquals(
        0,
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(2)
                    .getCDMethodList()
                    .get(0))
            .getCDParameterList()
            .size());

    // Check if method is not added
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().size());
  }

  @Test
  public void testPullUpPrivateMethod() throws IOException {

    FileUtility utility = new FileUtility("cdlib/AWithMethodPrivate");

    PullUp refactoring = new PullUp();

    ASTCDCompilationUnit oldAST = utility.getAst();

    // Perform transformation
    assertTrue(refactoring.pullUpMethods(utility.getAst()));

    // Check if attribute attribute1 was pulled up
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());

    assertEquals(
        ((ASTCDMethod) oldAST.getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0))
            .getName(),
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals(
        ((ASTCDMethod) oldAST.getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0))
            .getMCReturnType()
            .printType(),
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0))
            .getMCReturnType()
            .printType());
    assertTrue(
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0))
            .getModifier()
            .isProtected());
  }
}
