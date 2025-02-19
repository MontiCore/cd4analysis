/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd.codegen.decorators.GetterDecorator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link GetterDecorator} by applying it to a CD.
 * The cdlang/src/cdGenIntTest/java/getter/GetterDecoratorTest then tests the generated result
 */
public class GetterCDTest extends AbstractCDGenTest {


  @Test
  public void testGetter() throws Exception {
    setup.withDecorator(new GetterDecorator());
    setup.configApplyMatchName(GetterDecorator.class, "getter");
    setup.configIgnoreMatchName(GetterDecorator.class, "noGetter");


    var opt = CD4CodeMill.parser().parse_String("classdiagram TestGetter {\n" +
                                                  " <<getter>> public class TestGetter { \n" +
                                                  " boolean myBool;" +
                                                  " public int myInt;" +
                                                  " <<noGetter>> public int pubX;" +
                                                  " }" +
                                                  "}");

    Assertions.assertTrue(opt.isPresent());

    super.doTest(opt.get());
  }
}
