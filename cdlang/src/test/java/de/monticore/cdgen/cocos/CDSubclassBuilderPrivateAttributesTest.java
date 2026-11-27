/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code.cocos.AbstractJavaGenCoCoTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CDSubclassBuilderPrivateAttributesTest extends AbstractJavaGenCoCoTest {
  
  @Override
  protected CD4CodeCoCoChecker createChecker() {
    CD4CodeCoCoChecker checker = new CD4CodeCoCoChecker();
    checker.addCoCo(new CDSubclassBuilderPrivateAttributesCoCo());
    return checker;
  }
  
  //-----------------------------------------valid tests----------------------------------------------------------------
  
  @Test
  public void validSingleClassBuilderTest() throws IOException {
    String model = """
        classdiagram SingleClass {
          <<builder>> class A {
            private int x;
            private String y;
          }
        }""";
    runTest(model, false);
  }
  
  @Test
  public void validClassBuilderTest() throws IOException {
    String model = """
            classdiagram Class {
              <<builder>> class A {
                private int oneA;
                private String twoA;
              }
              <<builder>> class B extends A
              {
                  public int oneB;
                  public String twoB;
              }
            }
        """;
    runTest(model, false);
  }
  
  @Test
  public void validTest() throws IOException {
    String model = """
        classdiagram Valid {
         <<builder>> class A
         {
            public int oneA;
            public String twoA;
            public boolean threeA;
         }
         <<builder>> class B extends A
         {
            public int oneB;
            public String twoB;
         }
         <<builder>> class C extends B;
        }""";
    runTest(model, false);
  }
  
  @Test
  public void validBuilderTest() throws IOException {
    String model = """
        classdiagram validBuilder {
         <<builder>> class A;
          <<builder>> class B extends A;
          <<builder>> class C extends B;
        }""";
    runTest(model, false);
  }
  
  @Test
  public void validTestPublicAttributes() throws IOException {
    String model = """
        classdiagram publicAttributes {
          class A
          {
            public int one;
            public String two;
            public boolean three;
          }
          <<builder>> class B extends A;
          <<builder>> class C extends B;}""";
    runTest(model, false);
  }
  
  @Test
  public void validIntermediateBuilderTest() throws IOException {
    String model = """
        classdiagram IntermediateShield {
          class A {
            private int secretA;
          }
          class B extends A;
          <<builder>> class C extends B;
        }""";
    runTest(model, false);
  }
  
  //----------------------------------------------invalid tests------------------------------------------------
  
  @Test
  public void invalidBuilderChainTest() throws IOException {
    String model = """
        classdiagram invalidBuilderChain {
          class A
          {
            private int oneA;
            public String twoA;
            private boolean threeA;
          }
          <<builder>> class B extends A;
          <<builder>> class C extends B;
        }""";
    runTest(model, true);
  }
  
  @Test
  public void invalidBuilderChain2Test() throws IOException {
    String model = """
        classdiagram invalidBuilderChain {
          class A;
          class B extends A
          {
            public boolean oneB;
            private String twoB;
          }
          <<builder>> class C extends B;
        }""";
    runTest(model, true);
  }
  
  @Test
  public void invalidDeepBuilderChainTest() throws IOException {
    String model = """
            classdiagram invalidBuilderChain {
              <<builder>> class A;
              class B extends A
              {
                private int oneB;
              }
              <<builder>> class C extends B;
            }
        """;
    runTest(model, true);
  }
  
}
