/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cdbasis._parser.CDBasisAfterParseTrafo;
import de.monticore.cdbasis.prettyprint.CDBasisPlantUMLPrettyPrinter;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinter;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinterDelegator;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.prettyprint.IndentPrinter;

public class CDBasisMill extends CDBasisMillTOP {

  protected static CDBasisMill millCDBasisAfterParseTrafo;
  protected static CDBasisMill millCDBasisPlantUMLPrettyPrinter;
  protected static CDBasisMill millCDBasisPrettyPrinter;
  protected static CDBasisMill millCDBasisPrettyPrinterDelegator;
  protected static CDBasisMill millDeriveSymTypeOfCDBasis;

  public static CDBasisAfterParseTrafo cDBasisAfterParseTrafo() {
    if (millCDBasisAfterParseTrafo == null) {
      millCDBasisAfterParseTrafo = getMill();
    }
    return millCDBasisAfterParseTrafo._cDBasisAfterParseTrafo();
  }

  public static CDBasisAfterParseTrafo cDBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    if (millCDBasisAfterParseTrafo == null) {
      millCDBasisAfterParseTrafo = getMill();
    }
    return millCDBasisAfterParseTrafo._cDBasisAfterParseTrafo(cdAfterParseHelper);
  }

  public static CDBasisPlantUMLPrettyPrinter cDBasisPlantUMLPrettyPrinter() {
    if (millCDBasisPlantUMLPrettyPrinter == null) {
      millCDBasisPlantUMLPrettyPrinter = getMill();
    }
    return millCDBasisPlantUMLPrettyPrinter._cDBasisPlantUMLPrettyPrinter();
  }

  public static CDBasisPlantUMLPrettyPrinter cDBasisPlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    if (millCDBasisPlantUMLPrettyPrinter == null) {
      millCDBasisPlantUMLPrettyPrinter = getMill();
    }
    return millCDBasisPlantUMLPrettyPrinter._cDBasisPlantUMLPrettyPrinter(printer, config);
  }

  public static CDBasisPrettyPrinter cDBasisPrettyPrinter() {
    if (millCDBasisPrettyPrinter == null) {
      millCDBasisPrettyPrinter = getMill();
    }
    return millCDBasisPrettyPrinter._cDBasisPrettyPrinter();
  }

  public static CDBasisPrettyPrinter cDBasisPrettyPrinter(IndentPrinter printer) {
    if (millCDBasisPrettyPrinter == null) {
      millCDBasisPrettyPrinter = getMill();
    }
    return millCDBasisPrettyPrinter._cDBasisPrettyPrinter(printer);
  }

  public static CDBasisPrettyPrinterDelegator cDBasisPrettyPrinterDelegator() {
    if (millCDBasisPrettyPrinterDelegator == null) {
      millCDBasisPrettyPrinterDelegator = getMill();
    }
    return millCDBasisPrettyPrinterDelegator._cDBasisPrettyPrinterDelegator();
  }

  public static CDBasisPrettyPrinterDelegator cDBasisPrettyPrinterDelegator(IndentPrinter printer) {
    if (millCDBasisPrettyPrinterDelegator == null) {
      millCDBasisPrettyPrinterDelegator = getMill();
    }
    return millCDBasisPrettyPrinterDelegator._cDBasisPrettyPrinterDelegator(printer);
  }

  public static DeriveSymTypeOfCDBasis deriveSymTypeOfCDBasis() {
    if (millDeriveSymTypeOfCDBasis == null) {
      millDeriveSymTypeOfCDBasis = getMill();
    }
    return millDeriveSymTypeOfCDBasis._deriveSymTypeOfCDBasis();
  }

  public CDBasisAfterParseTrafo _cDBasisAfterParseTrafo() {
    return new CDBasisAfterParseTrafo();
  }

  public CDBasisAfterParseTrafo _cDBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    return new CDBasisAfterParseTrafo(cdAfterParseHelper);
  }

  public CDBasisPlantUMLPrettyPrinter _cDBasisPlantUMLPrettyPrinter() {
    return new CDBasisPlantUMLPrettyPrinter();
  }

  public CDBasisPlantUMLPrettyPrinter _cDBasisPlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    return new CDBasisPlantUMLPrettyPrinter(printer, config);
  }

  public CDBasisPrettyPrinter _cDBasisPrettyPrinter() {
    return new CDBasisPrettyPrinter();
  }

  public CDBasisPrettyPrinter _cDBasisPrettyPrinter(IndentPrinter printer) {
    return new CDBasisPrettyPrinter(printer);
  }

  public CDBasisPrettyPrinterDelegator _cDBasisPrettyPrinterDelegator() {
    return new CDBasisPrettyPrinterDelegator();
  }

  public CDBasisPrettyPrinterDelegator _cDBasisPrettyPrinterDelegator(IndentPrinter printer) {
    return new CDBasisPrettyPrinterDelegator(printer);
  }

  public DeriveSymTypeOfCDBasis _deriveSymTypeOfCDBasis() {
    return new DeriveSymTypeOfCDBasis();
  }

}
