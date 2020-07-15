/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdassociation;

import de.monticore.cd._parser.CDAfterParseHelper;
import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cdassociation._parser.CDAssociationAfterParseTrafo;
import de.monticore.cdassociation._symboltable.SymAssociationBuilder;
import de.monticore.cdassociation._symboltable.deser.CDCardinalityDeSer;
import de.monticore.cdassociation._visitor.CDAssocTypeForSymAssociationVisitor;
import de.monticore.cdassociation._visitor.CDAssociationNavigableVisitor;
import de.monticore.cdassociation.prettyprint.CDAssociationPlantUMLPrettyPrinter;
import de.monticore.cdassociation.prettyprint.CDAssociationPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CDAssociationMill extends CDAssociationMillTOP {
  protected static CDAssociationMill millSymAssocation;
  protected static CDAssociationMill millCDAssociationNavigableVisitor;
  protected static CDAssociationMill millCDAssociationAfterParseTrafo;
  protected static CDAssociationMill millCDCardinalityDeSer;
  protected static CDAssociationMill millCDAssocTypeForSymAssociationVisitor;
  protected static CDAssociationMill millCDAssociationPlantUMLPrettyPrinter;
  protected static CDAssociationMill millCDAssociationPrettyPrinter;

  public static SymAssociationBuilder symAssocationBuilder() {
    if (millSymAssocation == null) {
      millSymAssocation = getMill();
    }
    return millSymAssocation._symAssociationBuilder();
  }

  public static CDAssociationNavigableVisitor associationNavigableVisitor() {
    if (millCDAssociationNavigableVisitor == null) {
      millCDAssociationNavigableVisitor = getMill();
    }
    return millCDAssociationNavigableVisitor._associationNavigableVisitor();
  }

  public static CDAssociationAfterParseTrafo cDAssociationAfterParseTrafo() {
    if (millCDAssociationAfterParseTrafo == null) {
      millCDAssociationAfterParseTrafo = getMill();
    }
    return millCDAssociationAfterParseTrafo._cDAssociationAfterParseTrafo();
  }

  public static CDAssociationAfterParseTrafo cDAssociationAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    if (millCDAssociationAfterParseTrafo == null) {
      millCDAssociationAfterParseTrafo = getMill();
    }
    return millCDAssociationAfterParseTrafo._cDAssociationAfterParseTrafo(cdAfterParseHelper);
  }

  public static CDCardinalityDeSer cDCardinalityDeSer() {
    if (millCDCardinalityDeSer == null) {
      millCDCardinalityDeSer = getMill();
    }
    return millCDCardinalityDeSer._cDCardinalityDeSer();
  }

  public static CDAssocTypeForSymAssociationVisitor cDAssocTypeForSymAssociationVisitor() {
    if (millCDAssocTypeForSymAssociationVisitor == null) {
      millCDAssocTypeForSymAssociationVisitor = getMill();
    }
    return millCDAssocTypeForSymAssociationVisitor._cDAssocTypeForSymAssociationVisitor();
  }

  public static CDAssociationPlantUMLPrettyPrinter cDAssociationPlantUMLPrettyPrinter() {
    if (millCDAssociationPlantUMLPrettyPrinter == null) {
      millCDAssociationPlantUMLPrettyPrinter = getMill();
    }
    return millCDAssociationPlantUMLPrettyPrinter._cDAssociationPlantUMLPrettyPrinter();
  }

  public static CDAssociationPlantUMLPrettyPrinter cDAssociationPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    if (millCDAssociationPlantUMLPrettyPrinter == null) {
      millCDAssociationPlantUMLPrettyPrinter = getMill();
    }
    return millCDAssociationPlantUMLPrettyPrinter._cDAssociationPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public static CDAssociationPrettyPrinter cDAssociationPrettyPrinter() {
    if (millCDAssociationPrettyPrinter == null) {
      millCDAssociationPrettyPrinter = getMill();
    }
    return millCDAssociationPrettyPrinter._cDAssociationPrettyPrinter();
  }

  public static CDAssociationPrettyPrinter cDAssociationPrettyPrinter(IndentPrinter printer) {
    if (millCDAssociationPrettyPrinter == null) {
      millCDAssociationPrettyPrinter = getMill();
    }
    return millCDAssociationPrettyPrinter._cDAssociationPrettyPrinter(printer);
  }

  protected SymAssociationBuilder _symAssociationBuilder() {
    return new SymAssociationBuilder();
  }

  protected CDAssociationNavigableVisitor _associationNavigableVisitor() {
    return new CDAssociationNavigableVisitor();
  }

  public CDAssociationAfterParseTrafo _cDAssociationAfterParseTrafo() {
    return new CDAssociationAfterParseTrafo();
  }

  public CDAssociationAfterParseTrafo _cDAssociationAfterParseTrafo(CDAfterParseHelper cdAfterParseHelper) {
    return new CDAssociationAfterParseTrafo(cdAfterParseHelper);
  }

  public CDCardinalityDeSer _cDCardinalityDeSer() {
    return new CDCardinalityDeSer();
  }

  public CDAssocTypeForSymAssociationVisitor _cDAssocTypeForSymAssociationVisitor() {
    return new CDAssocTypeForSymAssociationVisitor();
  }

  public CDAssociationPlantUMLPrettyPrinter _cDAssociationPlantUMLPrettyPrinter() {
    return new CDAssociationPlantUMLPrettyPrinter();
  }

  public CDAssociationPlantUMLPrettyPrinter _cDAssociationPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    return new CDAssociationPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public CDAssociationPrettyPrinter _cDAssociationPrettyPrinter() {
    return new CDAssociationPrettyPrinter();
  }

  public CDAssociationPrettyPrinter _cDAssociationPrettyPrinter(IndentPrinter printer) {
    return new CDAssociationPrettyPrinter(printer);
  }
}
