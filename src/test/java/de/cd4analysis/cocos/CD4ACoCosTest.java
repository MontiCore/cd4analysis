/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import cd4analysis.cocos.CD4ACoCoChecker;
import de.cd4analysis.cocos.CoCosFireForInvalidModelsTest.ExpectedCoCoError;
import de.cd4analysis.cocos.CoCosFireForInvalidModelsTest.InvalidModelTest;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CD4ACoCosTest {
  private static String LOGNAME = CoCosFireForInvalidModelsTest.class.getName();
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/invalid/";
  
  // holds all models and their expected errors.
  private Collection<InvalidModelTest> invalidModelsCoCoTests = Arrays
      .asList(
          
          // tests for model A.cd
          // new InvalidModelTest("A.cd", Arrays.asList(
          // new ExpectedCoCoError("0x???",
          // "Attribute a is already defined in class C."),
          // new ExpectedCoCoError("0x???",
          // "Attribute b is already defined in class B.")
          // )
          // ),
          
//          new InvalidModelTest("A0134.cd", Arrays.asList(
//              new ExpectedCoCoError("0xA0134",
//                  "First character of the diagram name a0134 must be upper-case."))
//          ),
          
//          new InvalidModelTest(
//              "XXXNoCode1.cd",
//              Arrays
//                  .asList(
//                  new ExpectedCoCoError(
//                      "XXXNoCode1",
//                      "The name of the diagram XXXNoCode1_bad is not identical to the name of the filel XXXNoCode1.cd (without its fileextension)."))
//          ),
          
//          new InvalidModelTest("D00XX.cd", Arrays.asList(
//              new ExpectedCoCoError("D00XX", "Name DAO is reserved for internal use."),
//              new ExpectedCoCoError("D00XX", "Name Factory is reserved for internal use.")
//          )),
          
//          new InvalidModelTest("XXXNoCode2.cd", Arrays.asList(
//              new ExpectedCoCoError("XXXNoCode2", "The name A is used several times. Classes, interfaces and enumerations may not use the same names."),
//              new ExpectedCoCoError("XXXNoCode2", "The name B is used several times. Classes, interfaces and enumerations may not use the same names."),
//              new ExpectedCoCoError("XXXNoCode2", "The name C is used several times. Classes, interfaces and enumerations may not use the same names.")
//          )),

//          new InvalidModelTest("U0530.cd", Arrays.asList(
//              new ExpectedCoCoError("0xU0530 ", "The first character of the interface i must be upper-case."),
//              new ExpectedCoCoError("0xU0530 ", "The first character of the class c must be upper-case."),
//              new ExpectedCoCoError("0xU0530 ", "The first character of the enum e must be upper-case.")
//          )),
          
//          new InvalidModelTest("U0504.cd", Arrays.asList(
//              new ExpectedCoCoError("0xU0504 ", "Duplicate enum constant: a.")
//          )),
          
//          new InvalidModelTest("U0531.cd", Arrays.asList(
//              new ExpectedCoCoError("0xU0531 ", " The class C2 introduces an inheritance cycle. Inheritance may not be cyclic."),
//              new ExpectedCoCoError("0xU0531 ", " The interface I2 introduces an inheritance cycle. Inheritance may not be cyclic.")
//          ))
          
           
      // TODO ... tests for all CoCos of CD4A
      
      );
  
  @Test
  public void test() {
    CoCosFireForInvalidModelsTest c = new CoCosFireForInvalidModelsTest(LOGNAME,
        MODEL_PATH);
    c.testCoCosForInvalidModels(new CD4ACoCoChecker(), invalidModelsCoCoTests);
  }
}
