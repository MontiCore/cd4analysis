/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPatternTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import de.monticore.cdlib.designPattern.DecoratorPattern;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class DecoratorPattern
 *
 * Created by
 *
 * @author KE
 */
public class DecoratorTest {

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

		Reporting.init("target/generated-sources", "/target/reports", factory);
		Reporting.on("DecoratorTest");
	}

	/**
	 * Test method introduceDecoratorPattern
	 */
	@Test
	public void testDesignPatternDecorator() throws IOException {

		FileUtility utility = new FileUtility("cdlib/AWithMethod");

		// introduce Decorator Pattern
		DecoratorPattern decorator = new DecoratorPattern();
		assertTrue(decorator.introduceDecoratorPattern("A", "AComponent", "a", utility.getAst()));

		// Test if Decorator Pattern was introduced
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("AComponent", utility.getAst().getCDDefinition().getCDClassesList().get(0).printSuperclasses());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("a", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)).getName());

		assertEquals("ADecorator", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("AComponent", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("a", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0)).getName());

		assertEquals("AComponent", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals("", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
		assertEquals("a", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(0)).getName());

		assertEquals(1, utility.getAst().getCDDefinition().getCDAssociationsList().size());
		assertEquals("ADecorator",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
		assertEquals("AComponent",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
		assertTrue(utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getCDAssocDir().isDefinitiveNavigableRight());
    assertFalse(utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getCDAssocDir().isDefinitiveNavigableLeft());
	}

	/**
	 * Test method introduceDecoratorPattern counterexample for missing class
	 */
	@Test
	public void testDesignPatternDecoratorCounterExample() throws IOException {

		FileUtility utility = new FileUtility("cdlib/Empty");

		// introduce Decorator Pattern
		DecoratorPattern decorator = new DecoratorPattern();
		assertFalse(decorator.introduceDecoratorPattern("A", "AComponent", "a", utility.getAst()));
	}

}
