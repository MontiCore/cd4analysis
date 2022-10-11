package de.monticore;

import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odvalidity.OD2CDMatcher;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.file.PathUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class CDDiffCLIToolTest extends CD4CodeTestBasis {

  final String[] owDiffOptions = { "alloy-based", "reduction-based" };

  final String[] cwDiffOptions = { "", "--rule-based" };

  @Test
  public void testDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithDiff";

    for (String cwDiffOption : cwDiffOptions) {
      // when CD4CodeTool is used to compute the semantic difference
      String[] args = {"--semdiff", cd1, cd2, "--diffsize", "21", "-o", output, "--difflimit",
          "20", cwDiffOption };
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
        PathUtils.delete(Paths.get(output));
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

    for (String cwDiffOption : cwDiffOptions) {
      //when CD4CodeTool is used to compute the semantic difference
      String[] args = {"--semdiff", cd1, cd2, "--diffsize", "21", "-o", output,
          "--difflimit", "20", cwDiffOption };
      CD4CodeTool.main(args);

      //no corresponding .od files are generated
      File[] odFiles = Paths.get(output).toFile().listFiles();
      if (odFiles == null){
        assertEquals(0,Log.getErrorCount());
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
    String[] args = {"--semdiff", cd1, cd2, "-o", output };
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
      PathUtils.delete(Paths.get(output));
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
    for (String cwDiffOption : cwDiffOptions) {
      for (String owDiffOption : owDiffOptions) {
        //when CD4CodeTool is used to compute the semantic difference
        String[] args = {"--semdiff", cd1, cd2, "--diffsize", "21", "-o", output, "--difflimit",
            "20", "--open-world", owDiffOption , cwDiffOption};
        CD4CodeTool.main(args);

        //some corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        if (odFiles == null){
          assertEquals(0,Log.getErrorCount());
          return;
        }
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

    for (String cwDiffOption : cwDiffOptions) {
      for (String owDiffOption : owDiffOptions) {

        //when CD4CodeTool is used to compute the semantic difference
        String[] args = {"--semdiff", cd1, cd2, "--diffsize", "21", "-o", output, "--difflimit",
            "20", "--open-world", owDiffOption, cwDiffOption };
        CD4CodeTool.main(args);

        //no corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        if (odFiles == null){
          assertEquals(0,Log.getErrorCount());
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

    for (String cwDiffOption : cwDiffOptions) {
      for (String owDiffOption : owDiffOptions) {
        //when CD4CodeTool is used to compute the semantic difference
        String[] args = {"--semdiff", cd1, cd2, "--diffsize", "21", "-o", output, "--difflimit",
            "20", "--open-world", owDiffOption, cwDiffOption};
        CD4CodeTool.main(args);

        //no corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        if (odFiles == null){
          assertEquals(0,Log.getErrorCount());
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

    for (String cwDiffOption : cwDiffOptions) {
      for (String owDiffOption : owDiffOptions) {

        //when CD4CodeTool is used to compute the semantic difference
        String[] args = {"--semdiff", cd1, cd2, "--diffsize", "21", "-o", output, "--difflimit",
            "20", "--open-world", owDiffOption, cwDiffOption};
        CD4CodeTool.main(args);

        //no corresponding .od files are generated
        File[] odFiles = Paths.get(output).toFile().listFiles();
        if (odFiles == null){
          assertEquals(0,Log.getErrorCount());
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
        }
        catch (IOException e) {
          Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
        }
      }
    }

  }

  @Test
  public void testValidityOfCDDiffWithPackages() {

    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees4.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees3.cd";
    final String output = "target/generated/cddiff-test/ValidityOfCDDiffWithPackages";

    for (String cwDiffOption : cwDiffOptions) {
      //when CD4CodeTool is used to compute the semantic difference
      String[] args = {"--semdiff", cd1, cd2, "--diffsize", "21", "-o", output, "--difflimit",
          "20", cwDiffOption};
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

  }

  @Test
  public void testValidityOfOW2CWReduction() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees7.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd";
    final String output = "target/generated/cddiff-test/ValidityOfOW2CWReduction";

    for (String cwDiffOption : cwDiffOptions) {
      //when CD4CodeTool is used to compute the semantic difference
      String[] args = {"--semdiff", cd1, cd2, "--diffsize", "21", "-o", output,
          "--difflimit", "20", "--open-world", "reduction-based", cwDiffOption};
      CD4CodeTool.main(args);

      //no corresponding .od files are generated
      File[] odFiles = Paths.get(output).toFile().listFiles();
      Assert.assertNotNull(odFiles);

      try {
        for (File odFile : odFiles) {
          if (odFile.getName().endsWith(".od")) {
            Assert.assertTrue(new OD2CDMatcher().checkIfDiffWitness(CDSemantics.MULTI_INSTANCE_CLOSED_WORLD,
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

}
