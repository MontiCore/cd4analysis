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

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Test class FileUtility
 *
 * Created by
 *
 * @author KE
 */

public class FileUtilityTest {

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

	// Test constructors

	@Test
	public void testConstructor1() throws IOException {

		// Create Utility object
		String inputFile = "cdlib/A";
		FileUtility utility = new FileUtility(inputFile);

		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getName().equals("A"));
	}

	// Test writing an ast of a classdiagram to a file
	@Test
	public void TestWriteFile() throws IOException {

		// Create Utility object
		String inputFile = "cdlib/A";
		String outputFile = "UtilityB";
		String inputFolder = "src/main/models/";
		String outputFolder = "target/generated-models/TestUtility/";
		FileUtility utility = new FileUtility(inputFile, inputFolder);

		// Check if file doesn't exists
		File file = new File(outputFolder + outputFile + ".cd");
		if (file.exists()) {
			System.out.println("File for Test UtilityTest TestWriteFile() does already exists.");
		}

		// Write File
		utility.writeAst(outputFile, outputFolder);

		// Check if File was created
		assertTrue(file.exists());
	}
}
