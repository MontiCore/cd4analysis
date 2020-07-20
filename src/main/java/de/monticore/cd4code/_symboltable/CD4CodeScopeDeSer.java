/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cd4analysis._symboltable.CD4AnalysisScopeDeSer;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTablePrinter;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTablePrinter;
import de.monticore.cdassociation._symboltable.CDAssociationScopeDeSer;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTablePrinter;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  public void setSymbolTablePrinterHelper(CDSymbolTablePrinterHelper symbolTablePrinterHelper) {
    this.symbolTablePrinterHelper = symbolTablePrinterHelper;

    this.symbolTablePrinter
        .getCDBasisVisitor()
        .flatMap(v -> Optional.of((CDBasisSymbolTablePrinter) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
    this.symbolTablePrinter
        .getCDAssociationVisitor()
        .flatMap(v -> Optional.of((CDAssociationSymbolTablePrinter) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
    this.symbolTablePrinter
        .getCD4AnalysisVisitor()
        .flatMap(v -> Optional.of((CD4AnalysisSymbolTablePrinter) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
    this.symbolTablePrinter
        .getCD4CodeBasisVisitor()
        .flatMap(v -> Optional.of((CD4CodeBasisSymbolTablePrinter) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
    this.symbolTablePrinter
        .getCD4CodeVisitor()
        .flatMap(v -> Optional.of((CD4CodeSymbolTablePrinter) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
  }

  public void deserializeSymbols(JsonObject scopeJson, ICD4CodeScope scope) {
    if (scopeJson.hasArrayMember("symbols")) {
      // use dummy scope, to collect all symbols, and then split them up in their respective scope

      Map<String, ICD4CodeScope> scopes = scopeJson
          .getArrayMember("symbols")
          .stream().map(s -> s.getAsJsonObject().getStringMemberOpt("name"))
          .filter(Optional::isPresent)
          .map(s -> CD4AnalysisScopeDeSer.getBaseName(s.get()))
          .distinct()
          .collect(Collectors.toMap(Function.identity(), s -> CD4CodeMill.cD4CodeScopeBuilder().setName(s).build()));

      scopeJson.getArrayMember("symbols").forEach(s -> deserializeCDTypeSymbol((JsonObject) s, scopes));
      scopeJson.getArrayMember("symbols").forEach(s -> deserializeCDAssociationSymbol((JsonObject) s, scopes));

      scopes.values().forEach(scope::addSubScope);
    }
  }

  @Override
  protected void deserializeAdditionalArtifactScopeAttributes(CD4CodeArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAdditionalArtifactScopeAttributes(scope, scopeJson);
    CDAssociationScopeDeSer.deserializeSymAssociations(symAssociations, scopeJson);
  }

  @Override
  protected CD4CodeArtifactScope deserializeCD4CodeArtifactScope(JsonObject scopeJson) {
    final CD4CodeArtifactScope cd4CodeArtifactScope = super.deserializeCD4CodeArtifactScope(scopeJson);

    // deserialize all the symbols
    deserializeSymbols(scopeJson, cd4CodeArtifactScope);

    // TODO: move subscopes to the package scope
    return cd4CodeArtifactScope;
  }

  protected void deserializeCDTypeSymbol(JsonObject symbolJson, Map<String, ICD4CodeScope> scopes) {
    ICD4CodeScope scope = scopes.get(CD4AnalysisScopeDeSer.getBaseName(symbolJson.getStringMember(JsonDeSers.NAME)));
    if (scope == null) {
      Log.error(String.format(
          "0xCD005: the scope for package %s is not created",
          symbolJson.getStringMember(JsonDeSers.NAME)
      ));
      return;
    }

    deserializeCDTypeSymbol(symbolJson, scope);
  }

  @Override
  protected void deserializeCDTypeSymbol(JsonObject symbolJson, ICD4CodeScope scope) {
    if (symbolJson.getStringMemberOpt(JsonDeSers.KIND).flatMap(k -> Optional.of(k.equals(cDTypeSymbolDeSer.getSerializedKind()))).orElse(false)) {
      final CDTypeSymbol symbol = cDTypeSymbolDeSer.deserializeCDTypeSymbol(symbolJson, scope);
      scope.add(symbol);
      final CD4CodeScope spannedScope = CD4CodeMill.cD4CodeScopeBuilder().build();

      if (symbolJson.hasArrayMember("symbols")) {
        symbolJson.getArrayMember("symbols").forEach(m -> {
          if (m.isJsonObject()) {
            JsonObject o = (JsonObject) m;
            if (o.getStringMemberOpt(JsonDeSers.KIND).flatMap(k -> Optional.of(k.equals(cDRoleSymbolDeSer.getSerializedKind()))).orElse(false)) {
              spannedScope.add(cDRoleSymbolDeSer.deserializeCDRoleSymbol(o, spannedScope));
            }
            else if (o.getStringMemberOpt(JsonDeSers.KIND).flatMap(k -> Optional.of(k.equals(fieldSymbolDeSer.getSerializedKind()))).orElse(false)) {
              spannedScope.add(fieldSymbolDeSer.deserializeFieldSymbol(o, spannedScope));
            }
          }
        });
      }

      symbol.setSpannedScope(spannedScope);
      scope.addSubScope(spannedScope);
    }
  }

  protected void deserializeCDAssociationSymbol(JsonObject symbolJson, Map<String, ICD4CodeScope> scopes) {
    ICD4CodeScope scope = scopes.get(CD4AnalysisScopeDeSer.getBaseName(symbolJson.getStringMember(JsonDeSers.NAME)));
    if (scope == null) {
      Log.error(String.format(
          "0xCD005: the scope for package %s is not created",
          symbolJson.getStringMember(JsonDeSers.NAME)
      ));
      return;
    }

    deserializeCDAssociationSymbol(symbolJson, scope);
  }

  @Override
  protected void deserializeCDAssociationSymbol(JsonObject symbolJson, ICD4CodeScope scope) {
    if (symbolJson.getStringMemberOpt(JsonDeSers.KIND).flatMap(k -> Optional.of(k.equals(cDAssociationSymbolDeSer.getSerializedKind()))).orElse(false)) {
      final CDAssociationSymbol symbol = cDAssociationSymbolDeSer.deserializeCDAssociationSymbol(symbolJson, scope);
      scope.add(symbol);
      final CD4CodeScope spannedScope = CD4CodeMill.cD4CodeScopeBuilder().build();

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
}
