package de.monticore;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.CD4CodeTool;
import de.se_rwth.artifacts.lang.matcher.CDDiffOD2CDMatcher;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class CDDiffCLITest {

  @Before
  public void setup() {
    CD4CodeMill.reset();
  }

  @Test
  public void testRunWithDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Manager/Employees2.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Manager/Employees1.cd";
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
  public void testRunWithoutDiff() {
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
  public void testRunWithDefaultDiff() {
    // given 2 CDs that are not semantically equivalent
    final String cd1 = "src/cddifftest/resources/de/monticore/cddiff/Manager/Employees2.cd";
    final String cd2 = "src/cddifftest/resources/de/monticore/cddiff/Manager/Employees1.cd";
    final String output = "./target/generated/cddiff-test/CLITestWithDefaultDiff";

    //when CD4CodeTool is used to compute the semantic difference
    String[] args = { "-i", cd1, "--semdiff", cd2, "-o", output};
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

}
