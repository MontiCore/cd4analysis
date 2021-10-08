/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.refactorings.EncapsulateAttributes;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class EncapsulateAttributes
 *
 * Created by
 *
 * @author KE
 */
public class EncapsulateAttributeTest {

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
	 * Test method encapsulateAttributes
	 */
	@Test
	public void testEncapsulateAttribute() throws IOException {

		FileUtility utility = new FileUtility("cdlib/AAttribute");
		EncapsulateAttributes refactoring = new EncapsulateAttributes();

		// Encapsulate attributes
		assertTrue(refactoring.encapsulateAttributes(utility.getAst()));

		// test if attributes are encapsulated
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0).getModifier()
				.isPrivate());
		assertEquals("getA", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)).getName());
		assertEquals("setA", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(1)).getName());
	}

	/**
	 * Test method encapsulateAttributes with two attributes
	 */
	@Test
	public void testEncapsulateAttribute2() throws IOException {

		FileUtility utility = new FileUtility("cdlib/AAttribute2");
		EncapsulateAttributes refactoring = new EncapsulateAttributes();

		// Encapsulate attributes
		assertTrue(refactoring.encapsulateAttributes(utility.getAst()));

		// test if attribute are encapsulated
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0).getModifier()
				.isPrivate());
		assertEquals("getA", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)).getName());
		assertEquals("setA", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(1)).getName());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(1).getModifier()
				.isPrivate());
		assertEquals("getB", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(2)).getName());
		assertEquals("setB", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(3)).getName());
	}

	/**
	 * Test method encapsulateAttributes with boolean
	 */
	@Test
	public void testEncapsulateBoolean() throws IOException {

		FileUtility utility = new FileUtility("cdlib/AttributeBooleanAndInt");
		EncapsulateAttributes refactoring = new EncapsulateAttributes();

		// Encapsulate attributes
		assertTrue(refactoring.encapsulateAttributes(utility.getAst()));

		// test if attributes are encapsulated
		assertEquals("getA", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)).getName());
		assertEquals("setA", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(1)).getName());

		assertEquals("isB", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(2)).getName());
		assertEquals("setB", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(3)).getName());

	}

	/**
	 * Test method encapsulateAttributes for a concrete attribute
	 */
	@Test
	public void testEncapsulateAttributesWithConcreteAttribute() throws IOException {

		FileUtility utility = new FileUtility("cdlib/AAttribute2");
		EncapsulateAttributes refactoring = new EncapsulateAttributes();

		// Encapsulate attributes
		assertTrue(refactoring.encapsulateAttributes(Lists.newArrayList("a"), utility.getAst()));

		// test if attributes are encapsulated
		assertEquals("a", utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0).getName());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0).getModifier()
				.isPrivate());
		assertEquals("getA", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)).getName());
		assertEquals("setA", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(1)).getName());

		// test if attribute b was not changed
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(1).getModifier()
				.isPublic());
	}

	/**
	 * Test method encapsulateAttributes counterexample
	 */
	@Test
	public void testEncapsulateAttributeCounterExample() throws IOException {

		FileUtility utility = new FileUtility("cdlib/APrivate");
		EncapsulateAttributes refactoring = new EncapsulateAttributes();
		ASTCDCompilationUnit oldAst = utility.getAst();
		System.out.println("Expect error: ");

		// Encapsulate attributes
		assertFalse(refactoring.encapsulateAttributes(Lists.newArrayList("a"), utility.getAst()));
		assertTrue(oldAst.equals(utility.getAst()));

		// Check, if ast with private attributes isn't changed
		assertTrue(refactoring.encapsulateAttributes(utility.getAst()));
		assertTrue(oldAst.equals(utility.getAst()));
	}

}
