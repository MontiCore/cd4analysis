/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.refactorings.ExtractInterface;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class ExtractInterface
 *
 * Created by
 *
 * @author KE
 */
public class ExtractInterfaceTest {

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
	 * Test method ExtractInterface
	 */
	@Test
	public void testExtractInterface() throws IOException {
		FileUtility utility = new FileUtility("cdlib/AB");
		ExtractInterface refactoring = new ExtractInterface();

		// Check if classdiagram is as expected befor introducing interface
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("D", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());

		// Introduce interface
		assertTrue(refactoring.extractInterface("C", Lists.newArrayList("A", "B"), utility.getAst()));

		// Check if interface was introduced and added to As interfaces
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("D", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());
		assertEquals("C", utility.getAst().getCDDefinition().getCDInterfacesList().get(1).getName());
		assertEquals("D,C", utility.getAst().getCDDefinition().getCDClassesList().get(0).printInterfaces());
		assertEquals("C", utility.getAst().getCDDefinition().getCDClassesList().get(1).printInterfaces());
	}

	/**
	 * Test method ExtractInterface
	 */
	@Test
	public void testExtractInterface2() throws IOException {

		FileUtility utility = new FileUtility("cdlib/E1");

		ExtractInterface refactoring = new ExtractInterface();

		// Check if classdiagram is as expected befor introducing interface
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

		// Introduce interface
		assertTrue(refactoring.extractInterface("interfaceName", Lists.newArrayList("ClassC", "ClassA2"),
				utility.getAst()));

		// Check if interface was introduced
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("interfaceName", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());
	}

	/**
	 * Test method ExtractInterface counterexample for missing class
	 */
	@Test
	public void testExtractInterfaceCounterexample() throws IOException {

		FileUtility utility = new FileUtility("cdlib/Empty");

		ExtractInterface refactoring = new ExtractInterface();

		// Introduce interface
		assertFalse(refactoring.extractInterface("interfaceName", Lists.newArrayList("ClassC", "ClassA2"),
				utility.getAst()));
	}

}
