package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.decorators.*;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class ObserverCDTest extends AbstractCDGenTest {

  /**
   * Test the {@link ObserverDecorator} by applying it to a CD. The
   * cdlang/src/cdGenIntTest/java/observer/ObserverDecoratorTest then tests the generated result
   */
  @Test
  void testObserver() throws Exception {
    setup.withCopyCreator().defaultApply();

    setup.withDecorator(new GetterDecorator());
    setup.configApplyMatchName(GetterDecorator.class, "getter");
    setup.configIgnoreMatchName(GetterDecorator.class, "noGetter");

    setup.withDecorator(new SetterDecorator());
    setup.configApplyMatchName(SetterDecorator.class, "setter");
    setup.configIgnoreMatchName(SetterDecorator.class, "noSetter");

    setup.withDecorator(new ObserverDecorator());
    setup.configApplyMatchName(ObserverDecorator.class, "observer");
    setup.configIgnoreMatchName(ObserverDecorator.class, "noObserver");

    setup.withDecorator(new CardinalityDefaultDecorator());
    setup.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);

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
}
