/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis;

import de.monticore.cd4analysis.types3.CD4AnalysisTypeCheck3;

public class TestCDBasisMill extends TestCDBasisMillTOP {
  
  /**
   * {@inheritDoc}
   * Additionally inits the TypeCheck
   */
  public static void init() {
    TestCDBasisMillTOP.init();
    CD4AnalysisTypeCheck3.init();
  }
  
  /**
   * {@inheritDoc}
   */
  public static void reset() {
    CD4AnalysisTypeCheck3.reset();
    TestCDBasisMillTOP.reset();
  }
  
}
