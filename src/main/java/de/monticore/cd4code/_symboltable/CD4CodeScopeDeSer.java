/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbols2Json;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbols2Json;
import de.monticore.cdassociation._symboltable.CDAssociationSymbols2Json;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdbasis._symboltable.CDBasisSymbols2Json;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static de.monticore.cd4analysis._symboltable.CD4AnalysisScopeDeSer.deserializeFurtherObjects;

public class CD4CodeScopeDeSer extends CD4CodeScopeDeSerTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Map<Integer, SymAssociation> symAssociations;

  public CD4CodeScopeDeSer() {
    setSymbolTablePrinterHelper(new CDSymbolTablePrinterHelper());
    setSymAssociations(new HashMap<>());
  }

  public void setSymAssociations(Map<Integer, SymAssociation> symAssociations) {
    this.symAssociations = symAssociations;
    this.cDRoleSymbolDeSer.setSymAssociations(symAssociations);
    this.cDAssociationSymbolDeSer.setSymAssociations(symAssociations);
  }

  public CDSymbolTablePrinterHelper getSymbolTablePrinterHelper() {
    return symbolTablePrinterHelper;
  }

  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;
  }

  @Override
  protected void deserializeAddons(ICD4CodeArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(symAssociations, scopeJson);
  }
  
  @Override
  public String serialize(de.monticore.cd4code._symboltable.ICD4CodeScope toSerialize) {
    de.monticore.symboltable.serialization.JsonPrinter printer = new de.monticore.symboltable.serialization.JsonPrinter();
    de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor symbolTablePrinter = new de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor();
    
    CD4AnalysisSymbols2Json cd4as2j = new de.monticore.cd4analysis._symboltable.CD4AnalysisSymbols2Json(printer);
    cd4as2j.setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    symbolTablePrinter.setCD4AnalysisVisitor(cd4as2j);
    
    symbolTablePrinter.setBitExpressionsVisitor(new de.monticore.expressions.bitexpressions._symboltable.BitExpressionsSymbols2Json(printer));
    symbolTablePrinter.setCommonExpressionsVisitor(new de.monticore.expressions.commonexpressions._symboltable.CommonExpressionsSymbols2Json(printer));
    symbolTablePrinter.setMCFullGenericTypesVisitor(new de.monticore.types.mcfullgenerictypes._symboltable.MCFullGenericTypesSymbols2Json(printer));
    symbolTablePrinter.setMCLiteralsBasisVisitor(new de.monticore.literals.mcliteralsbasis._symboltable.MCLiteralsBasisSymbols2Json(printer));
    symbolTablePrinter.setMCBasicTypesVisitor(new de.monticore.types.mcbasictypes._symboltable.MCBasicTypesSymbols2Json(printer));
    symbolTablePrinter.setCDInterfaceAndEnumVisitor(new de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbols2Json(printer));
    
    CD4CodeSymbols2Json cd4cs2j = new de.monticore.cd4code._symboltable.CD4CodeSymbols2Json(printer);
    cd4cs2j.setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    symbolTablePrinter.setCD4CodeVisitor(new de.monticore.cd4code._symboltable.CD4CodeSymbols2Json(printer));
    
    CDBasisSymbols2Json cdbs2j = new de.monticore.cdbasis._symboltable.CDBasisSymbols2Json(printer);
    cdbs2j.setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    symbolTablePrinter.setCDBasisVisitor(cdbs2j);
    
    symbolTablePrinter.setBasicSymbolsVisitor(new de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsSymbols2Json(printer));
    symbolTablePrinter.setExpressionsBasisVisitor(new de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymbols2Json(printer));
    symbolTablePrinter.setUMLModifierVisitor(new de.monticore.umlmodifier._symboltable.UMLModifierSymbols2Json(printer));
    symbolTablePrinter.setMCArrayTypesVisitor(new de.monticore.types.mcarraytypes._symboltable.MCArrayTypesSymbols2Json(printer));
    symbolTablePrinter.setMCCommonLiteralsVisitor(new de.monticore.literals.mccommonliterals._symboltable.MCCommonLiteralsSymbols2Json(printer));
    
    CDAssociationSymbols2Json cdas2j = new de.monticore.cdassociation._symboltable.CDAssociationSymbols2Json(printer);
    cdas2j.setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    symbolTablePrinter.setCDAssociationVisitor(cdas2j);
    
    symbolTablePrinter.setMCSimpleGenericTypesVisitor(new de.monticore.types.mcsimplegenerictypes._symboltable.MCSimpleGenericTypesSymbols2Json(printer));
    symbolTablePrinter.setMCCollectionTypesVisitor(new de.monticore.types.mccollectiontypes._symboltable.MCCollectionTypesSymbols2Json(printer));
    
    CD4CodeBasisSymbols2Json cd4cbs2j = new de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbols2Json(printer);
    cd4cbs2j.setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    symbolTablePrinter.setCD4CodeBasisVisitor(cd4cbs2j);
    
    symbolTablePrinter.setOOSymbolsVisitor(new de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json(printer));
    symbolTablePrinter.setMCBasicsVisitor(new de.monticore.mcbasics._symboltable.MCBasicsSymbols2Json(printer));
    symbolTablePrinter.setUMLStereotypeVisitor(new de.monticore.umlstereotype._symboltable.UMLStereotypeSymbols2Json(printer));
    toSerialize.accept(symbolTablePrinter);
    return printer.getContent();
  }
}
