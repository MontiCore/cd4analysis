/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdlib.refactorings.ExtractIntermediateClass;
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
 * Test class ExtractSuperclass
 *
 * <p>Created by
 *
 * @author KE
 */
public class ExtractIntermediateClassTest {

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

  /** Test method extractAllSuperclasses */
  @Test
  public void testExtractSuperclasses3ClassesAutoName() throws IOException {
    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule23Classes");
    ExtractIntermediateClass refactoring = new ExtractIntermediateClass();


    // Check if input ast is correct
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassA3", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(3)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(3)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());

    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().size());

    // Perform transformation
    assertTrue(refactoring.extractAllIntermediateClasses(utility.getAst()));

    // Check if output ast is correct corresponding to the transformation
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals("ClassA3", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());

    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().size());
    assertEquals(
        "ClassA1ClassA3ClassA2",
        utility.getAst().getCDDefinition().getCDClassesList().get(6).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(6).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(6)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(6)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
  }

  /** Test method extractAllSuperclasses with auto generated name */
  @Test
  public void testExtractSuperclass2ClassesAutoName() throws IOException {

    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule2");
    ExtractIntermediateClass refactoring = new ExtractIntermediateClass();


    // Check if input ast is correct
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());

    // Perform transformation
    assertTrue(refactoring.extractAllIntermediateClasses(utility.getAst()));
    ;

    // Check if output ast is correct corresponding to the transformation
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());

    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "ClassA2ClassA1",
        utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());

    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "ClassA2ClassA1",
        utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());

    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals(
        "ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(3).printSuperclasses());

    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());
    assertEquals(
        "ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(4).printSuperclasses());

    assertEquals(
        "ClassA2ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(5)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(5)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals(
        "ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(5).printSuperclasses());
  }

  /** Test method extractSuperclass with manual name */
  @Test
  public void testExtractSuperclasses3ClassesManualName() throws IOException {
    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule23Classes");
    ExtractIntermediateClass refactoring = new ExtractIntermediateClass();


    // Check if input ast is correct
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassA3", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(3)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(3)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());

    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().size());

    // Perform transformation
    assertTrue(
        refactoring.extractIntermediateClass(
            "SuperA", Lists.newArrayList("ClassA1", "ClassA2", "ClassA3"), utility.getAst()));

    // Check if output ast is correct corresponding to the transformation
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());

    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "SuperA", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());

    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "SuperA", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());

    assertEquals("ClassA3", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals(
        "SuperA", utility.getAst().getCDDefinition().getCDClassesList().get(3).printSuperclasses());

    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());
    assertEquals(
        "ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(4).printSuperclasses());

    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().size());
    assertEquals(
        "ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(5).printSuperclasses());

    assertEquals("SuperA", utility.getAst().getCDDefinition().getCDClassesList().get(6).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(6).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(6)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(6)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals(
        "ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(6).printSuperclasses());
  }

  /** Test method extractSuperclass with manual naming */
  @Test
  public void testExtractSuperclass2ClassesManualName() throws IOException {

    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule2");
    ExtractIntermediateClass refactoring = new ExtractIntermediateClass();


    // Check if input ast is correct
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());

    // Perform transformation
    assertTrue(
        refactoring.extractIntermediateClass(
            "SuperA", Lists.newArrayList("ClassA1", "ClassA2"), utility.getAst()));

    // Check if output ast is correct corresponding to the transformation
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());

    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "SuperA", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());

    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "SuperA", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());

    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals(
        "ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(3).printSuperclasses());

    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());
    assertEquals(
        "ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(4).printSuperclasses());

    assertEquals("SuperA", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(5)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(5)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals(
        "ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(5).printSuperclasses());
  }

  /** Test method extractSuperclass */
  @Test
  public void testExtractSuperclass2ClassesManualName2() throws IOException {

    FileUtility utility =
        new FileUtility("cdlib/EvaluationCDs/EvaluationRule2CounterexampleExtractSuperclass");
    ExtractIntermediateClass refactoring = new ExtractIntermediateClass();


    // Check if input ast is correct
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());

    assertEquals("ClassC1", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().size());
    assertEquals("ClassC2", utility.getAst().getCDDefinition().getCDClassesList().get(6).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(6).getCDAttributeList().size());

    // Perform transformation
    assertTrue(
        refactoring.extractIntermediateClass(
            "CSuper", Lists.newArrayList("ClassC1", "ClassC2"), utility.getAst()));

    // Check if output ast is correct corresponding to the transformation
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals(
        "attribute1",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(2)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());

    assertEquals("ClassC1", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        "CSuper", utility.getAst().getCDDefinition().getCDClassesList().get(5).printSuperclasses());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().size());

    assertEquals("ClassC2", utility.getAst().getCDDefinition().getCDClassesList().get(6).getName());
    assertEquals(
        "CSuper", utility.getAst().getCDDefinition().getCDClassesList().get(6).printSuperclasses());
    assertEquals(
        0,
        utility.getAst().getCDDefinition().getCDClassesList().get(6).getCDAttributeList().size());

    assertEquals("CSuper", utility.getAst().getCDDefinition().getCDClassesList().get(7).getName());
    assertEquals(
        1,
        utility.getAst().getCDDefinition().getCDClassesList().get(7).getCDAttributeList().size());
    assertEquals(
        "attribute2",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(7)
            .getCDAttributeList()
            .get(0)
            .getName());
    assertEquals(
        "String",
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(7)
            .getCDAttributeList()
            .get(0)
            .getMCType()
            .printType());
    assertEquals(
        "ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(7).printSuperclasses());
  }

  /** Test method extractSuperclass */
  @Test
  public void testExtractSuperclass2ClassesManualNameCounterExample() throws IOException {

    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule1");
    ExtractIntermediateClass refactoring = new ExtractIntermediateClass();

    // Perform transformation
    assertFalse(
        refactoring.extractIntermediateClass(
            "CSuper", Lists.newArrayList("ClassC1", "ClassC2"), utility.getAst()));
  }
}
