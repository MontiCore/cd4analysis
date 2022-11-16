package de.monticore;

import static org.junit.Assert.*;

import de.monticore.cd.OutTestBasis;
import de.monticore.cd4code.CD4CodeMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExampleCommandTest extends OutTestBasis {

  static final String outputPath = "target/generated/example-commands/";

  @Before
  public void resetMill() {
    CD4CodeMill.reset();
  }

  /**
   * Tests commands: java -jar MCCD.jar -i src/MyBasics.cd -s symbols/MyBasics.cdsym java -jar
   * MCCD.jar -i src/MyLife --path symbols -pp
   */
  @Test
  public void testExampleCommands1and3() {
    String fileName = "doc/MyBasics.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "-s", outputPath + "symbols/MyBasics.cdsym"});
    fileName = "doc/MyLife.cd";
    CD4CodeTool.main(
        new String[] {
          "-i", fileName, "--path", outputPath + "symbols", "-o", outputPath + "out", "--gen"
        });
  }

  /**
   * Tests commands: java -jar MCCD.jar -i src/MyBasics.cd -s symbols/MyBasics.cdsym java -jar
   * MCCd.jar -i src/MyLife --path symbols -o out --gen
   */
  @Test
  public void testExampleCommands1and2() {
    String fileName = "doc/MyBasics.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "-s", outputPath + "symbols/MyBasics.cdsym"});
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
    CD4CodeTool.main(new String[] {"-i", fileName, "-pp", "target/PPExample.cd"});
    assertTrue(getErr(), getErr().isEmpty());
  }

  /** Step3: storing symbols for command: java -jar MCCD.jar -i src/MyExample.cd -s */
  @Test
  public void testStoringSymbolsExample1() {
    String fileName = "doc/MyExample.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "-s"});
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step3: storing symbols for command: java -jar MCCD.jar -i src/MyExample.cd -s
   * symbols/MyExample.cdsym
   */
  @Test
  public void testStoringSymbolsExample2() {
    String fileName = "doc/MyExample.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "-s", outputPath + "symbols/MyExample.cdsym"});
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
          e.getMessage().contains("MyLife.cd:<18,9>: 0xA0324 Cannot find symbol Address"));
      Log.clearFindings();
      return;
    }
    fail();
  }

  /**
   * Step 5: Importing Symbol Files Using a Path for commands: java -jar MCCD.jar -i src/MyBasics.cd
   * -s symbols/MyBasics.cdsym java -jar MCCD.jar -i src/monticore/MyLife.cd --defaultpackage --path
   * symbols
   */
  @Test
  public void testStoringSymbolsPerPathsExample2() {
    String fileName = "doc/MyBasics.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "-s", outputPath + "symbols/MyBasics.cdsym"});
    assertTrue(getErr(), getErr().isEmpty());
    fileName = "doc/MyLife.cd";
    CD4CodeTool.main(
        new String[] {"-i", fileName, "--defaultpackage", "--path", outputPath + "symbols"});
    assertTrue(getErr(), getErr().isEmpty());
  }

  /** Step 7: Generating .java-Files for command: java -jar MCCD.jar -i src/MyExample.cd --gen */
  @Test
  public void testGenerateJavaExample1() {
    String fileName = "doc/MyExample.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "--gen"});
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 7: Generating .java-Files for command: java -jar MCCD.jar -i src/MyExample.cd --gen -o out
   */
  @Test
  public void testGenerateJavaExample2() {
    String fileName = "doc/MyExample.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "--gen", "-o", outputPath + "out"});
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 7: Generating .java-Files for command: java -jar MCCD.jar -i src/MyCars.cd -o out --gen
   * --fieldfromrole navigable
   */
  @Test
  public void testGenerateJavaExample3() {
    String fileName = "doc/MyCars.cd";
    CD4CodeTool.main(
        new String[] {
          "-i", fileName, "-o", outputPath + "out", "--gen", "--fieldfromrole", "navigable"
        });
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 8: The Semantic Difference of Two Class Diagrams for command: java -jar MCCD.jar -i
   * src/Employees1.cd --semdiff scr/Employees2.cd
   */
  @Test
  public void testTwoCDsComparisonExample1() {
    final String fileName = "doc/Employees1.cd";
    CD4CodeTool.main(new String[] {"-i", fileName, "--semdiff", "doc/Employees2.cd"});
    assertEquals(0, Log.getErrorCount());
  }

  /**
   * Step 8: The Semantic Difference of Two Class Diagrams for command: java -jar MCCD.jar -i
   * src/Employees1.cd --semdiff src/Employees2.cd --difflimit 20 -o out
   */
  @Test
  public void testTwoCDsComparisonExample2() {
    final String fileName = "doc/Employees1.cd";
    CD4CodeTool.main(
        new String[] {
          "-i",
          fileName,
          "--semdiff",
          "doc/Employees2.cd",
          "--difflimit",
          "20",
          "-o",
          outputPath + "out"
        });
    assertEquals(0, Log.getErrorCount());
  }

  /**
   * Step 9: Merging Two Class Diagram for command: java -jar MCCD.jar -i src/Person1.cd --merge
   * src/Person2.cd -o out -pp
   */
  @Test
  public void testTwoCDsMergeExample1() {
    final String fileName = "doc/Person1.cd";
    CD4CodeTool.main(
        new String[] {
          "-i", fileName, "--merge", "doc/Person2.cd", "-o", outputPath + "out", "-pp"
        });
    assertTrue(getErr(), getErr().isEmpty());
  }

  /**
   * Step 9: Merging Two Class Diagram for command: java -jar MCCD.jar -i src/Person1.cd --merge
   * src/Person2.cd -o out -pp Person.cd
   */
  @Test
  public void testTwoCDsMergeExample2() {
    final String fileName = "doc/Person1.cd";
    CD4CodeTool.main(
        new String[] {
          "-i", fileName, "--merge", "doc/Person2.cd", "-o", outputPath + "out", "-pp", "Person.cd"
        });
    assertTrue(getErr(), getErr().isEmpty());
  }
}
