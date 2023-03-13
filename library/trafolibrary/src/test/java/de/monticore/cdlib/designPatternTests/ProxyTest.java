/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPatternTests;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdlib.designPattern.ProxyPattern;
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
 * Test class ProxyPattern
 *
 * <p>Created by
 *
 * @author KE
 */
public class ProxyTest {

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

  /** Test method introduceProxyPattern with auto-naming for proxy-class */
  @Test
  public void testProxy() throws IOException {

    FileUtility utility = new FileUtility("cdlib/A");
    ProxyPattern proxy = new ProxyPattern();

    // introduce proxy pattern
    assertTrue(proxy.introduceProxyPattern("A", utility.getAst()));

    // Test if Proxy Pattern was introduced
    assertEquals("IA", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ProxyA", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "IA", utility.getAst().getCDDefinition().getCDClassesList().get(0).printInterfaces());
    assertEquals(
        "IA", utility.getAst().getCDDefinition().getCDClassesList().get(1).printInterfaces());
    assertEquals(
        "ProxyA",
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
    assertTrue(
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getCDAssocDir()
            .isDefinitiveNavigableRight());
  }

  /** Test method introduceProxyPattern with adding method */
  @Test
  public void testProxyWithMethods() throws IOException {

    FileUtility utility = new FileUtility("cdlib/AWithMethod");
    ProxyPattern proxy = new ProxyPattern();

    ASTCDMethod m =
        (ASTCDMethod)
            utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0);

    // introduce proxy pattern with a method
    assertTrue(proxy.introduceProxyPattern("A", Lists.newArrayList(m.getName()), utility.getAst()));

    // Test if Proxy Pattern was introduced
    assertEquals("IA", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ProxyA", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "IA", utility.getAst().getCDDefinition().getCDClassesList().get(0).printInterfaces());
    assertEquals(
        "IA", utility.getAst().getCDDefinition().getCDClassesList().get(1).printInterfaces());
    assertEquals(
        "ProxyA",
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
    assertTrue(
        utility
            .getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getCDAssocDir()
            .isDefinitiveNavigableRight());
    assertTrue(
        utility
            .getAst()
            .getCDDefinition()
            .getCDInterfacesList()
            .get(0)
            .getCDMethodList()
            .get(0)
            .deepEquals(m));
    assertTrue(
        utility
            .getAst()
            .getCDDefinition()
            .getCDClassesList()
            .get(1)
            .getCDMethodList()
            .get(0)
            .deepEquals(m));
  }

  /**
   * Test method introduceProxyPattern with auto-naming for proxy-class counterexample for missing
   * class
   */
  @Test
  public void testProxyCounterExample() throws IOException {

    FileUtility utility = new FileUtility("cdlib/Empty");
    ProxyPattern proxy = new ProxyPattern();

    // introduce proxy pattern
    assertFalse(proxy.introduceProxyPattern("A", utility.getAst()));
  }
}
