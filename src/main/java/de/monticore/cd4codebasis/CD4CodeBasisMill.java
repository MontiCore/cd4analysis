/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisPlantUMLPrettyPrinter;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisPrettyPrinter;
import de.monticore.cd4codebasis.typescalculator.DeriveSymTypeOfCD4CodeBasis;
import de.monticore.prettyprint.IndentPrinter;

public class CD4CodeBasisMill extends CD4CodeBasisMillTOP {
  protected static CD4CodeBasisMill millCD4CodeBasisAfterParseTrafo;
  protected static CD4CodeBasisMill millCD4CodeBasisPlantUMLPrettyPrinter;
  protected static CD4CodeBasisMill millCD4CodeBasisPrettyPrinter;
  protected static CD4CodeBasisMill millDeriveSymTypeOfCD4CodeBasis;

  public static CD4CodeBasisPlantUMLPrettyPrinter cD4CodeBasisPlantUMLPrettyPrinter() {
    if (millCD4CodeBasisPlantUMLPrettyPrinter == null) {
      millCD4CodeBasisPlantUMLPrettyPrinter = getMill();
    }
    return millCD4CodeBasisPlantUMLPrettyPrinter._cD4CodeBasisPlantUMLPrettyPrinter();
  }

  public static CD4CodeBasisPlantUMLPrettyPrinter cD4CodeBasisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    if (millCD4CodeBasisPlantUMLPrettyPrinter == null) {
      millCD4CodeBasisPlantUMLPrettyPrinter = getMill();
    }
    return millCD4CodeBasisPlantUMLPrettyPrinter._cD4CodeBasisPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public static CD4CodeBasisPrettyPrinter cD4CodeBasisPrettyPrinter() {
    if (millCD4CodeBasisPrettyPrinter == null) {
      millCD4CodeBasisPrettyPrinter = getMill();
    }
    return millCD4CodeBasisPrettyPrinter._cD4CodeBasisPrettyPrinter();
  }

  public static CD4CodeBasisPrettyPrinter cD4CodeBasisPrettyPrinter(IndentPrinter printer) {
    if (millCD4CodeBasisPrettyPrinter == null) {
      millCD4CodeBasisPrettyPrinter = getMill();
    }
    return millCD4CodeBasisPrettyPrinter._cD4CodeBasisPrettyPrinter(printer);
  }

  public static DeriveSymTypeOfCD4CodeBasis deriveSymTypeOfCD4CodeBasis() {
    if (millDeriveSymTypeOfCD4CodeBasis == null) {
      millDeriveSymTypeOfCD4CodeBasis = getMill();
    }
    return millDeriveSymTypeOfCD4CodeBasis._deriveSymTypeOfCD4CodeBasis();
  }

  public CD4CodeBasisPlantUMLPrettyPrinter _cD4CodeBasisPlantUMLPrettyPrinter() {
    return new CD4CodeBasisPlantUMLPrettyPrinter();
  }

  public CD4CodeBasisPlantUMLPrettyPrinter _cD4CodeBasisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    return new CD4CodeBasisPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public CD4CodeBasisPrettyPrinter _cD4CodeBasisPrettyPrinter() {
    return new CD4CodeBasisPrettyPrinter();
  }

  public CD4CodeBasisPrettyPrinter _cD4CodeBasisPrettyPrinter(IndentPrinter printer) {
    return new CD4CodeBasisPrettyPrinter(printer);
  }

  public DeriveSymTypeOfCD4CodeBasis _deriveSymTypeOfCD4CodeBasis() {
    return new DeriveSymTypeOfCD4CodeBasis();
  }
}
