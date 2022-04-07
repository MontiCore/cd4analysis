/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.refactorings.ExtractClass;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class ParameterObject
 *
 * Created by
 *
 * @author KE
 */
public class ExtractClassTest {

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
	 * Test method extractClass
	 */
	@Test
	public void testIntroduceParameterObject() throws IOException {

		FileUtility utility = new FileUtility("cdlib/IntroduceParameterObject");

		ExtractClass refactoring = new ExtractClass();

		// Check input ast for correctness
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().size());

		// perform transformation
		assertTrue(refactoring.extractClass("ClassC", "ClassA2", Lists.newArrayList("b"),
				Lists.newArrayList("getAttributeB"), utility.getAst()));

		// Check if ParameterObject was introduced
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("getAttributeB",
         ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0)).getName());

	}

	/**
	 * Test method extractClass counter example with missing method
	 * Expect error
	 */
	@Test
	public void testIntroduceParameterObjectCounterExampleMethod() throws IOException {

		FileUtility utility = new FileUtility("cdlib/IntroduceParameterObject");

		ExtractClass refactoring = new ExtractClass();

		// Check input ast for correctness
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().size());

		System.out.println("Expect errror: ");
		// perform transformation expected error because method was not found
		assertFalse(refactoring.extractClass("ClassC", "ClassA2", Lists.newArrayList("a"),
				Lists.newArrayList("getAttributeA"), utility.getAst()));

	}

	/**
	 * Test method extractClass counter example with missing
	 * attribute expect error
	 */
	@Test
	public void testIntroduceParameterObjectCounterExampleAttribute() throws IOException {

		FileUtility utility = new FileUtility("cdlib/IntroduceParameterObject");

		ExtractClass refactoring = new ExtractClass();

		// Check input ast for correctness
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().size());

		System.out.print("Expect error: ");
		// perform transformation expected error because attribute was not found
		assertFalse(refactoring.extractClass("ClassC", "ClassA2", Lists.newArrayList("c"),
				Lists.newArrayList(), utility.getAst()));

	}

	/**
	 * Test method extractClass counter example with missing class
	 * expect error
	 */
	@Test
	public void testIntroduceParameterObjectCounterExampleClass() throws IOException {

		FileUtility utility = new FileUtility("cdlib/Empty");

		ExtractClass refactoring = new ExtractClass();

		// perform transformation expected error because attribute was not found
		assertFalse(refactoring.extractClass("ClassC", "ClassA2", Lists.newArrayList(),
				Lists.newArrayList(), utility.getAst()));

	}

}
