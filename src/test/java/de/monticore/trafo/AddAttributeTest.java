/* (c) https://github.com/MontiCore/monticore */
package de.monticore.trafo;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.literals.prettyprint.MCCommonLiteralsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.tf.AddAttribute;
import de.monticore.types.prettyprint.MCFullGenericTypesFullPrettyPrinter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddAttributeTest {

  @BeforeClass
  public static void init(){
    CD4CodeMill.init();
  }

  @Test
  public void testAddAttr() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/MoveAttrAB.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);
  
    assertTrue(ast.isPresent());
    
    AddAttribute addAttribute  = new AddAttribute(ast.get());
    assertTrue(addAttribute.doPatternMatching());
    
    addAttribute.doReplacement();
    
    assertEquals("boolean",ast.get().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(1).getMCType()
                .printType(new MCFullGenericTypesFullPrettyPrinter(new IndentPrinter())));
    IndentPrinter p = new IndentPrinter();
    ast.get().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().get(1).getInitial()
                .accept(new MCCommonLiteralsFullPrettyPrinter(p).getTraverser());
    assertEquals("true", p.getContent());
  }
  
}
