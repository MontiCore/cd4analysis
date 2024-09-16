/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import static org.junit.Assert.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdlib.refactorings.ExtractIntermediateClassArbitraryNumber;
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
 * Test class ExtractIntermediateClass
 *
 * <p>Created by
 *
 * @author Philipp Nolte
 */
public class ExtractIntermediateClassAttributeTest {

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

  /** Test method testExtractSuperclassesAutoNameAttribute */
  @Test
  public void testExtractSuperclassesAutoNameAttribute() throws IOException {
    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule23Classes");
    ExtractIntermediateClassArbitraryNumber refactoring =
        new ExtractIntermediateClassArbitraryNumber();
    //		System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(utility.getAst()));


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
    assertTrue(refactoring.extractAllIntermediateClassesAttribute(utility.getAst()));
    //		System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(utility.getAst()));

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
        "ClassA1ClassA2ClassA3",
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

  /** Test method testExtractSuperclassesManualNameAttribute */
  @Test
  public void testExtractSuperclassesManualNameAttribute() throws IOException {
    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule23Classes");
    ExtractIntermediateClassArbitraryNumber refactoring =
        new ExtractIntermediateClassArbitraryNumber();
    //		System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(utility.getAst()));


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
        refactoring.extractAllIntermediateClassesAttribute(utility.getAst(), "NewClassName"));
    //		System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(utility.getAst()));

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
        "NewClassName", utility.getAst().getCDDefinition().getCDClassesList().get(6).getName());
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

  /** Test method testExtractSuperclassesAutoNameMethod */
  @Test
  public void testExtractSuperclassesAutoNameMethod() throws IOException {
    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule23ClassesMethods");
    ExtractIntermediateClassArbitraryNumber refactoring =
        new ExtractIntermediateClassArbitraryNumber();
    //		System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(utility.getAst()));

    // Check if input ast is correct
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(
        "attribute1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(1)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    assertEquals(
        "attribute1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(2)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals("ClassA3", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().size());
    assertEquals(
        "attribute1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(3)
                    .getCDMethodList()
                    .get(0))
            .getName());

    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDMethodList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDMethodList().size());

    // Perform transformation
    assertTrue(refactoring.extractAllIntermediateClassesMethod(utility.getAst()));
    //		System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(utility.getAst()));

    // Check if output ast is correct corresponding to the transformation
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    assertEquals("ClassA3", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().size());

    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDMethodList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDMethodList().size());
    assertEquals(
        "ClassA1ClassA2ClassA3",
        utility.getAst().getCDDefinition().getCDClassesList().get(6).getName());
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(6).getCDMethodList().size());
    assertEquals(
        "attribute1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(6)
                    .getCDMethodList()
                    .get(0))
            .getName());
  }

  /** Test method testExtractSuperclassesManualNameMethod */
  @Test
  public void testExtractSuperclassesManualNameMethod() throws IOException {
    FileUtility utility = new FileUtility("cdlib/EvaluationCDs/EvaluationRule23ClassesMethods");
    ExtractIntermediateClassArbitraryNumber refactoring =
        new ExtractIntermediateClassArbitraryNumber();
    //		System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(utility.getAst()));

    // Check if input ast is correct
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(
        "attribute1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(1)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    assertEquals(
        "attribute1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(2)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals("ClassA3", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().size());
    assertEquals(
        "attribute1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(3)
                    .getCDMethodList()
                    .get(0))
            .getName());

    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDMethodList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDMethodList().size());

    // Perform transformation
    assertTrue(refactoring.extractAllIntermediateClassesMethod(utility.getAst(), "NewClassName"));
    //		System.out.println(new CDPrettyPrinterConcreteVisitor(new
    // IndentPrinter()).prettyprint(utility.getAst()));

    // Check if output ast is correct corresponding to the transformation
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    assertEquals("ClassA3", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().size());

    assertEquals("ClassB1", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDMethodList().size());
    assertEquals("ClassB2", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
    assertEquals(
        0, utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDMethodList().size());
    assertEquals(
        "NewClassName", utility.getAst().getCDDefinition().getCDClassesList().get(6).getName());
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(6).getCDMethodList().size());
    assertEquals(
        "attribute1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(6)
                    .getCDMethodList()
                    .get(0))
            .getName());
  }
}
