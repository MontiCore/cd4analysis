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

    this.symbolTablePrinter
        .getCDBasisVisitor()
        .flatMap(v -> Optional.of((CDBasisSymbols2Json) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
    this.symbolTablePrinter
        .getCDAssociationVisitor()
        .flatMap(v -> Optional.of((CDAssociationSymbols2Json) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
    this.symbolTablePrinter
        .getCD4AnalysisVisitor()
        .flatMap(v -> Optional.of((CD4AnalysisSymbols2Json) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
    this.symbolTablePrinter
        .getCD4CodeBasisVisitor()
        .flatMap(v -> Optional.of((CD4CodeBasisSymbols2Json) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
    this.symbolTablePrinter
        .getCD4CodeVisitor()
        .flatMap(v -> Optional.of((CD4CodeSymbols2Json) v))
        .ifPresent(v -> v.setSymbolTablePrinterHelper(symbolTablePrinterHelper));
  }

  @Override
  protected void deserializeAddons(ICD4CodeArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAddons(scope, scopeJson);
    deserializeFurtherObjects(symAssociations, scopeJson);
  }
}
