/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.decorators.CardinalityDefaultDecorator;
import de.monticore.cd.codegen.decorators.GetterDecorator;
import de.monticore.cd.codegen.decorators.matcher.MatchResult;
import de.monticore.cd4code.CD4CodeMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link GetterDecorator} by applying it to a CD. The
 * cdlang/src/cdGenIntTest/java/getter/GetterDecoratorTest then tests the generated result
 */
public class GetterCDTest extends AbstractCDGenTest {

  @Test
  public void testGetter() throws Exception {
    setup.withDecorator(new GetterDecorator());
    setup.withCopyCreator().defaultApply();
    setup.configApplyMatchName(GetterDecorator.class, "getter");
    setup.configIgnoreMatchName(GetterDecorator.class, "noGetter");

    setup.withDecorator(new CardinalityDefaultDecorator());
    setup.configDefault(CardinalityDefaultDecorator.class, MatchResult.APPLY);

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
  }
}
