/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cdbasis.trafo.CDBasisDefaultPackageTrafo;
import de.monticore.cdbasis.prettyprint.CDBasisPlantUMLPrettyPrinter;
import de.monticore.cdbasis.prettyprint.CDBasisPrettyPrinter;
import de.monticore.cdbasis.prettyprint.CDBasisFullPrettyPrinter;
import de.monticore.cdbasis.typescalculator.DeriveSymTypeOfCDBasis;
import de.monticore.prettyprint.IndentPrinter;

public class CDBasisMill extends CDBasisMillTOP {

  protected static CDBasisMill millCDBasisAfterParseTrafo;
  protected static CDBasisMill millCDBasisPlantUMLPrettyPrinter;
  protected static CDBasisMill millCDBasisPrettyPrinter;
  protected static CDBasisMill millCDBasisPrettyPrinterDelegator;
  protected static CDBasisMill millDeriveSymTypeOfCDBasis;

  public static CDBasisDefaultPackageTrafo cDBasisAfterParseTrafo() {
    if (millCDBasisAfterParseTrafo == null) {
      millCDBasisAfterParseTrafo = getMill();
    }
    return millCDBasisAfterParseTrafo._cDBasisAfterParseTrafo();
  }

  public static CDBasisDefaultPackageTrafo cDBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
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

  public static CDBasisPlantUMLPrettyPrinter cDBasisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    if (millCDBasisPlantUMLPrettyPrinter == null) {
      millCDBasisPlantUMLPrettyPrinter = getMill();
    }
    return millCDBasisPlantUMLPrettyPrinter._cDBasisPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
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

  public static CDBasisFullPrettyPrinter cDBasisPrettyPrinterDelegator() {
    if (millCDBasisPrettyPrinterDelegator == null) {
      millCDBasisPrettyPrinterDelegator = getMill();
    }
    return millCDBasisPrettyPrinterDelegator._cDBasisPrettyPrinterDelegator();
  }

  public static CDBasisFullPrettyPrinter cDBasisPrettyPrinterDelegator(IndentPrinter printer) {
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

  public CDBasisDefaultPackageTrafo _cDBasisAfterParseTrafo() {
    return new CDBasisDefaultPackageTrafo();
  }

  public CDBasisDefaultPackageTrafo _cDBasisAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    return new CDBasisDefaultPackageTrafo(cdAfterParseHelper);
  }

  public CDBasisPlantUMLPrettyPrinter _cDBasisPlantUMLPrettyPrinter() {
    return new CDBasisPlantUMLPrettyPrinter();
  }

  public CDBasisPlantUMLPrettyPrinter _cDBasisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    return new CDBasisPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public CDBasisPrettyPrinter _cDBasisPrettyPrinter() {
    return new CDBasisPrettyPrinter();
  }

  public CDBasisPrettyPrinter _cDBasisPrettyPrinter(IndentPrinter printer) {
    return new CDBasisPrettyPrinter(printer);
  }

  public CDBasisFullPrettyPrinter _cDBasisPrettyPrinterDelegator() {
    return new CDBasisFullPrettyPrinter();
  }

  public CDBasisFullPrettyPrinter _cDBasisPrettyPrinterDelegator(IndentPrinter printer) {
    return new CDBasisFullPrettyPrinter(printer);
  }

  public DeriveSymTypeOfCDBasis _deriveSymTypeOfCDBasis() {
    return new DeriveSymTypeOfCDBasis();
  }

}
