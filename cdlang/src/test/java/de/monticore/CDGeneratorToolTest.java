/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbols2Json;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdgen.CDGenTool;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-c2mc", "-c" });
  }
  
  @Test
  public void testGeneratorToolWithSymbolTable() {
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-c2mc", "-s",
        "target/generated/example/symboltable" });
    assertTrue(new File("target/generated/example/symboltable/model/Example.cdsym").isFile());
  }
  
  @Test
  public void testGeneratorToolWithJavaGeneration() {
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-c2mc", "-o",
        "target/generated/example/standard", });
    assertTrue(new File("target/generated/example/standard/model/Example/A.java").isFile());
  }
  
  @Test
  public void testGeneratorToolWithEmptyCDWithPackage() {
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/EmptyWithPackage.cd", "-c2mc", "-o",
        "target/generated/example/standard", });
    
    assertTrue(new File("target/generated/example/standard/model/EmptyWithPackage").isDirectory());
  }
  
  @Test
  public void testGeneratorToolWithEmptyCDWithoutPackage() {
    
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/EmptyWithoutPackage.cd", "-c2mc", "-o",
        "target/generated/example/standard", });
    assertTrue(new File("target/generated/example/standard/EmptyWithoutPackage").isDirectory());
  }
  
  @Test
  @Disabled // The new generator has a different configTemplate opinion
  public void testGeneratorToolWithCustomGeneratorTemplate() {
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-c2mc", "-o",
        "target/generated/example/ct", "-ct", "de.monticore.cdgentool.NewCustomTemplate" });
    assertTrue(new File("target/generated/example/ct/model/Example/A.java").isFile());
  }
  
  @Test
  @Disabled // New generator has a different CLI options
  public void testGeneratorToolWithAdditionalTemplates() {
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-c2mc", "-o",
        "target/generated/example/tp", "-tp",
        "src/test/resources/de/monticore/cdgentool/templates" });
    assertTrue(new File("target/generated/example/tp/model/Example/A.java").isFile());
  }
  
  @Test
  public void testGeneratorToolWithHWC() {
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-c2mc", "-o",
        "target/generated/example/hwc", "-hwc", "src/test/resources/de/monticore/cdgentool/hwc" });
    assertTrue(new File("target/generated/example/hwc/model/Example/ATOP.java").isFile());
  }
  
  @Test
  public void testToolPrintHelpOptions() {
    new CDGenTool().run(new String[] { "-h" });
  }
  
  @Test
  public void testToolPrintVersion() {
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-c2mc", "-v" });
  }
  
  @Test
  public void testGeneratorToolWithPkgSymTab() throws IOException {
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/pkg/ExampleWithPkg.cd", "-c2mc", "-o",
        "target/generated/example/examplewithpkg", "-s",
        "target/generated/example/examplewithpkg" });
    File symtab = new File("target/generated/example/examplewithpkg/pkg/ExampleWithPkg.cdsym");
    assertTrue(symtab.isFile());
    String contents = Files.readString(Path.of(symtab.toURI()));
    assertTrue(contents.contains("pkg"));
  }
  
  @Test
  public void testImportStatements() {
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-o",
        "target/generated/example/imports", "-c2mc", "-s", "target/generated/example/imports/" });
    
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/ImportTest.cd", "-o",
        "target/generated/example/imports", "-c2mc", "-path",
        "target/generated/example/imports/" });
  }
  
  @Test
  @Disabled // The new generator does not have a "fieldfromrole" option
  public void testAttributesForAssociations() {
    new CDGenTool().run(new String[] { "-i",
        "src/test/resources/de/monticore/cdgentool/model/Example.cd", "-c2mc", "-s",
        "target/generated/example/rolefield/", "-fieldfromrole", "navigable" });
    File symtab = new File("target/generated/example/rolefield/model/Example.cdsym");
    assertTrue(symtab.isFile());
    BasicSymbolsMill.initializePrimitives();
    ICD4CodeArtifactScope scope = new CD4CodeSymbols2Json().load(Paths.get(
        "target/generated/example/rolefield/model/Example.cdsym").toString());
    assertEquals(2, scope.getCDTypeSymbols().get("A").get(0).getSpannedScope().getFieldSymbols()
        .size());
  }
  
  @AfterEach
  public void after() {
    MCAssertions.assertNoFindings();
    CD4CodeMill.globalScope().clear();
  }
  
}
