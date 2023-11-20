/* (c) https://github.com/MontiCore/monticore */
package de.monticore.trafo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.tf.SplitName;
import java.io.IOException;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by
 *
 * @author KH
 */
public class SplitNameTest {

  @BeforeClass
  public static void init() {
    CD4CodeMill.init();
  }

  @Test
  public void testNameFound() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/RefactorCDsValid.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);

    assertTrue(ast.isPresent());

    SplitName splitName = new SplitName(ast.get());

    assertTrue(splitName.doPatternMatching());

    // name found
    assertEquals(1, splitName.get_$C().getCDAttributeList().size());
    assertEquals(splitName.get_$C().getCDAttributeList().get(0).getName(), "name");

    splitName.doReplacement();

    // name replaced
    assertEquals(2, splitName.get_$C().getCDAttributeList().size());
    assertEquals(splitName.get_$C().getCDAttributeList().get(0).getName(), "firstName");
    assertEquals(splitName.get_$C().getCDAttributeList().get(1).getName(), "lastName");
  }

  @Test
  public void testNameNotFound() throws IOException {
    String input = "src/test/resources/de/monticore/trafo/MoveAttrB.cd";
    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(input);

    assertTrue(ast.isPresent());

    SplitName splitName = new SplitName(ast.get());

    assertTrue(splitName.doPatternMatching());

    // name not found
    assertEquals(0, splitName.get_$C().getCDAttributeList().size());

    splitName.doReplacement();

    // name replaced
    assertEquals(2, splitName.get_$C().getCDAttributeList().size());
    assertEquals(splitName.get_$C().getCDAttributeList().get(0).getName(), "firstName");
    assertEquals(splitName.get_$C().getCDAttributeList().get(1).getName(), "lastName");
  }
}
