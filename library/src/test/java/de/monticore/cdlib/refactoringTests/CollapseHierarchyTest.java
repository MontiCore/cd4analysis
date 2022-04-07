/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.se_rwth.commons.logging.Log;
import de.monticore.cdlib.refactorings.CollapseHierarchy;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

/**
 * Tests for Collapse Hierarchy
 *
 * @author Philipp Nolte
 */
public class CollapseHierarchyTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    CD4CodeMill.init();
    ReportManager.ReportManagerFactory factory = new ReportManager.ReportManagerFactory() {
      @Override
      public ReportManager provide(String modelName) {
        ReportManager reports = new ReportManager("target/generated-sources");
        TransformationReporter transformationReporter = new TransformationReporter(
            "target/generated-sources", modelName,
            new ReportingRepository(new ASTNodeIdentHelper()));
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
  public void testCollapseHierarchyMethod() throws IOException {
    
    FileUtility utility = new FileUtility("cdlib/EvaluationCollapseHierarchy");
    CollapseHierarchy refactoring = new CollapseHierarchy();
    
    // Check input
    ASTCDMethod firstMethodToMove = (ASTCDMethod) utility.getAst().getCDDefinition().getCDClassesList().get(0)
        .getCDMethodList().get(0);
    ASTCDMethod secondMethodToMove = (ASTCDMethod) utility.getAst().getCDDefinition().getCDClassesList().get(0)
        .getCDMethodList().get(1);
    ASTCDMethod existingMethodInDOverwrite = (ASTCDMethod) utility.getAst().getCDDefinition().getCDClassesList()
        .get(3).getCDMethodList().get(0);
    ASTCDMethod existingMethodInDNotOverwrite = (ASTCDMethod) utility.getAst().getCDDefinition().getCDClassesList()
        .get(3).getCDMethodList().get(1);
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals("ClassD", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertFalse(
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty());
    assertFalse(
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty());
    assertFalse(
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getSuperclassList().isEmpty());
    
    // Perform transformation (push down methods from ClassC
    // to ClassA and ClassB)
    assertTrue(refactoring.collapseHierarchy("ClassC", utility.getAst()));
    
    // Check if ClassC is deleted and method added in ClassA and
    // ClassB
    assertEquals(3, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassD", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)
        .deepEquals(firstMethodToMove));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(1)
        .deepEquals(secondMethodToMove));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0)
        .deepEquals(firstMethodToMove));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(1)
        .deepEquals(secondMethodToMove));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(0)
        .deepEquals(existingMethodInDOverwrite));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(1)
        .deepEquals(existingMethodInDNotOverwrite));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(2)
        .deepEquals(secondMethodToMove));
    assertTrue(
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getSuperclassList().isEmpty());
    assertTrue(
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty());
    assertTrue(
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty());
  }
  
  /**
   * Test method pushDown with attributes
   */
  @Test
  public void testCollapseHierarchyAttribute() throws IOException {
    
    FileUtility utility = new FileUtility("cdlib/EvaluationCollapseHierarchy");
    CollapseHierarchy refactoring = new CollapseHierarchy();
    
    // Check input
    ASTCDAttribute firstAttributeToMove = utility.getAst().getCDDefinition().getCDClassesList().get(0)
        .getCDAttributeList()
        .get(0);
    ASTCDAttribute secondAttributeToMove = utility.getAst().getCDDefinition().getCDClassesList().get(0)
        .getCDAttributeList()
        .get(1);
    ASTCDAttribute existingAttributeInDOverwrite = utility.getAst().getCDDefinition().getCDClassesList()
        .get(3).getCDAttributeList()
        .get(0);
    ASTCDAttribute existingAttributeInDNotOverwrite = utility.getAst().getCDDefinition()
        .getCDClassesList().get(3).getCDAttributeList()
        .get(1);
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals("ClassD", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());
    assertFalse(
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty());
    assertFalse(
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty());
    assertFalse(
        utility.getAst().getCDDefinition().getCDClassesList().get(3).getSuperclassList().isEmpty());
    
    // Perform transformation (push down attributes from ClassC
    // to ClassA and ClassB)
    assertTrue(refactoring.collapseHierarchy("ClassC", utility.getAst()));
    
    // Check if ClassC is deleted and attribute added in ClassA and
    // ClassB
    assertEquals(3, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassD", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0)
        .deepEquals(firstAttributeToMove));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(1)
        .deepEquals(secondAttributeToMove));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0)
        .deepEquals(firstAttributeToMove));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(1)
        .deepEquals(secondAttributeToMove));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(0)
        .deepEquals(existingAttributeInDOverwrite));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(1)
        .deepEquals(existingAttributeInDNotOverwrite));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(2)
        .deepEquals(secondAttributeToMove));
    assertTrue(
        utility.getAst().getCDDefinition().getCDClassesList().get(0).getSuperclassList().isEmpty());
    assertTrue(
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty());
    assertTrue(
        utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty());
  }
}
