/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTablePrinter;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTablePrinter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTablePrinter;
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

  @Override
  protected void deserializeAdditionalArtifactScopeAttributes(ICD4CodeArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAdditionalArtifactScopeAttributes(scope, scopeJson);
    deserializeFurtherObjects(symAssociations, scopeJson);
  }
}
