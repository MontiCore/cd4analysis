/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4codebasis;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd4codebasis._parser.CD4CodeBasisAfterParseTrafo;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisPlantUMLPrettyPrinter;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisPrettyPrinter;
import de.monticore.cd4codebasis.typescalculator.DeriveSymTypeOfCD4CodeBasis;
import de.monticore.prettyprint.IndentPrinter;

public class CD4CodeBasisMill extends CD4CodeBasisMillTOP {
  protected static CD4CodeBasisMill millCD4CodeBasisAfterParseTrafo;
  protected static CD4CodeBasisMill millCD4CodeBasisPlantUMLPrettyPrinter;
  protected static CD4CodeBasisMill millCD4CodeBasisPrettyPrinter;
  protected static CD4CodeBasisMill millDeriveSymTypeOfCD4CodeBasis;

  public static CD4CodeBasisAfterParseTrafo cD4CodeBasisAfterParseTrafo() {
    if (millCD4CodeBasisAfterParseTrafo == null) {
      millCD4CodeBasisAfterParseTrafo = getMill();
    }
    return millCD4CodeBasisAfterParseTrafo._cD4CodeBasisAfterParseTrafo();
  }

  public static CD4CodeBasisAfterParseTrafo cD4CodeBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    if (millCD4CodeBasisAfterParseTrafo == null) {
      millCD4CodeBasisAfterParseTrafo = getMill();
    }
    return millCD4CodeBasisAfterParseTrafo._cD4CodeBasisAfterParseTrafo(cdAfterParseHelper);
  }

  public static CD4CodeBasisPlantUMLPrettyPrinter cD4CodeBasisPlantUMLPrettyPrinter() {
    if (millCD4CodeBasisPlantUMLPrettyPrinter == null) {
      millCD4CodeBasisPlantUMLPrettyPrinter = getMill();
    }
    return millCD4CodeBasisPlantUMLPrettyPrinter._cD4CodeBasisPlantUMLPrettyPrinter();
  }

  public static CD4CodeBasisPlantUMLPrettyPrinter cD4CodeBasisPlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    if (millCD4CodeBasisPlantUMLPrettyPrinter == null) {
      millCD4CodeBasisPlantUMLPrettyPrinter = getMill();
    }
    return millCD4CodeBasisPlantUMLPrettyPrinter._cD4CodeBasisPlantUMLPrettyPrinter(printer, config);
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

  public CD4CodeBasisAfterParseTrafo _cD4CodeBasisAfterParseTrafo() {
    return new CD4CodeBasisAfterParseTrafo();
  }

  public CD4CodeBasisAfterParseTrafo _cD4CodeBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    return new CD4CodeBasisAfterParseTrafo(cdAfterParseHelper);
  }

  public CD4CodeBasisPlantUMLPrettyPrinter _cD4CodeBasisPlantUMLPrettyPrinter() {
    return new CD4CodeBasisPlantUMLPrettyPrinter();
  }

  public CD4CodeBasisPlantUMLPrettyPrinter _cD4CodeBasisPlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    return new CD4CodeBasisPlantUMLPrettyPrinter(printer, config);
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
