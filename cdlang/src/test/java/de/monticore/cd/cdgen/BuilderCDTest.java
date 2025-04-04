package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.decorators.*;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.nio.file.Files;
import org.junit.Assert;
import java.util.List;
import java.nio.file.StandardCopyOption;

class BuilderCDTest extends AbstractCDGenTest{

  @Test
  public void testBuilder() throws Exception {
    setup.withDecorator(new SetterDecorator());
    setup.configApplyMatchName(SetterDecorator.class, ("setter"));
    setup.configIgnoreMatchName(SetterDecorator.class, ("noSetter"));

    setup.withDecorator(new GetterDecorator());
    setup.configApplyMatchName(GetterDecorator.class, "getter");
    setup.configIgnoreMatchName(GetterDecorator.class, "noGetter");

    setup.withDecorator(new BuilderDecorator());
    setup.configApplyMatchName(BuilderDecorator.class, "builder");
    setup.configIgnoreMatchName(BuilderDecorator.class, "noBuilder");

    setup.withDecorator(new CardinalityDefaultDecorator());
    setup.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);

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
            + "}\n"
            + "}");

    Assertions.assertTrue(opt.isPresent());

    super.doTest(opt.get());

    //copy the hwc to the target directory to ensure the test can be run
    Path source = Paths.get("src/test/resources/de/monticore/cd/codegen/hwc/TestBuilder/TestBuilderWithSetterBuilder.java");
    Path destination = Paths.get("target/cdGenOutTest/BuilderCDTest/TestBuilder/TestBuilderWithSetterBuilder.java");
    Files.createDirectories(destination.getParent());
    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
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
      Assert.assertTrue(Files.exists(temPath));
    }
  }
}
