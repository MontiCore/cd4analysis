/*
 * (c) https://github.com/MontiCore/monticore
 */

/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code.prettyprint;

import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.UMLModiferPlantUMLPrettyPrinter;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisPlantUMLPrettyPrinter;
import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisPlantUMLPrettyPrinter;
import de.monticore.cdassociation.prettyprint.CDAssociationPlantUMLPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.prettyprint.CDBasisPlantUMLPrettyPrinter;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPlantUMLPrettyPrinter;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;

public class CD4CodePlantUMLPrettyPrinter extends CD4CodeDelegatorVisitor
    implements ExpressionsBasisVisitor {
  protected IndentPrinter printer;
  protected PlantUMLConfig plantUMLConfig;

  public CD4CodePlantUMLPrettyPrinter() {
    this(new IndentPrinter());
  }

  public CD4CodePlantUMLPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    setRealThis(this);
    setCDBasisVisitor(new CDBasisPlantUMLPrettyPrinter(printer));
    setCDInterfaceAndEnumVisitor(new CDInterfaceAndEnumPlantUMLPrettyPrinter(printer));
    setCDAssociationVisitor(new CDAssociationPlantUMLPrettyPrinter(printer));
    setCD4AnalysisVisitor(new CD4AnalysisPlantUMLPrettyPrinter(printer));
    setCD4CodeBasisVisitor(new CD4CodeBasisPlantUMLPrettyPrinter(printer));
    setCD4CodeVisitor(this);

    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setUMLStereotypeVisitor(new UMLStereotypePrettyPrinter(printer));
    setUMLModifierVisitor(new UMLModiferPlantUMLPrettyPrinter(printer));
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
