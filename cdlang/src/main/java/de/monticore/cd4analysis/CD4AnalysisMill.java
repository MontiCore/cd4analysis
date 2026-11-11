/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis;

import de.monticore.cd4analysis.types3.CD4AnalysisTypeCheck3;

public class CD4AnalysisMill extends CD4AnalysisMillTOP {
  
  /**
   * {@inheritDoc}
   * Additionally inits the TypeCheck
   */
  public static void init() {
    CD4AnalysisMillTOP.init();
    CD4AnalysisTypeCheck3.init();
  }
  
  /**
   * {@inheritDoc}
   */
  public static void reset() {
    CD4AnalysisTypeCheck3.reset();
    CD4AnalysisMillTOP.reset();
  }
  
}
