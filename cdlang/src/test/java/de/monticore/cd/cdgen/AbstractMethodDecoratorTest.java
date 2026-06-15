package de.monticore.cd.cdgen;

import de.monticore.cd.codegen.DecoratorConfig;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class AbstractMethodDecoratorTest extends AbstractDecoratorTest {

  @Override
  protected Optional<MCPath> getHandWrittenPath() {
    // The path should point to the root of the handwritten source folder,
    // not to the package folder itself.
    return Optional.of(new MCPath("src/test/resources/de/monticore/cd/codegen/hwc"));
  }

  @Test
  public void testNotTopped() throws IOException {
    var opt = CD4CodeMill.parser().parse_String("""
       classdiagram TestAbstractMethodDecoratorNotTopped {
         abstract class Asset {
           void process();
         }
       
         class Task extends Asset {
           void process();
         }
       }
       """);
    Assertions.assertTrue(opt.isPresent());

    super.doTest(opt.get());

    MCAssertions.assertHasFinding(f -> f.getMsg().contains("0xC0FFEE00"));
    Log.clearFindings();
  }

  @Test
  public void testTopped() throws Exception {
    var opt = CD4CodeMill.parser().parse_String("""
       classdiagram TestAbstractMethodDecoratorTopped {
         abstract class Asset {
           void process();
         }
       
         class Task extends Asset {
           void process();
         }
       }
       """);
    Assertions.assertTrue(opt.isPresent());

    super.doTest(opt.get());

    MCAssertions.assertNoFindings();
  }


  @Override
  public void initializeDecConf(GlobalExtensionManagement glex, DecoratorConfig config, GeneratorSetup setup) {
    config.withCopyCreator().defaultApply();
    config.withAbstractMethodSignatures().defaultApply();
  }
}
