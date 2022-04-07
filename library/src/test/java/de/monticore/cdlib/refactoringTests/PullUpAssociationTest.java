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
import de.monticore.cdlib.refactorings.PullUp;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class PullUp
 *
 * Created by
 *
 * @author KE
 */
public class PullUpAssociationTest {

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
	 * Test method pullUpAssociations
	 */
	@Test
	public void testPullUpAssociation() throws IOException {

		FileUtility utility = new FileUtility("cdlib/PullUpAssociation");
		PullUp refactoring = new PullUp();

		// Get right reference name
		String rightName = utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName()
				.getQName();

		// Perform transformation
		assertTrue(refactoring.pullUpAssociations(utility.getAst()));

		// Check if association is now associated to Class C
		assertEquals(1, utility.getAst().getCDDefinition().getCDAssociationsList().size());
		assertEquals("C",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
		assertEquals(rightName, utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
	}

	/**
	 * Test method pullUpAssociations with counter example
	 */
	@Test
	public void testPullUpAssociationCounterExample() throws IOException {

		FileUtility utility = new FileUtility("cdlib/PullUpAssociationCounter");
		PullUp refactoring = new PullUp();

		// Should be false because there is no association to move
		assertFalse(refactoring.pullUpAssociations(utility.getAst()));
	}

}
