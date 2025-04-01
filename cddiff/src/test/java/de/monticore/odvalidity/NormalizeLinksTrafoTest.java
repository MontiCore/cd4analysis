/* (c) https://github.com/MontiCore/monticore */
package de.monticore.odvalidity;

import de.monticore.od4report.OD4ReportMill;
import de.monticore.odbasis._ast.ASTODArtifact;
import de.monticore.odbasis._ast.ASTObjectDiagram;
import de.monticore.odlink._ast.ASTODBiDir;
import de.monticore.odlink._ast.ASTODLeftToRightDir;
import de.monticore.odlink._ast.ASTODLink;
import de.monticore.odlink._ast.ASTODRightToLeftDir;
import de.monticore.odlink._prettyprint.ODLinkFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NormalizeLinksTrafoTest {

  NormalizeLinksTrafo trafo;

  @BeforeEach
  public void setUp() {

    OD4ReportMill.reset();
    OD4ReportMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
    trafo = new NormalizeLinksTrafo();
  }

  @Test
  public void transformLinksTest() throws FileNotFoundException {
    assert (true);

    ASTObjectDiagram od = loadModel("Cardinality.od");

    List<ASTODLink> odLinks = ODHelper.getAllLinks(od);

    // transform
    List<ASTODLink> result = trafo.transformLinksToLTR(odLinks);

    assertEquals(7, result.size());
    // check directions
    result.forEach(l -> assertInstanceOf(ASTODLeftToRightDir.class, l.getODLinkDirection()));
    result.forEach(
        l -> assertFalse((l.getODLinkDirection() instanceof ASTODRightToLeftDir)));
    result.forEach(l -> assertFalse(l.getODLinkDirection() instanceof ASTODBiDir));

    // check roles
    result.forEach(l -> assertTrue(l.getODLinkRightSide().isPresentRole()));

    ODLinkFullPrettyPrinter p = new ODLinkFullPrettyPrinter(new IndentPrinter(), false);
    result.forEach(l -> System.out.println(p.prettyprint(l)));
  }

  ASTObjectDiagram loadModel(String odName) throws FileNotFoundException {

    String resources = "src/test/resources/de/monticore/odvalidity/";
    File odModel = new File(resources + odName);

    ModelLoader loader = new ModelLoader();

    Optional<ASTODArtifact> od = loader.loadODModel(odModel);
    return od.get().getObjectDiagram();
  }

  @AfterEach
  public void reset() {
    OD4ReportMill.reset();
  }
}
