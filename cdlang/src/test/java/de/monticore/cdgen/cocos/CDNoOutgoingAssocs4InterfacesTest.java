/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import java.io.IOException;

import de.monticore.cd4code.cocos.AbstractJavaGenCoCoTest;
import org.junit.jupiter.api.Test;

public class CDNoOutgoingAssocs4InterfacesTest extends AbstractJavaGenCoCoTest {
  
  @Override
  protected CD4CodeCoCoChecker createChecker() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    checker.addCoCo(new CDNoOutgoingAssocs4Interfaces());
    return checker;
  }
  
  @Test
  public void testValidAssociation() throws IOException {
    String model = "classdiagram Valid {" + "  interface I;" + "  class C;"
        + "  association C -> I;" + "}";
    runTest(model, false);
  }
  
  @Test
  public void testInvalidDirectedAssociation() throws IOException {
    String model = "classdiagram Invalid {" + "  interface I;" + "  class C;"
        + "  association I -> C;" + "}";
    runTestForErrorCode(model, CDNoOutgoingAssocs4Interfaces.ERROR_CODE);
  }
  
  @Test
  public void testInvalidBidirectionalAssociation() throws IOException {
    String model = "classdiagram Invalid {" + "  interface I;" + "  class C;"
        + "  association I <-> C;" + "}";
    runTestForErrorCode(model, CDNoOutgoingAssocs4Interfaces.ERROR_CODE);
  }
  
  @Test
  public void testUndirectedAssociation() throws IOException {
    // An undirected association is not definitively navigable, so it does not trigger an error.
    String model = "classdiagram Valid {" + "  interface I;" + "  class C;"
        + "  association I -- C;" + "}";
    runTest(model, false);
  }
  
}
