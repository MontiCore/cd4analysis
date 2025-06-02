package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.*;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.nio.file.Files;
import org.junit.Assert;
import java.util.List;

class BuilderDecoratorTest extends AbstractDecoratorTest{

  @Test
  public void testBuilder() throws Exception {
    var opt =
      CD4CodeMill.parser()
        .parse_String(
          "classdiagram TestBuilder {\n"
            + " <<setter,getter,builder>> public class TestBuilderWithSetter { \n"
            + " public int myInt;\n"
            + " public boolean myBool;\n"
            + " -> (manyB) B [*] public;\n"
            + " -> (optB) B [0..1] public;\n"
            + " -> (oneB) B [1] public;\n"
            + " }\n"
            + " <<noSetter,getter,builder>> public class TestBuilderWithoutSetter { \n"
            + " public int myInt;\n"
            + " public boolean myBool;\n"
            + " -> (manyB) B [*] public;\n"
            + " -> (optB) B [0..1] public;\n"
            + " -> (oneB) B [1] public;\n"
            + " }\n"
            + " <<getter>> public class B { \n"
            + " }\n"
            + " <<setter,getter,builder>> public class NoDefaultConstructor { \n "
            + " public NoDefaultConstructor(int i);\n"
            + " int i; \n"
            + " } \n"
            + " <<setter,getter,builder>> public class PrivateDefaultConstructor { \n "
            + " private PrivateDefaultConstructor();\n"
            + " int i; \n"
            + " } \n"
            + "}");

    Assertions.assertTrue(opt.isPresent());

    super.doTest(opt.get());
  }

  @Test
  public void testTemplateExistence() {
    //test existence of the templates
    List<Path> templatePaths= new ArrayList<>();
    templatePaths.add(Paths.get("src/main/resources/methods/builder/unsafeBuild.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/builder/build.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/builder/isValid.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/builder/set.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/builder/setAbsent.ftl"));
    for (Path temPath: templatePaths) {
      Assertions.assertTrue(Files.exists(temPath));
    }
  }

  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config, GeneratorSetup setup) {
    config.withCopyCreator().defaultApply();
    config.withDecorator(new SetterDecorator());
    config.configApplyMatchName(SetterDecorator.class, ("setter"));
    config.configIgnoreMatchName(SetterDecorator.class, ("noSetter"));
    config.withDecorator(new GetterDecorator());
    config.configApplyMatchName(GetterDecorator.class, "getter");
    config.configIgnoreMatchName(GetterDecorator.class, "noGetter");
    config.withDecorator(new BuilderDecorator());
    config.configApplyMatchName(BuilderDecorator.class, "builder");
    config.configIgnoreMatchName(BuilderDecorator.class, "noBuilder");
    config.withDecorator(new CardinalityDefaultDecorator());
    config.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);
  }
}
