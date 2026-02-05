/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class CDSingleClassInheritanceTest extends AbstractJavaGenCoCoTest {
  
  @Override
  protected CD4CodeCoCoChecker createChecker() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    checker.addCoCo(new CDSingleClassInheritance());
    return checker;
  }
  
  @Test
  public void testNoInheritance() throws IOException {
    String model = "classdiagram Valid { class A; }";
    runTest(model, false);
  }
  
  @Test
  public void testSingleInheritance() throws IOException {
    String model = "classdiagram Valid { class A; class B extends A; }";
    runTest(model, false);
  }
  
  @Test
  public void testMultipleInterfaces() throws IOException {
    String model = "classdiagram Valid {" + "  interface I1;" + "  interface I2;"
        + "  class A implements I1, I2;" + "}";
    runTest(model, false);
  }
  
  @Test
  public void testInvalidMultipleInheritance() throws IOException {
    String model = "classdiagram Invalid { class A; class B; class C extends A, B; }";
    runTestForErrorCode(model, CDSingleClassInheritance.ERROR_CODE);
  }
  
}
