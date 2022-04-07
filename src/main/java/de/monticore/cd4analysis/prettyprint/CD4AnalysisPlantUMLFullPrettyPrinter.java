/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.prettyprint;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._visitor.CD4AnalysisTraverser;
import de.monticore.cdassociation.prettyprint.CDAssociationPlantUMLPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.prettyprint.CDBasisPlantUMLPrettyPrinter;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPlantUMLPrettyPrinter;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.UMLModifierPrettyPrinter;
import de.monticore.prettyprint.UMLStereotypePrettyPrinter;
import de.monticore.types.prettyprint.MCArrayTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;

public class CD4AnalysisPlantUMLFullPrettyPrinter {

  protected CD4AnalysisTraverser traverser;
  protected final PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil;

  public CD4AnalysisPlantUMLFullPrettyPrinter() {
    this(new PlantUMLPrettyPrintUtil());
  }

  public CD4AnalysisPlantUMLFullPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    this.plantUMLPrettyPrintUtil = plantUMLPrettyPrintUtil;
    this.traverser = CD4AnalysisMill.traverser();
    final IndentPrinter printer = this.plantUMLPrettyPrintUtil.getPrinter();

    CDBasisPlantUMLPrettyPrinter cdBasis = new CDBasisPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
    traverser.add4CDBasis(cdBasis);
    traverser.setCDBasisHandler(cdBasis);

    CDInterfaceAndEnumPlantUMLPrettyPrinter cdInterface =
        new CDInterfaceAndEnumPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
    traverser.add4CDInterfaceAndEnum(cdInterface);
    traverser.setCDInterfaceAndEnumHandler(cdInterface);

    CDAssociationPlantUMLPrettyPrinter cdAssoc =
        new CDAssociationPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
    traverser.add4CDAssociation(cdAssoc);
    traverser.setCDAssociationHandler(cdAssoc);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.add4MCBasicTypes(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    UMLStereotypePrettyPrinter umlStereotype = new UMLStereotypePrettyPrinter(printer);
    traverser.add4UMLStereotype(umlStereotype);
    traverser.setUMLStereotypeHandler(umlStereotype);

    UMLModifierPrettyPrinter umlModifier = new UMLModifierPrettyPrinter(printer);
    traverser.add4UMLModifier(umlModifier);
    traverser.setUMLModifierHandler(umlModifier);

    MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer);
    traverser.add4MCCollectionTypes(collectionTypes);
    traverser.setMCCollectionTypesHandler(collectionTypes);

    MCArrayTypesPrettyPrinter arrayTypes = new MCArrayTypesPrettyPrinter(printer);
    traverser.add4MCArrayTypes(arrayTypes);
    traverser.setMCArrayTypesHandler(arrayTypes);

    ExpressionsBasisPrettyPrinter expressionsBasis = new ExpressionsBasisPrettyPrinter(printer);
    traverser.add4ExpressionsBasis(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.add4MCCommonLiterals(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    BitExpressionsPrettyPrinter bitExpressions = new BitExpressionsPrettyPrinter(printer);
    traverser.add4BitExpressions(bitExpressions);
    traverser.setBitExpressionsHandler(bitExpressions);

    CommonExpressionsPrettyPrinter commonExpressions = new CommonExpressionsPrettyPrinter(printer);
    traverser.add4CommonExpressions(commonExpressions);
    traverser.setCommonExpressionsHandler(commonExpressions);
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

  public void setTraverser(CD4AnalysisTraverser traverser) {
    this.traverser = traverser;
  }

  public CD4AnalysisTraverser getTraverser() {
    return traverser;
  }

  public String prettyprint(ASTCDCompilationUnit node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
}
