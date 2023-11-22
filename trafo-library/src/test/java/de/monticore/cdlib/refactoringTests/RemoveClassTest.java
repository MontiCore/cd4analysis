/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdlib.refactorings.Remove;
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
 * Test class Remove Removing class
 *
 * <p>Created by
 *
 * @author KE
 */
public class RemoveClassTest {

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

  /** Test method removeClass */
  @Test
  public void testRemove() throws IOException {
    FileUtility utility = new FileUtility("cdlib/ClassAndAssociationLeft");
    Remove refactoring = new Remove();

    // Check input
    assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("Old", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

    // Remove Class Old
    assertTrue(refactoring.removeClass("Old", utility.getAst()));

    // Check if Class Old was removed
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
  }

  // /**
  // * Test method removeClass
  // */
  // @Test
  // public void testRemoveWithInheritance() throws IOException {
  // FileUtility utility = new FileUtility("cdlib/RemoveClass");
  // Remove refactoring = new Remove();
  //
  //
  // //Remove Class Old
  // assertFalse(refactoring.removeClass("A",utility.getAst()));
  //
  // //Check if Class Old was removed
  // assertEquals(1,
  // utility.getAst().getCDDefinition().getCDClassesList().size());
  // assertEquals("B",
  // utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
  // assertEquals(1,
  // utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
  // }

}
