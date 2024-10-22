/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClassMatcherTest {
  final String resources = "src/test/resources/de/monticore/odvalidity/classmatcher/";

  final String classMatcherCDModelPath = resources + "/ClassMatcherCD.cd";

  final String enumFailingODModelPath = resources + "/EnumFailing.od";
  final String failingListODModelPath = resources + "/FailingList.od";
  final String namedAttributeObjectODModelPath = resources + "/NamedAttributeObject.od";
  final String NamingODModelPath = resources + "/Naming.od";
  final String notExistingObjectODModelPath = resources + "/NotExistingObject.od";
  final String packagesODModelPath = resources + "/Packages.od";
  final String superclassObjectODModelPath = resources + "/SuperclassObject.od";
  final String totalFailingODModelPath = resources + "/TotalFailing.od";
  final String withAttributeTypesCWODModelPath = resources + "/WithAttributeTypesCW.od";
  final String withAttributeTypesOWODModelPath = resources + "/WithAttributeTypesOW.od";
  final String withoutAttributeTypesCWODModelPath = resources + "/WithoutAttributeTypesCW.od";
  final String withoutAttributeTypesOWODModelPath = resources + "/WithoutAttributeTypesOW.od";

  ASTCDCompilationUnit cd;

  ASTODArtifact od;

  final ModelLoader loader = new ModelLoader();

  final ClassMatcher classMatcher = new ClassMatcher();

  @Before
  public void initTests() {
    LogStub.init();
    CD4CodeMill.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void classMatcherTest() throws FileNotFoundException {

    reloadCD(classMatcherCDModelPath);

    simpleAssertion(enumFailingODModelPath, false, CDSemantics.SIMPLE_CLOSED_WORLD);

    simpleAssertion(enumFailingODModelPath, true, CDSemantics.SIMPLE_OPEN_WORLD);

    closedOpenWorldAssertion(failingListODModelPath, false);

    closedOpenWorldAssertion(namedAttributeObjectODModelPath, true);

    closedOpenWorldAssertion(NamingODModelPath, true);

    simpleAssertion(notExistingObjectODModelPath, false, CDSemantics.SIMPLE_CLOSED_WORLD);

    simpleAssertion(notExistingObjectODModelPath, true, CDSemantics.SIMPLE_OPEN_WORLD);

    closedOpenWorldAssertion(packagesODModelPath, true);

    closedOpenWorldAssertion(superclassObjectODModelPath, true);

    closedOpenWorldAssertion(totalFailingODModelPath, false);

    simpleAssertion(withAttributeTypesCWODModelPath, true, CDSemantics.SIMPLE_CLOSED_WORLD);

    simpleAssertion(withAttributeTypesOWODModelPath, false, CDSemantics.SIMPLE_CLOSED_WORLD);

    simpleAssertion(withAttributeTypesOWODModelPath, true, CDSemantics.SIMPLE_OPEN_WORLD);

    simpleAssertion(withoutAttributeTypesCWODModelPath, true, CDSemantics.SIMPLE_CLOSED_WORLD);

    simpleAssertion(withoutAttributeTypesOWODModelPath, false, CDSemantics.SIMPLE_CLOSED_WORLD);

    simpleAssertion(withoutAttributeTypesOWODModelPath, true, CDSemantics.SIMPLE_OPEN_WORLD);
  }

  private void reloadCD(String cdPath) throws FileNotFoundException {
    File cdModel = new File(cdPath);
    cd = loader.loadCDModel(cdModel).get();
  }

  private void reloadOD(String odPath) throws FileNotFoundException {
    File odModel = new File(odPath);
    od = loader.loadODModel(odModel).get();
  }

  private void closedOpenWorldAssertion(String odPath, boolean shouldPass)
      throws FileNotFoundException {
    reloadOD(odPath);

    Assert.assertEquals(
        shouldPass,
        classMatcher.checkAllObjectsInClassDiagram(od, cd, CDSemantics.SIMPLE_CLOSED_WORLD));
    Assert.assertEquals(
        shouldPass,
        classMatcher.checkAllObjectsInClassDiagram(od, cd, CDSemantics.SIMPLE_OPEN_WORLD));
  }

  private void simpleAssertion(String odPath, boolean shouldPass, CDSemantics semantic)
      throws FileNotFoundException {
    reloadOD(odPath);

    Assert.assertEquals(shouldPass, classMatcher.checkAllObjectsInClassDiagram(od, cd, semantic));
  }
}
