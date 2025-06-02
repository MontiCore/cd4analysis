package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.*;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class ObserverDecoratorTest extends AbstractDecoratorTest {

  /**
   * Test the {@link ObserverDecorator} by applying it to a CD. The
   * cdlang/src/cdGenIntTest/java/observer/ObserverDecoratorTest then tests the generated result
   */
  @Test
  void testObserver() throws Exception {


    var opt =
      CD4CodeMill.parser()
        .parse_String("classdiagram TestObserver {\n" +
        " <<setter,observer>> public class OtherC { \n" +
        " public int myInt;\n" +
        " public boolean myBool;\n" +
        " -> (manyB) B [*] public;\n" +
        " -> (optB) B [0..1] public;\n" +
        " -> (oneB) B [1] public;\n" +
        " public int ov;\n" +
        " }\n" +
        "<<setter>>public class B { " +
        "}\n " +
        "}");

    Assertions.assertTrue(opt.isPresent());

    super.doTest(opt.get());
  }

  @Test
  public void testTemplateExistence() {
    //test existence of the templates
    List<Path> templatePaths= new ArrayList<>();
    templatePaths.add(Paths.get("src/main/resources/methods/observer/addObserver.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/observer/removeObserver.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/observer/notifyObserver.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/observer/notifyObserverAttributeSpecific.ftl"));
    for (Path temPath: templatePaths) {
      Assert.assertTrue(Files.exists(temPath));
    }
  }

  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config, GeneratorSetup setup) {
    config.withCopyCreator().defaultApply();
    config.withDecorator(new GetterDecorator());
    config.configApplyMatchName(GetterDecorator.class, "getter");
    config.configIgnoreMatchName(GetterDecorator.class, "noGetter");
    config.withDecorator(new SetterDecorator());
    config.configApplyMatchName(SetterDecorator.class, "setter");
    config.configIgnoreMatchName(SetterDecorator.class, "noSetter");
    config.withDecorator(new ObserverDecorator());
    config.configApplyMatchName(ObserverDecorator.class, "observer");
    config.configIgnoreMatchName(ObserverDecorator.class, "noObserver");
    config.withDecorator(new CardinalityDefaultDecorator());
    config.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);
  }
}
