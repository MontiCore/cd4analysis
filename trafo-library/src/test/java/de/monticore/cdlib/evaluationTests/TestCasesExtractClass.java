/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.evaluationTests;

import static org.junit.Assert.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdlib.Refactoring.*;
import de.monticore.cdlib.refactorings.ExtractSuperClass;
import de.monticore.cdlib.refactorings.Move;
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
 * Test Testcases for ExtractClass
 *
 * <p>Created by
 *
 * @author KE
 */
public class TestCasesExtractClass {

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
  public void testCase1() throws IOException {

    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/TestCase1");
    ExtractSuperClass refactoring = new ExtractSuperClass();

    // Check if input Classdiagram is correct
    assertEquals("ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());

    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "attributeA",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());

    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        2,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "attributeA",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "attributeB",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(1)
            .getName());
    assertEquals(
        "ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());

    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals(
        "attributeB",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(3)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(3).printSuperclasses());

    assertEquals("ClassD", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());
    assertEquals(
        "attributeB",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(4)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(4).printSuperclasses());

    // Perform transformation
    assertFalse(refactoring.extractSuperClass(utility.getAst()));
  }

  @Test
  public void testCase2() throws IOException {
    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/TestCase2");
    ExtractSuperClass refactoring = new ExtractSuperClass();
    PullUp pullup = new PullUp();
    Move moveAttribute = new Move();

    // Perform transformation
    assertTrue(refactoring.extractSuperClass(utility.getAst()));
    assertTrue(pullup.pullUpAttributes(utility.getAst()));
    moveAttribute.moveMethodsAndAttributes("A", "AD", utility.getAst());
    DeleteAttribute deleteAttribute = new DeleteAttribute(utility.getAst());
    deleteAttribute.set_$className("D");
    deleteAttribute.doAll();
    assertTrue(refactoring.extractSuperClass(utility.getAst()));

    // Check if extractSuperClass was performed and superclass was introduced
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());

    assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "A", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());

    assertEquals("C", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "A", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());

    assertEquals("D", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals(
        "AD", utility.getAst().getCDDefinition().getCDClassesList().get(3).printSuperclasses());

    assertEquals("E", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());
    assertEquals(
        "D", utility.getAst().getCDDefinition().getCDClassesList().get(4).printSuperclasses());

    assertEquals("F", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().size());
    assertEquals(
        "D", utility.getAst().getCDDefinition().getCDClassesList().get(5).printSuperclasses());

    assertEquals("G", utility.getAst().getCDDefinition().getCDClassesList().get(6).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(6).getCDAttributeList().size());
    assertEquals(
        "GAD", utility.getAst().getCDDefinition().getCDClassesList().get(6).printSuperclasses());

    assertEquals("AD", utility.getAst().getCDDefinition().getCDClassesList().get(7).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(7).getCDAttributeList().size());
    assertEquals(
        "a",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(7)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "GAD", utility.getAst().getCDDefinition().getCDClassesList().get(7).printSuperclasses());

    assertEquals("GAD", utility.getAst().getCDDefinition().getCDClassesList().get(8).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(8).getCDAttributeList().size());
    assertEquals(
        "b",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(8)
            .getCDAttributeList()
            .get(0)
            .getName());
  }
}
