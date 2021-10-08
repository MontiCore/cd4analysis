/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.refactorings.InlineClass;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class InlineClass
 *
 * Created by
 *
 * @author KE
 */
public class InlineClassTest {

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
	 * Test method inlineClass
	 */
	@Test
	public void testInlineClass() throws IOException {

		FileUtility utility = new FileUtility("cdlib/Inline");

		InlineClass refactoring = new InlineClass();

		// Check ast befor transformation
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDAssociationsList().size());

		// perform transformation
		assertTrue(refactoring.inlineClass("ClassC", "ClassA2", utility.getAst()));

		// Check if ClassA2 holds ClassC attributes and methods
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDAssociationsList().size());

	}

	/**
	 * Test method inlineClass with one to one association
	 */
	@Test
	public void testInlineClassOneToOneAssociation() throws IOException {

		FileUtility utility = new FileUtility("cdlib/Inline2");

		InlineClass refactoring = new InlineClass();

		// Check ast befor transformation
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDAssociationsList().size());

		// perform transformation
		assertTrue(refactoring.inlineClass("ClassC", "ClassA2", utility.getAst()));

		// Check if ClassA2 holds ClassC attributes and methods
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDAssociationsList().size());

	}

	/**
	 * Test method inlineClass with changing name in association
	 */
	@Test
	public void testInlineClassWithAddAssociation() throws IOException {

		FileUtility utility = new FileUtility("cdlib/Inline3");

		InlineClass refactoring = new InlineClass();

		// Check ast befor transformation
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(2, utility.getAst().getCDDefinition().getCDAssociationsList().size());

		// perform transformation
		assertTrue(refactoring.inlineClass("ClassC", "ClassA2", utility.getAst()));

		// Check if ClassA2 holds ClassC attributes and methods
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDAssociationsList().size());
		assertEquals("ClassA2",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
		assertEquals("B",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
	}

	/**
	 * Test method inlineClass with counter example expect error
	 */
	@Test
	public void testInlineClassCounter() throws IOException {

		FileUtility utility = new FileUtility("cdlib/InlineCounterExample");

		InlineClass refactoring = new InlineClass();
		// Check ast befor transformation
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

		System.out.print("Expect error: One-to-One-Association between ClassA2 and ClassCis assumed. : ");
		// should not be matched
		assertFalse(refactoring.inlineClass("ClassC", "ClassA2", utility.getAst()));

	}

}
