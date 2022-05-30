package de.monticore;

import de.monticore.cd4code.CD4CodeTestBasis;
import de.se_rwth.artifacts.lang.matcher.CDDiffOD2CDMatcher;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class CDDiffCLIToolTest extends CD4CodeTestBasis {

  final String[] methods = { "alloy-based", "reduction-based" };

  @Test
  public void testDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithDiff";

    //when CD4CodeTool is used to compute the semantic difference
    String[] args = { "-i", cd1, "--semdiff", cd2, "--diffsize", "21", "-o", output, "--difflimit",
        "20" };
    CD4CodeTool.main(args);

    //then corresponding .od files are generated
    File[] odFiles = Paths.get(output).toFile().listFiles();
    assertNotNull(odFiles);

    List<String> odFilePaths = new LinkedList<>();
    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        odFilePaths.add(odFile.toPath().toString());
      }
    }

    // and the ODs match cd1 but not cd2
    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    boolean first = false;
    boolean second = true;

    try {
      first = matcher.checkODConsistency(cd1, odFilePaths);
      second = matcher.checkODConsistency(cd2, odFilePaths);
    }
    catch (Exception e) {
      Log.error("0xCDD07: Matching failed due to the following exception " + e.getMessage());
      fail();
    }

    assertTrue(first);
    assertFalse(second);

    // clean-up
    try {
      FileUtils.forceDelete(Paths.get(output).toFile());
    }
    catch (IOException e) {
      Log.warn(String.format("Could not delete %s due to %s", output, e.getMessage()));
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

    //when CD4CodeTool is used to compute the semantic difference
    String[] args = { "-i", cd1, "--semdiff", cd2, "--diffsize", "21", "-o", output, "--difflimit",
        "20" };
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

  @Test
  public void testDefaultDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithDefaultDiff";

    //when CD4CodeTool is used to compute the semantic difference
    String[] args = { "-i", cd1, "--semdiff", cd2, "-o", output };
    CD4CodeTool.main(args);

    //then corresponding .od files are generated
    File[] odFiles = Paths.get(output).toFile().listFiles();
    assertNotNull(odFiles);

    List<String> odFilePaths = new LinkedList<>();
    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        odFilePaths.add(odFile.toPath().toString());
      }
    }

    // and the ODs match cd1 but not cd2
    CDDiffOD2CDMatcher matcher = new CDDiffOD2CDMatcher();
    boolean first = false;
    boolean second = true;

    try {
      first = matcher.checkODConsistency(cd1, odFilePaths);
      second = matcher.checkODConsistency(cd2, odFilePaths);
    }
    catch (Exception e) {
      Log.error("0xCDD11: Matching failed due to the following exception " + e.getMessage());
      fail();
    }

    assertTrue(first);
    assertFalse(second);

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

    for (String method : methods) {
      //when CD4CodeTool is used to compute the semantic difference
      String[] args = { "-i", cd1, "--semdiff", cd2, "--diffsize", "21", "-o", output,
          "--difflimit", "20", "--open-world", method };
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

  @Test
  public void testNoOpenWorldDiff() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees2.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithoutOWDiff";

    for (String method : methods) {

      //when CD4CodeTool is used to compute the semantic difference
      String[] args = { "-i", cd1, "--semdiff", cd2, "--diffsize", "21", "-o", output,
          "--difflimit", "20", "--open-world", method };
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

  @Test
  public void testNoOpenWorldDiff4Abstract2Interface() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 =
        "src/cddifftest/resources/de/monticore/cddiff/Abstract2Interface" + "/AbstractPerson.cd";
    final String cd2 =
        "src/cddifftest/resources/de/monticore/cddiff/Abstract2Interface" + "/InterfacePerson.cd";
    final String output = "./target/generated/cddiff-test/CLITestAbstract2InterfaceNoOWDiff";

    for (String method : methods) {
      //when CD4CodeTool is used to compute the semantic difference
      String[] args = { "-i", cd1, "--semdiff", cd2, "--diffsize", "21", "-o", output,
          "--difflimit", "20", "--open-world", method };
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

  @Test
  public void testNoOpenWorldDiffWithPackages() {
    // given 2 CDs such that the first is a refinement of the second under an open-world assumption
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees8.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Employees/Employees7.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithPackagesAndNoOWDiff";

    for (String method : methods) {

      //when CD4CodeTool is used to compute the semantic difference
      String[] args = { "-i", cd1, "--semdiff", cd2, "--diffsize", "21", "-o", output,
          "--difflimit", "20", "--open-world", method };
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
