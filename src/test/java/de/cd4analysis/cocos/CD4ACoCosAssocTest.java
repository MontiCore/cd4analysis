/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.cd4analysis.cocos;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import de.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.cd4analysis._cocos.CD4AnalysisCoCoProfile;
import de.cd4analysis._parser.CDCompilationUnitMCParser;
import de.monticore.cocos.helper.CoCosFireForInvalidModelsHelper;
import de.monticore.cocos.helper.InvalidModel;

/**
 * Tests the codes and messages of CoCos regarding associations.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class CD4ACoCosAssocTest {
  private static String LOGNAME = CD4ACoCosAssocTest.class.getName();
  
  private static String MODEL_PATH = "src/test/resources/de/cd4analysis/cocos/invalid/";
  
  // holds all models and their expected errors.
  private Collection<InvalidModel> invalidModelsCoCoTests = Arrays
      .asList(
      
      // new InvalidModel.Builder("U0440.cd", "0xU0440")
      // .addExpected("Association Assoc1 must start in lower-case.")
      // .addExpected("Association Assoc2 must start in lower-case.")
      // .addExpected("Association Assoc3 must start in lower-case.")
      // .build(),
      
      new InvalidModel.Builder("U0441.cd", "0xU00441")
          .addExpected("Role RC0_1 of association assocName must start in lower-case.")
          // TODO maybe we should replace "association" with "composition"
          // in case that we actually check a composition?
          .addExpected("Role RC0_1 of association (A RC0_2 -> B) must start in lower-case.")
          
          .addExpected("Role RC1_1 of association (A RC1_1 -> B) must start in lower-case.")
          .addExpected("Role RC1_2 of association (A -> RC1_2 B) must start in lower-case.")
          .addExpected(
              "Role RC1_3 of association (A RC1_3 -> RC1_4 B) must start in lower-case.")
          .addExpected(
              "Role RC1_4 of association (A RC1_3 -> RC1_4 B) must start in lower-case.")
          
          .addExpected("Role RC2_1 of association (A RC2_1 <- B) must start in lower-case.")
          .addExpected("Role RC2_2 of association (A <- RC2_2 B) must start in lower-case.")
          .addExpected(
              "Role RC2_3 of association (A RC2_3 <- RC2_4 B) must start in lower-case.")
          .addExpected(
              "Role RC2_4 of association (A RC2_3 <- RC2_4 B) must start in lower-case.")
          
          .addExpected("Role RC3_1 of association (A RC3_1 <-> B) must start in lower-case.")
          .addExpected("Role RC3_2 of association (A <-> RC3_2 B) must start in lower-case.")
          .addExpected(
              "Role RC3_3 of association (A RC3_3 <-> RC3_4 B) must start in lower-case.")
          .addExpected(
              "Role RC3_4 of association (A RC3_3 <-> RC3_4 B) must start in lower-case.")
          
          .addExpected("Role RC4_1 of association (A RC4_1 -- B) must start in lower-case.")
          .addExpected("Role RC4_2 of association (A -- RC4_2 B) must start in lower-case.")
          .addExpected(
              "Role RC4_3 of association (A RC4_3 -- RC4_4 B) must start in lower-case.")
          .addExpected(
              "Role RC4_4 of association (A RC4_3 -- RC4_4 B) must start in lower-case.")
          
          // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
          
          .addExpected("Role RI1_1 of association (A RI1_1 -> I) must start in lower-case.")
          .addExpected("Role RI1_2 of association (A -> RI1_2 I) must start in lower-case.")
          .addExpected(
              "Role RI1_3 of association (A RI1_3 -> RI1_4 I) must start in lower-case.")
          .addExpected(
              "Role RI1_4 of association (A RI1_3 -> RI1_4 I) must start in lower-case.")
          
          .addExpected("Role RI2_1 of association (A RI2_1 <- I) must start in lower-case.")
          .addExpected("Role RI2_2 of association (A <- RI2_2 I) must start in lower-case.")
          .addExpected(
              "Role RI2_3 of association (A RI2_3 <- RI2_4 I) must start in lower-case.")
          .addExpected(
              "Role RI2_4 of association (A RI2_3 <- RI2_4 I) must start in lower-case.")
          
          .addExpected("Role RI3_1 of association (A RI3_1 <-> I) must start in lower-case.")
          .addExpected("Role RI3_2 of association (A <-> RI3_2 I) must start in lower-case.")
          .addExpected(
              "Role RI3_3 of association (A RI3_3 <-> RI3_4 I) must start in lower-case.")
          .addExpected(
              "Role RI3_4 of association (A RI3_3 <-> RI3_4 I) must start in lower-case.")
          
          .addExpected("Role RI4_1 of association (A RI4_1 -- I) must start in lower-case.")
          .addExpected("Role RI4_2 of association (A -- RI4_2 I) must start in lower-case.")
          .addExpected(
              "Role RI4_3 of association (A RI4_3 -- RI4_4 I) must start in lower-case.")
          .addExpected(
              "Role RI4_4 of association (A RI4_3 -- RI4_4 I) must start in lower-case.")
          
          // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
          
          .addExpected("Role RE1_1 of association (A RE1_1 -> E) must start in lower-case.")
          .addExpected("Role RE1_2 of association (A -> RE1_2 E) must start in lower-case.")
          .addExpected(
              "Role RE1_3 of association (A RE1_3 -> RE1_4 E) must start in lower-case.")
          .addExpected(
              "Role RE1_4 of association (A RE1_3 -> RE1_4 E) must start in lower-case.")
          
          .addExpected("Role RE4_1 of association (A RE4_1 -- E) must start in lower-case.")
          .addExpected("Role RE4_2 of association (A -- RE4_2 E) must start in lower-case.")
          .addExpected(
              "Role RE4_3 of association (A RE4_3 -- RE4_4 E) must start in lower-case.")
          .addExpected(
              "Role RE4_4 of association (A RE4_3 -- RE4_4 E) must start in lower-case.")
          .build()
      
      // TODO ... tests for all CoCos of CD4A
      
      );
  
  @Test
  public void test() {
    CoCosFireForInvalidModelsHelper c = new CoCosFireForInvalidModelsHelper(LOGNAME, MODEL_PATH,
        new CDCompilationUnitMCParser());
    
    CD4AnalysisCoCoProfile profile = new CD4AnalysisCoCoProfile();
    
    // TODO add CoCos
    // profile.addCoCo(coco);
    
    CD4AnalysisCoCoChecker checker = new CD4AnalysisCoCoChecker(profile);
    
    // TODO uncomment when CoCos are implemented
    // c.testCoCosForInvalidModels(checker, invalidModelsCoCoTests);
    
  }
}
