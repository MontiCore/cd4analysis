package de.monticore.odvalidity;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ModelLoaderTest {

  String validCDModel = "/CDModel.cd";

  String validODModel = "/ODModel.od";

  File cdModel1;

  File odModel1;

  ModelLoader loader;

  @Before
  public void reloadModels() {

    Log.enableFailQuick(false);
    String resources = "src/cddifftest/resources/de/monticore/odvalidity/";
    cdModel1 = new File(resources + validCDModel);
    odModel1 = new File(resources + validODModel);

    loader = new ModelLoader();
  }

  @Test
  public void loadCDModelTest() {
    try {

      Optional<ASTCDCompilationUnit> cd = loader.loadCDModel(cdModel1);
      Assert.assertTrue(cd.isPresent());

    } catch (FileNotFoundException e) {
      Assert.fail("File could not be found.");
    }
  }

  @Test
  public void loadODModelTest() {
    try {

      Optional<ASTODArtifact> od = loader.loadODModel(odModel1);
      Assert.assertTrue(od.isPresent());

    } catch (FileNotFoundException e) {
      Assert.fail("File could not be found.");
    }
  }
}
