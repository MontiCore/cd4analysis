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
import de.monticore.cdlib.refactorings.Rename;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class Rename Renaming classes
 *
 * Created by
 *
 * @author KE
 */
public class RenameClassTest {

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
	 * Test method renameClass with updating superclasses
	 */
	@Test
	public void testRenameClassWithSuperclass() throws IOException {
		Rename refactoring = new Rename();
		FileUtility utility = new FileUtility("cdlib/RenameClassTest");

		// Check input
		assertEquals("C", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("C", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());

		// Rename Class from C to F
		assertTrue(refactoring.renameClass("C", "F", utility.getAst()));

		// Check if Class was renamed
		assertEquals("F", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("F", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals("B", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());
	}

	/**
	 * Test method renameClass with updating association from right to left
	 */
	@Test
	public void testRenameClassWithAssociation2() throws IOException {
		FileUtility utility = new FileUtility("cdlib/ClassAndAssociationLeft");
		Rename refactoring = new Rename();

		// Check input
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("Old", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("Old",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
		assertEquals("A",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());

		// Rename Class from Old to NewClass
		assertTrue(refactoring.renameClass("Old", "NewClass", utility.getAst()));

		// Check if Class was renamed
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("NewClass", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("NewClass",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
		assertEquals("A",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
	}

	/**
	 * Test method renameClass with updating association from left to right
	 */
	@Test
	public void testRenameClassWithAssociation() throws IOException {
		FileUtility utility = new FileUtility("cdlib/ClassAndAssociationRight");
		Rename refactoring = new Rename();

		// Check input
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("Old", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("Old",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
		assertEquals("A",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());

		// Rename class Old to NewClass
		assertTrue(refactoring.renameClass("Old", "NewClass", utility.getAst()));

		// Rename class Old to NewClass
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("NewClass", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("NewClass",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
		assertEquals("A",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
	}

	/**
	 * Test method renameClass with updating bidirectional association
	 */
	@Test
	public void testRenameClassWithAssociationBothSides() throws IOException {
		FileUtility utility = new FileUtility("cdlib/ClassAndAssociationBothSides");
		Rename refactoring = new Rename();

		// Check input
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("Old", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("Old",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
		assertEquals("Old",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());

		// Rename class Old to NewClass
		assertTrue(refactoring.renameClass("Old", "NewClass", utility.getAst()));

		// Test if Classname ist changed
		assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("NewClass", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		// Test if Classname in Association is changed
		assertEquals("NewClass",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
		assertEquals("NewClass",
				utility.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
	}

	/**
	 * Test method renameClass with updating Type in attribute
	 */
	@Test
	public void testRenameClassInAttribute() throws IOException {
		FileUtility utility = new FileUtility("cdlib/RenameClass2");
		Rename refactoring = new Rename();

		// Rename class Old to NewClass
		assertTrue(refactoring.renameClass("A", "C", utility.getAst()));

		// Check if Type is changed
		assertEquals("C",
				utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
	}

}
