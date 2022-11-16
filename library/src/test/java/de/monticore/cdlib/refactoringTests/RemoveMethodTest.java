/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import static org.junit.Assert.assertEquals;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdlib.refactorings.Remove;
import de.monticore.cdlib.utilities.FileUtility;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test method renaming classes
 *
 * @author jiong
 */
public class RemoveMethodTest {

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

  @Test
  public void testRemoveMethod() {
    FileUtility utility = new FileUtility("cdlib/RemoveMethodTest");
    Remove refactoring = new Remove();

    // Check input, namely there should be two overloading occurrences of
    // getUserName methods
    ASTCDClass classA = utility.getAst().getCDDefinition().getCDClassesList().get(0);
    assertEquals("A", classA.getName());
    assertEquals(3, classA.getCDMethodList().size());

    ASTCDMethod method1 = (ASTCDMethod) classA.getCDMethodList().get(0);
    assertEquals("getUserName", method1.getName());
    assertEquals(0, method1.getCDParameterList().size());

    ASTCDMethod method2 = (ASTCDMethod) classA.getCDMethodList().get(1);
    assertEquals("getUserName", method2.getName());
    assertEquals(1, method2.getCDParameterList().size());

    assertEquals("setUserName", ((ASTCDMethod) classA.getCDMethodList().get(2)).getName());

    // Remove methods
    refactoring.removeMethod("A", "getUserName", utility.getAst());

    // Check output, namely only the first overloading occurrence of getUserName
    // method should be removed
    assertEquals(2, classA.getCDMethodList().size());

    method1 = (ASTCDMethod) classA.getCDMethodList().get(0);
    assertEquals("getUserName", method1.getName());
    assertEquals(1, method1.getCDParameterList().size());

    assertEquals("setUserName", ((ASTCDMethod) classA.getCDMethodList().get(1)).getName());
  }
}
