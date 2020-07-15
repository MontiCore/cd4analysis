/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisPlantUMLPrettyPrinter;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisPrettyPrinter;
import de.monticore.cd4analysis.typescalculator.DeriveSymTypeOfCD4Analysis;
import de.monticore.prettyprint.IndentPrinter;

public class CD4AnalysisMill extends CD4AnalysisMillTOP {
  protected static CD4AnalysisMill millCD4AnalysisPlantUMLPrettyPrinter;
  protected static CD4AnalysisMill millCD4AnalysisPrettyPrinter;
  protected static CD4AnalysisMill millDeriveSymTypeOfCD4Analysis;

  public static CD4AnalysisPlantUMLPrettyPrinter cD4AnalysisPlantUMLPrettyPrinter() {
    if (millCD4AnalysisPlantUMLPrettyPrinter == null) {
      millCD4AnalysisPlantUMLPrettyPrinter = getMill();
    }
    return millCD4AnalysisPlantUMLPrettyPrinter._cD4AnalysisPlantUMLPrettyPrinter();
  }

  public static CD4AnalysisPlantUMLPrettyPrinter cD4AnalysisPlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    if (millCD4AnalysisPlantUMLPrettyPrinter == null) {
      millCD4AnalysisPlantUMLPrettyPrinter = getMill();
    }
    return millCD4AnalysisPlantUMLPrettyPrinter._cD4AnalysisPlantUMLPrettyPrinter(printer, config);
  }

  public static CD4AnalysisPrettyPrinter cD4AnalysisPrettyPrinter() {
    if (millCD4AnalysisPrettyPrinter == null) {
      millCD4AnalysisPrettyPrinter = getMill();
    }
    return millCD4AnalysisPrettyPrinter._cD4AnalysisPrettyPrinter();
  }

  public static CD4AnalysisPrettyPrinter cD4AnalysisPrettyPrinter(IndentPrinter printer) {
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

  public CD4AnalysisPlantUMLPrettyPrinter _cD4AnalysisPlantUMLPrettyPrinter() {
    return new CD4AnalysisPlantUMLPrettyPrinter();
  }

  public CD4AnalysisPlantUMLPrettyPrinter _cD4AnalysisPlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    return new CD4AnalysisPlantUMLPrettyPrinter(printer, config);
  }

  public CD4AnalysisPrettyPrinter _cD4AnalysisPrettyPrinter() {
    return new CD4AnalysisPrettyPrinter();
  }

  public CD4AnalysisPrettyPrinter _cD4AnalysisPrettyPrinter(IndentPrinter printer) {
    return new CD4AnalysisPrettyPrinter(printer);
  }

  public DeriveSymTypeOfCD4Analysis _deriveSymTypeOfCD4Analysis() {
    return new DeriveSymTypeOfCD4Analysis();
  }
}
