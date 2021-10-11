/* (c) https://github.com/MontiCore/monticore */
package de.monticore.trafo;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.tf.CreatingNested;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreatingTest {

  @BeforeClass
  public static void init(){
    CD4CodeMill.init();
  }

  @Test
  public void testCreatingTest() throws IOException {

    String input = "src/test/resources/de/monticore/trafo/CreatingTestCD.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);

    assertTrue(ast.isPresent());

    CreatingNested addAttribute  = new CreatingNested(ast.get());
    assertTrue(addAttribute.doPatternMatching());

    addAttribute.doReplacement();

    assertEquals(2, ast.get().getCDDefinition().getCDClassesList().size());
    ASTCDClass cdClass1 = ast.get().getCDDefinition().getCDClassesList().get(0);
    ASTCDClass cdClass2 = ast.get().getCDDefinition().getCDClassesList().get(1);
    assertEquals("MySuper", cdClass1.getName());
    assertEquals("MyClass", cdClass2.getName());
    assertEquals("MySuper", ((ASTMCQualifiedType)cdClass2.getSuperclassList().get(0)).getNameList().get(0));
    assertEquals(1, cdClass2.getCDAttributeList().size());
    assertEquals("varName", cdClass2.getCDAttributeList().get(0).getName());
    assertEquals("VarType", ((ASTMCQualifiedType)cdClass2.getCDAttributeList().get(0).getMCType()).getNameList().get(0));

  }
  
}
