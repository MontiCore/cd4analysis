/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cdassociation._symboltable.CDAssociationScopeDeSer;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTablePrinter;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

  public void deserializeSubScopes(JsonObject scopeJson) {
    if (scopeJson.hasArrayMember("subscopes")) {
      scopeJson.getArrayMember("subscopes").forEach(s -> deserializeCD4AnalysisScope((JsonObject)s));
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

    // deserialize all the subscopes
    deserializeSubScopes(scopeJson);

    return cd4AnalysisArtifactScope;
  }
}
