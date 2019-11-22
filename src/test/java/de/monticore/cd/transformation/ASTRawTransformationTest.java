/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.transformation;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for the utility class {@link ASTCDRawTransformation}
 *
 * @author Galina Volkova
 */
public class ASTRawTransformationTest {

  private ASTCDDefinition astDef;

  private ASTCDRawTransformation astTransformation;

  public ASTRawTransformationTest() {
    astTransformation = new ASTCDRawTransformation();
  }

  @Before
  public void init() {
    astDef = CD4AnalysisMill.cDDefinitionBuilder().setName("ASTRawTransformationTest")
        .build();
  }

  @Test
  public void testAddCdClass() {
    assertTrue(astDef.getCDClassList().isEmpty());

    astTransformation.addCdClass(astDef, "A");
    assertEquals(astDef.getCDClassList().size(), 1);
    assertEquals(astDef.getCDClassList().get(0).getName(), "A");

    ASTCDClass cdClass1 = astTransformation.addCdClass(astDef, "B",
        "superC", Lists.newArrayList("i1", "i2"));
    assertNotNull(cdClass1);
    assertEquals(cdClass1.getName(), "B");

    assertEquals(astDef.getCDClassList().size(), 2);
    ASTCDClass astClass = astDef.getCDClassList().get(1);
    assertEquals(astClass.getName(), "B");

    assertTrue(astClass.isPresentSuperclass());
    assertTrue(astClass.getSuperclass() instanceof ASTMCObjectType);
    ASTMCObjectType superClass = (ASTMCObjectType) astClass
        .getSuperclass();
    assertEquals(superClass.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())), "superC");

    assertEquals(astClass.getInterfaceList().size(), 2);
    assertTrue(astClass.getInterfaceList().get(0) instanceof ASTMCObjectType);
    assertEquals((astClass.getInterfaceList().get(0)).printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())), "i1");
    assertTrue(astClass.getInterfaceList().get(1) instanceof ASTMCObjectType);
    assertEquals((astClass.getInterfaceList().get(1)).printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())), "i2");
  }

  @Test
  public void testAddCdInterface() {
    assertTrue(astDef.getCDInterfaceList().isEmpty());

    astTransformation.addCdInterface(astDef, "I1");
    assertEquals(astDef.getCDInterfaceList().size(), 1);
    assertEquals(astDef.getCDInterfaceList().get(0).getName(), "I1");

    astTransformation.addCdInterface(astDef, "I2",
        Lists.newArrayList("SuperI1", "SuperI2"));
    assertEquals(astDef.getCDInterfaceList().size(), 2);
    ASTCDInterface astInterface = astDef.getCDInterfaceList().get(1);
    assertEquals(astInterface.getName(), "I2");

    assertEquals(astInterface.getInterfaceList().size(), 2);
    assertTrue(astInterface.getInterfaceList().get(0) instanceof ASTMCObjectType);
    assertEquals((astInterface.getInterfaceList()
        .get(0)).printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())), "SuperI1");
    assertTrue(astInterface.getInterfaceList().get(1) instanceof ASTMCObjectType);
    assertEquals((astInterface.getInterfaceList().get(1)).printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())), "SuperI2");
  }

  @Test
  public void testAddCdAttribute() {
    ASTCDClass astClass = astTransformation.addCdClass(astDef, "A");
    ASTCDAttribute attr1 = astTransformation.addCdAttribute(astClass, "a",
        "String");
    assertNotNull(attr1);
    assertEquals(attr1.getName(), "a");
    assertTrue(attr1.getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType) attr1.getMCType()).printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())), "String");
    assertTrue(!attr1.isPresentModifier());

    ASTCDAttribute attr2 = astTransformation.addCdAttribute(astClass, "b",
        "a.b.C");
    assertNotNull(attr2);
    assertEquals(attr2.getName(), "b");
    assertTrue(attr2.getMCType() instanceof ASTMCObjectType);
    assertEquals((attr2.getMCType()).printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())), "a.b.C");
    assertTrue(!attr2.isPresentModifier());

  }

  @Test
  public void testAddCdMethod() {
    ASTCDClass astClass = astTransformation.addCdClass(astDef, "A");
    ASTCDMethod method1 = astTransformation.addCdMethod(astClass, "test1");
    assertTrue(method1 != null);
    assertEquals(method1.getName(), "test1");
    assertTrue(method1.getMCReturnType().isPresentMCVoidType());

    ASTCDMethod method2 = astTransformation.addCdMethod(astClass, "test2",
        "Integer", Lists.newArrayList("A", "a.b.C", "String"));
    assertNotNull(method2);
    assertEquals(method2.getName(), "test2");
    assertTrue(method2.getMCReturnType().isPresentMCType());
    assertEquals((method2.getMCReturnType().getMCType()).printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())), "Integer");
    assertEquals(method2.getCDParameterList().size(), 3);
    assertEquals(method2.getCDParameterList().get(0).getName(), "param0");
    assertTrue(method2.getCDParameterList().get(0).getMCType() instanceof ASTMCObjectType);
    assertEquals((method2.getCDParameterList().get(0).getMCType()).printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())), "A");
    assertEquals(method2.getCDParameterList().get(1).getName(), "param1");
    assertTrue(method2.getCDParameterList().get(1).getMCType() instanceof ASTMCObjectType);
    assertEquals((method2.getCDParameterList().get(1).getMCType()).printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())),"a.b.C");
    assertEquals(method2.getCDParameterList().get(2).getName(), "param2");
    assertTrue(method2.getCDParameterList().get(2).getMCType() instanceof ASTMCObjectType);
    ASTMCObjectType param2Type = (ASTMCObjectType) method2.getCDParameterList().get(2).getMCType();
    assertEquals(param2Type.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())), "String");
    assertNotNull(method2.getModifier());
    assertTrue(method2.getModifier().isPublic());
  }

}
