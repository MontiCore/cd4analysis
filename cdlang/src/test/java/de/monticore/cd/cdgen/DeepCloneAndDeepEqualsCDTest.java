package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.decorators.CardinalityDefaultDecorator;
import de.monticore.cd.codegen.decorators.DeepCloneAndDeepEqualsDecorator;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DeepCloneAndDeepEqualsCDTest extends AbstractCDGenTest{

  @Test
  public void testDeepCopyAndDeepEquals() throws Exception {
    setup.withDecorator(new CardinalityDefaultDecorator());
    setup.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);

    //we do not need to add the Equals and Clone decorator, as it is automatically added
    setup.withDecorator(new DeepCloneAndDeepEqualsDecorator());
    setup.configDefault(DeepCloneAndDeepEqualsDecorator.class, MatchResult.APPLY);

    var opt =
      CD4CodeMill.parser()
        .parse_String("classdiagram TestDeepCloneAndDeepEquals {\n" +
          " public class OtherC { \n" +
          " public int myInt;\n" +
          " public boolean myBool;\n" +
          " public List<List<B>> my2dimList;\n" +
          " -> (manyB)B [*] public;\n" +
          " -> (optB) B [0..1] public;\n" +
          " -> (oneB) B [1] public;\n" +
          " public Integer myInteger;\n" +
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
    templatePaths.add(Paths.get("src/main/resources/methods/deepCloneAndDeepEquals/deepClone.ftl"));
    //TODO add more later
    for (Path temPath: templatePaths) {
      Assert.assertTrue(Files.exists(temPath));
    }
  }
}
