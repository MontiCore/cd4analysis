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
        new String[] {
          "-i", "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-c2mc", "-c"
        });
    assertTrue(true);
  }

  @Test
  public void testGeneratorToolWithSymbolTable() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/model/Example.cd",
          "-c2mc",
          "-s",
          "target/generated/example/symboltable"
        });
    assertTrue(new File("target/generated/example/symboltable/model/Example.cdsym").isFile());
  }

  @Test
  public void testGeneratorToolWithJavaGeneration() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/model/Example.cd",
          "-c2mc",
          "-o",
          "target/generated/example/standard",
        });
    assertTrue(new File("target/generated/example/standard/model/Example/A.java").isFile());
  }

  @Test
  public void testGeneratorToolWithCustomGeneratorTemplate() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/model/Example.cd",
          "-c2mc",
          "-o",
          "target/generated/example/ct",
          "-ct",
          "de.monticore.cdgentool.NewCustomTemplate"
        });
    assertTrue(new File("target/generated/example/ct/model/Example/A.java").isFile());
  }

  @Test
  public void testGeneratorToolWithAdditionalTemplates() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/model/Example.cd",
          "-c2mc",
          "-o",
          "target/generated/example/tp",
          "-tp",
          "src/test/resources/de/monticore/cdgentool/templates"
        });
    assertTrue(new File("target/generated/example/tp/model/Example/A.java").isFile());
  }

  @Test
  public void testGeneratorToolWithHWC() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/model/Example.cd",
          "-c2mc",
          "-o",
          "target/generated/example/hwc",
          "-hwc",
          "src/test/resources/de/monticore/cdgentool/hwc"
        });
    assertTrue(new File("target/generated/example/hwc/model/Example/ATOP.java").isFile());
  }

  @Test
  public void testToolPrintHelpOptions() {
    CDGeneratorTool.main(new String[] {"-h"});
    assertTrue(true);
  }

  @Test
  public void testToolPrintVersion() {
    CDGeneratorTool.main(
        new String[] {
          "-i", "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-c2mc", "-v"
        });
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
          "target/generated/example/examplewithpkg",
          "-s",
          "target/generated/example/examplewithpkg"
        });
    File symtab =
        new File(
            "target/generated/example/examplewithpkg/veryUniquePkGNameZBSJKEBV/ExampleWithPkg.cdsym");
    assertTrue(symtab.isFile());
    String contents = Files.readString(Path.of(symtab.toURI()));
    assertTrue(contents.contains("veryUniquePkGNameZBSJKEBV"));
  }

  @Test
  public void testDefaultConstructorDecorator() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/model/Example.cd",
          "-c2mc",
          "-o",
          "target/generated/example/defaultctor",
          "-ct",
          "de.monticore.cdgentool.DefaultCtorTemplate",
        });

    assertTrue(new File("target/generated/example/defaultctor/model/Example/A.java").isFile());
  }

  @Test
  public void testImportStatements() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/model/Example.cd",
          "-o",
          "target/generated/example/imports",
          "-c2mc",
          "target/generated/example/imports/model/Example.cdsym"
        });

    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/ImportTest.cd",
          "-o",
          "target/generated/example/imports",
          "-c2mc",
          "-path",
          "target/generated/example/model"
        });

    assertTrue(true);
  }

  @After
  public void after() {
    CD4CodeMill.globalScope().clear();
    assertTrue(LogStub.getFindings().isEmpty());
  }
}
