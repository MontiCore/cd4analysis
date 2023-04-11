/* (c) https://github.com/MontiCore/monticore */
package de.monticore.trafo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.tf.AddAttribute;
import java.io.IOException;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;

public class AddAttributeTest {

  @BeforeClass
  public static void init() {
    CD4CodeMill.init();
  }

  @Test
  public void testAddAttr() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/MoveAttrAB.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);

    assertTrue(ast.isPresent());

    AddAttribute addAttribute = new AddAttribute(ast.get());
    assertTrue(addAttribute.doPatternMatching());

    addAttribute.doReplacement();

    assertEquals(
        "boolean",
        ast.get()
            .getCDDefinition()
            .getCDClassesList()
            .get(0)
            .getCDAttributeList()
            .get(1)
            .getMCType()
            .printType());
    String pp =
        MCCommonLiteralsMill.prettyPrint(
            ast.get()
                .getCDDefinition()
                .getCDClassesList()
                .get(0)
                .getCDAttributeList()
                .get(1)
                .getInitial(),
            true);
    assertEquals("true", pp);
  }
}
