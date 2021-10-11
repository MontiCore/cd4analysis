/* (c) https://github.com/MontiCore/monticore */
package de.monticore.trafo;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.tf.ReplaceDerivedAttribute;
import de.se_rwth.commons.logging.Log;
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
public class ReplaceDerivedAttributeTest {

  @BeforeClass
  public static void disableFailQuick() {
    CD4CodeMill.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testChangeNameTransformation() throws IOException {

    Optional<ASTCDCompilationUnit> ast = CD4CodeMill.parser().parse(
        "src/test/resources/de/monticore/trafo/DerivedAttr.cd");

    assertTrue(ast.isPresent());

    ReplaceDerivedAttribute renameClass = new ReplaceDerivedAttribute(ast.get());

    assertTrue(renameClass.doPatternMatching());
    renameClass.doReplacement();

    assertEquals(0, ast.get().getCDDefinition().getCDClassesList().get(0).getCDAttributeList().size());
    assertEquals(1, ast.get().getCDDefinition().getCDClassesList().get(0).getCDMethodList().size());
  }



}
