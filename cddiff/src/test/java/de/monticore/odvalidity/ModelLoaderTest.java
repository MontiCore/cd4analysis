/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ModelLoaderTest {

  String validCDModel = "/CDModel.cd";

  String validODModel = "/ODModel.od";

  File cdModel1;

  File odModel1;

  ModelLoader loader;

  @BeforeEach
  public void reloadModels() {

    LogStub.init();
    Log.enableFailQuick(false);
    String resources = "src/test/resources/de/monticore/odvalidity/";
    cdModel1 = new File(resources + validCDModel);
    odModel1 = new File(resources + validODModel);

    loader = new ModelLoader();
  }

  @Test
  public void loadCDModelTest() {
    try {

      Optional<ASTCDCompilationUnit> cd = loader.loadCDModel(cdModel1);
      assertTrue(cd.isPresent());

    } catch (FileNotFoundException e) {
      fail("File could not be found.");
    }
  }

  @Test
  public void loadODModelTest() {
    try {

      Optional<ASTODArtifact> od = loader.loadODModel(odModel1);
      assertTrue(od.isPresent());

    } catch (FileNotFoundException e) {
      fail("File could not be found.");
    }
  }
}
