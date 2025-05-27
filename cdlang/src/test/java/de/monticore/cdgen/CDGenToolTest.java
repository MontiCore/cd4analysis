/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.LogStub;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CDGenToolTest {

  @TempDir Path inputDir;

  @TempDir Path outputDir;

  @BeforeEach
  public void before() {
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.reset();
    BasicSymbolsMill.reset();
    LogStub.init();
  }

  @Test
  void testTransfersImportStatements() {
    // Given
    String packageName = "P";
    String diagramName = "D";
    String className = "C";

    String importStatement = "import java.nio.file.Path;";

    String model =
        String.format(
            "package %s;" + "%s" + "classdiagram %s {" + "  class %s { " +
              "void voidMethod();" +
              "String stringMethod();" +
              "static void staticVoidMethod();" +
              "static String staticStringMethod();" +
              " }" + "}",
            packageName, importStatement, diagramName, className);

    createModelInInputDir(model, packageName, diagramName);

    CDGenTool tool = new CDGenTool();

    // When
    tool.run(
        new String[] {
          "-i", inputDir.toAbsolutePath().toString(),
          "-o", outputDir.toAbsolutePath().toString(),
          "-c2mc"
        });

    // Then
    Path diagramTgtPath =
        outputDir
            .toAbsolutePath()
            .resolve(Names.getPathFromPackage(packageName))
            .resolve(diagramName)
            .resolve(className + ".java");

    assertTrue(diagramTgtPath.toFile().isFile());

    try {
      String generated = Files.readString(diagramTgtPath, StandardCharsets.UTF_8);
      assertTrue(generated.contains(importStatement), () -> "Missing import in: " + generated);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  protected void createModelInInputDir(String model, String packageName, String diagramName) {
    Path packagePath = inputDir.resolve(Path.of(Names.getPathFromPackage(packageName)));
    Path diagramPath = inputDir.resolve(packagePath.resolve(diagramName + ".cd"));

    packagePath.toFile().mkdirs();
    try {
      Files.writeString(
          diagramPath,
          model,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE_NEW,
          StandardOpenOption.WRITE);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
