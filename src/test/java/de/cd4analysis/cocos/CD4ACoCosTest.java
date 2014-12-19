/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import java.util.Arrays;
import java.util.Collection;

import mc.ast.SourcePosition;

import org.junit.Test;

import cd4analysis.cocos._tobegenerated.CD4ACoCoChecker;
import cd4analysis.cocos._tobegenerated.CD4ACoCoProfile;
import de.cd4analysis._parser.CDCompilationUnitMCParser;
import de.monticore.cocos.helper.CoCosFireForInvalidModelsHelper;
import de.monticore.cocos.helper.InvalidModel;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CD4ACoCosTest {
  private static String LOGNAME = CD4ACoCosTest.class.getName();
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/invalid/";
  
  // holds all models and their expected errors.
  private Collection<InvalidModel> invalidModelsCoCoTests = Arrays
      .asList(
          new InvalidModel.Builder("A0134.cd", "0xA0134")
              .addExpected("First character of the diagram name a0134 must be upper-case.")
              .build(),
          new InvalidModel.Builder("XXXNoCode1.cd", "XXXNoCode1")
              .addExpected(
                  "The name of the diagram XXXNoCode1_bad is not identical to the name of the filel XXXNoCode1.cd (without its fileextension).")
              .build(),
          new InvalidModel.Builder("D00XX.cd", "D00XX")
              .addExpected("Name DAO is reserved for internal use.")
              .addExpected("Name Factory is reserved for internal use.")
              .build(),
          new InvalidModel.Builder("XXXNoCode2.cd", "XXXNoCode2")
              .addExpected(
                  "The name A is used several times. Classes, interfaces and enumerations may not use the same names.")
              .addExpected(
                  "The name B is used several times. Classes, interfaces and enumerations may not use the same names.")
              .addExpected(
                  "The name C is used several times. Classes, interfaces and enumerations may not use the same names.")
              .build(),
          new InvalidModel.Builder("U0530.cd", "0xU0530")
              .addExpected("The first character of the interface i must be upper-case.")
              .addExpected("The first character of the class c must be upper-case.")
              .addExpected("The first character of the enum e must be upper-case.")
              .build(),
          new InvalidModel.Builder("U0504.cd", "0xU0504")
              .addExpected("Duplicate enum constant: a.")
              .build(),
          new InvalidModel.Builder(
              "U0531.cd", "0xU0531")
              .addExpected(
                  "The class C2 introduces an inheritance cycle. Inheritance may not be cyclic.")
              .addExpected(
                  "The interface I2 introduces an inheritance cycle. Inheritance may not be cyclic.")
              .build(),
          new InvalidModel.Builder("U0496_U0497.cd", "U0496_U0497")
              .addExpected("Class C1 cannot extend interface I. A class may only extend classes.")
              .addExpected("Class C2 cannot extend enum E. A class may only extend classes.")
              .build(),
          new InvalidModel.Builder("XXXNoCode3.cd", "XXXNoCode3")
              .addExpected(
                  "Interface I1 cannot extend class C. An interface may only extend interfaces.")
              .addExpected(
                  "Interface I2 cannot extend enum E. An interface may only extend interfaces.")
              .build(),
          new InvalidModel.Builder("U0533_U0534.cd", "U0533_U0534")
              .addExpected(
                  "The class C1 cannot implement class C. Only interfaces may be implemented.")
              .addExpected(
                  "The class C2 cannot implement enum E. Only interfaces may be implemented.")
              .addExpected(
                  "The enum E1 cannot implement class C. Only interfaces may be implemented.")
              .addExpected(
                  "The enum E2 cannot implement enum E. Only interfaces may be implemented.")
              .build(),
          new InvalidModel.Builder("U0447.cd", "0xU0447")
              .addExpected(
                  "The value assignment for the attribute a in class C1 is not compatible to its type String.")
              .addExpected(
                  "The value assignment for the attribute b in class C1 is not compatible to its type int.")
              .build(),
          new InvalidModel.Builder("U0454.cd", "0xU0454")
              .addError("Attribute Attr must start in lower-case.", new SourcePosition(5, 12))
              .build(),
          new InvalidModel.Builder("U0455.cd", "0xU0455")
              .addExpected(
                  "Class C2 overrides the attribute attr (type: String) of class C1 with the different type int.")
              .addExpected(
                  "Class C5 overrides the attribute attr (type: int) of class C4 with the different type String.")
              .build(),
          new InvalidModel.Builder("D0410.cd", "D0410")
              .addExpected("Type AnUndefinedType of the attribute attr is unkown.")
              .build(),
          new InvalidModel.Builder("XXXNoCode4.cd", "XXXNoCode4")
              .addExpected("Attribute a is defined multiple times in class C.")
              .addExpected("Attribute b is defined multiple times in class C.")
              .build()
      
      // TODO ... tests for all CoCos of CD4A
      
      );
  
  @Test
  public void test() {
    CoCosFireForInvalidModelsHelper helper = new CoCosFireForInvalidModelsHelper(LOGNAME,
        MODEL_PATH, new CDCompilationUnitMCParser());
    
    CD4ACoCoProfile profile = new CD4ACoCoProfile();
    
    // TODO add CoCos
    
    CD4ACoCoChecker checker = new CD4ACoCoChecker(profile);
    
    // TODO uncomment when CoCos are implemented
    // helper.testCoCosForInvalidModels(checker, invalidModelsCoCoTests);
  }
  
}
