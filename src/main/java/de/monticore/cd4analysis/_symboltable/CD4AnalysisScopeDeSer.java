/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import com.google.common.base.Functions;
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

import java.util.*;
import java.util.stream.Collectors;

public class CD4AnalysisScopeDeSer extends CD4AnalysisScopeDeSerTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Map<Integer, SymAssociation> symAssociations;

  public CD4AnalysisScopeDeSer() {
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
  }

  public void deserializeSymbols(JsonObject scopeJson, ICD4AnalysisScope scope) {
    if (scopeJson.hasArrayMember("symbols")) {
      // use dummy scope, to collect all symbols, and then split them up in their respective scope
      final CD4AnalysisScope dummyPackageScope = CD4AnalysisMill.cD4AnalysisScopeBuilder().build();
      scopeJson.getArrayMember("symbols").forEach(s -> deserializeCDTypeSymbol((JsonObject) s, dummyPackageScope));
      scopeJson.getArrayMember("symbols").forEach(s -> deserializeCDAssociationSymbol((JsonObject) s, dummyPackageScope));

      splitIntoScopesByFullName(dummyPackageScope).forEach(s -> scope.addSubScope(dummyPackageScope));
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

    // TODO SVa: link elements in the scopes (Association, Role, SymAssociation)
    // move subscopes to the package scope
    return cd4AnalysisArtifactScope;
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
            deserializeCDRoleSymbol((JsonObject) m, spannedScope);
          }
        });
      }

      symbol.setSpannedScope(spannedScope);
      scope.addSubScope(spannedScope);
    }
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

  public static String getBaseName(ISymbol symbol) {
    final List<String> partList = MCQualifiedNameFacade.createQualifiedName(symbol.getFullName()).getPartList();
    return partList.stream().limit(partList.size() - 1).collect(Collectors.joining("."));
  }

  public static Collection<CD4AnalysisScope> splitIntoScopesByFullName(CD4AnalysisScope dummyPackageScope) {
    final Set<String> packageScopeNames = dummyPackageScope.getCDAssociationSymbols().values().stream().map(CD4AnalysisScopeDeSer::getBaseName).collect(Collectors.toSet());
    packageScopeNames.addAll(dummyPackageScope.getCDTypeSymbols().values().stream().map(CD4AnalysisScopeDeSer::getBaseName).collect(Collectors.toSet()));
    packageScopeNames.addAll(dummyPackageScope.getCDRoleSymbols().values().stream().map(CD4AnalysisScopeDeSer::getBaseName).collect(Collectors.toSet()));

    Map<String, CD4AnalysisScope> packageScopes = packageScopeNames.stream().collect(Collectors.toMap(Functions.identity(), p -> CD4AnalysisMill.cD4AnalysisScopeBuilder().setName(p).build()));

    dummyPackageScope.getCDAssociationSymbols().values().forEach(s -> {
      final CD4AnalysisScope cd4AnalysisScope = packageScopes.get(getBaseName(s));
      cd4AnalysisScope.add(s);
      cd4AnalysisScope.addSubScope(s.getSpannedScope());
    });
    dummyPackageScope.getCDTypeSymbols().values().forEach(s -> {
      final CD4AnalysisScope cd4AnalysisScope = packageScopes.get(getBaseName(s));
      cd4AnalysisScope.add(s);
      cd4AnalysisScope.addSubScope(s.getSpannedScope());
    });
    dummyPackageScope.getCDRoleSymbols().values().forEach(s -> {
      final CD4AnalysisScope cd4AnalysisScope = packageScopes.get(getBaseName(s));
      cd4AnalysisScope.add(s);
    });

    return packageScopes.values();
  }
}
