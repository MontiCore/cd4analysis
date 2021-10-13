/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import static org.junit.Assert.assertEquals;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import org.junit.BeforeClass;
import org.junit.Test;


import de.monticore.cdlib.refactorings.Remove;
import de.monticore.cdlib.utilities.FileUtility;

/**
 * Test remove Attribute
 * 
 * @author Ahmed Diab
 *
 */
public class RemoveAttributeTest {

  @BeforeClass
  public static void init(){
    CD4CodeMill.init();
  }

  @Test
  public void testRemoveAttribute() {
    FileUtility utility = new FileUtility("cdlib/RemoveAttribute");
    Remove refactoring = new Remove();
    MCBasicTypesFullPrettyPrinter btfpp = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
     
    // Check input, namely there should be two overloading occurrences of
    // getUserName attribute
    ASTCDClass classA = utility.getAst().getCDDefinition().getCDClassesList().get(0);
    assertEquals("A", classA.getName());
    assertEquals(3, classA.getCDAttributeList().size());

    ASTCDAttribute attributeFirst = classA.getCDAttributeList().get(0);
    assertEquals("a", attributeFirst.getName());
    assertEquals("int", attributeFirst.getMCType().printType(btfpp));
    assertEquals(true, attributeFirst.getModifier().isPublic());
    
    ASTCDAttribute attributeSecond = classA.getCDAttributeList().get(1);
    assertEquals("b", attributeSecond.getName());
    assertEquals("String", attributeSecond.getMCType().printType(btfpp));
    assertEquals(true, attributeSecond.getModifier().isPrivate());
    
    ASTCDAttribute attributeThird = classA.getCDAttributeList().get(2);
    assertEquals("c", attributeThird.getName());
    assertEquals("int", attributeThird.getMCType().printType(btfpp));

    //remove an attribute
    refactoring.removeAttribute("A", "b", utility.getAst());
   
    //Check output, namely only the second attribute should be removed
    assertEquals(2, classA.getCDAttributeList().size());
    
    attributeFirst = classA.getCDAttributeList().get(0);
    assertEquals("a", attributeFirst.getName());
    assertEquals("int", attributeFirst.getMCType().printType(btfpp));
    assertEquals(true, attributeFirst.getModifier().isPublic());

    attributeSecond = classA.getCDAttributeList().get(1);
    assertEquals("c", attributeThird.getName());
    assertEquals("int", attributeThird.getMCType().printType(btfpp));
  
  }
}
