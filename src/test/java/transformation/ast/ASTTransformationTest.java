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

import com.google.common.collect.Lists;

import de.cd4analysis._ast.ASTCDClass;
import de.cd4analysis._ast.ASTCDDefinition;
import de.cd4analysis._ast.ASTCDInterface;
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
  
}
