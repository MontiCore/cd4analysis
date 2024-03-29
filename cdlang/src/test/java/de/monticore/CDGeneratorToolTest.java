/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
  public void testGeneratorToolWithEmptyCDWithPackage() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/model/EmptyWithPackage.cd",
          "-c2mc",
          "-o",
          "target/generated/example/standard",
        });

    assertTrue(new File("target/generated/example/standard/model/EmptyWithPackage").isDirectory());
  }

  @Test
  public void testGeneratorToolWithEmptyCDWithoutPackage() {

    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/model/EmptyWithoutPackage.cd",
          "-c2mc",
          "-o",
          "target/generated/example/standard",
        });
    assertTrue(new File("target/generated/example/standard/EmptyWithoutPackage").isDirectory());
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
    File symtab = new File("target/generated/example/examplewithpkg/pkg/ExampleWithPkg.cdsym");
    assertTrue(symtab.isFile());
    String contents = Files.readString(Path.of(symtab.toURI()));
    assertTrue(contents.contains("pkg"));
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
          "-s",
          "target/generated/example/imports/"
        });

    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/ImportTest.cd",
          "-o",
          "target/generated/example/imports",
          "-c2mc",
          "-path",
          "target/generated/example/imports/"
        });

    assertTrue(true);
  }

  @Test
  public void testAttributesForAssociations() {
    CDGeneratorTool.main(
        new String[] {
          "-i",
          "src/test/resources/de/monticore/cdgentool/model/Example.cd",
          "-c2mc",
          "-s",
          "target/generated/example/rolefield/",
          "-fieldfromrole",
          "navigable"
        });
    File symtab = new File("target/generated/example/rolefield/model/Example.cdsym");
    assertTrue(symtab.isFile());
    BasicSymbolsMill.initializePrimitives();
    ICD4CodeArtifactScope scope =
        new CD4CodeSymbols2Json()
            .load(Paths.get("target/generated/example/rolefield/model/Example.cdsym").toString());
    assertEquals(
        scope.getCDTypeSymbols().get("A").get(0).getSpannedScope().getFieldSymbols().size(), 2);
  }

  @After
  public void after() {
    CD4CodeMill.globalScope().clear();
    assertTrue(LogStub.getFindings().isEmpty());
  }
}
