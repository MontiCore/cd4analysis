/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis.trafo;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.testcdbasis.TestCDBasisMill;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.monticore.testcdbasis._visitor.TestCDBasisTraverser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class CDBasisTrafoTest extends TestBasis {

  @Before
  public void setupAll() {
    // reset the GlobalScope
    TestCDBasisMill.reset();
    TestCDBasisMill.init();
    TestCDBasisMill.globalScope().clear();

    // reset the logger
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void trafoTest() throws IOException {
    TestCDBasisParser parser = TestCDBasisMill.parser();
    Optional<ASTCDCompilationUnit> astOpt = parser.parse(getFilePath("cdbasis/trafo/PkgMerge.cd"));

    assertFalse(parser.hasErrors());
    assertTrue(astOpt.isPresent());

    ASTCDDefinition cd = astOpt.get().getCDDefinition();

    // before trafo
    assertEquals(3, cd.getCDElementList().size());

    // after parse trafo
    TestCDBasisTraverser t = TestCDBasisMill.traverser();
    CDBasisDefaultPackageTrafo trafo = new CDBasisDefaultPackageTrafo();
    t.add4CDBasis(trafo);
    astOpt.get().accept(t);

    // after trafo
    assertEquals(2, cd.getCDElementList().size());
  }
}
