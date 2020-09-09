/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
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
  protected final PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil;

  public CD4AnalysisPlantUMLPrettyPrinter() {
    this(new PlantUMLPrettyPrintUtil());
  }

  public CD4AnalysisPlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    this.plantUMLPrettyPrintUtil = plantUMLPrettyPrintUtil;
    final IndentPrinter printer = this.plantUMLPrettyPrintUtil.getPrinter();

    setRealThis(this);

    setCDBasisVisitor(new CDBasisPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil));
    setCDAssociationVisitor(new CDAssociationPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil));
    setCD4AnalysisVisitor(this);

    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setUMLStereotypeVisitor(new UMLStereotypePrettyPrinter(printer));
    setUMLModifierVisitor(new UMLModiferPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil));
    setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
    setBitExpressionsVisitor(new BitExpressionsPrettyPrinter(printer));
    setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(printer));
  }

  public IndentPrinter getPrinter() {
    return this.plantUMLPrettyPrintUtil.getPrinter();
  }

  public void setPrinter(IndentPrinter printer) {
    this.plantUMLPrettyPrintUtil.setPrinter(printer);
  }

  public PlantUMLConfig getPlantUMLConfig() {
    return this.plantUMLPrettyPrintUtil.getPlantUMLConfig();
  }

  public void setPlantUMLConfig(PlantUMLConfig plantUMLConfig) {
    this.plantUMLPrettyPrintUtil.setPlantUMLConfig(plantUMLConfig);
  }

  public String prettyprint(ASTCDCompilationUnit node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
}
