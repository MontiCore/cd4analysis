/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactoringTests;

import static org.junit.Assert.assertEquals;

import de.monticore.cdbasis._ast.ASTCDClass;
import org.junit.Test;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdlib.refactorings.Remove;
import de.monticore.cdlib.utilities.FileUtility;

/**
 * Test method renaming classes
 * 
 * @author jiong
 *
 */
public class RemoveMethodTest {

  @Test
  public void testRemoveMethod() {
    FileUtility utility = new FileUtility("cdlib/RemoveMethodTest");
    Remove refactoring = new Remove();

    // Check input, namely there should be two overloading occurrences of
    // getUserName methods
    ASTCDClass classA = utility.getAst().getCDDefinition().getCDClassesList().get(0);
    assertEquals("A", classA.getName());
    assertEquals(3, classA.getCDMethodList().size());

    ASTCDMethod method1 = (ASTCDMethod)classA.getCDMethodList().get(0);
    assertEquals("getUserName", method1.getName());
    assertEquals(0, method1.getCDParameterList().size());

    ASTCDMethod method2 = (ASTCDMethod)classA.getCDMethodList().get(1);
    assertEquals("getUserName", method2.getName());
    assertEquals(1, method2.getCDParameterList().size());

    assertEquals("setUserName", ((ASTCDMethod)classA.getCDMethodList().get(2)).getName());

    // Remove methods
    refactoring.removeMethod("A", "getUserName", utility.getAst());

    // Check output, namely only the first overloading occurrence of getUserName
    // method should be removed
    assertEquals(2, classA.getCDMethodList().size());

    method1 = (ASTCDMethod) classA.getCDMethodList().get(0);
    assertEquals("getUserName", method1.getName());
    assertEquals(1, method1.getCDParameterList().size());

    assertEquals("setUserName", ((ASTCDMethod)classA.getCDMethodList().get(1)).getName());
  }
}
