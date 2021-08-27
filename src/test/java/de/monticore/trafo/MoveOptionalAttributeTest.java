/* (c) https://github.com/MontiCore/monticore */
package de.monticore.trafo;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.tf.MoveOptionalAttribute;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by
 *
 * @author KH
 */
public class MoveOptionalAttributeTest {

  @BeforeClass
  public static void init(){
    CD4CodeMill.init();
  }

  @Test
  public void testBothFound() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/MoveAttrAB.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);

    assertTrue(ast.isPresent());

    MoveOptionalAttribute moveAttr = new MoveOptionalAttribute(ast.get());

    assertTrue(moveAttr.doPatternMatching());
    moveAttr.doReplacement();

    assertEquals(ast.get().getCDDefinition().getCDClassesList().size(), 2);
    assertEquals(ast.get().getCDDefinition().getCDClassesList().get(0).getName(), "A");
    assertEquals(ast.get().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size(), 0);
    assertEquals(ast.get().getCDDefinition().getCDClassesList().get(1).getName(), "B");
    assertEquals(ast.get().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().size(), 1);
    assertEquals(ast.get().getCDDefinition().getCDClassesList().get(1).getCDAttributeList().get(0).getName(), "foo");
  }

  @Test
  public void testOnlyAFound() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/MoveAttrA.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);

    assertTrue(ast.isPresent());

    MoveOptionalAttribute moveAttr = new MoveOptionalAttribute(ast.get());

    assertTrue(moveAttr.doPatternMatching());
    moveAttr.doReplacement();

    assertEquals(ast.get().getCDDefinition().getCDClassesList().size(), 1);
    assertEquals(ast.get().getCDDefinition().getCDClassesList().get(0).getName(), "A");
    assertEquals(ast.get().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size(), 0);
  }

  @Test
  public void testOnlyBFound() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/MoveAttrB.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);

    assertTrue(ast.isPresent());

    MoveOptionalAttribute moveAttr = new MoveOptionalAttribute(ast.get());

    assertTrue(moveAttr.doPatternMatching());
    moveAttr.doReplacement();

    assertEquals(ast.get().getCDDefinition().getCDClassesList().size(), 1);
    assertEquals(ast.get().getCDDefinition().getCDClassesList().get(0).getName(), "B");
    assertEquals(ast.get().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size(), 0);
  }
}
