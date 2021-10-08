/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.refactorings.Move;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class move
 *
 * Created by
 *
 * @author KE
 */
public class MoveTest {

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

	// Move methods and attributes

	/**
	 * Test method moveMethodsAndAttributes
	 */
	@Test
	public void testMoveMethodsAndAttributes() throws IOException {

		FileUtility utility = new FileUtility("cdlib/E1");
		Move refactoring = new Move();

		// Check input
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

		// Move methods and attributes from ClassC to ClassA2
		assertTrue(refactoring.moveMethodsAndAttributes("ClassC", "ClassA2", utility.getAst()));

		// Check if methods and attributes are moved
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());

	}

	// Move attributes

	/**
	 * Test method moveAllAttributes
	 */
	@Test
	public void testMoveAttributes() throws IOException {

		FileUtility utility = new FileUtility("cdlib/E1");
		Move refactoring = new Move();

		// Check input
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

		// Move attributes from ClassC to ClassA2
		assertTrue(refactoring.moveAllAttributes("ClassC", "ClassA2", utility.getAst()));

		// Check if attributes are moved
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());

	}

	/**
	 * Test method moveAttributes for a concrete attribute
	 */
	@Test
	public void testMoveConcreteAttribute() throws IOException {

		FileUtility utility = new FileUtility("cdlib/E2");

		Move refactoring = new Move();

		// Check, if ast is correct
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

		// Concrete attribute b should be moved from ClassC to ClassA2
		assertTrue(refactoring.moveAttributes("ClassC", "ClassA2", Lists.newArrayList("b"), utility.getAst()));

		// Check if, concrete attribute b is moved from ClassC to ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals("b", utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());

	}

	/**
	 * Test method moveAttributes with counter example
	 */
	@Test
	public void testMoveConcreteAttributeCounterExample() throws IOException {

		FileUtility utility = new FileUtility("cdlib/E1");

		Move refactoring = new Move();

		// Check, if ast is correct
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

		// Attribute should not be found and method should return false
		assertFalse(refactoring.moveAttributes("ClassC", "ClassA2", Lists.newArrayList(""), utility.getAst()));

	}

	// Move methods

	/**
	 * Test method moveAllMethods
	 */
	@Test
	public void testMoveMethods() throws IOException {

		FileUtility utility = new FileUtility("cdlib/E1");

		Move refactoring = new Move();

		// Check, if ast is correct
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

		// Move attributes from ClassC to ClassA2
		assertTrue(refactoring.moveAllMethods("ClassC", "ClassA2", utility.getAst()));

		// Check if methods are moved from ClassC to ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
	}

	/**
	 * Test method moveMethods for concrete method
	 */
	@Test
	public void testMoveConcreteMethod() throws IOException {

		FileUtility utility = new FileUtility("cdlib/E2");

		Move refactoring = new Move();

		// Check, if ast is correct
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

		// Concrete method getAttributeB should be moved from ClassC to ClassA2
		assertTrue(refactoring.moveMethods("ClassC", "ClassA2", Lists.newArrayList("getAttributeB"), utility.getAst()));

		// Check, if method getAttributeB is moved from ClassC to ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("getAttributeB",
       ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0)).getName());

	}

	/**
	 * Test method moveMethods with counter example
	 */
	@Test
	public void testMoveConcreteMethodCounterExample() throws IOException {

		FileUtility utility = new FileUtility("cdlib/E1");

		Move refactoring = new Move();

		// Check, if ast is correct
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

		// Method should not be found and method should return false
		assertFalse(refactoring.moveMethods("ClassC", "ClassA2", Lists.newArrayList(""), utility.getAst()));

	}

	// Move to neighbor class

	/**
	 * Test method moveMethodsAndAttributesToNeighborClass
	 */
	@Test
	public void testMoveMethodsAndAttributesToNeighborClass() throws IOException {

		FileUtility utility = new FileUtility("cdlib/MoveToNeighbor");

		Move refactoring = new Move();

		// Check, if ast is correct
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDAssociationsList().size());

		// Move attributes from ClassC to ClassA2
		assertTrue(refactoring.moveMethodsAndAttributesToNeighborClass("ClassC", "ClassA2", utility.getAst()));

		// Check if attributes and methods are moved from ClassC to ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());

	}

	/**
	 * Test method moveMethodsAndAttributesToNeighborClass with counter example
	 */
	@Test
	public void testMoveMethodsAndAttributesToNeighborClassCounterExample() throws IOException {

		FileUtility utility = new FileUtility("cdlib/E1");
		Move refactoring = new Move();

		// Check input
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());

		// Should return false, if Class C and A2 are no neighbors
		assertFalse(refactoring.moveMethodsAndAttributesToNeighborClass("ClassC", "ClassA2", utility.getAst()));

	}

}
