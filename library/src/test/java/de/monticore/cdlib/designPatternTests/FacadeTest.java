/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPatternTests;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import de.monticore.cdlib.designPattern.FacadePattern;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class FacadePattern
 *
 * Created by
 *
 * @author KE
 */
public class FacadeTest {

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
	}

	/**
	 * Test method introduceFacadePattern
	 */
	@Test
	public void testDesignPatternFacadeWithoutAssociation() throws IOException {

		FileUtility file = new FileUtility("cdlib/A");
		FacadePattern fassade = new FacadePattern();

		// Check input
		assertEquals(1, file.getAst().getCDDefinition().getCDClassesList().size());
		assertEquals("A", file.getAst().getCDDefinition().getCDClassesList().get(0).getName());

		// Introduce fassade pattern
		fassade.introduceFacadePattern(Lists.newArrayList("A"), file.getAst());

		// Check if pattern was introduced
		assertEquals(2, file.getAst().getCDDefinition().getCDClassesList().size());
		assertEquals("A", file.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("AFacade", file.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("AFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
		assertEquals("A",
				file.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
		assertTrue(file.getAst().getCDDefinition().getCDAssociationsList().get(0).getCDAssocDir().isBidirectional());

	}

	/**
	 * Test method introduceFacadePattern with updating associations
	 */
	@Test
	public void testDesignPatternFacade() throws IOException {

		FileUtility file = new FileUtility("cdlib/Association");
		FacadePattern fassade = new FacadePattern();

		// Check input
		assertEquals(2, file.getAst().getCDDefinition().getCDClassesList().size());
		assertEquals("A", file.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", file.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("A", file.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
		assertEquals("B",
				file.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
		assertFalse(file.getAst().getCDDefinition().getCDAssociationsList().get(0).getCDAssocDir().isDefinitiveNavigableRight());
    assertFalse(file.getAst().getCDDefinition().getCDAssociationsList().get(0).getCDAssocDir().isDefinitiveNavigableLeft());

		// Introduce fassade pattern
		fassade.introduceFacadePattern(Lists.newArrayList("A"), file.getAst());

		// Check if pattern was introduced
		assertEquals(3, file.getAst().getCDDefinition().getCDClassesList().size());
		assertEquals("A", file.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", file.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("AFacade", file.getAst().getCDDefinition().getCDClassesList().get(2).getName());

		assertEquals("AFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());
		assertEquals("B",
				file.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
		// is unspecified
		assertFalse(file.getAst().getCDDefinition().getCDAssociationsList().get(0).getCDAssocDir().isDefinitiveNavigableRight());
    assertFalse(file.getAst().getCDDefinition().getCDAssociationsList().get(0).getCDAssocDir().isDefinitiveNavigableLeft());

    assertEquals("AFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(1).getLeftQualifiedName().getQName());
		assertEquals("A",
				file.getAst().getCDDefinition().getCDAssociationsList().get(1).getRightQualifiedName().getQName());
		assertTrue(file.getAst().getCDDefinition().getCDAssociationsList().get(1).getCDAssocDir().isBidirectional());
	}

	/**
	 * Test method introduceFacadePattern with updating associations all
	 * variants of associations
	 */
	@Test
	public void testDesignPatternFacade2() throws IOException {

		FileUtility file = new FileUtility("cdlib/Fassade");
		FacadePattern fassade = new FacadePattern();

		// Introduce fassade pattern
		fassade.introduceFacadePattern(Lists.newArrayList("A", "B"), file.getAst());

		// Check if pattern was introduced
		assertEquals(4, file.getAst().getCDDefinition().getCDClassesList().size());
		assertEquals("A", file.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", file.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("C", file.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals("ABFacade", file.getAst().getCDDefinition().getCDClassesList().get(3).getName());

		assertEquals("ABFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
		assertEquals("C", file.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());

		assertEquals("ABFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(1).getRightQualifiedName().getQName());
		assertEquals("C", file.getAst().getCDDefinition().getCDAssociationsList().get(1).getLeftQualifiedName().getQName());

		assertEquals("ABFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(2).getRightQualifiedName().getQName());
		assertEquals("C", file.getAst().getCDDefinition().getCDAssociationsList().get(2).getLeftQualifiedName().getQName());

		assertEquals("ABFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(3).getRightQualifiedName().getQName());
		assertEquals("C", file.getAst().getCDDefinition().getCDAssociationsList().get(3).getLeftQualifiedName().getQName());

		assertEquals("ABFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(4).getLeftQualifiedName().getQName());
		assertEquals("C",
				file.getAst().getCDDefinition().getCDAssociationsList().get(4).getRightQualifiedName().getQName());

		assertEquals("ABFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(5).getLeftQualifiedName().getQName());
		assertEquals("C",
				file.getAst().getCDDefinition().getCDAssociationsList().get(5).getRightQualifiedName().getQName());

		assertEquals("ABFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(6).getLeftQualifiedName().getQName());
		assertEquals("C",
				file.getAst().getCDDefinition().getCDAssociationsList().get(6).getRightQualifiedName().getQName());

		assertEquals("ABFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(7).getLeftQualifiedName().getQName());
		assertEquals("C",
				file.getAst().getCDDefinition().getCDAssociationsList().get(7).getRightQualifiedName().getQName());

		assertEquals("A", file.getAst().getCDDefinition().getCDAssociationsList().get(8).getLeftQualifiedName().getQName());
		assertEquals("B",
				file.getAst().getCDDefinition().getCDAssociationsList().get(8).getRightQualifiedName().getQName());

		assertEquals("ABFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(9).getLeftQualifiedName().getQName());
		assertEquals("A",
				file.getAst().getCDDefinition().getCDAssociationsList().get(9).getRightQualifiedName().getQName());

		assertEquals("ABFacade",
				file.getAst().getCDDefinition().getCDAssociationsList().get(10).getLeftQualifiedName().getQName());
		assertEquals("B",
				file.getAst().getCDDefinition().getCDAssociationsList().get(10).getRightQualifiedName().getQName());

	}

	/**
	 * Test method introduceFacadePattern with updating associations all
	 * variants of associations
	 */
	@Test
	public void testDesignPatternFacadeManualNaming() throws IOException {

		FileUtility file = new FileUtility("cdlib/Fassade");
		FacadePattern fassade = new FacadePattern();

		// Introduce fassade pattern
		fassade.introduceFacadePattern(Lists.newArrayList("A", "B"), "D", file.getAst());

		// Check if pattern was introduced
		assertEquals(4, file.getAst().getCDDefinition().getCDClassesList().size());
		assertEquals("A", file.getAst().getCDDefinition().getCDClassesList().get(0).getName());
		assertEquals("B", file.getAst().getCDDefinition().getCDClassesList().get(1).getName());
		assertEquals("C", file.getAst().getCDDefinition().getCDClassesList().get(2).getName());
		assertEquals("D", file.getAst().getCDDefinition().getCDClassesList().get(3).getName());

		assertEquals("D",
				file.getAst().getCDDefinition().getCDAssociationsList().get(0).getRightQualifiedName().getQName());
		assertEquals("C", file.getAst().getCDDefinition().getCDAssociationsList().get(0).getLeftQualifiedName().getQName());

		assertEquals("D",
				file.getAst().getCDDefinition().getCDAssociationsList().get(1).getRightQualifiedName().getQName());
		assertEquals("C", file.getAst().getCDDefinition().getCDAssociationsList().get(1).getLeftQualifiedName().getQName());

		assertEquals("D",
				file.getAst().getCDDefinition().getCDAssociationsList().get(2).getRightQualifiedName().getQName());
		assertEquals("C", file.getAst().getCDDefinition().getCDAssociationsList().get(2).getLeftQualifiedName().getQName());

		assertEquals("D",
				file.getAst().getCDDefinition().getCDAssociationsList().get(3).getRightQualifiedName().getQName());
		assertEquals("C", file.getAst().getCDDefinition().getCDAssociationsList().get(3).getLeftQualifiedName().getQName());

		assertEquals("D", file.getAst().getCDDefinition().getCDAssociationsList().get(4).getLeftQualifiedName().getQName());
		assertEquals("C",
				file.getAst().getCDDefinition().getCDAssociationsList().get(4).getRightQualifiedName().getQName());

		assertEquals("D", file.getAst().getCDDefinition().getCDAssociationsList().get(5).getLeftQualifiedName().getQName());
		assertEquals("C",
				file.getAst().getCDDefinition().getCDAssociationsList().get(5).getRightQualifiedName().getQName());

		assertEquals("D", file.getAst().getCDDefinition().getCDAssociationsList().get(6).getLeftQualifiedName().getQName());
		assertEquals("C",
				file.getAst().getCDDefinition().getCDAssociationsList().get(6).getRightQualifiedName().getQName());

		assertEquals("D", file.getAst().getCDDefinition().getCDAssociationsList().get(7).getLeftQualifiedName().getQName());
		assertEquals("C",
				file.getAst().getCDDefinition().getCDAssociationsList().get(7).getRightQualifiedName().getQName());

		assertEquals("A", file.getAst().getCDDefinition().getCDAssociationsList().get(8).getLeftQualifiedName().getQName());
		assertEquals("B",
				file.getAst().getCDDefinition().getCDAssociationsList().get(8).getRightQualifiedName().getQName());

		assertEquals("D", file.getAst().getCDDefinition().getCDAssociationsList().get(9).getLeftQualifiedName().getQName());
		assertEquals("A",
				file.getAst().getCDDefinition().getCDAssociationsList().get(9).getRightQualifiedName().getQName());

		assertEquals("D",
				file.getAst().getCDDefinition().getCDAssociationsList().get(10).getLeftQualifiedName().getQName());
		assertEquals("B",
				file.getAst().getCDDefinition().getCDAssociationsList().get(10).getRightQualifiedName().getQName());

	}

}
