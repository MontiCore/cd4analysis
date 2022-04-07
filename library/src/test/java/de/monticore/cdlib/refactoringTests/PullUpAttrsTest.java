/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdlib.refactoring.pullup.attribute.tf.PullUpAttributes;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class PullUp
 *
 * Created by
 *
 * @author hoelldobler
 */
public class PullUpAttrsTest {

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
	 * Test method pullUpAttributes
	 */
	@Test
	public void testPullUpAttrs() throws IOException {

		ASTCDCompilationUnit ast = CD4CodeMill.parser().parse(
				"src/main/models/cdlib/PullUpAttrs.cd").get();
		PullUpAttributes refactoring = new PullUpAttributes(ast);

		// Perform transformation
		assertTrue(refactoring.doPatternMatching());

		refactoring.doReplacement();
//		CDPrettyPrinterConcreteVisitor p = new CDPrettyPrinterConcreteVisitor(new IndentPrinter());
//		System.out.println(p.prettyprint(ast));

		refactoring = new PullUpAttributes(ast);

		assertTrue(refactoring.doPatternMatching());

		refactoring.doReplacement();
//		System.out.println(p.prettyprint(ast));



		// Check if attribute attribute1 was pulled up
	}


}
