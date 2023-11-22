/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class STAMatchingTest {
  String[] validBaseCDModelsOpenWorld = {
    "MultiBaseNoOpenDiff.cd", "MultiBaseOpenSubsetDiff.cd", "MultiBaseOpenCycleDiff.cd"
  };

  String[] validCompareCDModelsOpenWorld = {
    "MultiCompNoOpenDiff.cd", "MultiCompOpenSubsetDiff.cd", "MultiCompOpenCycleDiff.cd"
  };

  String validODModel = "/MultiOdNoStereotype.od";

  boolean[] openDiff = {false, true, true};

  String[] validBaseCDModelsClosedWorld = {"MultiBaseClosedDiff.cd", "MultiBaseClosedNoDiff.cd"};

  String[] validCompareCDModelsClosedWorld = {"MultiCompClosedDiff.cd", "MultiCompClosedNoDiff.cd"};

  boolean[] closedDiff = {true, false};

  File cdBaseModel;

  File cdCompareModel;

  File odModel;

  ModelLoader loader = new ModelLoader();

  @Before
  public void loadModels() {
    Log.enableFailQuick(false);

    CD4CodeMill.reset();
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().init();
  }

  @Test
  public void testMultiInstanceClosedWorldNoStereotype() throws FileNotFoundException {

    STAObjectMatcher matcher = new STAObjectMatcher(new OD2CDMatcher());

    String resources = "src/test/resources/de/monticore/odvalidity/STAObjectMatcher/";
    odModel = new File(resources + validODModel);
    ASTODArtifact od = loader.loadODModel(odModel).get();

    for (int i = 0; i < validBaseCDModelsClosedWorld.length; i++) {
      cdBaseModel = new File(resources + validBaseCDModelsClosedWorld[i]);
      cdCompareModel = new File(resources + validCompareCDModelsClosedWorld[i]);
      ASTCDCompilationUnit baseCD = loader.loadCDModel(cdBaseModel).get();
      ASTCDCompilationUnit compCD = loader.loadCDModel(cdCompareModel).get();

      if (closedDiff[i]) {
        Assert.assertTrue(matcher.isDiffWitness(CDSemantics.STA_CLOSED_WORLD, baseCD, compCD, od));
      } else {
        Assert.assertFalse(matcher.isDiffWitness(CDSemantics.STA_CLOSED_WORLD, baseCD, compCD, od));
      }
    }
  }

  @Test
  public void testMultiInstanceOpenWorldNoStereotype() throws FileNotFoundException {

    STAObjectMatcher matcher = new STAObjectMatcher(new OD2CDMatcher());

    String resources = "src/test/resources/de/monticore/odvalidity/STAObjectMatcher/";
    odModel = new File(resources + validODModel);
    ASTODArtifact od = loader.loadODModel(odModel).get();

    for (int i = 0; i < validBaseCDModelsOpenWorld.length; i++) {
      cdBaseModel = new File(resources + validBaseCDModelsOpenWorld[i]);
      cdCompareModel = new File(resources + validCompareCDModelsOpenWorld[i]);
      ASTCDCompilationUnit baseCD = loader.loadCDModel(cdBaseModel).get();
      ASTCDCompilationUnit compCD = loader.loadCDModel(cdCompareModel).get();

      if (openDiff[i]) {
        Assert.assertTrue(matcher.isDiffWitness(CDSemantics.STA_OPEN_WORLD, baseCD, compCD, od));
      } else {
        Assert.assertFalse(matcher.isDiffWitness(CDSemantics.STA_OPEN_WORLD, baseCD, compCD, od));
      }
    }
  }
}
