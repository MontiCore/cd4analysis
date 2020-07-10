/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.UMLModiferPlantUMLPrettyPrinter;
import de.monticore.cd4analysis._visitor.CD4AnalysisDelegatorVisitor;
import de.monticore.cdassociation.prettyprint.CDAssociationPlantUMLPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.prettyprint.CDBasisPlantUMLPrettyPrinter;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPlantUMLPrettyPrinter;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;

public class CD4AnalysisPlantUMLPrettyPrinter
    extends CD4AnalysisDelegatorVisitor {
  protected IndentPrinter printer;
  protected PlantUMLConfig plantUMLConfig;

  public CD4AnalysisPlantUMLPrettyPrinter() {
    this(new IndentPrinter(), new PlantUMLConfig());
  }

  public CD4AnalysisPlantUMLPrettyPrinter(IndentPrinter printer, PlantUMLConfig config) {
    this.printer = printer;
    this.plantUMLConfig = config;
    setRealThis(this);

    setCDBasisVisitor(new CDBasisPlantUMLPrettyPrinter(printer, plantUMLConfig));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumPlantUMLPrettyPrinter(printer, plantUMLConfig));
    setCDAssociationVisitor(new CDAssociationPlantUMLPrettyPrinter(printer, plantUMLConfig));
    setCD4AnalysisVisitor(this);

    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setUMLStereotypeVisitor(new UMLStereotypePrettyPrinter(printer));
    setUMLModifierVisitor(new UMLModiferPlantUMLPrettyPrinter(printer, plantUMLConfig));
    setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
    setBitExpressionsVisitor(new BitExpressionsPrettyPrinter(printer));
    setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(printer));
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public PlantUMLConfig getPlantUMLConfig() {
    return plantUMLConfig;
  }

  public void setPlantUMLConfig(PlantUMLConfig plantUMLConfig) {
    this.plantUMLConfig = plantUMLConfig;
  }

  public String prettyprint(ASTCDCompilationUnit node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
}
