/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.cd2alloy.cocos;

import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;

/** Defines a CoCo Checker with additional CoCos for this tool */
public class CD2AlloyCoCos {
  public CD4AnalysisCoCoChecker getCheckerForAllCoCos() {
    // Get checker for all general CoCos
    CD4AnalysisCoCos generalCoCos = new CD4AnalysisCoCos();
    CD4AnalysisCoCoChecker checker = generalCoCos.addCheckerForMcg2EbnfCoCos();

    // Add specific CoCos for this tool
    checker.addCoCo(new NoAttributeModifierCoCo());
    checker.addCoCo(new NotAlloyOperator());

    return checker;
  }
}
