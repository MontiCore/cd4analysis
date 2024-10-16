/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.prettyprint;

import de.monticore.cd.plantuml.PlantUMLConfig;
import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis.prettyprint.CD4CodeBasisPlantUMLPrettyPrinter;
import de.monticore.cdassociation.prettyprint.CDAssociationPlantUMLPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis.prettyprint.CDBasisPlantUMLPrettyPrinter;
import de.monticore.cdinterfaceandenum.prettyprint.CDInterfaceAndEnumPlantUMLPrettyPrinter;
import de.monticore.expressions.bitexpressions._prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.commonexpressions._prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.mccommonliterals._prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcarraytypes._prettyprint.MCArrayTypesPrettyPrinter;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.mcfullgenerictypes._prettyprint.MCFullGenericTypesPrettyPrinter;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter;
import de.monticore.umlmodifier._prettyprint.UMLModifierPrettyPrinter;
import de.monticore.umlstereotype._prettyprint.UMLStereotypePrettyPrinter;

public class CD4CodePlantUMLFullPrettyPrinter {

  protected CD4CodeTraverser traverser;
  protected final PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil;

  public CD4CodePlantUMLFullPrettyPrinter() {
    this(new PlantUMLPrettyPrintUtil());
  }

  public CD4CodePlantUMLFullPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    this.plantUMLPrettyPrintUtil = plantUMLPrettyPrintUtil;
    final IndentPrinter printer = this.plantUMLPrettyPrintUtil.getPrinter();

    this.traverser = CD4CodeMill.inheritanceTraverser();
    CDBasisPlantUMLPrettyPrinter cdBasis =
        new CDBasisPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
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

    CD4CodeBasisPlantUMLPrettyPrinter cd4CodeBasis =
        new CD4CodeBasisPlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
    traverser.add4CD4CodeBasis(cd4CodeBasis);
    traverser.setCD4CodeBasisHandler(cd4CodeBasis);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer, false);
    traverser.add4MCBasicTypes(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    UMLStereotypePrettyPrinter umlStereotype = new UMLStereotypePrettyPrinter(printer, false);
    traverser.add4UMLStereotype(umlStereotype);
    traverser.setUMLStereotypeHandler(umlStereotype);

    UMLModifierPrettyPrinter umlModifier = new UMLModifierPrettyPrinter(printer, false);
    traverser.add4UMLModifier(umlModifier);
    traverser.setUMLModifierHandler(umlModifier);

    MCCollectionTypesPrettyPrinter collectionTypes =
        new MCCollectionTypesPrettyPrinter(printer, false);
    traverser.add4MCCollectionTypes(collectionTypes);
    traverser.setMCCollectionTypesHandler(collectionTypes);

    MCArrayTypesPrettyPrinter arrayTypes = new MCArrayTypesPrettyPrinter(printer, false);
    traverser.add4MCArrayTypes(arrayTypes);
    traverser.setMCArrayTypesHandler(arrayTypes);

    MCSimpleGenericTypesPrettyPrinter simpleGenericTypes =
        new MCSimpleGenericTypesPrettyPrinter(printer, false);
    traverser.add4MCSimpleGenericTypes(simpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(simpleGenericTypes);

    MCFullGenericTypesPrettyPrinter fullGenericTypes =
        new MCFullGenericTypesPrettyPrinter(printer, false);
    traverser.add4MCFullGenericTypes(fullGenericTypes);
    traverser.setMCFullGenericTypesHandler(fullGenericTypes);

    ExpressionsBasisPrettyPrinter expressionsBasis =
        new ExpressionsBasisPrettyPrinter(printer, false);
    traverser.add4ExpressionsBasis(expressionsBasis);
    traverser.setExpressionsBasisHandler(expressionsBasis);

    MCCommonLiteralsPrettyPrinter commonLiterals =
        new MCCommonLiteralsPrettyPrinter(printer, false);
    traverser.add4MCCommonLiterals(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    BitExpressionsPrettyPrinter bitExpressions = new BitExpressionsPrettyPrinter(printer, false);
    traverser.add4BitExpressions(bitExpressions);
    traverser.setBitExpressionsHandler(bitExpressions);

    CommonExpressionsPrettyPrinter commonExpressions =
        new CommonExpressionsPrettyPrinter(printer, false);
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

  public CD4CodeTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CD4CodeTraverser traverser) {
    this.traverser = traverser;
  }

  public String prettyprint(ASTCDCompilationUnit node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }
}
