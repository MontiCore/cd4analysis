/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisPlantUMLFullPrettyPrinter;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.cd4analysis.resolver.CD4AnalysisResolver;
import de.monticore.cd4analysis.typescalculator.DeriveSymTypeOfCD4Analysis;
import de.monticore.prettyprint.IndentPrinter;

public class CD4AnalysisMill extends CD4AnalysisMillTOP {
  protected static CD4AnalysisMill millCD4AnalysisPlantUMLPrettyPrinter;
  protected static CD4AnalysisMill millCD4AnalysisPrettyPrinter;
  protected static CD4AnalysisMill millDeriveSymTypeOfCD4Analysis;
  protected static CD4AnalysisMill millCD4AnalysisResolvingDelegate;

  public static CD4AnalysisPlantUMLFullPrettyPrinter cD4AnalysisPlantUMLPrettyPrinter() {
    if (millCD4AnalysisPlantUMLPrettyPrinter == null) {
      millCD4AnalysisPlantUMLPrettyPrinter = getMill();
    }
    return millCD4AnalysisPlantUMLPrettyPrinter._cD4AnalysisPlantUMLPrettyPrinter();
  }

  public static CD4AnalysisPlantUMLFullPrettyPrinter cD4AnalysisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    if (millCD4AnalysisPlantUMLPrettyPrinter == null) {
      millCD4AnalysisPlantUMLPrettyPrinter = getMill();
    }
    return millCD4AnalysisPlantUMLPrettyPrinter._cD4AnalysisPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public static CD4AnalysisFullPrettyPrinter cD4AnalysisPrettyPrinter() {
    if (millCD4AnalysisPrettyPrinter == null) {
      millCD4AnalysisPrettyPrinter = getMill();
    }
    return millCD4AnalysisPrettyPrinter._cD4AnalysisPrettyPrinter();
  }

  public static CD4AnalysisFullPrettyPrinter cD4AnalysisPrettyPrinter(IndentPrinter printer) {
    if (millCD4AnalysisPrettyPrinter == null) {
      millCD4AnalysisPrettyPrinter = getMill();
    }
    return millCD4AnalysisPrettyPrinter._cD4AnalysisPrettyPrinter(printer);
  }

  public static DeriveSymTypeOfCD4Analysis deriveSymTypeOfCD4Analysis() {
    if (millDeriveSymTypeOfCD4Analysis == null) {
      millDeriveSymTypeOfCD4Analysis = getMill();
    }
    return millDeriveSymTypeOfCD4Analysis._deriveSymTypeOfCD4Analysis();
  }

  public static CD4AnalysisResolver cD4AnalysisResolvingDelegate(ICD4AnalysisGlobalScope cdGlobalScope) {
    if (millCD4AnalysisResolvingDelegate == null) {
      millCD4AnalysisResolvingDelegate = getMill();
    }
    return millCD4AnalysisResolvingDelegate._cD4AnalysisResolvingDelegate(cdGlobalScope);
  }

  public CD4AnalysisPlantUMLFullPrettyPrinter _cD4AnalysisPlantUMLPrettyPrinter() {
    return new CD4AnalysisPlantUMLFullPrettyPrinter();
  }

  public CD4AnalysisPlantUMLFullPrettyPrinter _cD4AnalysisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    return new CD4AnalysisPlantUMLFullPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public CD4AnalysisFullPrettyPrinter _cD4AnalysisPrettyPrinter() {
    return new CD4AnalysisFullPrettyPrinter();
  }

  public CD4AnalysisFullPrettyPrinter _cD4AnalysisPrettyPrinter(IndentPrinter printer) {
    return new CD4AnalysisFullPrettyPrinter(printer);
  }

  public DeriveSymTypeOfCD4Analysis _deriveSymTypeOfCD4Analysis() {
    return new DeriveSymTypeOfCD4Analysis();
  }

  public CD4AnalysisResolver _cD4AnalysisResolvingDelegate(ICD4AnalysisGlobalScope cdGlobalScope) {
    return new CD4AnalysisResolver(cdGlobalScope);
  }
}
