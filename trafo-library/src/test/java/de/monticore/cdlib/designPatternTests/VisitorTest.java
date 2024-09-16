/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPatternTests;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdlib.designPattern.VisitorPattern;
import de.monticore.cdlib.utilities.FileUtility;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class Visitor
 *
 * <p>Created by
 *
 * @author KE
 */
public class VisitorTest {

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

  /** Test method introduceVisitorPattern */
  @Test
  @Ignore // Also in master 1a795a08b0d34e976f41c303a1314cdc2e479d38
  public void testDesignPatternVisitor() throws IOException {

    FileUtility utility = new FileUtility("cdlib/Node");

    String node = "Node";
    List<String> replacedMethods = new ArrayList<String>();
    replacedMethods.add("optimize");
    replacedMethods.add("generate");

    VisitorPattern visitor = new VisitorPattern();
    List<String> visitors = Lists.newArrayList("optimize");
    visitors.add("generate");

    // Introduce visitor pattern
    assertTrue(visitor.introduceVisitorPattern(node, replacedMethods, utility.getAst()));

    assertEquals(6, utility.getAst().getCDDefinition().getCDClassesList().size());

    // Test Names of Classes
    assertEquals("Node", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("CNode1", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("CNode2", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        "NodeVisitor", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        "OptimizeVisitor", utility.getAst().getCDDefinition().getCDClassesList().get(4).getName());
    assertEquals(
        "GenerateVisitor", utility.getAst().getCDDefinition().getCDClassesList().get(5).getName());

    // Test inheritance
    assertEquals(
        "Node", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());
    assertEquals(
        "Node", utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());
    assertEquals(
        "NodeVisitor",
        utility.getAst().getCDDefinition().getCDClassesList().get(4).printSuperclasses());
    assertEquals(
        "NodeVisitor",
        utility.getAst().getCDDefinition().getCDClassesList().get(5).printSuperclasses());

    // Test Methods

    // Test Name
    assertEquals(
        "accept",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals(
        "accept",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(1)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals(
        "accept",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(2)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals(
        "visit",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(3)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals(
        "visit",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(4)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals(
        "visit",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(5)
                    .getCDMethodList()
                    .get(0))
            .getName());
    assertEquals(
        "visit",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(3)
                    .getCDMethodList()
                    .get(1))
            .getName());
    assertEquals(
        "visit",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(4)
                    .getCDMethodList()
                    .get(1))
            .getName());
    assertEquals(
        "visit",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(5)
                    .getCDMethodList()
                    .get(1))
            .getName());

    // Test number of methods
    CD4CodeFullPrettyPrinter pp = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    System.err.println(new CD4CodeFullPrettyPrinter(new IndentPrinter()).prettyprint(utility.getAst()));
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(
        1, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    assertEquals(
        2, utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().size());
    assertEquals(
        2, utility.getAst().getCDDefinition().getCDClassesList().get(4).getCDMethodList().size());
    assertEquals(
        2, utility.getAst().getCDDefinition().getCDClassesList().get(5).getCDMethodList().size());

    // Test Return Type
    assertEquals(
        "void",
        pp.prettyprint(utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0)
            .getMCReturnType()));
    assertEquals(
        "void",
        pp.prettyprint(utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(1)
                    .getCDMethodList()
                    .get(0)
            .getMCReturnType()));
    assertEquals(
        "void",
      pp.prettyprint(utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(2)
                    .getCDMethodList()
                    .get(0)
            .getMCReturnType()));
    assertEquals(
        "void",
      pp.prettyprint(utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(3)
                    .getCDMethodList()
                    .get(0)
            .getMCReturnType()));
    assertEquals(
        "void",
      pp.prettyprint(utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(4)
                    .getCDMethodList()
                    .get(0)
            .getMCReturnType()));
    assertEquals(
        "void",
      pp.prettyprint(utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(5)
                    .getCDMethodList()
                    .get(0)
            .getMCReturnType()));
    assertEquals(
        "void",
      pp.prettyprint(utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(3)
                    .getCDMethodList()
                    .get(1)
            .getMCReturnType()));
    assertEquals(
        "void",
      pp.prettyprint(utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(4)
                    .getCDMethodList()
                    .get(1)
            .getMCReturnType()));
    assertEquals(
        "void",
      pp.prettyprint(utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(5)
                    .getCDMethodList()
                    .get(1)
            .getMCReturnType()));

    // Test Return Parameter Name
    assertEquals(
        "nodeVisitor",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(0)
                    .getCDMethodList()
                    .get(0))
            .getCDParameterList()
            .get(0)
            .getName());
    assertEquals(
        "nodeVisitor",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(1)
                    .getCDMethodList()
                    .get(0))
            .getCDParameterList()
            .get(0)
            .getName());
    assertEquals(
        "nodeVisitor",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(2)
                    .getCDMethodList()
                    .get(0))
            .getCDParameterList()
            .get(0)
            .getName());
    assertEquals(
        "cNode1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(3)
                    .getCDMethodList()
                    .get(0))
            .getCDParameterList()
            .get(0)
            .getName());
    assertEquals(
        "cNode1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(4)
                    .getCDMethodList()
                    .get(0))
            .getCDParameterList()
            .get(0)
            .getName());
    assertEquals(
        "cNode1",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(5)
                    .getCDMethodList()
                    .get(0))
            .getCDParameterList()
            .get(0)
            .getName());
    assertEquals(
        "cNode2",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(3)
                    .getCDMethodList()
                    .get(1))
            .getCDParameterList()
            .get(0)
            .getName());
    assertEquals(
        "cNode2",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(4)
                    .getCDMethodList()
                    .get(1))
            .getCDParameterList()
            .get(0)
            .getName());
    assertEquals(
        "cNode2",
        ((ASTCDMethod)
                utility
                    .getAst()
                    .getCDDefinition()
                    .getCDClassesList()
                    .get(5)
                    .getCDMethodList()
                    .get(1))
            .getCDParameterList()
            .get(0)
            .getName());
  }

  /** Test method introduceVisitorPattern for counterexample expect error "Not found method" */
  @Test
  public void testDesignPatternVisitorCounterExample() throws IOException {

    FileUtility utility = new FileUtility("cdlib/A");

    String node = "Node";
    List<String> replacedMethods = new ArrayList<String>();
    replacedMethods.add("optimize");
    replacedMethods.add("generate");

    VisitorPattern visitor = new VisitorPattern();
    List<String> visitors = Lists.newArrayList("optimize");
    visitors.add("generate");

    System.out.print("expect error: Not found method: ");
    // expect that methods are not found
    assertFalse(visitor.introduceVisitorPattern(node, replacedMethods, utility.getAst()));
  }
}
