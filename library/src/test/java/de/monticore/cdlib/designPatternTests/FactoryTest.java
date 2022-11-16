/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPatternTests;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdlib.designPattern.FactoryPattern;
import de.monticore.cdlib.utilities.FileUtility;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class FactoryPattern
 *
 * <p>Created by
 *
 * @author KE
 */
public class FactoryTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    CD4CodeMill.init();
    ReportManager.ReportManagerFactory factory =
        new ReportManager.ReportManagerFactory() {
          @Override
          public ReportManager provide(String modelName) {
            ReportManager reports = new ReportManager("target/generated-sources");
            TransformationReporter transformationReporter =
                new TransformationReporter(
                    "target/generated-sources",
                    modelName,
                    new ReportingRepository(new ASTNodeIdentHelper()));
            reports.addReportEventHandler(transformationReporter);
            return reports;
          }
        };

    Reporting.init("target/generated-sources", "target/reports", factory);
  }

  /** Test method introduceFactoryPattern */
  @Test
  public void testFactory() throws IOException {

    FileUtility utility = new FileUtility("cdlib/Node");
    FactoryPattern designPattern = new FactoryPattern();

    assertEquals("Node", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("CNode1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("CNode2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());

    // introduce pattern factory
    assertTrue(
        designPattern.introduceFactoryPattern(
            Lists.newArrayList("CNode1", "CNode2"), "Node", utility.getAst()));

    // Check if pattern was introduced
    assertEquals("Node", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("CNode1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("CNode2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        "NodeFactory", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        3, utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().size());
    // TODO
    //		assertEquals("create",
    //
    //	utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(0).getName());
    //
    //	assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(0).getModifier()
    //				.isPublic());
    //		assertEquals("Node",
    //
    //	utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(0).printReturnType());
    //
    //		assertEquals("doCreate",
    //
    //	utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(1).getName());
    //
    //	assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(1).getModifier()
    //				.isPrivate());
    //		assertEquals("CNode1",
    //
    //	utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(1).printReturnType());
    //
    //		assertEquals("doCreate",
    //
    //	utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(2).getName());
    //
    //	assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(2).getModifier()
    //				.isPrivate());
    //		assertEquals("CNode2",
    //
    //	utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(2).printReturnType());
  }

  /** Test method introduceFactoryPattern counterexample for missing classes */
  @Test
  public void testFactoryCounterexample() throws IOException {

    FileUtility utility = new FileUtility("cdlib/Empty");
    FactoryPattern designPattern = new FactoryPattern();

    // introduce pattern factory
    assertFalse(
        designPattern.introduceFactoryPattern(Lists.newArrayList(), "Node", utility.getAst()));
  }
}
