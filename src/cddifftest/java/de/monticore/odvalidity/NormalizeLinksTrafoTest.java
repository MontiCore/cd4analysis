package de.monticore.odvalidity;

import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTObjectDiagram;
import de.monticore.odlink._ast.ASTODBiDir;
import de.monticore.odlink._ast.ASTODLeftToRightDir;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odlink._ast.ASTODRightToLeftDir;
import de.monticore.odlink.prettyprinter.ODLinkFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

public class NormalizeLinksTrafoTest {

  NormalizeLinksTrafo trafo;

  @Before
  public void setUp() {

    OD4ReportMill.reset();
    OD4ReportMill.init();
    Log.enableFailQuick(false);
    trafo = new NormalizeLinksTrafo();

  }

  @Test
  public void transformLinksTest() throws FileNotFoundException {
    assert (true);

    ASTObjectDiagram od = loadModel("Cardinality.od");

    List<ASTODLink> odLinks = ODHelper.getAllLinks(od);

    //transform
    List<ASTODLink> result = trafo.transformLinksToLTR(odLinks);

    Assert.assertEquals(7, result.size());
    //check directions
    result.forEach(l -> Assert.assertTrue(l.getODLinkDirection() instanceof ASTODLeftToRightDir));
    result.forEach(l -> Assert.assertFalse((l.getODLinkDirection() instanceof ASTODRightToLeftDir)));
    result.forEach(l -> Assert.assertFalse(l.getODLinkDirection() instanceof ASTODBiDir));

    //check roles
    result.forEach(l -> Assert.assertTrue(l.getODLinkRightSide().isPresentRole()));

    ODLinkFullPrettyPrinter p = new ODLinkFullPrettyPrinter();
    result.forEach(l -> System.out.println(p.prettyprint(l)));

  }

  ASTObjectDiagram loadModel(String odName) throws FileNotFoundException {

    String resources = "src/cddifftest/resources/de/monticore/odvalidity/";
    File odModel = new File(resources + odName);

    ModelLoader loader = new ModelLoader();

    Optional<ASTODArtifact> od = loader.loadODModel(odModel);
    return od.get().getObjectDiagram();

  }

  @After
  public void reset(){
    OD4ReportMill.reset();
  }

}
