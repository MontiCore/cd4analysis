/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.utilityTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.utilities.FileUtility;
import de.monticore.cdlib.utilities.TransformationUtility;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test class TransformationUtility
 *
 * Created by
 *
 * @author KE
 */
public class ChangeInheritanceClass {

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

	// Test method changeInheritanceClass
	@Test
	public void changeNameInExtend() throws IOException {
		FileUtility utility = new FileUtility("cdlib/A2");
		TransformationUtility refactoring = new TransformationUtility();

		assertEquals("C", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(3).printSuperclasses());

		// Change the inheritance From C to F
		while (refactoring.changeInheritanceClass("C", "F", utility.getAst()));

		assertEquals("F", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(3).printSuperclasses());
	}

}
