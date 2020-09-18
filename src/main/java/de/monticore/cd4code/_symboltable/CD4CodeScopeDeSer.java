/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.BasicSymbolsSymbolTablePrinterWithDuplicateCheck;
import de.monticore.cd._symboltable.CDSymbolTablePrinterHelper;
import de.monticore.cd._symboltable.OOSymbolsSymbolTablePrinterWithDuplicateCheck;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTablePrinter;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTablePrinter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTablePrinter;
import de.monticore.cdassociation._symboltable.SymAssociation;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTablePrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static de.monticore.cd4analysis._symboltable.CD4AnalysisScopeDeSer.deserializeFurtherObjects;

public class CD4CodeScopeDeSer extends CD4CodeScopeDeSerTOP {
  protected CDSymbolTablePrinterHelper symbolTablePrinterHelper;
  protected Map<Integer, SymAssociation> symAssociations;

  public CD4CodeScopeDeSer() {
    this.symbolTablePrinter.setBasicSymbolsVisitor(new BasicSymbolsSymbolTablePrinterWithDuplicateCheck(printer));
    this.symbolTablePrinter.setOOSymbolsVisitor(new OOSymbolsSymbolTablePrinterWithDuplicateCheck(printer));

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
  public void store(ICD4CodeArtifactScope toSerialize, Path symbolPath) {
    // 1. Throw errors and abort storing in case of missing required information:
    if (!toSerialize.isPresentName()) {
      Log.error("0xCD00C:CD4CodeScopeDeSer cannot store an artifact scope that has no name!");
      return;
    }
    if (null == getSymbolFileExtension()) {
      Log.error("0xCD00E:File extension for stored symbol tables has not been set in CD4CodeScopeDeSer!");
      return;
    }

    //2. calculate absolute location for the file to create, including the package if it is non-empty
    java.nio.file.Path path = getPath(toSerialize, symbolPath);

    //3. serialize artifact scope, which will become the file content
    String serialized = serialize(toSerialize);

    //4. store serialized artifact scope to calculated location
    de.monticore.io.FileReaderWriter.storeInFile(path, serialized);
  }

  public Path getPath(ICD4CodeArtifactScope toSerialize, Path symbolPath) {
    java.nio.file.Path path = symbolPath; //starting with symbol path
    if (null != toSerialize.getRealPackageName() && toSerialize.getRealPackageName().length() > 0) {
      path = path.resolve(de.se_rwth.commons.Names.getPathFromPackage(toSerialize.getRealPackageName()));
    }
    path = path.resolve(toSerialize.getName() + "." + getSymbolFileExtension());
    return path;
  }

  @Override
  protected void deserializeAdditionalArtifactScopeAttributes(ICD4CodeArtifactScope scope, JsonObject scopeJson) {
    super.deserializeAdditionalArtifactScopeAttributes(scope, scopeJson);
    deserializeFurtherObjects(symAssociations, scopeJson);
  }
}
