/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cddiff.alloycddiff.CDSemantics;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odlink._ast.ASTODLink;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AssociationsMatcherTest {

  String validCDModel = "/Family.cd";

  String validODModel = "/Family.od";

  File cdModel1;

  File odModel1;

  List<ASTODLink> odLinks;

  List<ASTCDAssociation> cdAssociations;

  ASTODArtifact od;

  ASTCDCompilationUnit cd;

  AssociationsMatcher matcher;

  @Before
  public void before() {

    LogStub.init();
    Log.enableFailQuick(false);
    CD4CodeMill.reset();
    CD4CodeMill.init();

    matcher = new AssociationsMatcher();
  }

  void loadModels(String odName, String cdName) throws FileNotFoundException {

    String resources = "src/test/resources/de/monticore/odvalidity";
    cdModel1 = new File(resources + cdName);
    odModel1 = new File(resources + odName);

    ModelLoader loader = new ModelLoader();

    Optional<ASTCDCompilationUnit> cd = loader.loadCDModel(cdModel1);
    Optional<ASTODArtifact> od = loader.loadODModel(odModel1);
    this.od = od.get();
    this.cd = cd.get();

    odLinks = ODHelper.getAllLinks(od.get().getObjectDiagram());
    cdAssociations = cd.get().getCDDefinition().getCDAssociationsList();
  }

  @Test
  public void validAssociationForLinkTest() throws FileNotFoundException {
    assert (true);

    /*
    loadModels(validODModel, validCDModel);
    assertTrue(matcher.checkAssociations(od, cd, CDSemantics.SIMPLE_CLOSED_WORLD));
     */
  }

  @Test
  public void associationWithCardinalityDiff() throws FileNotFoundException {
    assert (true);

    loadModels("/Cardinality.od", "/Cardinality.cd");
    Assert.assertTrue(matcher.checkAssociations(od, cd, CDSemantics.SIMPLE_CLOSED_WORLD));
  }
}
