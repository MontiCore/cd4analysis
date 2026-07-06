/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.*;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

class BuilderDecoratorTest extends AbstractDecoratorTest {
  
  @Test
  public void testBuilder() throws Exception {
    var opt = CD4CodeMill.parser().parse_String("""
        classdiagram TestBuilder {
          <<setter,getter,builder>>
          public class TestBuilderWithSetter {
            public int myInt;
            public boolean myBool;
            -> (manyB) B [*] public;
            -> (optB) B [0..1] public;
            -> (oneB) B [1] public;
            public TestEnum myTestEnum;
            public Level1Interface myLevel1;
          }
          <<setter,getter,builder>>
          public class TestBuilderWithSuperClass extends TestBuilderWithSetter;
          <<noSetter,getter,builder>>
          public class TestBuilderWithoutSetter {
           public int myInt;
           public boolean myBool;
           -> (manyB) B [*] public;
           -> (optB) B [0..1] public;
           -> (oneB) B [1] public;
           public TestEnum myTestEnum;
           public Level1Interface myLevel1;
          }
          <<getter>>
          public class B { }
          <<setter,getter,builder>>
          public class NoDefaultConstructor {
            public NoDefaultConstructor(int i);
            int i;
          }
          <<setter,getter,builder>>
          public class PrivateDefaultConstructor {
            private PrivateDefaultConstructor();
            int i;
          }
          enum TestEnum {
            RUNNING, IDLE, ERROR;
          }
          interface Level1Interface;
          class Level2class implements Level1Interface{
            int myInt;
          }
        }""");
    
    Assertions.assertTrue(opt.isPresent());
    
    super.doTest(opt.get());
    
    // Test that the warnings about missing setters are present.
    Assertions.assertEquals(7, Log.getFindings().stream().filter(f -> f.getMsg().startsWith(
        "Requested setter of TestBuilder") && f.isWarning()).count());
    Assertions.assertTrue(Log.getFindings().stream().allMatch(f -> f.getMsg().startsWith(
        "Requested setter of TestBuilder") && f.isWarning()));
    Log.clearFindings();
  }
  
  @Test
  public void testTemplateExistence() {
    //test existence of the templates
    List<Path> templatePaths = new ArrayList<>();
    templatePaths.add(Paths.get("src/main/resources/methods/builder/unsafeBuild.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/builder/build.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/builder/isValid.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/builder/set.ftl"));
    templatePaths.add(Paths.get("src/main/resources/methods/builder/setAbsent.ftl"));
    for (Path temPath : templatePaths) {
      Assertions.assertTrue(Files.exists(temPath));
    }
  }
  
  @Override
  protected Optional<MCPath> getHandWrittenPath() {
    return Optional.of(new MCPath("src/cdGenIntTestHwc/java"));
  }
  
  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config,
      GeneratorSetup setup) {
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
