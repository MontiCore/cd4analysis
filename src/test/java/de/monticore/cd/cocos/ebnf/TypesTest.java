/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cocos.ebnf;

import de.monticore.cd.CD4ACoCos;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tests CoCos dealing with types.
 *
 * @author Robert Heim
 */
public class TypesTest extends AbstractCoCoTest {
  /**
   * @see AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForEbnfCoCos();
  }

  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    Log.getFindings().clear();
  }

  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/valid/";

  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/ebnf/invalid/";

//  @Test
//  public void testNestedGeneric() {
//    String modelName = "C4A29.cd";
//    String errorCode = "0xC4A29";
//
//    testModelNoErrors(MODEL_PATH_VALID + modelName);
//
//    Collection<Finding> expectedErrors = Arrays
//        .asList(
//            Finding
//                .error(errorCode
//                    + " Invalid type parameter List<Optional<String>>. Generic types may not be nested."),
//            Finding
//                .error(errorCode
//                    + " Invalid type parameter Optional<List<String>>. Generic types may not be nested.")
//        );
//    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
//  }

//  @Test
//  public void testUnparameterizedGenerics() throws RecognitionException, IOException {
//    String modelName = "C4A30.cd";
//    String errorCode = "0xC4A30";
//
//    testModelNoErrors(MODEL_PATH_VALID + modelName);
//
//    Collection<Finding> expectedErrors = Arrays
//        .asList(
//            Finding
//                .error(errorCode
//                    + " Generic type List has no type-parameter. References to generic types must be parametrized."),
//            Finding
//                .error(errorCode
//                    + " Generic type Optional has no type-parameter. References to generic types must be parametrized."),
//            Finding
//                .error(errorCode
//                    + " Generic type Set has no type-parameter. References to generic types must be parametrized.")
//        );
//    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
//
//    // some further tests
//    Optional<ASTMCGenericType> type = (new CD4AnalysisParser())
//        .parseMCGenericType(new StringReader("a.b.C<S>"));
//    assertTrue(type.isPresent());
//
//    Log.getFindings().clear();
//
//    // check the coco
//    new GenericTypeHasParameters().check(type.get());
//    // asert expected error
//    assertEquals(0, Log.getFindings().size());
//
//    type = (new CD4AnalysisParser())
//        .parseMCGenericType(new StringReader("a.b.C<>"));
//    assertTrue(type.isPresent());
//    new GenericTypeHasParameters().check(type.get());
//    // asert expected error
//    assertEquals(1, Log.getFindings().size());
//
//    assertEquals(1, Log.getFindings().stream().filter(f -> f.buildMsg().contains(errorCode))
//        .count());
//  }
//
//  @Test
//  public void testInvalidTypeParameterCount() {
//    String modelName = "C4A31.cd";
//    String errorCode = "0xC4A31";
//
//    testModelNoErrors(MODEL_PATH_VALID + modelName);
//
//    Collection<Finding> expectedErrors = Arrays
//        .asList(
//            Finding
//                .error(errorCode
//                    + " Generic type List has 1 type-parameter, but 2 where given ('List<String, String>')."),
//            Finding
//                .error(errorCode
//                    + " Generic type Optional has 1 type-parameter, but 2 where given ('Optional<String, String>')."),
//            Finding
//                .error(errorCode
//                    + " Generic type Set has 1 type-parameter, but 2 where given ('Set<String, String>').")
//        );
//    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
//  }
  
  @Test
  public void testInvalidInitializationOfDerivedAttr() {
    String modelName = "C4A34.cd";
    String errorCode = "0xC4A34";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays
        .asList(
            Finding
                .error(errorCode
                    + " Invalid initialization of the derived attribute a. Derived attributes may not be initialized."),
            Finding
                .error(errorCode
                    + " Invalid initialization of the derived attribute b. Derived attributes may not be initialized."),
            Finding
                .error(errorCode
                    + " Invalid initialization of the derived attribute c. Derived attributes may not be initialized.")
        );
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
