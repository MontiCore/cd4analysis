/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdinterfaceandenum;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cdinterfaceandenum.trafo.CDInterfaceAndEnumAfterParseTrafo;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPlantUMLPrettyPrinter;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CDInterfaceAndEnumMill extends CDInterfaceAndEnumMillTOP {

  protected static CDInterfaceAndEnumMill millCDInterfaceAndEnumAfterParseTrafo;
  protected static CDInterfaceAndEnumMill millCDInterfaceAndEnumPlantUMLPrettyPrinter;
  protected static CDInterfaceAndEnumMill millCDInterfaceAndEnumPrettyPrinter;

  public static CDInterfaceAndEnumAfterParseTrafo cDInterfaceAndEnumAfterParseTrafo() {
    if (millCDInterfaceAndEnumAfterParseTrafo == null) {
      millCDInterfaceAndEnumAfterParseTrafo = getMill();
    }
    return millCDInterfaceAndEnumAfterParseTrafo._cDInterfaceAndEnumAfterParseTrafo();
  }

  public static CDInterfaceAndEnumAfterParseTrafo cDInterfaceAndEnumAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    if (millCDInterfaceAndEnumAfterParseTrafo == null) {
      millCDInterfaceAndEnumAfterParseTrafo = getMill();
    }
    return millCDInterfaceAndEnumAfterParseTrafo._cDInterfaceAndEnumAfterParseTrafo(cdAfterParseHelper);
  }

  public static CDInterfaceAndEnumPlantUMLPrettyPrinter cDInterfaceAndEnumPlantUMLPrettyPrinter() {
    if (millCDInterfaceAndEnumPlantUMLPrettyPrinter == null) {
      millCDInterfaceAndEnumPlantUMLPrettyPrinter = getMill();
    }
    return millCDInterfaceAndEnumPlantUMLPrettyPrinter._cDInterfaceAndEnumPlantUMLPrettyPrinter();
  }

  public static CDInterfaceAndEnumPlantUMLPrettyPrinter cDInterfaceAndEnumPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    if (millCDInterfaceAndEnumPlantUMLPrettyPrinter == null) {
      millCDInterfaceAndEnumPlantUMLPrettyPrinter = getMill();
    }
    return millCDInterfaceAndEnumPlantUMLPrettyPrinter._cDInterfaceAndEnumPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public static CDInterfaceAndEnumPrettyPrinter cDInterfaceAndEnumPrettyPrinter() {
    if (millCDInterfaceAndEnumPrettyPrinter == null) {
      millCDInterfaceAndEnumPrettyPrinter = getMill();
    }
    return millCDInterfaceAndEnumPrettyPrinter._cDInterfaceAndEnumPrettyPrinter();
  }

  public static CDInterfaceAndEnumPrettyPrinter cDInterfaceAndEnumPrettyPrinter(IndentPrinter printer) {
    if (millCDInterfaceAndEnumPrettyPrinter == null) {
      millCDInterfaceAndEnumPrettyPrinter = getMill();
    }
    return millCDInterfaceAndEnumPrettyPrinter._cDInterfaceAndEnumPrettyPrinter(printer);
  }

  public CDInterfaceAndEnumAfterParseTrafo _cDInterfaceAndEnumAfterParseTrafo() {
    return new CDInterfaceAndEnumAfterParseTrafo();
  }

  public CDInterfaceAndEnumAfterParseTrafo _cDInterfaceAndEnumAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    return new CDInterfaceAndEnumAfterParseTrafo(cdAfterParseHelper);
  }

  public CDInterfaceAndEnumPlantUMLPrettyPrinter _cDInterfaceAndEnumPlantUMLPrettyPrinter() {
    return new CDInterfaceAndEnumPlantUMLPrettyPrinter();
  }

  public CDInterfaceAndEnumPlantUMLPrettyPrinter _cDInterfaceAndEnumPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    return new CDInterfaceAndEnumPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public CDInterfaceAndEnumPrettyPrinter _cDInterfaceAndEnumPrettyPrinter() {
    return new CDInterfaceAndEnumPrettyPrinter();
  }

  public CDInterfaceAndEnumPrettyPrinter _cDInterfaceAndEnumPrettyPrinter(IndentPrinter printer) {
    return new CDInterfaceAndEnumPrettyPrinter(printer);
  }
}
