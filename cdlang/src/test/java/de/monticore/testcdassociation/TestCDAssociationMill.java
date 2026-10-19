/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdassociation;

import de.monticore.cd4analysis.types3.CD4AnalysisTypeCheck3;

public class TestCDAssociationMill extends TestCDAssociationMillTOP {
  
  /**
   * {@inheritDoc}
   * Additionally inits the TypeCheck
   */
  public static void init() {
    TestCDAssociationMillTOP.init();
    CD4AnalysisTypeCheck3.init();
  }
  
  /**
   * {@inheritDoc}
   */
  public static void reset() {
    CD4AnalysisTypeCheck3.reset();
    TestCDAssociationMillTOP.reset();
  }
  
}
