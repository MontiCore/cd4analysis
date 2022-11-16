/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPatternTests;

import static org.junit.Assert.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdlib.designPattern.AdapterPattern;
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
 * Test class AdapterPattern Test objectAdapter
 *
 * <p>Created by
 *
 * @author KE
 */
public class ObjectAdapterTest {

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

  /** Test method introduceObjectAdapterPattern without adding methods */
  @Test
  public void testDesignPatternObjectAdapter() throws IOException {

    FileUtility utility = new FileUtility("cdlib/A");
    AdapterPattern objectAdapter = new AdapterPattern();

    // introduce object adapter pattern
    assertTrue(objectAdapter.introduceObjectAdapterPattern("A", "Target", utility.getAst()));

    // Check if pattern was introduced
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        "AAdapter", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "Target", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());

    assertEquals(
        "AAdapter",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getLeftQualifiedName()
            .getQName());
    assertEquals(
        "A",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getRightQualifiedName()
            .getQName());
    assertFalse(
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getInterfaceList().isEmpty());
  }

  /** Test method introduceObjectAdapterPattern with adding methods */
  @Test
  public void testDesignPatternObjectAdapterWithMethod() throws IOException {

    FileUtility utility = new FileUtility("cdlib/A");
    FileUtility utility2 = new FileUtility("cdlib/AWithMethod");
    AdapterPattern objectAdapter = new AdapterPattern();

    ASTCDMethod method =
        (ASTCDMethod)
            utility2.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0);

    // introduce object adapter patterm
    assertTrue(
        objectAdapter.introduceObjectAdapterPattern("A", "Target", method, utility.getAst()));

    // Check if pattern was introduced
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        "AAdapter", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "Target", utility.getAst().getCDDefinition().getCDClassesList().get(1).printInterfaces());
    assertEquals(
        "Target", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());
    assertTrue(
        method.deepEquals(
            utility
                .getAst()
                .getCDDefinition()
                .getCDInterfacesList()
                .get(0)
                .getCDMethodList()
                .get(0)));
    assertTrue(
        method.deepEquals(
            utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0)));

    assertEquals(
        "AAdapter",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getLeftQualifiedName()
            .getQName());
    assertEquals(
        "A",
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getRightQualifiedName()
            .getQName());
    assertFalse(
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getInterfaceList().isEmpty());
  }

  /** Test method introduceObjectAdapterPattern without adding methods counterexample */
  @Test
  public void testDesignPatternObjectAdapterCounterExample() throws IOException {

    // Create objects
    FileUtility utility = new FileUtility("cdlib/Empty");
    AdapterPattern objectAdapter = new AdapterPattern();

    // introduce Pattern class adapter expect false, because of missing
    // class
    assertFalse(objectAdapter.introduceObjectAdapterPattern("A", "Target", utility.getAst()));
  }

  /** Test method introduceObjectAdapterPattern with adding methods counterexample */
  @Test
  public void testDesignPatternObjectAdapterWithMethodCounterExample() throws IOException {

    // Create objects
    FileUtility utility = new FileUtility("cdlib/Empty");
    AdapterPattern objectAdapter = new AdapterPattern();
    FileUtility utility2 = new FileUtility("cdlib/AWithMethod");

    ASTCDMethod method =
        (ASTCDMethod)
            utility2.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0);
    // introduce Pattern class adapter expect false, because of missing
    // class
    assertFalse(
        objectAdapter.introduceObjectAdapterPattern("A", "Target", method, utility.getAst()));
  }
}
