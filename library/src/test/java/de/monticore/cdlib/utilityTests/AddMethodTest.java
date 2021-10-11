/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.utilityTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
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
public class AddMethodTest {

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

	// Test method addMethod
	@Test
	public void testAddMethod() throws IOException {
		FileUtility utility = new FileUtility("cdlib/AAttribute");
		FileUtility utility2 = new FileUtility("cdlib/AWithMethod");
		TransformationUtility refactoring = new TransformationUtility();
		TransformationUtility refactoring2 = new TransformationUtility();

		// Get Method from classdiagram "AWithMethod"
		ASTCDMethod a = refactoring2.getMethod("a", "A", utility2.getAst());

		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());

		// Add method to classdiagramm "AAttribute"
		assertTrue(refactoring.addMethod(a, "A", utility.getAst()));

		// Check if Method was added
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("a", utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0).getName());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0).deepEquals(a));

	}

}
