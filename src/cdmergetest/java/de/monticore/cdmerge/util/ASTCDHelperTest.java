/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmerge.BaseTest;
import de.monticore.cdmerge.exceptions.MergingException;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ASTCDHelperTest extends BaseTest {

  public static final String INPUT_MODEL_FILE = "General/university/Staff.cd";

  public ASTCDHelper testant;

  @Before
  public void initHelper() throws IOException {
    ASTCDCompilationUnit cd = loadModel(Paths.get(MODEL_PATH, INPUT_MODEL_FILE).toString());
    this.testant = new ASTCDHelper(cd);
  }

  @Test
  public void testGetType() {
    assertTrue(testant.getType("Person").isPresent());
    assertTrue(testant.getType("Employee").isPresent());
    assertTrue(testant.getType("Faculty").isPresent());
    assertTrue(testant.getType("StaffFunction").isPresent());
  }

  @Test
  public void testGetInterface() {
    assertTrue(testant.getInterface("Human").isPresent());
    assertTrue(!testant.getInterface("noInterface").isPresent());
  }

  @Test
  public void testGetClass() {
    assertTrue(testant.getClass("Employee").isPresent());
    assertTrue(testant.getClass("Room").isPresent());
    assertTrue(!testant.getClass("noclass").isPresent());
  }

  @Test
  public void testGetEnum() {
    assertTrue(testant.getEnum("StaffFunction").isPresent());
    assertTrue(!testant.getClass("noenum").isPresent());
  }

  @Test
  public void testGetAttributesForClass() {
    assertTrue(testant.getAttributeFromClass("emplNumber", "Employee").isPresent());
    assertTrue(testant.getAttributeFromClass("email", "Employee").isPresent());
    assertTrue(!testant.getAttributeFromClass("noAttribute", "Employee").isPresent());
  }

  @Test
  public void testGetNamedAssociation() {
    assertTrue(testant.getNamedAssociations("employment").isPresent());
    assertTrue(!testant.getNamedAssociations("novalidassocname").isPresent());
  }

  @Test
  public void testGetAssociationsForType() {
    assertTrue(testant.getAssociationsForType("Room").isPresent());
    assertEquals(4, testant.getAssociationsForType("Room").get().size());
    assertTrue(testant.getAssociationsForType("Employee").isPresent());
    assertEquals(3, testant.getAssociationsForType("Employee").get().size());
  }

  @Test
  public void testGetEsternalAssociations() {
    assertTrue(testant.getAssociationsWithExternalReferences().isPresent());
    assertEquals(0, testant.getAssociationsWithExternalReferences().get().size());
  }

  @Test
  public void testGetSuperClasses() {
    assertEquals(2, testant.getLocalSuperClasses("Professor").size());
  }

  @Test
  public void testGetSuperInterfaces() {
    assertEquals(1, testant.getLocalSuperInterfaces("Person").size());
    assertEquals(2, testant.getLocalImplementedInterfaces("Employee").size());
  }
}
