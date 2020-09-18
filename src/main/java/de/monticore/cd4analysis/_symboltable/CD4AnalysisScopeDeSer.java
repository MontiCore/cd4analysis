/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.BasicSymbolsSymbolTablePrinterWithDuplicateCheck;
import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cd._symboltable.OOSymbolsSymbolTablePrinterWithDuplicateCheck;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._symboltable.CDAssociationScopeDeSer;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTablePrinter;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Path;
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
    this.symbolTablePrinter.setBasicSymbolsVisitor(new BasicSymbolsSymbolTablePrinterWithDuplicateCheck(printer));
    this.symbolTablePrinter.setOOSymbolsVisitor(new OOSymbolsSymbolTablePrinterWithDuplicateCheck(printer));

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

    this.symbolTablePrinter
        .getBasicSymbolsVisitor()
        .flatMap(v -> Optional.of((BasicSymbolsSymbolTablePrinterWithDuplicateCheck) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
    this.symbolTablePrinter
        .getOOSymbolsVisitor()
        .flatMap(v -> Optional.of((OOSymbolsSymbolTablePrinterWithDuplicateCheck) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
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
  public void store(ICD4AnalysisArtifactScope toSerialize, Path symbolPath) {
    // 1. Throw errors and abort storing in case of missing required information:
    if (!toSerialize.isPresentName()) {
      Log.error("0xCD007:CD4AnalysisScopeDeSer cannot store an artifact scope that has no name!");
      return;
    }
    if (null == getSymbolFileExtension()) {
      Log.error("0xCD008:File extension for stored symbol tables has not been set in CD4AnalysisScopeDeSer!");
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

  @Override
  protected void deserializeAdditionalArtifactScopeAttributes(ICD4AnalysisArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAdditionalArtifactScopeAttributes(scope, scopeJson);
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
      final CDAssociationSymbol symbol = cDAssociationSymbolDeSer.deserializeCDAssociationSymbol(symbolJson, scope);
      scope.add(symbol);
      final ICD4AnalysisScope spannedScope = CD4AnalysisMill.cD4AnalysisScopeBuilder().build();

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
