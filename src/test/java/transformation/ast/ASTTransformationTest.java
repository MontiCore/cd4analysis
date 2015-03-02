/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package transformation.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import de.cd4analysis._ast.ASTCDAttribute;
import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDDefinition;
import de.cd4analysis._ast.ASTCDInterface;
import de.cd4analysis._ast.ASTCDMethod;
import de.monticore.types._ast.ASTSimpleReferenceType;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class ASTTransformationTest {
  
  private ASTCDDefinition astDef;
  
  @Before
  public void init() {
    astDef = ASTCDDefinition.getBuilder().name("ASTTransformationTest").build();
  }
  
  @Test
  public void testAddCdClass() {
    assertTrue(astDef.getCDClasses().isEmpty());
    
    ASTCDTransformation.addCdClass(astDef, "A");
    assertEquals(astDef.getCDClasses().size(), 1);
    assertEquals(astDef.getCDClasses().get(0).getName(), "A");
    
    ASTCDTransformation.addCdClass(astDef, "B", "superC", Lists.newArrayList("i1", "i2"));
    assertEquals(astDef.getCDClasses().size(), 2);
    ASTCDClass astClass = astDef.getCDClasses().get(1);
    assertEquals(astClass.getName(), "B");
    
    assertTrue(astClass.getSuperclass().isPresent());
    assertTrue(astClass.getSuperclass().get() instanceof ASTSimpleReferenceType);
    ASTSimpleReferenceType superClass = (ASTSimpleReferenceType)astClass.getSuperclass().get();
    assertEquals(superClass.getName(), Lists.newArrayList("superC"));
    
    assertEquals(astClass.getInterfaces().size(), 2);
    assertTrue(astClass.getInterfaces().get(0) instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)astClass.getInterfaces().get(0)).getName(), Lists.newArrayList("i1"));
    assertTrue(astClass.getInterfaces().get(1) instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)astClass.getInterfaces().get(1)).getName(), Lists.newArrayList("i2"));
  }
  
  @Test
  public void testAddCdInterface() {
    assertTrue(astDef.getCDInterfaces().isEmpty());
    
    ASTCDTransformation.addCdInterface(astDef, "I1");
    assertEquals(astDef.getCDInterfaces().size(), 1);
    assertEquals(astDef.getCDInterfaces().get(0).getName(), "I1");
    
    ASTCDTransformation.addCdInterface(astDef, "I2", Lists.newArrayList("SuperI1", "SuperI2"));
    assertEquals(astDef.getCDInterfaces().size(), 2);
    ASTCDInterface astInterface = astDef.getCDInterfaces().get(1);
    assertEquals(astInterface.getName(), "I2");
    
    assertEquals(astInterface.getInterfaces().size(), 2);
    assertTrue(astInterface.getInterfaces().get(0) instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)astInterface.getInterfaces().get(0)).getName(), Lists.newArrayList("SuperI1"));
    assertTrue(astInterface.getInterfaces().get(1) instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)astInterface.getInterfaces().get(1)).getName(), Lists.newArrayList("SuperI2"));
  }
  
  @Test
  public void testAddCdAttribute() {
    ASTCDClass astClass = ASTCDTransformation.addCdClass(astDef, "A");
    ASTCDAttribute attr1 = ASTCDTransformation.addCdAttribute(astClass, "a", "String");
    assertTrue(attr1 != null);
    assertEquals(attr1.getName(), "a");
    assertTrue(attr1.getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)attr1.getType()).getName(), Lists.newArrayList("String"));
    
    ASTCDAttribute attr2 = ASTCDTransformation.addCdAttribute(astClass, "b", "a.b.C");
    assertTrue(attr2 != null);
    assertEquals(attr2.getName(), "b");
    assertTrue(attr2.getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)attr2.getType()).getName(), Lists.newArrayList("a", "b", "C"));
  }
  
  @Test
  public void testAddCdAttributeUsingDefinition() {
    ASTCDClass astClass = ASTCDTransformation.addCdClass(astDef, "A");
    Optional<ASTCDAttribute> attr1 = ASTCDTransformation.addCdAttributeUsingDefinition(astClass, "String a;");
    assertTrue(attr1.isPresent());
    assertEquals(attr1.get().getName(), "a");
    assertTrue(attr1.get().getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)attr1.get().getType()).getName(), Lists.newArrayList("String"));
    
    Optional<ASTCDAttribute> attr2 = ASTCDTransformation.addCdAttributeUsingDefinition(astClass, "protected a.b.C b;");
    assertTrue(attr2.isPresent());
    assertEquals(attr2.get().getName(), "b");
    assertTrue(attr2.get().getModifier().isPresent());
    assertTrue(attr2.get().getModifier().get().isProtected());
    assertTrue(attr2.get().getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)attr2.get().getType()).getName(), Lists.newArrayList("a", "b", "C"));
  }
  
  @Test
  public void testAddCdMethod() {
    ASTCDClass astClass = ASTCDTransformation.addCdClass(astDef, "A");
    ASTCDMethod method1 = ASTCDTransformation.addCdMethod(astClass, "test1");
    assertTrue(method1 != null);
    assertEquals(method1.getName(), "test1");
    assertTrue(method1.getReturnType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method1.getReturnType()).getName(), Lists.newArrayList("void"));
    
    ASTCDMethod method2 = ASTCDTransformation.addCdMethod(astClass, "test2", "Integer", Lists.newArrayList("A", "a.b.C", "List<String>"));
    assertTrue(method2 != null);
    assertEquals(method2.getName(), "test2");
    assertTrue(method2.getReturnType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.getReturnType()).getName(), Lists.newArrayList("Integer"));
    assertEquals(method2.getCDParameters().size(), 3);
    assertEquals(method2.getCDParameters().get(0).getName(), "param0");
    assertTrue(method2.getCDParameters().get(0).getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.getCDParameters().get(0).getType()).getName(), Lists.newArrayList("A"));
    assertEquals(method2.getCDParameters().get(1).getName(), "param1");
    assertTrue(method2.getCDParameters().get(1).getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.getCDParameters().get(1).getType()).getName(), Lists.newArrayList("a.b.C"));
    assertEquals(method2.getCDParameters().get(2).getName(), "param2");
    assertTrue(method2.getCDParameters().get(2).getType() instanceof ASTSimpleReferenceType);
    assertEquals(((ASTSimpleReferenceType)method2.getCDParameters().get(2).getType()).getName(), Lists.newArrayList("List<String>"));
  }
  
}
