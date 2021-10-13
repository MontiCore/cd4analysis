/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.evaluationTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.refactorings.ExtractIntermediateClass;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test ExtractSuperclass for Testcases
 *
 * Created by
 *
 * @author KE
 */
public class TestCasesExtractSuperclass {

	@BeforeClass
	public static void disableFailQuick() {
		Log.enableFailQuick(false);
    CD4CodeMill.init();
    ReportManager.ReportManagerFactory factory = new ReportManager.ReportManagerFactory() {
			@Override public ReportManager provide(String modelName) {
				ReportManager reports = new ReportManager("target/generated-sources");
				TransformationReporter transformationReporter = new TransformationReporter(
						"target/generated-sources", modelName, new ReportingRepository(new ASTNodeIdentHelper()));
				reports.addReportEventHandler(transformationReporter);
				return reports;
			}
		};

		Reporting.init("target/generated-sources", "target/reports", factory);
	}

	/**
	 * Test method extractAllSuperclasses
	 */
	@Test
	public void testCase1() throws IOException {

		FileUtility utility = new FileUtility("cdlib/EvaluationCDs/TestCase1");
		ExtractIntermediateClass refactoring = new ExtractIntermediateClass();

		// Check input
		assertEquals("ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());

		assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals("attributeA",
				utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).getName());
		assertEquals("ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());

		assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
		assertEquals("attributeA",
				utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(0).getName());
		assertEquals("attributeB",
				utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(1).getName());
		assertEquals("ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());

		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
		assertEquals("attributeB",
				utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().get(0).getName());
		assertEquals("ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(3).printSuperclasses());

		assertEquals("ClassD", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());
		assertEquals("attributeB",
				utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().get(0).getName());
		assertEquals("ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(4).printSuperclasses());

		// Perform transformation
		assertTrue(refactoring.extractAllIntermediateClasses(utility.getAst()));

		// Check if Subclass was extracted
		assertEquals("ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());

		assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals("attributeA",
				utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).getName());
		assertEquals("ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());

		assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
		assertEquals("attributeA",
				utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(0).getName());
		assertEquals("ClassBClassDClassC", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());

		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().size());
		assertEquals("ClassBClassDClassC", utility.getAst().getCDDefinition().getCDClassesList().get(3).printSuperclasses());

		assertEquals("ClassD", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDAttributeList().size());
		assertEquals("ClassBClassDClassC", utility.getAst().getCDDefinition().getCDClassesList().get(4).printSuperclasses());

		assertEquals("ClassBClassDClassC", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().size());
		assertEquals("attributeB",
				utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDAttributeList().get(0).getName());
		assertEquals("ClassS", utility.getAst().getCDDefinition().getCDClassesList().get(5).printSuperclasses());

	}

	/**
	 * Test method extractAllSuperclasses
	 */
	@Test
	public void testCase2() throws IOException {
		FileUtility utility = new FileUtility("cdlib/EvaluationCDs/TestCase2");
		ExtractIntermediateClass refactoring = new ExtractIntermediateClass();

		// it should no superclass be extracted
		assertFalse(refactoring.extractAllIntermediateClasses(utility.getAst()));
	}

}
