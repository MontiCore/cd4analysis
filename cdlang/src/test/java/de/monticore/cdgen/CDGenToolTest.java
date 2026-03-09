/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.io.paths.MCPath;
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
  
  @TempDir
  Path inputDir;
  
  @TempDir
  Path outputDir;
  
  @BeforeEach
  public void before() {
    CD4CodeMill.reset();
    BasicSymbolsMill.reset();
    LogStub.init();
  }
  
  @Test
  void testTransfersImportStatementsAndDecSym() throws IOException {
    // Given
    String packageName = "P";
    String diagramName = "D";
    String className = "C";
    
    String importStatement = "import java.nio.file.Path;";
    
    String model = String.format("package %s;" + "%s\n" + "classdiagram %s {" + "  class %s { "
        + "void voidMethod();" + "String stringMethod();" + "static void staticVoidMethod();"
        + "static String staticStringMethod();" + " }" + "}", packageName, importStatement,
        diagramName, className);
    
    createModelInInputDir(model, packageName, diagramName);
    
    CDGenTool tool = new CDGenTool();
    
    Path codeOutput = outputDir.resolve("code");
    Path decSymbolOutput = outputDir.resolve("decsym");
    
    // When
    tool.run(new String[] { "-i", inputDir.toAbsolutePath().toString(), "-o", codeOutput
        .toAbsolutePath().toString(), "-c2mc", "-sd", decSymbolOutput.toAbsolutePath()
            .toString(), });
    
    // Then
    Path diagramTgtPath = codeOutput.toAbsolutePath().resolve(Names.getPathFromPackage(packageName))
        .resolve(diagramName).resolve(className + ".java");
    
    assertTrue(diagramTgtPath.toFile().isFile());
    
    // Test that the import statement is there
    String generated = Files.readString(diagramTgtPath, StandardCharsets.UTF_8);
    assertTrue(generated.contains(importStatement), () -> "Missing import in: " + generated);
    
    // Test that the symbol exists
    assertTrue(decSymbolOutput.resolve(packageName).toFile().isDirectory(),
        "failed to find decsym package");
    assertTrue(decSymbolOutput.resolve(packageName).resolve(diagramName + ".deccdsym").toFile()
        .exists(), "failed to find decsym classname");
    
    // Test that the decorated symbol table can be used
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().setSymbolPath(new MCPath(decSymbolOutput));
    
    assertTrue(CD4CodeMill.globalScope().resolveCDType("P.D.C").isPresent(),
        "Failed to look for P.D.C");
    assertTrue(LogStub.getFindings().isEmpty());
  }
  
  protected void createModelInInputDir(String model, String packageName, String diagramName) {
    Path packagePath = inputDir.resolve(Path.of(Names.getPathFromPackage(packageName)));
    Path diagramPath = inputDir.resolve(packagePath.resolve(diagramName + ".cd"));
    
    packagePath.toFile().mkdirs();
    try {
      Files.writeString(diagramPath, model, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW,
          StandardOpenOption.WRITE);
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
  
}
