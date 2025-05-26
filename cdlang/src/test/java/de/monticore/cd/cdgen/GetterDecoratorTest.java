/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.CardinalityDefaultDecorator;
import de.monticore.cd.codegen.decorators.GetterDecorator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link GetterDecorator} by applying it to a CD. The
 * cdlang/src/cdGenIntTest/java/getter/GetterDecoratorResultTest then tests the generated result
 */
public class GetterDecoratorTest extends AbstractDecoratorTest {

  @Test
  public void testGetter() throws Exception {
    var opt =
      CD4CodeMill.parser()
        .parse_String(
          "classdiagram TestGetter {\n"
            + " <<getter>> public class TestGetterC { \n"
            + " boolean myBool;"
            + " public int myInt;"
            + " <<noGetter>> public int pubX;"
            + " }\n"
            + " public association TestGetterC -> (roleB) Other [*];\n"
            + " public association TestGetterC -> (orderedRole) Other [*] {ordered};\n"
            + " <<getter>> public class Other { \n"
            + "}\n"
            + "}");

    Assertions.assertTrue(opt.isPresent());

    super.doTest(opt.get());

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config, GeneratorSetup setup) {
    config.withDecorator(new GetterDecorator());
    config.withCopyCreator().defaultApply();
    config.configApplyMatchName(GetterDecorator.class, "getter");
    config.configIgnoreMatchName(GetterDecorator.class, "noGetter");

    config.withDecorator(new CardinalityDefaultDecorator());
  }
}
