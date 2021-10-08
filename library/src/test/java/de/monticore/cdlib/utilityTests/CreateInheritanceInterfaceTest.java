/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.utilityTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;
import de.monticore.cdlib.utilities.TransformationUtility;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class TransformationUtility
 *
 * Created by
 *
 * @author KE
 */

public class CreateInheritanceInterfaceTest {

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

	// Test method createInheritanceToInterface
	@Test
	public void testCreateInheritanceInterface() throws IOException {
		FileUtility utility = new FileUtility("cdlib/AInterface");
		TransformationUtility refactoring = new TransformationUtility();

		// Check if classdiagram is as expected
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());

		// Change Inheritance
		assertTrue(refactoring.createInheritanceToInterface("A", "B", utility.getAst()));

		// Check if inheritance was changed
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(0).printInterfaces());
	}

	// Test method addInheritanceToInterface
	@Test
	public void testCreateInheritance2Interface() throws IOException {
		FileUtility utility = new FileUtility("cdlib/A2Interface");
		TransformationUtility refactoring = new TransformationUtility();

		// Check if classdiagram is as expected
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());
		assertEquals("C", utility.getAst().getCDDefinition().getCDInterfacesList().get(1).getName());

		// Change Inheritance
		assertTrue(refactoring.addInheritanceToInterface("A", "C", utility.getAst()));

		// Check if inheritance was changed
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());
		assertEquals("C", utility.getAst().getCDDefinition().getCDInterfacesList().get(1).getName());
		assertEquals("B,C", utility.getAst().getCDDefinition().getCDClassesList().get(0).printInterfaces());

	}

}
