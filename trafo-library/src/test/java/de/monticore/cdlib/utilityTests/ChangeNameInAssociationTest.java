/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.utilityTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
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
public class ChangeNameInAssociationTest {

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

  // Test method changeRefNameInAllAssociations for an association with direction from right to left
  @Test
  public void testRenameClassWithAssociationLeft() throws IOException {
    FileUtility utility = new FileUtility("cdlib/ClassAndAssociationLeft");
    TransformationUtility refactoring = new TransformationUtility();

    // Check if classdiagram as expected
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("Old", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "Old",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getLeftQualifiedName()
            .getQName());
    assertEquals(
        "A",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getRightQualifiedName()
            .getQName());

    // Change the referenceName of Old to A
    assertTrue(refactoring.changeRefNameInAllAssociations("Old", "A", utility.getAst()));

    // Check if Name was updated
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("Old", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "A",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getLeftQualifiedName()
            .getQName());
    assertEquals(
        "A",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getRightQualifiedName()
            .getQName());
  }

  // Test method changeRefNameInAllAssociations for a bi-directional association
  @Test
  public void testRenameClassWithAssociationBoth() throws IOException {
    FileUtility utility = new FileUtility("cdlib/ClassAndAssociationBothSides");
    TransformationUtility refactoring = new TransformationUtility();

    // Check if classdiagram as expected
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("Old", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "Old",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getLeftQualifiedName()
            .getQName());
    assertEquals(
        "Old",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getRightQualifiedName()
            .getQName());

    // Change the referenceName of Old to A
    assertTrue(refactoring.changeRefNameInAllAssociations("Old", "A", utility.getAst()));

    // Check if Name was updated
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("Old", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "A",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getLeftQualifiedName()
            .getQName());
    assertEquals(
        "A",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getRightQualifiedName()
            .getQName());
  }

  // Test method changeRefNameInAllAssociations for association with direction from left to right
  @Test
  public void testRenameClassWithAssociationRight() throws IOException {
    FileUtility utility = new FileUtility("cdlib/ClassAndAssociationRight");
    TransformationUtility refactoring = new TransformationUtility();

    // Check if classdiagram as expected
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("Old", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "Old",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getRightQualifiedName()
            .getQName());
    assertEquals(
        "A",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getLeftQualifiedName()
            .getQName());

    // Change the referenceName of Old to A
    assertTrue(refactoring.changeRefNameInAllAssociations("Old", "A", utility.getAst()));

    // Check if Name was updated
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("Old", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "A",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getRightQualifiedName()
            .getQName());
    assertEquals(
        "A",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getLeftQualifiedName()
            .getQName());
  }
}
