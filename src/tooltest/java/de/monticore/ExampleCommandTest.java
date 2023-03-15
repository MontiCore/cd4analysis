/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.cd.OutTestBasis;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odvalidity.OD2CDMatcher;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExampleCommandTest extends OutTestBasis {

  static final String outputPath = "target/generated/example-commands/";

  @Before
  public void resetMill() {
    CD4CodeMill.reset();
  }

  /**
   * Tests commands: java -jar MCCD.jar -i src/MyAddress.cd -s symbols/MyAddress.cdsym java -jar
   * MCCD.jar -i src/MyLife --path symbols -pp
   */
  @Test
  public void testExampleCommands1and3() {
    String fileName = "doc/MyAddress.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "-s", outputPath + "symbols/MyAddress.cdsym"});
    fileName = "doc/MyLife.cd";
    CD4CodeTool.main(
        new String[] {
          "-i", fileName, "--path", outputPath + "symbols", "-o", outputPath + "out", "--gen"
        });
  }

  /**
   * Tests commands: java -jar MCCD.jar -i src/MyAddress.cd -s symbols/MyAddress.cdsym java -jar
   * MCCd.jar -i src/MyLife --path symbols -o out --gen
   */
  @Test
  public void testExampleCommands1and2() {
    String fileName = "doc/MyAddress.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "-s", outputPath + "symbols/MyAddress.cdsym"});
    fileName = "doc/MyLife.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "--path", outputPath + "symbols", "-pp"});
    assertTrue(getErr(), getErr().isEmpty());
  }

  /** Step1: Getting started for command: java -jar MCCD.jar -i src/MyExample.cd */
  @Test
  public void testGettingStartedExample() {
    String fileName = "doc/MyExample.cd";
    CD4CodeTool.main(new String[] {"-i", fileName});
    assertTrue(getErr(), getErr().isEmpty());
  }

  /** Step2: Pretty printing for command: java -jar MCCD.jar -i src/MyExample.cd -pp */
  @Test
  public void testPrettyPrintingExample1() {
    String fileName = "doc/MyExample.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "-pp"});
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step2: Pretty printing for command: java -jar MCCD.jar -i src/MyExample.cd -pp
   * target/PPExample.cd
   */
  @Test
  public void testPrettyPrintingExample2() {
    String fileName = "doc/MyExample.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "-pp", outputPath + "PPExample.cd"});
    assertTrue(Files.exists(Paths.get(outputPath + "PPExample.cd")));
    assertTrue(
        loadAndCheckCD("doc/MyExample.cd")
            .deepEquals(loadAndCheckCD(outputPath + "PPExample.cd"), false));
    assertTrue(getErr(), getErr().isEmpty());
  }

  /** Step3: storing symbols for command: java -jar MCCD.jar -i src/MyExample.cd -s */
  @Test
  public void testStoringSymbolsExample1() {
    String fileName = "doc/MyExample.cd";

    // copy the CD into test-directory
    CD4CodeTool.main(new String[] {"-i", fileName, "-pp", outputPath + "MyExample.cd"});
    fileName = outputPath + "MyExample.cd";

    // execute the command at test
    CD4CodeTool.main(new String[] {"-i", fileName, "-s"});

    // test if the result exists and no errors occur
    assertTrue(Files.exists(Paths.get(outputPath + "MyExample.cdsym")));
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step3: storing symbols for command: java -jar MCCD.jar -i src/MyExample.cd -s
   * symbols/MyExample.cdsym
   */
  @Test
  public void testStoringSymbolsExample2() {
    String fileName = "doc/MyExample.cd";

    // execute the command at test
    CD4CodeTool.main(new String[] {"-i", fileName, "-s", outputPath + "symbols/MyExample.cdsym"});

    // test if the result exists and no errors occur
    assertTrue(Files.exists(Paths.get(outputPath + "symbols/MyExample.cdsym")));
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 4: Adding FieldSymbols corresponding to association roles for command: java -jar MCCD.jar
   * -i src/MyExample.cd -s symbols/MyExample.cdsym --fieldfromrole all
   */
  @Test
  public void testAddingFieldSymbolsExample1() {
    String fileName = "doc/MyExample.cd";
    CD4CodeTool.main(
        new String[] {
          "-i", fileName, "-s", outputPath + "symbols/MyExample.cdsym", "--fieldfromrole", "all"
        });
    assertTrue(Files.exists(Paths.get(outputPath + "symbols/MyExample.cdsym")));
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 4: Adding FieldSymbols corresponding to association roles for command: java -jar MCCD.jar
   * -i src/MyExample.cd -s symbols/MyExample.cdsym --fieldfromrole navigable
   */
  @Test
  public void testAddingFieldSymbolsExample2() {
    String fileName = "doc/MyExample.cd";
    CD4CodeTool.main(
        new String[] {
          "-i",
          fileName,
          "-s",
          outputPath + "symbols/MyExample.cdsym",
          "--fieldfromrole",
          "navigable"
        });
    assertTrue(Files.exists(Paths.get(outputPath + "symbols/MyExample.cdsym")));
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 5: Importing Symbol Files Using a Path for command: java -jar MCCD.jar -i
   * src/monticore/MyLife.cd
   */
  @Test
  public void testStoringSymbolsPerPathsExample1() {
    String fileName = "doc/MyLife.cd";
    try {
      CD4CodeTool.main(new String[] {"-i", fileName});
    } catch (Error e) {
      Assert.assertTrue(
          e.getMessage().contains("MyLife.cd:<19,9>: 0xA0324 Cannot find symbol Address"));
      Log.clearFindings();
      return;
    }
    fail();
  }

  /**
   * Step 5: Importing Symbol Files Using a Path for commands: java -jar MCCD.jar -i
   * src/MyAddress.cd -s symbols/MyAddress.cdsym java -jar MCCD.jar -i src/monticore/MyLife.cd
   * --defaultpackage --path symbols
   */
  @Test
  public void testStoringSymbolsPerPathsExample2() {
    String fileName = "doc/MyAddress.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "-s", outputPath + "symbols/MyAddress.cdsym"});
    assertTrue(getErr(), getErr().isEmpty());
    fileName = "doc/MyLife.cd";
    CD4CodeTool.main(
        new String[] {"-i", fileName, "--defaultpackage", "--path", outputPath + "symbols"});
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 7: Generating .java-Files for command: java -jar MCCD.jar -i src/MyExample.cd --gen -o out
   */
  @Test
  public void testGenerateJavaExample2() {
    String fileName = "doc/MyExample.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "--gen", "-o", outputPath + "out"});
    ASTCDCompilationUnit cd = loadAndCheckCD(fileName);
    cd.getCDDefinition()
        .getCDClassesList()
        .forEach(
            c ->
                assertTrue(
                    Files.exists(
                        Paths.get(
                            outputPath + "out/" + retrieveRelativeGenPath(c, cd) + ".java"))));
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 7: Generating .java-Files for command: java -jar MCCD.jar -i src/MyCompany.cd -o out --gen
   * --fieldfromrole navigable
   */
  @Test
  public void testGenerateJavaExample3() {
    String fileName = "doc/MyCompany.cd";
    CD4CodeTool.main(
        new String[] {
          "-i", fileName, "-o", outputPath + "out", "--gen", "--fieldfromrole", "navigable"
        });
    resetGlobalScope(); // --fieldfromrole makes this necessary
    ASTCDCompilationUnit cd = loadAndCheckCD(fileName);
    cd.getCDDefinition()
        .getCDClassesList()
        .forEach(
            c ->
                assertTrue(
                    Files.exists(
                        Paths.get(
                            outputPath + "out/" + retrieveRelativeGenPath(c, cd) + ".java"))));
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 8: The Semantic Difference of Two Class Diagrams for command: java -jar MCCD.jar -i
   * src/MyEmployees1.cd --semdiff scr/MyEmployees2.cd
   */
  @Test
  public void testTwoCDsComparisonExample1() {
    final String fileName = "doc/MyEmployees1.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "--semdiff", "doc/MyEmployees2.cd"});
    assertEquals(0, Log.getErrorCount());
  }

  /**
   * Step 8: The Semantic Difference of Two Class Diagrams for command: java -jar MCCD.jar -i
   * src/MyEmployees1.cd --semdiff src/MyEmployees2.cd --difflimit 20 -o out
   */
  @Test
  public void testTwoCDsComparisonExample2() {
    final String cd1 = "doc/MyEmployees1.cd";
    final String cd2 = "doc/MyEmployees2.cd";
    CD4CodeTool.main(
        new String[] {"-i", cd1, "--semdiff", cd2, "--difflimit", "20", "-o", outputPath + "out"});

    try {
      ASTCDCompilationUnit ast1 = Objects.requireNonNull(CDDiffUtil.loadCD(cd1)).deepClone();
      ASTCDCompilationUnit ast2 = Objects.requireNonNull(CDDiffUtil.loadCD(cd2)).deepClone();

      // then corresponding .od files are generated
      File[] odFiles = Paths.get(outputPath + "out").toFile().listFiles();
      assertNotNull(odFiles);

      // now check for each OD if it is a diff-witness, i.e., in sem(cd1)\sem(cd2)

      for (File odFile : odFiles) {
        if (odFile.getName().endsWith(".od")) {
          Assert.assertTrue(
              new OD2CDMatcher()
                  .checkIfDiffWitness(
                      CDSemantics.SIMPLE_CLOSED_WORLD,
                      ast1,
                      ast2,
                      CDDiffUtil.loadODModel(odFile.getPath())));
        }
      }
    } catch (NullPointerException | IOException e) {
      fail(e.getMessage());
    }
    assertEquals(0, Log.getErrorCount());
  }

  /**
   * Step 9: Merging Two Class Diagram for command: java -jar MCCD.jar -i src/MyEmployees2.cd
   * --merge src/MyWorkplace.cd -o out -pp
   */
  @Test
  public void testTwoCDsMergeExample1() {
    final String fileName = "doc/Teaching.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "--merge", "doc/Management.cd", "-pp"});
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 9: Merging Two Class Diagram for command: java -jar MCCD.jar -i src/MyEmployees2.cd
   * --merge src/MyWorkplace.cd -o out -pp MyJob.cd
   */
  @Test
  public void testTwoCDsMergeExample2() {
    final String fileName = "doc/Teaching.cd";
    CD4CodeTool.main(
        new String[] {
          "-i",
          fileName,
          "--merge",
          "doc/Management.cd",
          "-o",
          outputPath + "out",
          "-pp",
          "UniversitySystem.cd"
        });
    assertTrue(Files.exists(Paths.get(outputPath + "out/UniversitySystem.cd")));
    assertTrue(getErr(), getErr().isEmpty());
  }

  protected void resetGlobalScope() {
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.globalScope().init();
    BuiltInTypes.addBuiltInTypes(CD4CodeMill.globalScope());
  }

  protected ASTCDCompilationUnit loadAndCheckCD(String filePath) {
    try {
      Optional<ASTCDCompilationUnit> optCD = CD4CodeMill.parser().parse(filePath);
      assertTrue(optCD.isPresent());
      new CD4CodeAfterParseTrafo().transform(optCD.get());
      new CD4CodeDirectCompositionTrafo().transform(optCD.get());
      ICD4CodeArtifactScope as = CD4CodeMill.scopesGenitorDelegator().createFromAST(optCD.get());
      as.addImports(new ImportStatement("java.lang", true));
      CD4CodeSymbolTableCompleter c = new CD4CodeSymbolTableCompleter(optCD.get());
      optCD.get().accept(c.getTraverser());
      new CD4CodeCoCosDelegator().getCheckerForAllCoCos().checkAll(optCD.get());
      return optCD.get();

    } catch (IOException e) {
      fail(e.getMessage());
    }
    return null;
  }

  protected String retrieveRelativeGenPath(ASTCDClass c, ASTCDCompilationUnit cd) {
    String res = "";
    if (cd.isPresentMCPackageDeclaration()) {
      res = cd.getMCPackageDeclaration().getMCQualifiedName().getQName() + ".";
    }
    res += cd.getCDDefinition().getName() + ".";
    res += c.getSymbol().getInternalQualifiedName();
    res = res.replaceAll("\\.", "/");
    return res;
  }
}
