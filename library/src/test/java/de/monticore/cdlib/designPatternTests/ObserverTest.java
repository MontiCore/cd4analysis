/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPatternTests;

import static org.junit.Assert.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdlib.designPattern.ObserverPattern;
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
 * Test class ObserverPattern
 *
 * <p>Created by
 *
 * @author KE
 */
public class ObserverTest {

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

  /** Test method introduceObserverPattern */
  @Test
  public void testDesignPatternObserver() throws IOException {

    FileUtility file = new FileUtility("cdlib/A");
    ObserverPattern observer = new ObserverPattern();

    // introduce observer pattern
    assertTrue(observer.introduceObserverPattern("A", "AObserver", "AObservable", file.getAst()));

    // Check if pattern was introduced
    assertEquals("A", file.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        "AObservable",
        file.getAst().getCDDefinition().getCDClassesList().get(0).printSuperclasses());
    assertEquals(
        1, file.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals(
        "getState",
        ((ASTCDMethod)
                file.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0))
            .getName());

    assertEquals(
        "AObservable", file.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        4, file.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(
        "addAObserver",
        ((ASTCDMethod)
                file.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0))
            .getName());
    assertEquals(
        "deleteAObserver",
        ((ASTCDMethod)
                file.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(1))
            .getName());
    assertEquals(
        "setAObserver",
        ((ASTCDMethod)
                file.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(2))
            .getName());
    assertEquals(
        "notifyAObserver",
        ((ASTCDMethod)
                file.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(3))
            .getName());

    assertEquals("AObserver", file.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(
        1, file.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    assertEquals(
        "update",
        ((ASTCDMethod)
                file.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(0))
            .getName());

    assertEquals(
        "ConcreteAObserver", file.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertEquals(
        "AObserver", file.getAst().getCDDefinition().getCDClassesList().get(3).printSuperclasses());
    assertEquals(
        1, file.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().size());
    assertEquals(
        "update",
        ((ASTCDMethod)
                file.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(0))
            .getName());

    assertEquals(2, file.getAst().getCDDefinition().getCDAssociationsList().size());
    assertTrue(file.getAst().getCDDefinition().getCDAssociationsList().get(0).isPresentName());
    assertEquals(
        "observers", file.getAst().getCDDefinition().getCDAssociationsList().get(0).getName());
    assertEquals(
        "AObservable",
        file.getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getLeftQualifiedName()
            .getQName());
    assertEquals(
        "AObserver",
        file.getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getRightQualifiedName()
            .getQName());
    assertTrue(
        file.getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getCDAssocDir()
            .isDefinitiveNavigableRight());
    assertTrue(
        file.getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getLeft()
            .getCDCardinality()
            .isOne());
    assertTrue(
        file.getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(0)
            .getRight()
            .getCDCardinality()
            .isMult()); // isMany

    assertEquals(
        "subject", file.getAst().getCDDefinition().getCDAssociationsList().get(1).getName());
    assertEquals(
        "ConcreteAObserver",
        file.getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(1)
            .getLeftQualifiedName()
            .getQName());
    assertEquals(
        "A",
        file.getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(1)
            .getRightQualifiedName()
            .getQName());
    assertTrue(
        file.getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(1)
            .getCDAssocDir()
            .isDefinitiveNavigableRight()); // .isLeftToRight());
    assertTrue(
        file.getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(1)
            .getLeft()
            .getCDCardinality()
            .isMult());
    assertTrue(
        file.getAst()
            .getCDDefinition()
            .getCDAssociationsList()
            .get(1)
            .getRight()
            .getCDCardinality()
            .isOne());
  }

  /** Test method introduceObserverPattern counterexample with missing class */
  @Test
  public void testDesignPatternObserverCounterexample() throws IOException {

    FileUtility file = new FileUtility("cdlib/Empty");
    ObserverPattern observer = new ObserverPattern();

    // introduce observer pattern
    assertFalse(observer.introduceObserverPattern("A", "AObserver", "AObservable", file.getAst()));
  }
}
