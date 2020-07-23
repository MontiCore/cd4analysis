/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._symboltable.CDAssociationScopeDeSer;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTablePrinter;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CD4AnalysisScopeDeSer extends CD4AnalysisScopeDeSerTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Map<Integer, SymAssociation> symAssociations;

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
  }

  @Override
  public void store(CD4AnalysisArtifactScope toSerialize, Path symbolPath) {
    // 1. Throw errors and abort storing in case of missing required information:
    if (!toSerialize.isPresentName()) {
      Log.error("0xA7015x1436847926 CD4AnalysisScopeDeSer cannot store an artifact scope that has no name!");
      return;
    }
    if (null == getSymbolFileExtension()) {
      Log.error("0xA7016x1436848888 File extension for stored symbol tables has not been set in CD4AnalysisScopeDeSer!");
      return;
    }

    //2. calculate absolute location for the file to create, including the package if it is non-empty
    java.nio.file.Path path = symbolPath; //starting with symbol path
    if (null != toSerialize.getRealPackageName() && toSerialize.getRealPackageName().length() > 0) {
      path = path.resolve(de.se_rwth.commons.Names.getPathFromPackage(toSerialize.getRealPackageName()));
    }
    path = path.resolve(toSerialize.getName() + "." + getSymbolFileExtension());

    //3. serialize artifact scope, which will become the file content
    String serialized = serialize(toSerialize);

    //4. store serialized artifact scope to calculated location
    de.monticore.io.FileReaderWriter.storeInFile(path, serialized);
  }

  public void deserializeSymbols(JsonObject scopeJson, ICD4AnalysisScope scope) {
    if (scopeJson.hasArrayMember("symbols")) {
      // use dummy scope, to collect all symbols, and then split them up in their respective scope

      Map<String, ICD4AnalysisScope> scopes = scopeJson
          .getArrayMember("symbols")
          .stream().map(s -> s.getAsJsonObject().getStringMemberOpt("name"))
          .filter(Optional::isPresent)
          .map(s -> CD4AnalysisScopeDeSer.getBaseName(s.get()))
          .distinct()
          .collect(Collectors.toMap(Function.identity(), s -> CD4AnalysisMill.cD4AnalysisScopeBuilder().setName(s).build()));

      scopeJson.getArrayMember("symbols").forEach(s -> deserializeCDTypeSymbol((JsonObject) s, scopes));
      scopeJson.getArrayMember("symbols").forEach(s -> deserializeCDAssociationSymbol((JsonObject) s, scopes));

      scopes.values().forEach(scope::addSubScope);
    }
  }

  @Override
  protected void deserializeAdditionalArtifactScopeAttributes(CD4AnalysisArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAdditionalArtifactScopeAttributes(scope, scopeJson);
    CDAssociationScopeDeSer.deserializeSymAssociations(symAssociations, scopeJson);
  }

  @Override
  protected CD4AnalysisArtifactScope deserializeCD4AnalysisArtifactScope(JsonObject scopeJson) {
    final CD4AnalysisArtifactScope cd4AnalysisArtifactScope = super.deserializeCD4AnalysisArtifactScope(scopeJson);

    // deserialize all the symbols
    deserializeSymbols(scopeJson, cd4AnalysisArtifactScope);

    // TODO: move subscopes to the package scope
    return cd4AnalysisArtifactScope;
  }

  protected void deserializeCDTypeSymbol(JsonObject symbolJson, Map<String, ICD4AnalysisScope> scopes) {
    ICD4AnalysisScope scope = scopes.get(getBaseName(symbolJson.getStringMember(JsonDeSers.NAME)));
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
  protected void deserializeCDTypeSymbol(JsonObject symbolJson, ICD4AnalysisScope scope) {
    if (symbolJson.getStringMemberOpt(JsonDeSers.KIND).flatMap(k -> Optional.of(k.equals(cDTypeSymbolDeSer.getSerializedKind()))).orElse(false)) {
      final CDTypeSymbol symbol = cDTypeSymbolDeSer.deserializeCDTypeSymbol(symbolJson, scope);
      scope.add(symbol);
      final CD4AnalysisScope spannedScope = CD4AnalysisMill.cD4AnalysisScopeBuilder().build();

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

  protected void deserializeCDAssociationSymbol(JsonObject symbolJson, Map<String, ICD4AnalysisScope> scopes) {
    ICD4AnalysisScope scope = scopes.get(getBaseName(symbolJson.getStringMember(JsonDeSers.NAME)));
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
  protected void deserializeCDAssociationSymbol(JsonObject symbolJson, ICD4AnalysisScope scope) {
    if (symbolJson.getStringMemberOpt(JsonDeSers.KIND).flatMap(k -> Optional.of(k.equals(cDAssociationSymbolDeSer.getSerializedKind()))).orElse(false)) {
      final CDAssociationSymbol symbol = cDAssociationSymbolDeSer.deserializeCDAssociationSymbol(symbolJson, scope);
      scope.add(symbol);
      final CD4AnalysisScope spannedScope = CD4AnalysisMill.cD4AnalysisScopeBuilder().build();

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
