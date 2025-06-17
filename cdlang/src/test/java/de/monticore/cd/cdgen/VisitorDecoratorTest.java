/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.CardinalityDefaultDecorator;
import de.monticore.cd.codegen.decorators.VisitorDecorator;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class VisitorDecoratorTest extends AbstractDecoratorTest {

  /**
   * Test the {@link VisitorDecorator} by applying it to a CD. The
   * cdlang/src/cdGenIntTest/java/visitor/VisitorDecoratorTest then tests the generated result
   */
  @Test
  public void testVisitor() throws Exception {
    var opt = // @formatter:off
      CD4CodeMill.parser()
        .parse_String("classdiagram TestVisitor {\n" +
          " <<visitor>> public class ClassToBeTopped { \n" +
          " public int myInt;\n" +
          " public boolean myBool;\n" +
          " -> (manyB) B [*] public;\n" +
          " -> (optB) B [0..1] public;\n" +
          " -> (oneB) B [1] public;\n" +
          " public int ov;\n" +
          " }\n" +
          "<<visitor>>public class B { " +
          "}\n " +
          "}");
    // @formatter:on

    Assertions.assertTrue(opt.isPresent());

    super.doTest(opt.get());

  }

  @Override
  protected Optional<MCPath> getHandWrittenPath() {
    return Optional.of(new MCPath("src/cdGenIntTestHwc/java"));
  }

  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config,
      GeneratorSetup setup) {
    config.withCopyCreator().defaultApply();
    config.withDecorator(new VisitorDecorator());
    config.configApplyMatchName(VisitorDecorator.class, "visitor");
    config.configIgnoreMatchName(VisitorDecorator.class, "noVisitor");
  }

}
