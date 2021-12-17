package de.monticore.cddiff;

import de.monticore.cd4code.CD4CodeCLI;
import de.se_rwth.artifacts.lang.matcher.CDDiffOD2CDMatcher;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class CDDiffCLITest {

  @Test
  public void testRunWithDiff() {
    // given 2 CDs
    final String cd1 = "src/test/resources/de/monticore/cddiff/Manager/cd2v2.cd";
    final String cd2 = "src/test/resources/de/monticore/cddiff/Manager/cd2v1.cd";
    final String output = "./diff_5_cd2v2_cd2v1/";

    //when CDDiff CLI is used to compute the semantic difference
    String[] args = { "-cddiff", "-cd1", cd1, "-cd2", cd2, "-scope", "5", "-o", output, "-limit", "20" };
    CD4CodeCLI cli = new CD4CodeCLI();
    cli.run(args);

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
    try {
      assertTrue(matcher.checkODConsistency(cd1, odFilePaths));
      assertFalse(matcher.checkODConsistency(cd2, odFilePaths));
    }
    catch (Exception e) {
      Log.error(e.getMessage());
      fail();
    }

    // clean-up
    for (File odFile : odFiles) {
      if (!odFile.delete()) {
        Log.warn("Could not delete " + odFile.getName());
      }
    }
    if (!Paths.get(output).toFile().delete()) {
      Log.warn("Could not delete " + output);
    }

  }

  @Test
  public void testRunWithoutDiff() {
    // given 2 CDs
    final String cd1 = "src/test/resources/de/monticore/cddiff/SimilarManagers/cdSimilarManagerv1"
        + ".cd";
    final String cd2 = "src/test/resources/de/monticore/cddiff/SimilarManagers/cdSimilarManagerv2"
        + ".cd";
    final String output = "./diff_5_cd2v2_cd2v1/";

    //when CDDiff CLI is used to compute the semantic difference
    String[] args = { "-cddiff", "-cd1", cd1, "-cd2", cd2, "-scope", "5", "-o", output, "-limit",
        "20" };
    CD4CodeCLI cli = new CD4CodeCLI();
    cli.run(args);

    //then corresponding .od files are generated
    File[] odFiles = Paths.get(output).toFile().listFiles();
    assertNotNull(odFiles);

    List<String> odFilePaths = new LinkedList<>();
    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        odFilePaths.add(odFile.toPath().toString());
      }
    }
    assertTrue(odFilePaths.isEmpty());

    if (!Paths.get(output).toFile().delete()) {
      Log.warn("Could not delete " + output);
    }

  }

}
