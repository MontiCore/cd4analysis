/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.transformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

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
    assertTrue(astClass.getSuperclass() instanceof ASTSimpleReferenceType);
    ASTSimpleReferenceType superClass = (ASTSimpleReferenceType)astClass.getSuperclass();
    assertEquals(superClass.getNameList(), Lists.newArrayList("superC"));
    
    assertEquals(astClass.getInterfaceList().size(), 2);
    assertTrue(astClass.getInterfaceList().get(0) instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)astClass.getInterfaceList().get(0)).getNameList(), Lists.newArrayList("i1"));
    assertTrue(astClass.getInterfaceList().get(1) instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)astClass.getInterfaceList().get(1)).getNameList(), Lists.newArrayList("i2"));
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
    assertTrue(astInterface.getInterfaceList().get(0) instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)astInterface.getInterfaceList().get(0)).getNameList(), Lists.newArrayList("SuperI1"));
    assertTrue(astInterface.getInterfaceList().get(1) instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)astInterface.getInterfaceList().get(1)).getNameList(), Lists.newArrayList("SuperI2"));
  }
  
  @Test
  public void testAddCdAttribute() {
    ASTCDClass astClass = astTransformation.addCdClass(astDef, "A");
    Optional<ASTCDAttribute> attr1 = astTransformation.addCdAttribute(astClass, "a", "String");
    assertTrue(attr1.isPresent());
    assertEquals(attr1.get().getName(), "a");
    assertTrue(attr1.get().getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)attr1.get().getType()).getNameList(), Lists.newArrayList("String"));
    assertTrue(!attr1.get().isPresentModifier());
    
    Optional<ASTCDAttribute> attr2 = astTransformation.addCdAttribute(astClass, "b", "a.b.C");
    assertTrue(attr2.isPresent());
    assertEquals(attr2.get().getName(), "b");
    assertTrue(attr2.get().getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)attr2.get().getType()).getNameList(), Lists.newArrayList("a", "b", "C"));
    assertTrue(!attr2.get().isPresentModifier());
    
    Optional<ASTCDAttribute> attr3 = astTransformation.addCdAttribute(astClass, "c", "List<String>", "private static");
    assertTrue(attr3.isPresent());
    assertEquals(attr3.get().getName(), "c");
    assertTrue(attr3.get().getType() instanceof ASTSimpleReferenceType);
    ASTSimpleReferenceType attrType = (ASTSimpleReferenceType)attr3.get().getType();
    assertEquals(attrType.getNameList(), Lists.newArrayList("List"));
    assertTrue(attrType.isPresentTypeArguments());
    assertEquals(attrType.getTypeArguments().getTypeArgumentList().size(), 1);
    assertTrue(attrType.getTypeArguments().getTypeArgumentList().get(0) instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)attrType.getTypeArguments().getTypeArgumentList().get(0)).getNameList(), Lists.newArrayList("String"));
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
    assertTrue(attr1.get().getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)attr1.get().getType()).getNameList(), Lists.newArrayList("String"));
    
    Optional<ASTCDAttribute> attr2 = astTransformation.addCdAttributeUsingDefinition(astClass, "protected a.b.C b;");
    assertTrue(attr2.isPresent());
    assertEquals(attr2.get().getName(), "b");
    assertTrue(attr2.get().isPresentModifier());
    assertTrue(attr2.get().getModifier().isProtected());
    assertTrue(attr2.get().getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)attr2.get().getType()).getNameList(), Lists.newArrayList("a", "b", "C"));
    
    Optional<ASTCDAttribute> attr3 = astTransformation.addCdAttributeUsingDefinition(astClass, "+Date d;");
    assertTrue(attr3.isPresent());
    assertEquals(attr3.get().getName(), "d");
    assertTrue(attr3.get().isPresentModifier());
    assertTrue(attr3.get().getModifier().isPublic());
    assertTrue(attr3.get().getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)attr3.get().getType()).getNameList(), Lists.newArrayList("Date"));
  }
  
  @Test
  public void testAddCdMethod() {
    ASTCDClass astClass = astTransformation.addCdClass(astDef, "A");
    ASTCDMethod method1 = astTransformation.addCdMethod(astClass, "test1");
    assertTrue(method1 != null);
    assertEquals(method1.getName(), "test1");
    assertTrue(method1.getReturnType() instanceof ASTVoidType);
    
    Optional<ASTCDMethod> method2 = astTransformation.addCdMethod(astClass, "test2", "Integer", "protected static final", Lists.newArrayList("A", "a.b.C", "List<String>"));
    assertTrue(method2.isPresent());
    assertEquals(method2.get().getName(), "test2");
    assertTrue(method2.get().getReturnType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.get().getReturnType()).getNameList(), Lists.newArrayList("Integer"));
    assertEquals(method2.get().getCDParameterList().size(), 3);
    assertEquals(method2.get().getCDParameterList().get(0).getName(), "param0");
    assertTrue(method2.get().getCDParameterList().get(0).getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.get().getCDParameterList().get(0).getType()).getNameList(), Lists.newArrayList("A"));
    assertEquals(method2.get().getCDParameterList().get(1).getName(), "param1");
    assertTrue(method2.get().getCDParameterList().get(1).getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.get().getCDParameterList().get(1).getType()).getNameList(), Lists.newArrayList("a", "b", "C"));
    assertEquals(method2.get().getCDParameterList().get(2).getName(), "param2");
    assertTrue(method2.get().getCDParameterList().get(2).getType() instanceof ASTSimpleReferenceType);
    ASTSimpleReferenceType param2Type = (ASTSimpleReferenceType)method2.get().getCDParameterList().get(2).getType();
    assertEquals(param2Type.getNameList(), Lists.newArrayList("List"));
    assertTrue(param2Type.isPresentTypeArguments());
    assertEquals(param2Type.getTypeArguments().getTypeArgumentList().size(), 1);
    assertTrue(param2Type.getTypeArguments().getTypeArgumentList().get(0) instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)param2Type.getTypeArguments().getTypeArgumentList().get(0)).getNameList(), Lists.newArrayList("String"));
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
    assertTrue(method1.get().getReturnType() instanceof ASTVoidType);
    assertTrue(method1.get().getModifier() != null);
    assertTrue(method1.get().getModifier().isPublic());
    
    Optional<ASTCDMethod> method2 = astTransformation.addCdMethodUsingDefinition(astClass, "protected static final Integer test2(A param0, a.b.C param1, List<String> param2);");
    assertTrue(method2.isPresent());
    assertEquals(method2.get().getName(), "test2");
    assertTrue(method2.get().getReturnType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.get().getReturnType()).getNameList(), Lists.newArrayList("Integer"));
    assertTrue(method2.get().getModifier() != null);
    assertTrue(method2.get().getModifier().isProtected());
    assertTrue(!method2.get().getModifier().isPublic());
    assertTrue(method2.get().getModifier().isStatic());
    assertTrue(method2.get().getModifier().isFinal());
    assertEquals(method2.get().getCDParameterList().size(), 3);
    assertEquals(method2.get().getCDParameterList().get(0).getName(), "param0");
    assertTrue(method2.get().getCDParameterList().get(0).getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.get().getCDParameterList().get(0).getType()).getNameList(), Lists.newArrayList("A"));
    assertEquals(method2.get().getCDParameterList().get(1).getName(), "param1");
    assertTrue(method2.get().getCDParameterList().get(1).getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.get().getCDParameterList().get(1).getType()).getNameList(), Lists.newArrayList("a", "b", "C"));
    assertEquals(method2.get().getCDParameterList().get(2).getName(), "param2");
    assertTrue(method2.get().getCDParameterList().get(2).getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.get().getCDParameterList().get(2).getType()).getNameList(), Lists.newArrayList("List"));
    
    Optional<ASTCDMethod> method3 = astTransformation.addCdMethodUsingDefinition(astClass, "protected Date foo(String a, int b);");
    assertTrue(method3.isPresent());
    assertEquals(method3.get().getName(), "foo");
    assertTrue(method3.get().getReturnType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method3.get().getReturnType()).getNameList(), Lists.newArrayList("Date"));
    assertTrue(method3.get().getModifier() != null);
    assertTrue(method3.get().getModifier().isProtected());
    assertEquals(method3.get().getCDParameterList().size(), 2);
    assertEquals(method3.get().getCDParameterList().get(0).getName(), "a");
    assertTrue(method3.get().getCDParameterList().get(0).getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method3.get().getCDParameterList().get(0).getType()).getNameList(), Lists.newArrayList("String"));
    assertEquals(method3.get().getCDParameterList().get(1).getName(), "b");
    assertTrue(method3.get().getCDParameterList().get(1).getType() instanceof ASTPrimitiveType);
    assertEquals(((ASTPrimitiveType)method3.get().getCDParameterList().get(1).getType()).getPrimitive(), ASTConstantsTypes.INT);
  }
  
}
