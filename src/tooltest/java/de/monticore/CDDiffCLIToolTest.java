/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.Assert.*;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.CDDiffUtil;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odvalidity.OD2CDMatcher;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.file.PathUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CDDiffCLIToolTest {

  private static final String TOOL_PATH = "src/tooltest/resources/de/monticore/";
  final String[] owDiffOptions = {"alloy-based", "reduction-based"};

  final String[] cwDiffOptions = {"", "--rule-based"};

  @Before
  public void init() {
    Log.init();
  }

  @Test
  public void testChain() {
    final String cd1 = TOOL_PATH + "/cddiff/Employees/Employees2.cd";
    final String cd2 = TOOL_PATH + "cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/chain";
    String[] args = {
      "-i", cd1, "--merge", cd2, "--semdiff", cd2, "-o", output, "-pp", "Employees12.cd"
    };
    CD4CodeTool.main(args);

    // assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertEquals(Log.getErrorCount(), 0);
  }

  @Test
  public void testSyntaxDiff() {
    final String cd1 = TOOL_PATH + "/cddiff/Employees/Employees2.cd";
    final String cd2 = TOOL_PATH + "/cddiff/Employees/Employees1.cd";
    CD4CodeTool.main(new String[] {"-i", cd1, "--syntaxdiff", cd2, "--show", "all"});

    // assertEquals("Parsing and CoCo check successful!\r\n", getOut());
    assertEquals(Log.getErrorCount(), 0);
  }

  @Test
  public void testSemDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = TOOL_PATH + "/cddiff/Employees/Employees2.cd";
    final String cd2 = TOOL_PATH + "/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithDiff";

    for (String cwDiffOption : cwDiffOptions) {
      // when CD4CodeTool is used to compute the semantic difference
      String[] args = {
        "-i",
        cd1,
        "--semdiff",
        cd2,
        "--diffsize",
        "21",
        "-o",
        output,
        "--difflimit",
        "20",
        cwDiffOption
      };
      CD4CodeTool.main(args);

      try {
        ASTCDCompilationUnit ast1 = Objects.requireNonNull(CDDiffUtil.loadCD(cd1)).deepClone();
        ASTCDCompilationUnit ast2 = Objects.requireNonNull(CDDiffUtil.loadCD(cd2)).deepClone();

        // then corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
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

      // clean-up
      try {
        PathUtils.delete(Paths.get(output));
      } catch (IOException e) {
        Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
      }
    }
  }

  @Test
  public void testNoSemDiff() {
    // given 2 CDs that are semantically equivalent
    final String cd1 = TOOL_PATH + "/cddiff/SimilarManagers/CDSimilarManagerv1" + ".cd";
    final String cd2 = TOOL_PATH + "/cddiff/SimilarManagers/CDSimilarManagerv2" + ".cd";
    final String output = "./target/generated/cddiff-test/CLITestWithoutDiff";

    for (String cwDiffOption : cwDiffOptions) {
      // when CD4CodeTool is used to compute the semantic difference
      String[] args = {
        "-i",
        cd1,
        "--semdiff",
        cd2,
        "--diffsize",
        "21",
        "-o",
        output,
        "--difflimit",
        "20",
        cwDiffOption
      };
      CD4CodeTool.main(args);

      // no corresponding .od files are generated
      File[] odFiles = Paths.get(output).toFile().listFiles();
      if (odFiles == null) {
        assertEquals(0, Log.getErrorCount());
        return;
      }
      List<String> odFilePaths = new LinkedList<>();
      for (File odFile : odFiles) {
        if (odFile.getName().endsWith(".od")) {
          odFilePaths.add(odFile.toPath().toString());
        }
      }
      assertTrue(odFilePaths.isEmpty());

      // clean-up
      try {
        PathUtils.delete(Paths.get(output));
      } catch (IOException e) {
        Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
      }
    }
  }

  @Test
  public void testDefaultSemDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = TOOL_PATH + "/cddiff/Employees/Employees2.cd";
    final String cd2 = TOOL_PATH + "/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithDefaultDiff";

    // when CD4CodeTool is used to compute the semantic difference
    String[] args = {"-i", cd1, "--semdiff", cd2, "-o", output};
    CD4CodeTool.main(args);

    try {
      ASTCDCompilationUnit ast1 = Objects.requireNonNull(CDDiffUtil.loadCD(cd1)).deepClone();
      ASTCDCompilationUnit ast2 = Objects.requireNonNull(CDDiffUtil.loadCD(cd2)).deepClone();

      // then corresponding .od files are generated
      File[] odFiles = Paths.get(output).toFile().listFiles();
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

    // clean-up
    try {
      PathUtils.delete(Paths.get(output));
    } catch (IOException e) {
      Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
    }
  }

  @Test
  public void testOpenWorldDiff() {
    // given 2 CDs such that the first is simply missing an association defined in the second
    final String cd1 = TOOL_PATH + "/cddiff/Employees/Employees0.cd";
    final String cd2 = TOOL_PATH + "/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithOWDiff";
    for (String cwDiffOption : cwDiffOptions) {
      for (String owDiffOption : owDiffOptions) {
        // when CD4CodeTool is used to compute the semantic difference
        String[] args = {
          "-i",
          cd1,
          "--semdiff",
          cd2,
          "--diffsize",
          "21",
          "-o",
          output,
          "--difflimit",
          "20",
          "--open-world",
          owDiffOption,
          cwDiffOption
        };
        CD4CodeTool.main(args);

        // some corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        assertNotNull(odFiles);
        List<String> odFilePaths = new LinkedList<>();
        for (File odFile : odFiles) {
          if (odFile.getName().endsWith(".od")) {
            odFilePaths.add(odFile.toPath().toString());
          }
        }
        assertFalse(odFilePaths.isEmpty());

        // clean-up
        try {
          PathUtils.delete(Paths.get(output));
        } catch (IOException e) {
          Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
        }
      }
    }
  }

  @Test
  public void testNoOpenWorldDiff() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = TOOL_PATH + "/cddiff/Employees/Employees2.cd";
    final String cd2 = TOOL_PATH + "/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithoutOWDiff";

    for (String cwDiffOption : cwDiffOptions) {
      for (String owDiffOption : owDiffOptions) {

        // when CD4CodeTool is used to compute the semantic difference
        String[] args = {
          "-i",
          cd1,
          "--semdiff",
          cd2,
          "--diffsize",
          "21",
          "-o",
          output,
          "--difflimit",
          "20",
          "--open-world",
          owDiffOption,
          cwDiffOption
        };
        CD4CodeTool.main(args);

        // no corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        if (odFiles == null) {
          assertEquals(0, Log.getErrorCount());
          return;
        }
        List<String> odFilePaths = new LinkedList<>();
        for (File odFile : odFiles) {
          if (odFile.getName().endsWith(".od")) {
            odFilePaths.add(odFile.toPath().toString());
          }
        }
        assertTrue(odFilePaths.isEmpty());

        // clean-up
        try {
          PathUtils.delete(Paths.get(output));
        } catch (IOException e) {
          Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
        }
      }
    }
  }

  @Test
  public void testNoOpenWorldDiff4Abstract2Interface() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = TOOL_PATH + "/cddiff/Abstract2Interface" + "/AbstractPerson.cd";
    final String cd2 = TOOL_PATH + "/cddiff/Abstract2Interface" + "/InterfacePerson.cd";
    final String output = "./target/generated/cddiff-test/CLITestAbstract2InterfaceNoOWDiff";

    for (String cwDiffOption : cwDiffOptions) {
      for (String owDiffOption : owDiffOptions) {
        // when CD4CodeTool is used to compute the semantic difference
        String[] args = {
          "-i",
          cd1,
          "--semdiff",
          cd2,
          "--diffsize",
          "21",
          "-o",
          output,
          "--difflimit",
          "20",
          "--open-world",
          owDiffOption,
          cwDiffOption
        };
        CD4CodeTool.main(args);

        // no corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        if (odFiles == null) {
          assertEquals(0, Log.getErrorCount());
          return;
        }
        List<String> odFilePaths = new LinkedList<>();
        for (File odFile : odFiles) {
          if (odFile.getName().endsWith(".od")) {
            odFilePaths.add(odFile.toPath().toString());
          }
        }
        assertTrue(odFilePaths.isEmpty());

        // clean-up
        try {
          PathUtils.delete(Paths.get(output));
        } catch (IOException e) {
          Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
        }
      }
    }
  }

  @Test
  public void testNoOpenWorldDiffWithPackages() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = TOOL_PATH + "/cddiff/Employees/Employees8.cd";
    final String cd2 = TOOL_PATH + "/cddiff/Employees/Employees7.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithPackagesAndNoOWDiff";

    for (String cwDiffOption : cwDiffOptions) {
      for (String owDiffOption : owDiffOptions) {

        // when CD4CodeTool is used to compute the semantic difference
        String[] args = {
          "-i",
          cd1,
          "--semdiff",
          cd2,
          "--diffsize",
          "21",
          "-o",
          output,
          "--difflimit",
          "20",
          "--open-world",
          owDiffOption,
          cwDiffOption
        };
        CD4CodeTool.main(args);

        // no corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        if (odFiles == null) {
          assertEquals(0, Log.getErrorCount());
          return;
        }
        List<String> odFilePaths = new LinkedList<>();
        for (File odFile : odFiles) {
          if (odFile.getName().endsWith(".od")) {
            odFilePaths.add(odFile.toPath().toString());
          }
        }
        assertTrue(odFilePaths.isEmpty());

        // clean-up
        try {
          PathUtils.delete(Paths.get(output));
        } catch (IOException e) {
          Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
        }
      }
    }
  }

  @Test
  public void testValidityOfSemDiffWithPackages() {

    // given 2 CDs that are not semantically equivalent
    final String cd1 = TOOL_PATH + "/cddiff/Employees/Employees4.cd";
    final String cd2 = TOOL_PATH + "/cddiff/Employees/Employees3.cd";
    final String output = "target/generated/cddiff-test/ValidityOfCDDiffWithPackages";

    for (String cwDiffOption : cwDiffOptions) {
      // when CD4CodeTool is used to compute the semantic difference
      String[] args = {
        "-i",
        cd1,
        "--semdiff",
        cd2,
        "--diffsize",
        "21",
        "-o",
        output,
        "--difflimit",
        "20",
        cwDiffOption
      };
      CD4CodeTool.main(args);

      try {
        ASTCDCompilationUnit ast1 = Objects.requireNonNull(CDDiffUtil.loadCD(cd1)).deepClone();
        ASTCDCompilationUnit ast2 = Objects.requireNonNull(CDDiffUtil.loadCD(cd2)).deepClone();

        // then corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
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
    }
  }

  @Test
  public void testValidityOfOW2CWReduction() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = TOOL_PATH + "/cddiff/Employees/Employees7.cd";
    final String cd2 = TOOL_PATH + "/cddiff/Employees/Employees8.cd";
    final String output = "target/generated/cddiff-test/ValidityOfOW2CWReduction";

    for (String cwDiffOption : cwDiffOptions) {
      // when CD4CodeTool is used to compute the semantic difference
      String[] args = {
        "-i",
        cd1,
        "--semdiff",
        cd2,
        "--diffsize",
        "21",
        "-o",
        output,
        "--difflimit",
        "20",
        "--open-world",
        "reduction-based",
        cwDiffOption
      };
      CD4CodeTool.main(args);

      // no corresponding .od files are generated
      File[] odFiles = Paths.get(output).toFile().listFiles();
      Assert.assertNotNull(odFiles);

      try {
        for (File odFile : odFiles) {
          if (odFile.getName().endsWith(".od")) {
            Assert.assertTrue(
                new OD2CDMatcher()
                    .checkIfDiffWitness(
                        CDSemantics.STA_CLOSED_WORLD,
                        Paths.get(output + "/Employees7.cd").toFile(),
                        Paths.get(output + "/Employees8.cd").toFile(),
                        odFile));
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
        Log.warn("This should not happen!");
        Assert.fail();
      }
    }
  }

  @Test
  public void testValidityOfOW2CWReduction2() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = "src/tooltest/resources/doc/DigitalTwin3.cd";
    final String cd2 = "src/tooltest/resources/doc/DigitalTwin2.cd";
    final String output = "target/generated/cddiff-test/ValidityOfOW2CWReduction2";

    // when CD4CodeTool is used to compute the semantic difference
    String[] args = {
      "-i",
      cd1,
      "--semdiff",
      cd2,
      "--diffsize",
      "21",
      "-o",
      output,
      "--difflimit",
      "20",
      "--open-world",
      "reduction-based"
    };
    CD4CodeTool.main(args);

    // no corresponding .od files are generated
    File[] odFiles = Paths.get(output).toFile().listFiles();
    Assert.assertNotNull(odFiles);

    try {
      ASTCDCompilationUnit ast1 = Objects.requireNonNull(CDDiffUtil.loadCD(cd1)).deepClone();
      ASTCDCompilationUnit ast2 = Objects.requireNonNull(CDDiffUtil.loadCD(cd2)).deepClone();
      for (File odFile : odFiles) {
        if (odFile.getName().endsWith(".od")) {
          Assert.assertTrue(
              new OD2CDMatcher()
                  .checkIfDiffWitness(
                      CDSemantics.STA_OPEN_WORLD,
                      ast1,
                      ast2,
                      CDDiffUtil.loadODModel(odFile.getPath())));
          Assert.assertTrue(
              new OD2CDMatcher()
                  .checkIfDiffWitness(
                      CDSemantics.STA_CLOSED_WORLD,
                      Paths.get(output + "/DigitalTwin3.cd").toFile(),
                      Paths.get(output + "/DigitalTwin2.cd").toFile(),
                      odFile));
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      Log.warn("This should not happen!");
      Assert.fail();
    }
  }
}
