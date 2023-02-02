/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class CDGeneratorToolTest {

  @BeforeEach
  public void before() {
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.reset();
    BasicSymbolsMill.reset();
    LogStub.init();
  }

  @Test
  public void testGeneratorToolWithCoCos() {
    CDGeneratorTool.main(
        new String[] {"-i", "src/test/resources/de/monticore/cdgentool/Example.cd", "-c2mc", "-c"});
    assertTrue(true);
  }

  @Test
  public void testGeneratorToolWithSymbolTable() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/Example.cd",
          "-c2mc",
          "-s",
          "target/generated/example/cdgentool/Example.cdsym"
        });
    assertTrue(new File("target/generated/example/cdgentool/Example.cdsym").isFile());
  }

  @Test
  public void testGeneratorToolWithJavaGeneration() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/Example.cd",
          "-c2mc",
          "-o",
          "target/generated/example/standard/",
          "-gen"
        });
    assertTrue(new File("target/generated/example/standard/A.java").isFile());
  }

  @Test
  public void testGeneratorToolWithCustomGeneratorTemplate() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/Example.cd",
          "-c2mc",
          "-o",
          "target/generated/example/ct/",
          "-gen",
          "-ct",
          "de.monticore.cdgentool.NewCustomTemplate"
        });
    assertTrue(new File("target/generated/example/ct/A.java").isFile());
  }

  @Test
  public void testGeneratorToolWithAdditionalTemplates() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/Example.cd",
          "-c2mc",
          "-o",
          "target/generated/example/tp/",
          "-gen",
          "-tp",
          "src/test/resources/de/monticore/cdgentool/templates/"
        });
    assertTrue(new File("target/generated/example/tp/A.java").isFile());
  }

  @Test
  public void testGeneratorToolWithHWC() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/Example.cd",
          "-c2mc",
          "-o",
          "target/generated/example/hwc/",
          "-gen",
          "-hwc",
          "src/test/resources/de/monticore/cdgentool/hwc/"
        });
    assertTrue(new File("target/generated/example/hwc/ATOP.java").isFile());
  }

  @Test
  public void testToolPrintHelpOptions() {
    CDGeneratorTool.main(new String[] {"-h"});
    assertTrue(true);
  }

  @Test
  public void testToolPrintVersion() {
    CDGeneratorTool.main(
        new String[] {"-i", "src/test/resources/de/monticore/cdgentool/Example.cd", "-c2mc", "-v"});
    assertTrue(true);
  }

  @After
  public void after() {
    CD4CodeMill.globalScope().clear();
    assertTrue(LogStub.getFindings().isEmpty());
  }
}
