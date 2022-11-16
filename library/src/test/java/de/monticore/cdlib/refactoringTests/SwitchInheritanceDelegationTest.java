/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import static org.junit.Assert.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdlib.refactorings.SwitchInheritanceDelegation;
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
 * Test class SwitchInheritanceDelegation
 *
 * <p>Created by
 *
 * @author KE
 */
public class SwitchInheritanceDelegationTest {

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

  /** Test method replaceInheritanceByDelegation */
  @Test
  public void testReplaceInheritanceByDelegation() throws IOException {
    SwitchInheritanceDelegation switchBetween = new SwitchInheritanceDelegation();
    FileUtility utility = new FileUtility("cdlib/AInheritance");

    // Check input
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "A", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());

    // Replace Inheritance between A and B by an association from B to A
    assertTrue(switchBetween.replaceInheritanceByDelegation("A", "B", utility.getAst()));

    // Check if inheritance was deleted and association was added
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertTrue(
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty());
    assertEquals(
        "B",
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

  /** Test method replaceInheritanceByDelegation with counter example */
  @Test
  public void testReplaceInheritanceByDelegationCounter() throws IOException {
    SwitchInheritanceDelegation switchBetween = new SwitchInheritanceDelegation();
    FileUtility utility = new FileUtility("cdlib/A");

    // Check input
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());

    // Should not be introduced without inheritance
    assertFalse(switchBetween.replaceInheritanceByDelegation("A", "B", utility.getAst()));
  }

  /** Test method replaceDelegationByInheritance */
  @Test
  public void testReplaceDelegationByInheritance() throws IOException {
    SwitchInheritanceDelegation switchBetween = new SwitchInheritanceDelegation();
    FileUtility utility = new FileUtility("cdlib/AAssociationRight");

    // Check input
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertFalse(
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty()
            ^ true);
    assertEquals(
        "B",
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

    // Replace association from B to A by inheritance between A and B
    assertTrue(switchBetween.replaceDelegationByInheritance("A", "B", utility.getAst()));

    // Check if assoication was deleted and A was added to superclass of B
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "A", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());
    assertEquals(0, utility.getAst().getCDDefinition().getCDAssociationsList().size());
  }

  /** Test method replaceDelegationByInheritance with counterexample */
  @Test
  public void testReplaceDelegationByInheritanceCounter() throws IOException {
    SwitchInheritanceDelegation switchBetween = new SwitchInheritanceDelegation();
    FileUtility utility = new FileUtility("cdlib/A");

    // Check input
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());

    // Should not be introduced without inheritance
    assertFalse(switchBetween.replaceInheritanceByDelegation("A", "B", utility.getAst()));
  }
}
