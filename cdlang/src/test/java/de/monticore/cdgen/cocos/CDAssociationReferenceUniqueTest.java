/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code.cocos.AbstractJavaGenCoCoTest;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class CDAssociationReferenceUniqueTest extends AbstractJavaGenCoCoTest {
  
  @Override
  protected CD4CodeCoCoChecker createChecker() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    checker.addCoCo(new CDAssociationReferenceUnique());
    return checker;
  }
  
  private static final String ERROR_CODE = "0xCDCE1";
  
  @Test
  public void testUniqueNames() throws IOException {
    String model = "classdiagram UniqueAssocs {" + "  class A; class B;" + "  association A -> B;"
        + "  association A (l) -> (r) B;" + "}";
    runTest(model, false);
  }
  
  @Test
  public void testDuplicatesWithExplicitRoles() throws IOException {
    String model = "classdiagram DuplicateAssocs {" + "  class A; class B;"
        + "  association A -> B;" + "  association A -> (b) B;" + "}";
    runTestForErrorCode(model, ERROR_CODE);
  }
  
  @Test
  public void testDuplicatesWithImplicitRoles() throws IOException {
    String model = "classdiagram DuplicateAssocs {" + "  class A; class B;"
        + "  association A -> B;" + "  association A -> B;" + "}";
    runTestForErrorCode(model, ERROR_CODE);
  }
  
  @Test
  public void testUniqueExplicitRole() throws IOException {
    String model = "classdiagram UniqueAssocs {" + "  class A; class B;" + "  association A -> B;"
        + "  association A -> (other) B;" + "}";
    runTest(model, false);
  }
  
  @Test
  public void testDuplicatesInReverse() throws IOException {
    String model = "classdiagram DuplicateAssocs {" + "  class A; class B;"
        + "  association A -> B;" + "  association B <- A;" + "}";
    runTestForErrorCode(model, ERROR_CODE);
  }
  
  @Test
  public void testUniqueAssocName() throws IOException {
    String model = "classdiagram UniqueAssocs {" + "  class A; class B;"
        + "  association assoc1 A -> B;" + "  association assoc2 A -> B;" + "}";
    runTest(model, false);
  }
  
  @Test
  public void testUniqueAssocNameWithSameRole() throws IOException {
    String model = "classdiagram UniqueAssocs {" + "  class A; class B;"
        + "  association assoc1 A -> (b) B;" + "  association assoc2 A -> (b) B;" + "}";
    runTest(model, false);
  }
  
}
