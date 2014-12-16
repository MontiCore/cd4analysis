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
  private Collection<InvalidModelTest> invalidModelsCoCoTests = Arrays.asList(
      
      // tests for model A.cd
      new InvalidModelTest("A.cd", Arrays.asList(
          new ExpectedCoCoError("0x???", "Attribute a is already defined in class C."),
          new ExpectedCoCoError("0x???", "Attribute b is already defined in class B.")
          )
      ),
      
      // tests for model B.cd
      new InvalidModelTest("B.cd", Arrays.asList(
          new ExpectedCoCoError("0x???",
              "Attribute a is already defined in class C."))
      )
      
      // TODO ... tests for all CoCos of CD4A
      
      );
  
  @Test
  public void test() {
    CoCosFireForInvalidModelsTest c = new CoCosFireForInvalidModelsTest(LOGNAME,
        MODEL_PATH);
    c.testCoCosForInvalidModels(new CD4ACoCoChecker(), invalidModelsCoCoTests);
  }
}
