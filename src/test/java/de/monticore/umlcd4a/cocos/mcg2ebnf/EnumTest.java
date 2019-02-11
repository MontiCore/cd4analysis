/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.cocos.mcg2ebnf;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.cocos.helper.Assert;
import de.monticore.umlcd4a.CD4ACoCos;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDEnum;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.umlcd4a.cocos.AbstractCoCoTest;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import transformation.ast.ASTCDRawTransformation;

import static org.junit.Assert.assertEquals;

/**
 * Tests the CoCos that restrict ASTCDEnum to match the EBNF grammar.
 *
 * @author Robert Heim
 */
public class EnumTest extends AbstractCoCoTest {
  private static String MODEL_PATH_VALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg2ebnf/valid/";
  
  private static String MODEL_PATH_INVALID = "src/test/resources/de/monticore/umlcd4a/cocos/mcg2ebnf/invalid/";
  
  /**
   * @see de.monticore.umlcd4a.cocos.AbstractCoCoTest#getChecker()
   */
  @Override
  protected CD4AnalysisCoCoChecker getChecker() {
    return new CD4ACoCos().getCheckerForMcg2EbnfCoCos();
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
  
  @Test
  public void noModifierCoCo() {
    String modelName = "C4A68.cd";
    String errorCode = "0xC4A68";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Enum A may not have modifiers."),
        Finding.error(errorCode + " Enum B may not have modifiers."),
        Finding.error(errorCode + " Enum C may not have modifiers."),
        Finding.error(errorCode + " Enum D may not have modifiers."),
        Finding.error(errorCode + " Enum E may not have modifiers."),
        Finding.error(errorCode + " Enum F may not have modifiers."),
        Finding.error(errorCode + " Enum G may not have modifiers."),
        Finding.error(errorCode + " Enum H may not have modifiers."),
        Finding.error(errorCode + " Enum I may not have modifiers."));
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void noAttributesCoCoTest() {
    String modelName = "C4A70.cd";
    String errorCode = "0xC4A98";
    
    ASTCDCompilationUnit root = testModelNoErrors(MODEL_PATH_VALID + modelName);
    List<ASTCDEnum> enums = root.getCDDefinition().getCDEnumList();
    assertEquals(3, enums.size());
    
    // Add attribute to enum
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Enum A may not have attributes."));
    
    enums.get(0).addCDAttribute(CD4AnalysisMill.cDAttributeBuilder().setName("attr1")
        .setType(new ASTCDRawTransformation().createType("int")).build());
    CD4AnalysisCoCoChecker checker = getChecker();
    checker.checkAll(root);
    
    Collection<Finding> errors = Log.getFindings().stream().filter(f -> f.isError())
        .collect(Collectors.toList());
 
    assertEquals(1, errors.size());
    Assert.assertErrorMsg(expectedErrors, errors);
  }
  
  @Test
  public void noConstructorsCoCoTest() {
    String modelName = "C4A69.cd";
    String errorCode = "0xC4A69";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Enum A may not have constructors."),
        Finding.error(errorCode + " Enum B may not have constructors."),
        Finding.error(errorCode + " Enum C may not have constructors."));
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
  
  @Test
  public void noMethodsCoCoTest() {
    String modelName = "C4A70.cd";
    String errorCode = "0xC4A70";
    
    testModelNoErrors(MODEL_PATH_VALID + modelName);
    
    Collection<Finding> expectedErrors = Arrays.asList(
        Finding.error(errorCode + " Enum A may not have methods."),
        Finding.error(errorCode + " Enum B may not have methods."));
    
    testModelForErrors(MODEL_PATH_INVALID + modelName, expectedErrors);
  }
}
