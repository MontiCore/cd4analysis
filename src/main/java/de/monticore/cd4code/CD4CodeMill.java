/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code;

import de.monticore.cd.plantuml.PlantUMLPrettyPrintUtil;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code.prettyprint.CD4CodePlantUMLFullPrettyPrinter;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4code.resolver.CD4CodeResolver;
import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.prettyprint.IndentPrinter;

public class CD4CodeMill extends CD4CodeMillTOP {
  protected static CD4CodeMill millCD4CodePlantUMLPrettyPrinter;
  protected static CD4CodeMill millCD4CodePrettyPrinter;
  protected static CD4CodeMill millDeriveSymTypeOfCD4Code;
  protected static CD4CodeMill millCD4CodeResolvingDelegate;

  public static CD4CodePlantUMLFullPrettyPrinter cD4CodePlantUMLPrettyPrinter() {
    if (millCD4CodePlantUMLPrettyPrinter == null) {
      millCD4CodePlantUMLPrettyPrinter = getMill();
    }
    return millCD4CodePlantUMLPrettyPrinter._cD4CodePlantUMLPrettyPrinter();
  }

  public static CD4CodePlantUMLFullPrettyPrinter cD4CodePlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    if (millCD4CodePlantUMLPrettyPrinter == null) {
      millCD4CodePlantUMLPrettyPrinter = getMill();
    }
    return millCD4CodePlantUMLPrettyPrinter._cD4CodePlantUMLPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public static CD4CodeFullPrettyPrinter cD4CodePrettyPrinter() {
    if (millCD4CodePrettyPrinter == null) {
      millCD4CodePrettyPrinter = getMill();
    }
    return millCD4CodePrettyPrinter._cD4CodePrettyPrinter();
  }

  public static CD4CodeFullPrettyPrinter cD4CodePrettyPrinter(IndentPrinter printer) {
    if (millCD4CodePrettyPrinter == null) {
      millCD4CodePrettyPrinter = getMill();
    }
    return millCD4CodePrettyPrinter._cD4CodePrettyPrinter(printer);
  }

  public static DeriveSymTypeOfCD4Code deriveSymTypeOfCD4Code() {
    if (millDeriveSymTypeOfCD4Code == null) {
      millDeriveSymTypeOfCD4Code = getMill();
    }
    return millDeriveSymTypeOfCD4Code._deriveSymTypeOfCD4Code();
  }

  public static CD4CodeResolver cD4CodeResolvingDelegate(ICD4CodeGlobalScope cdGlobalScope) {
    if (millCD4CodeResolvingDelegate == null) {
      millCD4CodeResolvingDelegate = getMill();
    }
    return millCD4CodeResolvingDelegate._cD4CodeResolvingDelegate(cdGlobalScope);
  }

  public CD4CodePlantUMLFullPrettyPrinter _cD4CodePlantUMLPrettyPrinter() {
    return new CD4CodePlantUMLFullPrettyPrinter();
  }

  public CD4CodePlantUMLFullPrettyPrinter _cD4CodePlantUMLPrettyPrinter(PlantUMLPrettyPrintUtil plantUMLPrettyPrintUtil) {
    return new CD4CodePlantUMLFullPrettyPrinter(plantUMLPrettyPrintUtil);
  }

  public CD4CodeFullPrettyPrinter _cD4CodePrettyPrinter() {
    return new CD4CodeFullPrettyPrinter();
  }

  public CD4CodeFullPrettyPrinter _cD4CodePrettyPrinter(IndentPrinter printer) {
    return new CD4CodeFullPrettyPrinter(printer);
  }

  public DeriveSymTypeOfCD4Code _deriveSymTypeOfCD4Code() {
    return new DeriveSymTypeOfCD4Code();
  }

  public CD4CodeResolver _cD4CodeResolvingDelegate(ICD4CodeGlobalScope cdGlobalScope) {
    return new CD4CodeResolver(cdGlobalScope);
  }

  public static void reset() {
    mill = null;
    millCD4CodeDelegatorVisitorBuilder = null;
    millCD4CodePhasedSymbolTableCreatorDelegator = null;
    millCD4CodeSymbolTableCreatorDelegator = null;
    millCD4CodeSymbolTableCreator = null;
    millCD4CodeTraverserImplementation = null;
    millCD4CodeScope = null;
    millCD4CodeArtifactScope = null;
    millCD4CodeGlobalScope = null;
    de.monticore.cd4analysis.CD4AnalysisMill.reset();
    de.monticore.cd4codebasis.CD4CodeBasisMill.reset();
    de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill.reset();
    de.monticore.cdinterfaceandenum.CDInterfaceAndEnumMill.reset();
    de.monticore.cdassociation.CDAssociationMill.reset();
    de.monticore.types.mccollectiontypes.MCCollectionTypesMill.reset();
    de.monticore.types.mcarraytypes.MCArrayTypesMill.reset();
    de.monticore.literals.mccommonliterals.MCCommonLiteralsMill.reset();
    de.monticore.expressions.bitexpressions.BitExpressionsMill.reset();
    de.monticore.expressions.commonexpressions.CommonExpressionsMill.reset();
    de.monticore.cdbasis.CDBasisMill.reset();
    de.monticore.literals.mcliteralsbasis.MCLiteralsBasisMill.reset();
    de.monticore.expressions.expressionsbasis.ExpressionsBasisMill.reset();
    de.monticore.types.mcbasictypes.MCBasicTypesMill.reset();
    de.monticore.symbols.oosymbols.OOSymbolsMill.reset();
    de.monticore.umlstereotype.UMLStereotypeMill.reset();
    de.monticore.umlmodifier.UMLModifierMill.reset();
    de.monticore.mcbasics.MCBasicsMill.reset();
    de.monticore.symbols.basicsymbols.BasicSymbolsMill.reset();
    de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill.reset();
  }
}
