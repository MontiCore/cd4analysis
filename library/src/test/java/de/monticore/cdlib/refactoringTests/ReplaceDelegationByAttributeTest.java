/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.refactorings.ReplaceDelegationByAttribute;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class ReplaceDelegationByAttribute
 *
 * Created by
 *
 * @author KE
 */
public class ReplaceDelegationByAttributeTest {

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
	 * Test method replaceAssociationByAttribute
	 */
	@Test
	public void testReplaceDelegationByAttribute() throws IOException {

		ReplaceDelegationByAttribute replace = new ReplaceDelegationByAttribute();
		FileUtility utility = new FileUtility("cdlib/AAssociationRight");

		// Check input
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertFalse(utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty() ^ true);
		assertEquals("B",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
		assertEquals("A",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());

		// Replace association from B to A by inheritance between A and B
		assertTrue(replace.replaceAssociationByAttribute("B", "A", utility.getAst()));

		// Check if assoication was deleted and A was added to superclass of B
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("a", utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).getName());
		assertEquals("A",
				utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
		assertEquals(0, utility.getAst().getCDDefinition().getCDAssociationsList().size());

	}

	/**
	 * Test method replaceAssociationByAttribute with counterexample
	 */
	@Test
	public void testReplaceDelegationByAttributeCounter() throws IOException {
		ReplaceDelegationByAttribute switchBetween = new ReplaceDelegationByAttribute();
		FileUtility utility = new FileUtility("cdlib/A");

		// Check input
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());

		// Should not be introduced without inheritance
		assertFalse(switchBetween.replaceAssociationByAttribute("A", "B", utility.getAst()));

	}
}
