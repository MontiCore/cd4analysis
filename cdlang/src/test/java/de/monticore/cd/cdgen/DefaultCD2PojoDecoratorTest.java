/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.logging.Log;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the decorators configured by CD2Java. The
 * cdlang/src/cdGenIntTest/java/getter/GetterDecoratorResultTest then tests the generated result
 */
public class DefaultCD2PojoDecoratorTest extends AbstractDecoratorTest {

  @Test
  public void testAll() throws Exception {
    var opt =
        CD4CodeMill.parser()
            .parse_String(
                "classdiagram TestDefaultCD2Pojo {\n"
                    + " <<getter>> public class TestGetterC { \n"
                    + " boolean myBool;"
                    + " public int myInt;"
                    + " <<noGetter>> public int pubX;"
                    + " public void voidM();"
                    + " public String stringM();"
                    + " public static void staticVoidM();"
                    + " public static String staticStringM();"
                    + " }\n"
                    + " public association TestGetterC -> (roleB) Other [*];\n"
                    + " public association TestGetterC -> (orderedRole) Other [*] {ordered};\n"
                    + " <<getter>> public class Other { \n"
                    + "}\n"
                    + "}");

    Assertions.assertTrue(opt.isPresent());

    super.doTest(opt.get());

    // TODO: Remove once WIP Set Setter is implemented
    Log.getFindings().remove(0);
    Log.getFindings().remove(0);

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Override
  public void initializeDecConf(
      GlobalExtensionManagement glex, DecoratorConfig config, GeneratorSetup setup) {
    // Instead of adding the getters via the API, we call the config template
    List<Object> configTemplateArgs = Arrays.asList(glex, config);
    String configTemplate = "cd2java.init.CD2Pojo";
    TemplateController tc = setup.getNewTemplateController(configTemplate);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
    hpp.processValue(tc, configTemplateArgs);
  }
}
