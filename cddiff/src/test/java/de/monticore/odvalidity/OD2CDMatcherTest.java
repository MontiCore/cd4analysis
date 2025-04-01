/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OD2CDMatcherTest {

  final OD2CDMatcher matcher = new OD2CDMatcher();

  protected final String resources = "src/test/resources/de/monticore/odvalidity/";

  String validCDModel = "/MyFamily.cd";

  String validODModel = "/MyFamily.od";

  File cdModel1;

  File odModel1;

  @BeforeEach
  public void reloadModels() {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.globalScope().init();
    LogStub.init();
    Log.enableFailQuick(false);
    cdModel1 = new File(resources + validCDModel);
    odModel1 = new File(resources + validODModel);
  }

  @Test
  public void singleInstanceCheckClosedWorldTest() {
    // TODO
    assertFalse(
        matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, cdModel1, odModel1));
  }

  @Test
  public void testOCLDiff() {
    final File cd1 = new File(resources + "IntegrationTest/OCLDiff/car.cd");

    File[] odFiles = Paths.get(resources + "IntegrationTest/OCLDiff").toFile().listFiles();
    assertNotNull(odFiles);

    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        assertTrue(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, cd1, odFile));
      }
    }
  }

  @Test
  public void testClass() {
    final File cd1 = new File(resources + "IntegrationTest/Class/CD1.cd");
    final File cd2 = new File(resources + "IntegrationTest/Class/CD2.cd");

    File[] odFiles = Paths.get(resources + "IntegrationTest/Class").toFile().listFiles();
    assertNotNull(odFiles);

    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        assertTrue(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, cd1, odFile));
        assertFalse(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, cd2, odFile));
      }
    }
  }

  @Test
  public void testCombination() {
    final File cd1 = new File(resources + "IntegrationTest/Combination/Employees1B.cd");
    final File cd2 = new File(resources + "IntegrationTest/Combination/Employees1A.cd");

    File[] odFiles = Paths.get(resources + "IntegrationTest/Combination").toFile().listFiles();
    assertNotNull(odFiles);

    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        assertTrue(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, cd1, odFile));
        assertFalse(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, cd2, odFile));
      }
    }
  }

  @Test
  public void testDirection() {
    final File cd1 = new File(resources + "IntegrationTest/Direction/Direction1G.cd");
    final File cd2 = new File(resources + "IntegrationTest/Direction/Direction1A.cd");

    File[] odFiles = Paths.get(resources + "IntegrationTest/Direction").toFile().listFiles();
    assertNotNull(odFiles);

    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        assertTrue(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, cd1, odFile));
        assertFalse(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, cd2, odFile));
      }
    }
  }

  @Test
  public void testOverlap() {
    final File cd1 = new File(resources + "IntegrationTest/Overlap/OverlapA.cd");
    final File cd2 = new File(resources + "IntegrationTest/Overlap/OverlapB.cd");

    File[] odFiles = Paths.get(resources + "IntegrationTest/Overlap").toFile().listFiles();
    assertNotNull(odFiles);

    for (File odFile : odFiles) {
      if (odFile.getName().endsWith(".od")) {
        assertTrue(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, cd1, odFile));
        assertFalse(matcher.checkODValidity(CDSemantics.SIMPLE_CLOSED_WORLD, cd2, odFile));
      }
    }
  }
}
