/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd4code.prettyprint.CD4CodePlantUMLPrettyPrinter;
import de.monticore.cd4code.prettyprint.CD4CodePrettyPrinter;
import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.prettyprint.IndentPrinter;

public class CD4CodeMill extends CD4CodeMillTOP {
  protected static CD4CodeMill millCD4CodePlantUMLPrettyPrinter;
  protected static CD4CodeMill millCD4CodePrettyPrinter;
  protected static CD4CodeMill millDeriveSymTypeOfCD4Code;

  public static CD4CodePlantUMLPrettyPrinter cD4CodePlantUMLPrettyPrinter() {
    if (millCD4CodePlantUMLPrettyPrinter == null) {
      millCD4CodePlantUMLPrettyPrinter = getMill();
    }
    return millCD4CodePlantUMLPrettyPrinter._cD4CodePlantUMLPrettyPrinter();
  }

  public static CD4CodePlantUMLPrettyPrinter cD4CodePlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    if (millCD4CodePlantUMLPrettyPrinter == null) {
      millCD4CodePlantUMLPrettyPrinter = getMill();
    }
    return millCD4CodePlantUMLPrettyPrinter._cD4CodePlantUMLPrettyPrinter(printer, config);
  }

  public static CD4CodePrettyPrinter cD4CodePrettyPrinter() {
    if (millCD4CodePrettyPrinter == null) {
      millCD4CodePrettyPrinter = getMill();
    }
    return millCD4CodePrettyPrinter._cD4CodePrettyPrinter();
  }

  public static CD4CodePrettyPrinter cD4CodePrettyPrinter(IndentPrinter printer) {
    if (millCD4CodePrettyPrinter == null) {
      millCD4CodePrettyPrinter = getMill();
    }
    return millCD4CodePrettyPrinter._cD4CodePrettyPrinter(printer);
  }

  public static DeriveSymTypeOfCD4Code deriveSymTypeOfCD4Code() {
    if (millDeriveSymTypeOfCD4Code == null) {
      millDeriveSymTypeOfCD4Code = getMill();
    }
    return millDeriveSymTypeOfCD4Code._deriveSymTypeOfCD4Code();
  }

  public CD4CodePlantUMLPrettyPrinter _cD4CodePlantUMLPrettyPrinter() {
    return new CD4CodePlantUMLPrettyPrinter();
  }

  public CD4CodePlantUMLPrettyPrinter _cD4CodePlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    return new CD4CodePlantUMLPrettyPrinter(printer, config);
  }

  public CD4CodePrettyPrinter _cD4CodePrettyPrinter() {
    return new CD4CodePrettyPrinter();
  }

  public CD4CodePrettyPrinter _cD4CodePrettyPrinter(IndentPrinter printer) {
    return new CD4CodePrettyPrinter(printer);
  }

  public DeriveSymTypeOfCD4Code _deriveSymTypeOfCD4Code() {
    return new DeriveSymTypeOfCD4Code();
  }
}
