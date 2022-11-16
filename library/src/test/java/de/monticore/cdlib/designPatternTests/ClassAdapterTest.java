/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPatternTests;

import static org.junit.Assert.*;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdlib.designPattern.AdapterPattern;
import de.monticore.cdlib.utilities.FileUtility;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class AdapterPattern Test ClassAdapter
 *
 * <p>Created by
 *
 * @author KE
 */
public class ClassAdapterTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    CD4CodeMill.init();
  }

  @After
  public void flush() {
    Reporting.flush(null);
  }

  /** Test method introduceClassAdapterPattern without adding methods */
  @Test
  public void testDesignPatternClassAdapter() throws IOException {

    // Create objects
    FileUtility utility = new FileUtility("cdlib/A");
    AdapterPattern classAdapter = new AdapterPattern();

    // introduce Pattern class adapter
    assertTrue(classAdapter.introduceClassAdapterPattern("A", "Target", utility.getAst()));

    // Check if pattern was introduced
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        "AAdapter", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "Target", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());
    assertEquals(
        "Target", utility.getAst().getCDDefinition().getCDClassesList().get(1).printInterfaces());
    assertEquals(
        "A", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());
  }

  /** Test method introduceClassAdapterPattern with adding methods */
  @Test
  public void testDesignPatternClassAdapterWithMethod() throws IOException {

    FileUtility utility = new FileUtility("cdlib/A");
    FileUtility utility2 = new FileUtility("cdlib/AWithMethod");
    AdapterPattern classAdapter = new AdapterPattern();

    ASTCDMethod method =
        (ASTCDMethod)
            utility2.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0);

    // introduce Pattern class adapter with method
    assertTrue(classAdapter.introduceClassAdapterPattern("A", "Target", method, utility.getAst()));

    // Check if pattern was introduced
    assertEquals("A", utility.getAst().getCDDefinition().getCDClassesList().get(0).getName());
    assertEquals(
        "AAdapter", utility.getAst().getCDDefinition().getCDClassesList().get(1).getName());
    assertEquals(
        "Target", utility.getAst().getCDDefinition().getCDInterfacesList().get(0).getName());
    assertEquals(
        "Target", utility.getAst().getCDDefinition().getCDClassesList().get(1).printInterfaces());
    assertFalse(
        utility.getAst().getCDDefinition().getCDClassesList().get(1).getSuperclassList().isEmpty());
    assertEquals(
        "A", utility.getAst().getCDDefinition().getCDClassesList().get(1).printSuperclasses());

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
  }

  /** Test method introduceClassAdapterPattern without adding methods counterexample */
  @Test
  public void testDesignPatternClassAdapterCounterExample() throws IOException {

    // Create objects
    FileUtility utility = new FileUtility("cdlib/Empty");
    AdapterPattern classAdapter = new AdapterPattern();

    // introduce Pattern class adapter expect false, because of missing
    // class
    assertFalse(classAdapter.introduceClassAdapterPattern("A", "Target", utility.getAst()));
  }

  /** Test method introduceClassAdapterPattern with adding methods counterexample */
  @Test
  public void testDesignPatternClassAdapterWithMethodCounterExample() throws IOException {

    // Create objects
    FileUtility utility = new FileUtility("cdlib/Empty");
    AdapterPattern classAdapter = new AdapterPattern();
    FileUtility utility2 = new FileUtility("cdlib/AWithMethod");

    ASTCDMethod method =
        (ASTCDMethod)
            utility2.getAst().getCDDefinition().getCDClassesList().get(0).getCDMethodList().get(0);
    // introduce Pattern class adapter expect false, because of missing
    // class
    assertFalse(classAdapter.introduceClassAdapterPattern("A", "Target", method, utility.getAst()));
  }
}
