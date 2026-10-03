/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd.codegen.decorators.CardinalityDefaultDecorator;
import de.monticore.cd.codegen.decorators.GetterDecorator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.runtime.junit.MCAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link GetterDecorator} by applying it to a CD. The
 * cdlang/src/cdGenIntTest/java/getter/GetterDecoratorResultTest then tests the generated result
 */
public class GetterDecoratorTest extends AbstractDecoratorTest {
  
  @Test
  public void testGetter() throws Exception {
    var opt = CD4CodeMill.parser().parse_String("""
        classdiagram TestGetter {
         <<getter>> public class TestGetterC {
           boolean myBool; public int myInt;
           <<noGetter>> public int pubX;
         }
         public association TestGetterC -> (roleB) Other [*];
         public association TestGetterC -> (orderedRole) Other [*] {ordered};
         <<getter>> public class Other {
         }
         <<getter>> public class AlreadyExisting {
          String x;
          void getX(); // already existing
         }
        }""");
    
    Assertions.assertTrue(opt.isPresent());
    
    super.doTest(opt.get());
    
    MCAssertions.assertHasFindingsStartingWith(
        "0xTODO: Unable to decorate setter of `x` as such a method already exists.");
  }
  
  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config,
      GeneratorSetup setup) {
    config.withDecorator(new GetterDecorator());
    config.configApplyMatchName(GetterDecorator.class, "getter");
    config.configIgnoreMatchName(GetterDecorator.class, "noGetter");
    config.withDecorator(new CardinalityDefaultDecorator()).defaultApply();
    config.withCopyCreator().defaultApply();
  }
  
}
