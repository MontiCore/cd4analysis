/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._symboltable.CDAssociationScopeDeSer;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDAssociationSymbols2Json;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdbasis._symboltable.CDBasisSymbols2Json;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CD4AnalysisScopeDeSer extends CD4AnalysisScopeDeSerTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Map<Integer, SymAssociation> symAssociations;
  public static final String FURTHER_OBJECTS_MAP = "furtherObjects";

  public CD4AnalysisScopeDeSer() {
    setSymbolTablePrinterHelper(new CDSymbolTablePrinterHelper());
    setSymAssociations(new HashMap<>());
  }

  public static String getBaseName(String name) {
    final List<String> partList = MCQualifiedNameFacade.createQualifiedName(name).getPartsList();
    return partList.stream().limit(partList.size() - 1).collect(Collectors.joining("."));
  }

  public static String getBaseName(ISymbol symbol) {
    return getBaseName(symbol.getFullName());
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
  protected void deserializeAddons(ICD4AnalysisArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(symAssociations, scopeJson);
  }

  public static void deserializeFurtherObjects(Map<Integer, SymAssociation> symAssociations, JsonObject scopeJson) {
    if (scopeJson.hasObjectMember(FURTHER_OBJECTS_MAP)) {
      final JsonObject objectJson = scopeJson.getObjectMember(FURTHER_OBJECTS_MAP);
      objectJson.getMembers().entrySet().forEach(m -> CDAssociationScopeDeSer.deserializeSymAssociations(symAssociations, m));
    }
  }

  @Override
  protected void deserializeCDAssociationSymbol(JsonObject symbolJson, ICD4AnalysisScope scope) {
    if (symbolJson.getStringMemberOpt(JsonDeSers.KIND).flatMap(k -> Optional.of(k.equals(cDAssociationSymbolDeSer.getSerializedKind()))).orElse(false)) {
      final CDAssociationSymbol symbol = cDAssociationSymbolDeSer.deserializeCDAssociationSymbol(symbolJson);
      scope.add(symbol);
      final ICD4AnalysisScope spannedScope = CD4AnalysisMill.scope();

      if (symbolJson.hasArrayMember("symbols")) {
        symbolJson.getArrayMember("symbols").forEach(m -> {
          if (m.isJsonObject()) {
            deserializeCDRoleSymbol((JsonObject) m, spannedScope);
          }
        });
      }

      symbol.setSpannedScope(spannedScope);
      scope.addSubScope(spannedScope);
    }
  }
  
  @Override
  public String serialize(de.monticore.cd4analysis._symboltable.ICD4AnalysisScope toSerialize) {
    de.monticore.symboltable.serialization.JsonPrinter printer = new de.monticore.symboltable.serialization.JsonPrinter();
    de.monticore.cd4analysis._visitor.CD4AnalysisDelegatorVisitor symbolTablePrinter = new de.monticore.cd4analysis._visitor.CD4AnalysisDelegatorVisitor();
    
    CD4AnalysisSymbols2Json cd4as2j = new de.monticore.cd4analysis._symboltable.CD4AnalysisSymbols2Json(printer);
    cd4as2j.setSymbolTablePrinterHelper(symbolTablePrinterHelper);
    symbolTablePrinter.setCD4AnalysisVisitor(cd4as2j);
    
    symbolTablePrinter.setBitExpressionsVisitor(new de.monticore.expressions.bitexpressions._symboltable.BitExpressionsSymbols2Json(printer));
    symbolTablePrinter.setCommonExpressionsVisitor(new de.monticore.expressions.commonexpressions._symboltable.CommonExpressionsSymbols2Json(printer));
    symbolTablePrinter.setMCLiteralsBasisVisitor(new de.monticore.literals.mcliteralsbasis._symboltable.MCLiteralsBasisSymbols2Json(printer));
    symbolTablePrinter.setMCBasicTypesVisitor(new de.monticore.types.mcbasictypes._symboltable.MCBasicTypesSymbols2Json(printer));
    symbolTablePrinter.setCDInterfaceAndEnumVisitor(new de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbols2Json(printer));
    
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
    
    symbolTablePrinter.setMCCollectionTypesVisitor(new de.monticore.types.mccollectiontypes._symboltable.MCCollectionTypesSymbols2Json(printer));
    symbolTablePrinter.setOOSymbolsVisitor(new de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json(printer));
    symbolTablePrinter.setMCBasicsVisitor(new de.monticore.mcbasics._symboltable.MCBasicsSymbols2Json(printer));
    symbolTablePrinter.setUMLStereotypeVisitor(new de.monticore.umlstereotype._symboltable.UMLStereotypeSymbols2Json(printer));
    toSerialize.accept(symbolTablePrinter);
    return printer.getContent();
  }
}
