/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.transformation;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for the utility class {@link ASTCDTransformation}
 *
 * @author  Galina Volkova
 *
 */
public class ASTTransformationTest {
  
  private ASTCDDefinition astDef;
  
  private ASTCDTransformation astTransformation;
  
  public ASTTransformationTest() {
    astTransformation = new ASTCDTransformation();
  }
  
  @Before
  public void init() {
    astDef = CD4AnalysisMill.cDDefinitionBuilder().setName("ASTTransformationTest").build();
  }
  
  @Test
  public void testAddCdClass() {
    assertTrue(astDef.getCDClassList().isEmpty());
    
    astTransformation.addCdClass(astDef, "A");
    assertEquals(astDef.getCDClassList().size(), 1);
    assertEquals(astDef.getCDClassList().get(0).getName(), "A");
    
    Optional<ASTCDClass> cdClass1 = astTransformation.addCdClass(astDef, "B", "superC", Lists.newArrayList("i1", "i2"));
    assertTrue(cdClass1.isPresent());
    assertEquals(cdClass1.get().getName(), "B");
    
    assertEquals(astDef.getCDClassList().size(), 2);
    ASTCDClass astClass = astDef.getCDClassList().get(1);
    assertEquals(astClass.getName(), "B");
    
    assertTrue(astClass.isPresentSuperclass());
    assertTrue(astClass.getSuperclass() instanceof ASTMCObjectType);
    ASTMCObjectType superClass = (ASTMCObjectType)astClass.getSuperclass();
    assertEquals(superClass.getNameList(), Lists.newArrayList("superC"));
    
    assertEquals(astClass.getInterfaceList().size(), 2);
    assertTrue(astClass.getInterfaceList().get(0) instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)astClass.getInterfaceList().get(0)).getNameList(), Lists.newArrayList("i1"));
    assertTrue(astClass.getInterfaceList().get(1) instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)astClass.getInterfaceList().get(1)).getNameList(), Lists.newArrayList("i2"));
  }
  
  @Test
  public void testAddCdInterface() {
    assertTrue(astDef.getCDInterfaceList().isEmpty());
    
    astTransformation.addCdInterface(astDef, "I1");
    assertEquals(astDef.getCDInterfaceList().size(), 1);
    assertEquals(astDef.getCDInterfaceList().get(0).getName(), "I1");
    
    astTransformation.addCdInterface(astDef, "I2", Lists.newArrayList("SuperI1", "SuperI2"));
    assertEquals(astDef.getCDInterfaceList().size(), 2);
    ASTCDInterface astInterface = astDef.getCDInterfaceList().get(1);
    assertEquals(astInterface.getName(), "I2");
    
    assertEquals(astInterface.getInterfaceList().size(), 2);
    assertTrue(astInterface.getInterfaceList().get(0) instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)astInterface.getInterfaceList().get(0)).getNameList(), Lists.newArrayList("SuperI1"));
    assertTrue(astInterface.getInterfaceList().get(1) instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)astInterface.getInterfaceList().get(1)).getNameList(), Lists.newArrayList("SuperI2"));
  }
  
  @Test
  public void testAddCdAttribute() {
    ASTCDClass astClass = astTransformation.addCdClass(astDef, "A");
    Optional<ASTCDAttribute> attr1 = astTransformation.addCdAttribute(astClass, "a", "String");
    assertTrue(attr1.isPresent());
    assertEquals(attr1.get().getName(), "a");
    assertTrue(attr1.get().getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)attr1.get().getMCType()).getNameList(), Lists.newArrayList("String"));
    assertTrue(!attr1.get().isPresentModifier());
    
    Optional<ASTCDAttribute> attr2 = astTransformation.addCdAttribute(astClass, "b", "a.b.C");
    assertTrue(attr2.isPresent());
    assertEquals(attr2.get().getName(), "b");
    assertTrue(attr2.get().getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)attr2.get().getMCType()).getNameList(), Lists.newArrayList("a", "b", "C"));
    assertTrue(!attr2.get().isPresentModifier());
    
    Optional<ASTCDAttribute> attr3 = astTransformation.addCdAttribute(astClass, "c", "List<String>", "private static");
    assertTrue(attr3.isPresent());
    assertEquals(attr3.get().getName(), "c");
    assertTrue(attr3.get().getMCType() instanceof ASTMCGenericType);
    ASTMCGenericType attrType = (ASTMCGenericType)attr3.get().getMCType();
    assertEquals(attrType.getNameList(), Lists.newArrayList("List"));
    assertEquals(attrType.getMCTypeArgumentList().size(), 1);
    assertTrue(attrType.getMCTypeArgumentList().get(0) instanceof ASTMCBasicTypeArgument);
    assertEquals(((ASTMCBasicTypeArgument)attrType.getMCTypeArgumentList().get(0)).getMCQualifiedType().getNameList(), Lists.newArrayList("String"));
    assertTrue(attr3.get().isPresentModifier());
    assertTrue(attr3.get().getModifier().isPrivate());
    assertTrue(attr3.get().getModifier().isStatic());
    assertTrue(!attr3.get().getModifier().isPublic());
    assertTrue(!attr3.get().getModifier().isFinal());
  }
  
  @Test
  @Ignore("TODO GV<-RH source position optional funktioniert hier nicht")
  public void testAddCdAttributeUsingDefinition() {
    ASTCDClass astClass = astTransformation.addCdClass(astDef, "A");
    Optional<ASTCDAttribute> attr1 = astTransformation.addCdAttributeUsingDefinition(astClass, "String a;");
    assertTrue(attr1.isPresent());
    assertEquals(attr1.get().getName(), "a");
    assertTrue(attr1.get().getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)attr1.get().getMCType()).getNameList(), Lists.newArrayList("String"));
    
    Optional<ASTCDAttribute> attr2 = astTransformation.addCdAttributeUsingDefinition(astClass, "protected a.b.C b;");
    assertTrue(attr2.isPresent());
    assertEquals(attr2.get().getName(), "b");
    assertTrue(attr2.get().isPresentModifier());
    assertTrue(attr2.get().getModifier().isProtected());
    assertTrue(attr2.get().getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)attr2.get().getMCType()).getNameList(), Lists.newArrayList("a", "b", "C"));
    
    Optional<ASTCDAttribute> attr3 = astTransformation.addCdAttributeUsingDefinition(astClass, "+Date d;");
    assertTrue(attr3.isPresent());
    assertEquals(attr3.get().getName(), "d");
    assertTrue(attr3.get().isPresentModifier());
    assertTrue(attr3.get().getModifier().isPublic());
    assertTrue(attr3.get().getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)attr3.get().getMCType()).getNameList(), Lists.newArrayList("Date"));
  }
  
  @Test
  public void testAddCdMethod() {
    ASTCDClass astClass = astTransformation.addCdClass(astDef, "A");
    ASTCDMethod method1 = astTransformation.addCdMethod(astClass, "test1");
    assertTrue(method1 != null);
    assertEquals(method1.getName(), "test1");
    assertTrue(method1.getMCReturnType().isPresentMCVoidType());
    
    Optional<ASTCDMethod> method2 = astTransformation.addCdMethod(astClass, "test2", "Integer", "protected static final", Lists.newArrayList("A", "a.b.C", "List<String>"));
    assertTrue(method2.isPresent());
    assertEquals(method2.get().getName(), "test2");
    assertTrue(method2.get().getMCReturnType().isPresentMCType());
    assertEquals(((ASTMCType)method2.get().getMCReturnType().getMCType()).getNameList(), Lists.newArrayList("Integer"));
    assertEquals(method2.get().getCDParameterList().size(), 3);
    assertEquals(method2.get().getCDParameterList().get(0).getName(), "param0");
    assertTrue(method2.get().getCDParameterList().get(0).getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)method2.get().getCDParameterList().get(0).getMCType()).getNameList(), Lists.newArrayList("A"));
    assertEquals(method2.get().getCDParameterList().get(1).getName(), "param1");
    assertTrue(method2.get().getCDParameterList().get(1).getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)method2.get().getCDParameterList().get(1).getMCType()).getNameList(), Lists.newArrayList("a", "b", "C"));
    assertEquals(method2.get().getCDParameterList().get(2).getName(), "param2");
    assertTrue(method2.get().getCDParameterList().get(2).getMCType() instanceof ASTMCGenericType);
    ASTMCGenericType param2Type = (ASTMCGenericType)method2.get().getCDParameterList().get(2).getMCType();
    assertEquals(param2Type.getNameList(), Lists.newArrayList("List"));
    assertEquals(param2Type.getMCTypeArgumentList().size(), 1);
    assertTrue(param2Type.getMCTypeArgumentList().get(0) instanceof ASTMCBasicTypeArgument);
    assertEquals(((ASTMCBasicTypeArgument)param2Type.getMCTypeArgumentList().get(0)).getMCQualifiedType().getNameList(), Lists.newArrayList("String"));
    assertTrue(method2.get().getModifier() != null);
    assertTrue(method2.get().getModifier().isProtected());
    assertTrue(!method2.get().getModifier().isPublic());
    assertTrue(method2.get().getModifier().isFinal());
    assertTrue(method2.get().getModifier().isStatic());
  }
  
  @Test
  public void testAddCdMethodUsingDefinition() {
    ASTCDClass astClass = astTransformation.addCdClass(astDef, "A");
    Optional<ASTCDMethod> method1 = astTransformation.addCdMethodUsingDefinition(astClass, "public void test1();");
    assertTrue(method1.isPresent());
    assertEquals(method1.get().getName(), "test1");
    assertTrue(method1.get().getMCReturnType().isPresentMCVoidType());
    assertTrue(method1.get().getModifier() != null);
    assertTrue(method1.get().getModifier().isPublic());
    
    Optional<ASTCDMethod> method2 = astTransformation.addCdMethodUsingDefinition(astClass, "protected static final Integer test2(A param0, a.b.C param1, List<String> param2);");
    assertTrue(method2.isPresent());
    assertEquals(method2.get().getName(), "test2");
    assertTrue(method2.get().getMCReturnType().isPresentMCType());
    assertEquals(method2.get().getMCReturnType().getMCType().getNameList(), Lists.newArrayList("Integer"));
    assertTrue(method2.get().getModifier() != null);
    assertTrue(method2.get().getModifier().isProtected());
    assertTrue(!method2.get().getModifier().isPublic());
    assertTrue(method2.get().getModifier().isStatic());
    assertTrue(method2.get().getModifier().isFinal());
    assertEquals(method2.get().getCDParameterList().size(), 3);
    assertEquals(method2.get().getCDParameterList().get(0).getName(), "param0");
    assertTrue(method2.get().getCDParameterList().get(0).getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)method2.get().getCDParameterList().get(0).getMCType()).getNameList(), Lists.newArrayList("A"));
    assertEquals(method2.get().getCDParameterList().get(1).getName(), "param1");
    assertTrue(method2.get().getCDParameterList().get(1).getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)method2.get().getCDParameterList().get(1).getMCType()).getNameList(), Lists.newArrayList("a", "b", "C"));
    assertEquals(method2.get().getCDParameterList().get(2).getName(), "param2");
    assertTrue(method2.get().getCDParameterList().get(2).getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)method2.get().getCDParameterList().get(2).getMCType()).getNameList(), Lists.newArrayList("List"));
    
    Optional<ASTCDMethod> method3 = astTransformation.addCdMethodUsingDefinition(astClass, "protected Date foo(String a, int b);");
    assertTrue(method3.isPresent());
    assertEquals(method3.get().getName(), "foo");
    assertTrue(method3.get().getMCReturnType().isPresentMCType());
    assertEquals(method3.get().getMCReturnType().getMCType().getNameList(), Lists.newArrayList("Date"));
    assertTrue(method3.get().getModifier() != null);
    assertTrue(method3.get().getModifier().isProtected());
    assertEquals(method3.get().getCDParameterList().size(), 2);
    assertEquals(method3.get().getCDParameterList().get(0).getName(), "a");
    assertTrue(method3.get().getCDParameterList().get(0).getMCType() instanceof ASTMCObjectType);
    assertEquals(((ASTMCObjectType)method3.get().getCDParameterList().get(0).getMCType()).getNameList(), Lists.newArrayList("String"));
    assertEquals(method3.get().getCDParameterList().get(1).getName(), "b");
    assertTrue(method3.get().getCDParameterList().get(1).getMCType() instanceof ASTMCPrimitiveType);
    assertEquals(((ASTMCPrimitiveType)method3.get().getCDParameterList().get(1).getMCType()).getPrimitive(), ASTConstantsMCBasicTypes.INT);
  }
  
}
