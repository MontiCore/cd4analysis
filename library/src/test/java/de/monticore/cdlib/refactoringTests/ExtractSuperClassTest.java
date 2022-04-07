/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import de.monticore.cdlib.refactorings.ExtractSuperClass;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
import de.monticore.cdlib.utilities.FileUtility;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test class ExtractClass
 *
 * Created by
 *
 * @author KE
 */
public class ExtractSuperClassTest {

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
   * Test method extractClassAttribute
   */
  @Test
  public void testExtractClassAttribute() throws IOException {
    FileUtility utility = new FileUtility("cdlib/ExtractSuperClass");
    ExtractSuperClass refactoring = new ExtractSuperClass();

    // Check input ast
    assertEquals(3, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(0).getSuperclassList().isEmpty() ^ true);
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty() ^ true);
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0)
        .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0)));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(1)
            .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(1)));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0)
            .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(0)));
    ASTCDAttribute a = utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0);

    // Perform transformation
    assertTrue(refactoring.extractSuperClassAttribute(utility.getAst()));

    // Check resulting ast
    assertEquals(4, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    // New superclass was added
    assertEquals("ClassAClassBClassC", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());

    // Attribute is deleted in subclasses
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    // attribute is added in new superclass
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().get(0).deepEquals(a));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty() ^ true);
    assertEquals("ClassAClassBClassC",utility.getAst().getCDDefinition().getCDClassesList().get(0).printSuperclasses());
    assertEquals("ClassAClassBClassC",utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());
    assertEquals("ClassAClassBClassC",utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());
  }
  
  /**
   * Test method extractClassAttributeWithName
   */
  @Test
  public void testExtractClassAttributeWithName() throws IOException {
    FileUtility utility = new FileUtility("cdlib/ExtractSuperClass");
    ExtractSuperClass refactoring = new ExtractSuperClass();

    // Check input ast
    assertEquals(3, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(0).getSuperclassList().isEmpty() ^ true);
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty() ^ true);
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0)
        .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0)));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(1)
            .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(1)));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0)
            .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().get(0)));
    ASTCDAttribute a = utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0);

    // Perform transformation
    assertTrue(refactoring.extractSuperClassAttributeWithName(utility.getAst(), "TestName"));
   

//    System.out.println(new CDPrettyPrinterConcreteVisitor(new IndentPrinter()).prettyprint(utility.getAst()));

    // Check resulting ast
    assertEquals(4, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    // New superclass was added
    assertEquals("TestName", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());

    // Attribute is deleted in subclasses
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    // attribute is added in new superclass
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().get(0).deepEquals(a));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty() ^ true);
    assertEquals("TestName",utility.getAst().getCDDefinition().getCDClassesList().get(0).printSuperclasses());
    assertEquals("TestName",utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());
    assertEquals("TestName",utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());
  }

  /**
   * Test method extractClassMethod
   */
  @Test
  public void testExtractClassMethod() throws IOException {
    FileUtility utility = new FileUtility("cdlib/ExtractSuperClass");
    ExtractSuperClass refactoring = new ExtractSuperClass();
    // Check input ast
    assertEquals(3, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(0).getSuperclassList().isEmpty() ^ true);
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty() ^ true);
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)
        .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0)));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(1)
            .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(1)));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)
            .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(0)));
    ASTCDMethod a = (ASTCDMethod) utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0);

    // Perform transformation
    assertTrue(refactoring.extractSuperClassMethod(utility.getAst()));

    // Check resulting ast
    assertEquals(4, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    // New superclass was added
    assertEquals("ClassAClassBClassC", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());

    // Method is deleted in subclasses
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    // Method is added in new superclass
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(0).deepEquals(a));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty() ^ true);
    assertEquals("ClassAClassBClassC",utility.getAst().getCDDefinition().getCDClassesList().get(0).printSuperclasses());
    assertEquals("ClassAClassBClassC",utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());
    assertEquals("ClassAClassBClassC",utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());
  }
  
  /**
   * Test method extractClassMethodWithName
   */
  @Test
  public void testExtractClassMethodWithName() throws IOException {
    FileUtility utility = new FileUtility("cdlib/ExtractSuperClass");
    ExtractSuperClass refactoring = new ExtractSuperClass();
    // Check input ast
    assertEquals(3, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals(2, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(0).getSuperclassList().isEmpty() ^ true);
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty() ^ true);
    assertTrue(!utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)
        .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0)));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(1)
            .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(1)));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)
            .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().get(0)));
    ASTCDMethod a = (ASTCDMethod) utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0);

    // Perform transformation
    assertTrue(refactoring.extractSuperClassMethodWithName(utility.getAst(), "TestName"));

    // Check resulting ast
    assertEquals(4, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassB", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals("ClassC", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    // New superclass was added
    assertEquals("TestName", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());

    // Method is deleted in subclasses
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    // Method is added in new superclass
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(0).deepEquals(a));
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty() ^ true);
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(2).getSuperclassList().isEmpty() ^ true);
    assertEquals("TestName",utility.getAst().getCDDefinition().getCDClassesList().get(0).printSuperclasses());
    assertEquals("TestName",utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());
    assertEquals("TestName",utility.getAst().getCDDefinition().getCDClassesList().get(2).printSuperclasses());
  }

  /**
   * Test method extractSuperClass
   */
  @Test
  public void testExtractClass() throws IOException {

    FileUtility utility = new FileUtility("cdlib/ExtractClassTest");
    ExtractSuperClass refactoring = new ExtractSuperClass();

    // Check input ast
    assertEquals(3, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals(1, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0)
        .deepEquals(utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0)));
    assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(2).getCDAttributeList().size());
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    ASTCDAttribute a = utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(0);

    // Perform transformation
    assertTrue(refactoring.extractSuperClass(utility.getAst()));

    // Check resulting ast
    assertEquals(4, utility.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA1", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    // New superclass was added
    assertEquals("ClassA1ClassA2", utility.getAst().getCDDefinition().getCDClassesList().get(3).getName());

    // Attribute is deleted in subclasses
    assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals(0, utility.getAst().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size());
    // attribute is added in new superclass
    assertTrue(utility.getAst().getCDDefinition().getCDClassesList().get(3).getCDAttributeList().get(0).deepEquals(a));

    FileUtility utility2 = new FileUtility("cdlib/ExtractClassTestMethod");
    ExtractSuperClass refactoring2 = new ExtractSuperClass();

    // Check input ast
    assertEquals(3, utility2.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA1", utility2.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassA2", utility2.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(1, utility2.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals(1, utility2.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    assertTrue(utility2.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0)
        .deepEquals(utility2.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().get(0)));
    assertEquals(0, utility2.getAst().getCDDefinition().getCDClassesList().get(2).getCDMethodList().size());
    assertEquals("A", utility2.getAst().getCDDefinition().getCDClassesList().get(2).getName());
    ASTCDMethod m = (ASTCDMethod) utility2.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0);

    // Perform transformation
    assertTrue(refactoring2.extractSuperClass(utility2.getAst()));

    // Check resulting ast
    assertEquals(4, utility2.getAst().getCDDefinition().getCDClassesList().size());
    assertEquals("ClassA1", utility2.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals("ClassA2", utility2.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    // New superclass was added
    assertEquals("ClassA1ClassA2", utility2.getAst().getCDDefinition().getCDClassesList().get(3).getName());

    // Attribute is deleted in subclasses
    assertEquals(0, utility2.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
    assertEquals(0, utility2.getAst().getCDDefinition().getCDClassesList().get(1).getCDMethodList().size());
    // attribute is added in new superclass
    assertTrue(utility2.getAst().getCDDefinition().getCDClassesList().get(3).getCDMethodList().get(0).deepEquals(m));

  }

  /**
   * Test method extractSuperClass with counter example
   */
  @Test
  public void testExtractClassCounterExample() throws IOException {

    FileUtility utility = new FileUtility("cdlib/A");
    ExtractSuperClass refactoring = new ExtractSuperClass();

    // Perform transformation
    assertFalse(refactoring.extractSuperClass(utility.getAst()));
  }

}
