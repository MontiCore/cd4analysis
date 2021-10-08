/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.refactorings.PushDown;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class PushDown
 *
 * Created by
 *
 * @author KE
 */
public class PushDownTest {

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
	 * Test method pushDown with methods
	 */
	@Test
	public void testPushDownMethod() throws IOException {

		FileUtility utility = new FileUtility("cdlib/EvaluationRule1PullUpMethod");
		PushDown refactoring = new PushDown();

		// Check input
		ASTCDMethod m = (ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0);
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());

		// Perform transformation (push down attributes and methods from ClassC
		// to ClassA1 and ClassA2)
		assertTrue(refactoring.pushDown("ClassC", utility.getAst()));

		// Check if attribute is deleted in ClassC and added in ClassA1 and
		// ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0).deepEquals(m));
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(0).deepEquals(m));
	}

	/**
	 * Test method pushDown with attributes
	 */
	@Test
	public void testPushDownAttribute() throws IOException {

		FileUtility utility = new FileUtility("cdlib/EvaluationRule1PullUpAttribute");
		PushDown refactoring = new PushDown();

		// Check input
		ASTCDAttribute m = utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0)
				.deepClone();
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());

		// Perform transformation (push down attributes from ClassC to ClassA1
		// and ClassA2)
		assertTrue(refactoring.pushDownAllAttributes("ClassC", utility.getAst()));

		// Check if attribute is deleted in ClassC and added in ClassA1 and
		// ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).deepEquals(m));
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(0).deepEquals(m));
	}

	/**
	 * Test method pushDown with attributes and methods
	 */
	@Test
	public void testPushDownAttributeAndMethod() throws IOException {

		FileUtility utility = new FileUtility("cdlib/EvaluationRule1PullUpAttributeMethods");
		PushDown refactoring = new PushDown();

		// Check input
		ASTCDAttribute a = utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0)
				.deepClone();
		ASTCDMethod m = (ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0).deepClone();
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());

		// Perform transformation (push down methods and attributes from ClassC
		// to ClassA1 and ClassA2)
		assertTrue(refactoring.pushDown("ClassC", utility.getAst()));

		// Check if attributes and methods are deleted in ClassC and added in
		// ClassA1 and ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).deepEquals(a));
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(0).deepEquals(a));
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0).deepEquals(m));
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(0).deepEquals(m));
	}

	/**
	 * Test method pushDownToAllSubclasses with methods
	 */
	@Test
	public void testPushDownMethodToAllSubclasses() throws IOException {

		FileUtility utility = new FileUtility("cdlib/EvaluationRule1PullUpMethod");
		PushDown refactoring = new PushDown();

		// Check input
		ASTCDMethod m = (ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0);
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());

		// Perform transformation (push down attributes and methods from ClassC
		// to ClassA1 and ClassA2)
		assertTrue(refactoring.pushDownAllMethods("ClassC", utility.getAst()));

		// Check if attribute is deleted in ClassC and added in ClassA1 and
		// ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0).deepEquals(m));
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(0).deepEquals(m));
	}

	/**
	 * Test method pushDownToAllSubclasses with attributes
	 */
	@Test
	public void testPushDownAttributeToAllSubclasses() throws IOException {

		FileUtility utility = new FileUtility("cdlib/EvaluationRule1PullUpAttribute");
		PushDown refactoring = new PushDown();

		// Check input
		ASTCDAttribute m = utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0)
				.deepClone();
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());

		// Perform transformation (push down attributes from ClassC to ClassA1
		// and ClassA2)
		assertTrue(refactoring.pushDownAllAttributes("ClassC", utility.getAst()));

		// Check if attribute is deleted in ClassC and added in ClassA1 and
		// ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).deepEquals(m));
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(0).deepEquals(m));
	}

	/**
	 * Test method pushDownToAllSubclasses with attributes
	 */
	@Test
	public void testPushDownSpecificAttributeToAllSubclasses() throws IOException {

		FileUtility utility = new FileUtility("cdlib/EvaluationRule1PullUpAttribute2Attributes");
		PushDown refactoring = new PushDown();

		// Check input
		ASTCDAttribute m = utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(1)
				.deepClone();
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());

		// Perform transformation (push down attributes from ClassC to ClassA1
		// and ClassA2)
		assertTrue(refactoring.pushDownAttributes("ClassC", Lists.newArrayList("attribute1"), utility.getAst()));

		// Check if attribute is deleted in ClassC and added in ClassA1 and
		// ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("a", utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).deepEquals(m));
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(0).deepEquals(m));
	}

	/**
	 * Test method pushDownToAllSubclasses with attributes
	 */
	@Test
	public void testPushDownSpecificMethodToAllSubclasses() throws IOException {

		FileUtility utility = new FileUtility("cdlib/EvaluationRule1PullUpMethod2Methods");
		PushDown refactoring = new PushDown();

		// Check input
		ASTCDMethod m = (ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0).deepClone();
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());

		// Perform transformation (push down attributes from ClassC to ClassA1
		// and ClassA2)
		assertTrue(refactoring.pushDownMethods("ClassC", Lists.newArrayList("getAttribute"), utility.getAst()));

		// Check if attribute is deleted in ClassC and added in ClassA1 and
		// ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("a", ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0).deepEquals(m));
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(0).deepEquals(m));
	}

	/**
	 * Test method pushDownToAllSubclasses with attributes
	 */
	@Test
	public void testPushDownSpecificAttributeToOneSubclass() throws IOException {

		FileUtility utility = new FileUtility("cdlib/EvaluationRule1PullUpAttribute2Attributes");
		PushDown refactoring = new PushDown();

		// Check input
		ASTCDAttribute m = utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0)
				.deepClone();
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());

		// Perform transformation (push down attributes from ClassC to ClassA1
		// and ClassA2)
		assertTrue(refactoring.pushDownAttributes("ClassC", Lists.newArrayList("ClassA1"), Lists.newArrayList("a"),
				utility.getAst()));

		// Check if attribute is deleted in ClassC and added in ClassA1 and
		// ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("attribute1",
				utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).deepEquals(m));
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
	}

	/**
	 * Test method pushDownToAllSubclasses with attributes
	 */
	@Test
	public void testPushDownSpecificMethodToOneSubclass() throws IOException {

		FileUtility utility = new FileUtility("cdlib/EvaluationRule1PullUpMethod2Methods");
		PushDown refactoring = new PushDown();

		// Check input
		ASTCDMethod m = (ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(1).deepClone();
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());

		// Perform transformation (push down attributes from ClassC to ClassA1
		// and ClassA2)
		assertTrue(refactoring.pushDownMethods("ClassC", Lists.newArrayList("ClassA1"), Lists.newArrayList("a"),
				utility.getAst()));

		// Check if attribute is deleted in ClassC and added in ClassA1 and
		// ClassA2
		assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("getAttribute",
       ((ASTCDMethod)utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)).getName());
		assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
		assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0).deepEquals(m));
		assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
	}

	@Test
	public void testPushDownAttributesCounter() throws IOException {

		FileUtility utility = new FileUtility("cdlib/Empty");
		PushDown refactoring = new PushDown();

		// Perform transformation (push down methods and attributes from ClassC
		// to ClassA1 and ClassA2)
		assertFalse(refactoring.pushDown("A", utility.getAst()));
	}

}
