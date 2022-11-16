/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.utilityTests;

import static org.junit.Assert.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdlib.utilities.FileUtility;
import de.monticore.cdlib.utilities.TransformationUtility;
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
 * Test class TransformationUtility
 *
 * <p>Created by
 *
 * @author KE
 */
public class FindTest {

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

  // Test method getAttribute
  @Test
  public void testFindAttribute() throws IOException {
    FileUtility utility = new FileUtility("cdlib/AAttribute");
    TransformationUtility refactoring = new TransformationUtility();

    // Check if classdiagram is as expected
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        "a",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDAttributeList()
            .get(0)
            .getName());

    // Find the attribute a in the classdiagram
    ASTCDAttribute a = refactoring.getAttribute("a", "A", utility.getAst());

    // Check if attribute a was found
    assertEquals("a", a.getName());
  }

  // Test method getMethod
  @Test
  public void testFindMethod() throws IOException {
    FileUtility utility = new FileUtility("cdlib/AWithMethod");
    TransformationUtility refactoring = new TransformationUtility();

    // Check if classdiagram is as expected
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        "a",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0))
            .getName());

    // Find the Method a in the classdiagram
    ASTCDMethod a = refactoring.getMethod("a", "A", utility.getAst());

    // Check if method a was found
    assertNotNull(a);
    assertEquals("a", a.getName());
    assertTrue(
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDMethodList()
            .get(0)
            .deepEquals(a));
  }
}
