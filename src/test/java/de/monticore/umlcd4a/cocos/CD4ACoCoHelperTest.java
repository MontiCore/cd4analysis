/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import de.monticore.types._ast.TypesNodeFactory;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._ast.CD4AnalysisNodeFactory;

/**
 * Test for {@link CD4ACoCoHelper}.
 *
 * @author Robert Heim
 */
public class CD4ACoCoHelperTest {
  @Test
  public void testAssocDirections() {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    assoc.setName("expectedName");
    assoc.setLeftReferenceName(TypesNodeFactory.createASTQualifiedName(Arrays.asList("left",
        "TypeName")));
    assoc.setRightReferenceName(TypesNodeFactory.createASTQualifiedName(Arrays.asList("right",
        "TypeName")));
    
    assoc.setLeftToRight(true);
    assertEquals("expectedName (left.TypeName -> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    assoc.setLeftToRight(false);
    
    assoc.setBidirectional(true);
    assertEquals("expectedName (left.TypeName <-> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    assoc.setBidirectional(false);
    
    assoc.setRightToLeft(true);
    assertEquals("expectedName (left.TypeName <- right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    assoc.setRightToLeft(false);
    
    assoc.setUnspecified(true);
    assertEquals("expectedName (left.TypeName -- right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    assoc.setUnspecified(false);
    
  }
  
  @Test
  public void testAssocRoles() {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    assoc.setName("expectedName");
    assoc.setLeftReferenceName(TypesNodeFactory.createASTQualifiedName(Arrays.asList("left",
        "TypeName")));
    assoc.setBidirectional(true);
    assoc.setRightReferenceName(TypesNodeFactory.createASTQualifiedName(Arrays.asList("right",
        "TypeName")));
    
    assoc.setLeftRole("leftRole");
    assertEquals("expectedName (left.TypeName (leftRole) <-> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    
    assoc.setRightRole("rightRole");
    assertEquals("expectedName (left.TypeName (leftRole) <-> (rightRole) right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    
    assoc.setLeftRole(null);
    assertEquals("expectedName (left.TypeName <-> (rightRole) right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
  }
  
  @Test
  public void testAssocName() {
    ASTCDAssociation assoc = CD4AnalysisNodeFactory.createASTCDAssociation();
    assoc.setLeftReferenceName(TypesNodeFactory.createASTQualifiedName(Arrays.asList("left",
        "TypeName")));
    assoc.setBidirectional(true);
    assoc.setRightReferenceName(TypesNodeFactory.createASTQualifiedName(Arrays.asList("right",
        "TypeName")));
    
    assertEquals("(left.TypeName <-> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
    
    assoc.setName("expectedName");
    assertEquals("expectedName (left.TypeName <-> right.TypeName)",
        CD4ACoCoHelper.printAssociation(assoc));
  }
}
