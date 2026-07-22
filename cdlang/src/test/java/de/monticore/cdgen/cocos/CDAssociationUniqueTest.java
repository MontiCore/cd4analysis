/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import java.io.IOException;

import de.monticore.cd4code.cocos.AbstractJavaGenCoCoTest;
import org.junit.jupiter.api.Test;

public class CDAssociationUniqueTest extends AbstractJavaGenCoCoTest {
  
  // This CoCo checks for unique association names within a single class diagram.
  // Assumed to be available from the de.monticore.cdassociation.cocos package.
  @Override
  protected CD4CodeCoCoChecker createChecker() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    checker.addCoCo(new CDAssociationUnique());
    return checker;
  }
  
  // The error code is assumed, as the definition for CDAssociationUnique was not provided.
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
  public void testNavNotMatch() throws IOException {
    String model = "classdiagram DuplicateAssocs {" + "  class A; class B;"
        + "  association A -> (r) B;" + "  association A <- (r) B;" + "}";
    runTest(model, false);
  }
  
  @Test
  public void testNavNotMatchReverse() throws IOException {
    String model = "classdiagram DuplicateAssocs {" + "  class A; class B;"
        + "  association A -> (r) B;" + "  association B (r) -> A;" + "}";
    runTest(model, false);
  }
  
  @Test
  public void testNavNotMatchBiDir() throws IOException {
    String model = "classdiagram DuplicateAssocs {" + "  class A; class B;"
        + "  association A -- (r) B;" + "  association A <-> (r) B;" + "}";
    runTest(model, false);
  }
  
  @Test
  public void testDuplicatesDifferentClassesWithExplicitRoles() throws IOException {
    String model = "classdiagram DuplicateAssocs {" + "  class A; class B; class C;"
        + "  association A -> (k) B;" + "  association A -> (k) C;" + "}";
    runTestForErrorCode(model, ERROR_CODE);
  }
  
  @Test
  public void testDuplicatesWithC2MC() throws IOException {
    String model = "classdiagram DuplicateAssocs {" + "  class A;"
      + "  association A <-> java.lang.Integer;" + "  association A <-> java.lang.Integer;" + "}";
    runTestForErrorCode(model, ERROR_CODE);
  }

}
