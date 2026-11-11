/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import java.io.IOException;

import de.monticore.cd4code.cocos.AbstractJavaGenCoCoTest;
import org.junit.jupiter.api.Test;

public class CDNoAttributesInInterfacesTest extends AbstractJavaGenCoCoTest {
  
  @Override
  protected CD4CodeCoCoChecker createChecker() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    checker.addCoCo(new CDNoAttributesInInterfaces());
    return checker;
  }
  
  @Test
  public void testValidInterface() throws IOException {
    String model = "classdiagram Valid { interface I; }";
    runTest(model, false);
  }
  
  @Test
  public void testInterfaceWithMethods() throws IOException {
    String model = "classdiagram Valid { interface I { void doSomething(); } }";
    runTest(model, false);
  }
  
  @Test
  public void testInvalidInterfaceWithAttribute() throws IOException {
    String model = "import java.lang.String;" + System.lineSeparator()
        + "classdiagram Invalid { interface I { String name; } }";
    runTestForErrorCode(model, CDNoAttributesInInterfaces.ERROR_CODE);
  }
  
  @Test
  public void testInvalidInterfaceWithMultipleAttributes() throws IOException {
    String model = "import java.lang.String;" + System.lineSeparator()
        + "classdiagram Invalid { interface I { String name; int age; } }";
    runTestForErrorCode(model, CDNoAttributesInInterfaces.ERROR_CODE);
  }
  
}
