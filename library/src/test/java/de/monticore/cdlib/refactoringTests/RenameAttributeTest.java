/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.refactorings.Rename;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class Rename
 *
 * Created by
 *
 * @author KE
 */
public class RenameAttributeTest {

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
	 * Test method renameAttribute
	 */
	@Test
	public void testRenameAttribute() throws IOException {

		FileUtility utility = new FileUtility("cdlib/AAttribute");

		Rename refactoring = new Rename();

		// Rename attribute from a to c
		assertTrue(refactoring.renameAttribute("a", "c", utility.getAst()));

		// Check if attribute was renamed
		assertEquals("c", utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0).getName());
	}

	/**
	 * Test method renameAttribute with getter and setter
	 */
	@Test
	public void testRenameAttributeWithGetterAndSetter() throws IOException {

		FileUtility utility = new FileUtility("cdlib/AAttributeGetterSetter");

		Rename refactoring = new Rename();

		// Rename attribute from a to c
		assertTrue(refactoring.renameAttribute("a", "c", utility.getAst()));

		// Check if attribute was renamed
		assertEquals("c", utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0).getName());
		assertEquals("getC", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)).getName());
		assertEquals("setC", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(1)).getName());
	}

}
