package de.monticore;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odvalidity.OD2CDMatcher;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class CDDiffCLIToolTest extends CD4CodeTestBasis {

  final String[] methods = { "alloy-based", "reduction-based" };

  final String[] commands = { "--semdiff", "--jsemdiff" };

  @Test
  public void testDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithDiff";

    for (String command : commands) {
      // when CD4CodeTool is used to compute the semantic difference
      String[] args = { "-i", cd1, command, cd2, "--diffsize", "21", "-o", output, "--difflimit",
          "20" };
      CD4CodeTool.main(args);

      // then corresponding .od files are generated
      File[] odFiles = Paths.get(output).toFile().listFiles();
      assertNotNull(odFiles);

      // now check for each OD if it is a diff-witness, i.e., in sem(cd1)\sem(cd2)
      boolean isWitness = false;

      File baseCDFile = Paths.get(cd1).toFile();
      File compareCDFile = Paths.get(cd2).toFile();

      for (File odFile : odFiles) {
        if (odFile.getName().endsWith(".od")) {
          try {
            isWitness = new OD2CDMatcher().checkIfDiffWitness(CDSemantics.SIMPLE_CLOSED_WORLD,
                baseCDFile, compareCDFile, odFile);
          }
          catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
          }
          Assert.assertTrue(isWitness);
        }
      }

      // clean-up
      try {
        FileUtils.forceDelete(Paths.get(output).toFile());
      }
      catch (IOException e) {
        Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
      }
    }

  }

  @Test
  public void testNoDiff() {
    // given 2 CDs that are semantically equivalent
    final String cd1 =
        "src/cddifftest/resources/de/monticore/cddiff/SimilarManagers/cdSimilarManagerv1" + ".cd";
    final String cd2 =
        "src/cddifftest/resources/de/monticore/cddiff/SimilarManagers/cdSimilarManagerv2" + ".cd";
    final String output = "./target/generated/cddiff-test/CLITestWithoutDiff";

    for (String command : commands) {
      //when CD4CodeTool is used to compute the semantic difference
      String[] args = { "-i", cd1, command, cd2, "--diffsize", "21", "-o", output, "--difflimit",
          "20" };
      CD4CodeTool.main(args);

      //no corresponding .od files are generated
      File[] odFiles = Paths.get(output).toFile().listFiles();

      if (odFiles == null) {
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
        FileUtils.forceDelete(Paths.get(output).toFile());
      }
      catch (IOException e) {
        Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
      }
    }

  }

  @Test
  public void testDefaultDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithDefaultDiff";

    //when CD4CodeTool is used to compute the semantic difference
    String[] args = { "-i", cd1, "--semdiff", cd2, "-o", output };
    CD4CodeTool.main(args);

    // then corresponding .od files are generated
    File[] odFiles = Paths.get(output).toFile().listFiles();
    assertNotNull(odFiles);

    // now check for each OD if it is a diff-witness, i.e., in sem(cd1)\sem(cd2)
    boolean isWitness = false;

    File baseCDFile = Paths.get(cd1).toFile();
    File compareCDFile = Paths.get(cd2).toFile();

    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        try {
          isWitness = new OD2CDMatcher().checkIfDiffWitness(CDSemantics.SIMPLE_CLOSED_WORLD,
              baseCDFile, compareCDFile, odFile);
        }
        catch (IOException e) {
          e.printStackTrace();
          Assert.fail();
        }
        Assert.assertTrue(isWitness);
      }
    }

    // clean-up
    try {
      FileUtils.forceDelete(Paths.get(output).toFile());
    }
    catch (IOException e) {
      Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
    }

  }

  @Test
  public void testOpenWorldDiff() {
    // given 2 CDs such that the first is simply missing an association defined in the second
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees0.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithOWDiff";
    for (String command : commands) {
      for (String method : methods) {
        //when CD4CodeTool is used to compute the semantic difference
        String[] args = { "-i", cd1, command, cd2, "--diffsize", "21", "-o", output, "--difflimit",
            "20", "--open-world", method };
        CD4CodeTool.main(args);

        //some corresponding .od files are generated
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
          FileUtils.forceDelete(Paths.get(output).toFile());
        }
        catch (IOException e) {
          Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
        }
      }
    }

  }

  @Test
  public void testNoOpenWorldDiff() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithoutOWDiff";

    for (String command : commands) {
      for (String method : methods) {

        //when CD4CodeTool is used to compute the semantic difference
        String[] args = { "-i", cd1, command, cd2, "--diffsize", "21", "-o", output, "--difflimit",
            "20", "--open-world", method };
        CD4CodeTool.main(args);

        //no corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        assertNotNull(odFiles);

        List<String> odFilePaths = new LinkedList<>();
        for (File odFile : odFiles) {
          if (odFile.getName().endsWith(".od")) {
            odFilePaths.add(odFile.toPath().toString());
          }
        }
        assertTrue(odFilePaths.isEmpty());

        // clean-up
        try {
          FileUtils.forceDelete(Paths.get(output).toFile());
        }
        catch (IOException e) {
          Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
        }
      }
    }

  }

  @Test
  public void testNoOpenWorldDiff4Abstract2Interface() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 =
        "src/cddifftest/resources/de/monticore/cddiff/Abstract2Interface" + "/AbstractPerson.cd";
    final String cd2 =
        "src/cddifftest/resources/de/monticore/cddiff/Abstract2Interface" + "/InterfacePerson.cd";
    final String output = "./target/generated/cddiff-test/CLITestAbstract2InterfaceNoOWDiff";

    for (String command : commands) {
      for (String method : methods) {
        //when CD4CodeTool is used to compute the semantic difference
        String[] args = { "-i", cd1, command, cd2, "--diffsize", "21", "-o", output, "--difflimit",
            "20", "--open-world", method };
        CD4CodeTool.main(args);

        //no corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        assertNotNull(odFiles);

        List<String> odFilePaths = new LinkedList<>();
        for (File odFile : odFiles) {
          if (odFile.getName().endsWith(".od")) {
            odFilePaths.add(odFile.toPath().toString());
          }
        }
        assertTrue(odFilePaths.isEmpty());

        // clean-up
        try {
          FileUtils.forceDelete(Paths.get(output).toFile());
        }
        catch (IOException e) {
          Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
        }
      }
    }
  }

  @Test
  public void testNoOpenWorldDiffWithPackages() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees7.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithPackagesAndNoOWDiff";

    for (String command : commands) {
      for (String method : methods) {

        //when CD4CodeTool is used to compute the semantic difference
        String[] args = { "-i", cd1, command, cd2, "--diffsize", "21", "-o", output, "--difflimit",
            "20", "--open-world", method };
        CD4CodeTool.main(args);

        //no corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        assertNotNull(odFiles);

        List<String> odFilePaths = new LinkedList<>();
        for (File odFile : odFiles) {
          if (odFile.getName().endsWith(".od")) {
            odFilePaths.add(odFile.toPath().toString());
          }
        }
        assertTrue(odFilePaths.isEmpty());

        // clean-up
        try {
          FileUtils.forceDelete(Paths.get(output).toFile());
        }
        catch (IOException e) {
          Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
        }
      }
    }

  }

  //Todo: Remove redundant tests and old Matcher
  @Test
  public void testValidityOfCDDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd";
    final String output = "target/generated/cddiff-test/ValidityOfCDDiff";

    //when CD4CodeTool is used to compute the semantic difference
    String[] args = { "-i", cd1, "--jsemdiff", cd2, "--diffsize", "21", "-o", output, "--difflimit",
        "20" };
    CD4CodeTool.main(args);

    //then corresponding .od files are generated
    File[] odFiles = Paths.get(output).toFile().listFiles();
    Assert.assertNotNull(odFiles);

    // and the ODs match cd1 but not cd2
    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        Assert.assertTrue(new OD2CDMatcher().checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD,
            Paths.get(cd1).toFile(), odFile));
        Assert.assertFalse(new OD2CDMatcher().checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD,
            Paths.get(cd2).toFile(), odFile));
      }
    }
  }

  @Test
  public void testValidityOfCDDiffWithPackages() {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.globalScope().init();
    Log.init();
    Log.enableFailQuick(false);

    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees4.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees3.cd";
    final String output = "target/generated/cddiff-test/ValidityOfCDDiffWithPackages";

    //when CD4CodeTool is used to compute the semantic difference
    String[] args = { "-i", cd1, "--jsemdiff", cd2, "--diffsize", "21", "-o", output, "--difflimit",
        "20" };
    CD4CodeTool.main(args);

    //then corresponding .od files are generated
    File[] odFiles = Paths.get(output).toFile().listFiles();
    Assert.assertNotNull(odFiles);

    // and the ODs match cd1 but not cd2

    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        Assert.assertTrue(new OD2CDMatcher().checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD,
            Paths.get(cd1).toFile(), odFile));
        Assert.assertFalse(new OD2CDMatcher().checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD,
            Paths.get(cd2).toFile(), odFile));
      }
    }

  }

  @Test
  public void testValidityOfOW2CWReduction() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees7.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd";
    final String output = "target/generated/cddiff-test/ValidityOfOW2CWReduction";

    //TODO: fix for --jsemdiff
    //when CD4CodeTool is used to compute the semantic difference
    String[] args = { "-i", cd1, "--jsemdiff", cd2, "--diffsize", "21", "-o", output, "--difflimit",
        "20", "--open-world" };
    CD4CodeTool.main(args);

    //no corresponding .od files are generated
    File[] odFiles = Paths.get(output).toFile().listFiles();
    Assert.assertNotNull(odFiles);

    try {
      for (File odFile : odFiles) {
        if (odFile.getName().endsWith(".od")) {
          Assert.assertTrue(
              new OD2CDMatcher().checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_CLOSED_WORLD,
                  Paths.get(output + "/Employees7.cd").toFile(),
                  Paths.get(output + "/Employees8.cd").toFile(), odFile));
        }
      }

    }
    catch (Exception e) {
      e.printStackTrace();
      Log.warn("This should not happen!");
      Assert.fail();
    }

  }

}
