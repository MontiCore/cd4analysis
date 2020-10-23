/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.prettyprint.CD4CodePlantUMLPrettyPrinter;
import de.monticore.cd4code.prettyprint.CD4CodePrettyPrinter;
import de.monticore.cd4code.resolver.CD4CodeResolver;
import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.prettyprint.IndentPrinter;

public class CD4CodeMill extends CD4CodeMillTOP {
  protected static CD4CodeMill millCD4CodePlantUMLPrettyPrinter;
  protected static CD4CodeMill millCD4CodePrettyPrinter;
  protected static CD4CodeMill millDeriveSymTypeOfCD4Code;
  protected static CD4CodeMill millCD4CodeResolvingDelegate;

  public static CD4CodePlantUMLPrettyPrinter cD4CodePlantUMLPrettyPrinter() {
    if (millCD4CodePlantUMLPrettyPrinter == null) {
      millCD4CodePlantUMLPrettyPrinter = getMill();
    }
    return millCD4CodePlantUMLPrettyPrinter._cD4CodePlantUMLPrettyPrinter();
  }

  public static CD4CodePlantUMLPrettyPrinter cD4CodePlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    if (millCD4CodePlantUMLPrettyPrinter == null) {
      millCD4CodePlantUMLPrettyPrinter = getMill();
    }
    return millCD4CodePlantUMLPrettyPrinter._cD4CodePlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
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

  public static CD4CodeResolver cD4CodeResolvingDelegate(ICD4CodeGlobalScope cdGlobalScope) {
    if (millCD4CodeResolvingDelegate == null) {
      millCD4CodeResolvingDelegate = getMill();
    }
    return millCD4CodeResolvingDelegate._cD4CodeResolvingDelegate(cdGlobalScope);
  }

  public CD4CodePlantUMLPrettyPrinter _cD4CodePlantUMLPrettyPrinter() {
    return new CD4CodePlantUMLPrettyPrinter();
  }

  public CD4CodePlantUMLPrettyPrinter _cD4CodePlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    return new CD4CodePlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
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

  public CD4CodeResolver _cD4CodeResolvingDelegate(ICD4CodeGlobalScope cdGlobalScope) {
    return new CD4CodeResolver(cdGlobalScope);
  }
}
