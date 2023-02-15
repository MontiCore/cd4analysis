/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    assertTrue(new File("target/generated/example/standard/example/A.java").isFile());
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
    assertTrue(new File("target/generated/example/ct/example/A.java").isFile());
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
    assertTrue(new File("target/generated/example/tp/example/A.java").isFile());
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
    assertTrue(new File("target/generated/example/hwc/example/ATOP.java").isFile());
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

  @Test
  public void testGeneratorToolWithPkgSymTab() throws IOException {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/pkg/ExampleWithPkg.cd",
          "-c2mc",
          "-o",
          "target/generated/example/examplewithpkg/",
          "-s",
          "exp.cdsym"
        });
    File symtab = new File("target/generated/example/examplewithpkg/exp.cdsym");
    assertTrue(symtab.isFile());
    String contents = Files.readString(Path.of(symtab.toURI()));
    assertTrue(contents.contains("veryUniquePkGNameZBSJKEBV"));
  }

  @Test
  public void testDefaultConstructorDecorator() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/Example.cd",
          "-c2mc",
          "-o",
          "target/generated/example/defaultctor/",
          "-ct",
          "de.monticore.cdgentool.DefaultCtorTemplate",
          "-gen"
        });

    assertTrue(new File("target/generated/example/defaultctor/example/A.java").isFile());
  }

  @After
  public void after() {
    CD4CodeMill.globalScope().clear();
    assertTrue(LogStub.getFindings().isEmpty());
  }
}
