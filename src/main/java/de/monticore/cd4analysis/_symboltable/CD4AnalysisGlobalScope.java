/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.typescalculator.DeriveSymTypeOfCD4Analysis;
import de.monticore.io.paths.ModelPath;

import java.util.Set;

public class CD4AnalysisGlobalScope extends CD4AnalysisGlobalScopeTOP {
  public static final String EXTENSION = "cd";
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4AnalysisGlobalScope(){
    super();
    setSymbolTableHelper(new CDSymbolTableHelper(new DeriveSymTypeOfCD4Analysis()));
  }

  public CD4AnalysisGlobalScope(ModelPath modelPath) {
    super(modelPath, EXTENSION);
    setSymbolTableHelper(new CDSymbolTableHelper(new DeriveSymTypeOfCD4Analysis()));
  }

  public CD4AnalysisGlobalScope(ModelPath modelPath, String modelFileExtension) {
    super(modelPath, modelFileExtension);
    setSymbolTableHelper(new CDSymbolTableHelper(new DeriveSymTypeOfCD4Analysis()));
  }

  @Override
  public CD4AnalysisGlobalScope getRealThis() {
    return this;
  }

  public void setSymbolTableHelper(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
  }

  public CDSymbolTableHelper getSymbolTableHelper() {
    return symbolTableHelper;
  }

  public Set<String> calculateModelNamesSimple(String qName) {
    return CDSymbolTableHelper.calculateModelNamesSimple(qName, symbolTableHelper);
  }

  public void addBuiltInTypes() {
    if (getSubScopes().stream().noneMatch(s -> s.getName().equals(BuiltInTypes.SCOPE_NAME))) {
      final ICD4AnalysisArtifactScope artifactScope = CD4AnalysisMill
          .artifactScope();
      artifactScope.setPackageName("");
      artifactScope.setEnclosingScope(this);
      artifactScope.setName(BuiltInTypes.SCOPE_NAME);

      addBuiltInPrimitiveTypes(artifactScope);
      addBuiltInObjectTypes(artifactScope);
      addBuiltInUtilTypes(artifactScope);
    }
  }

  public void addBuiltInPrimitiveTypes(ICD4AnalysisArtifactScope artifactScope) {
    final ICD4AnalysisScope primitiveTypesScope = CD4AnalysisMill
        .scope();
    primitiveTypesScope.setNameAbsent();
    primitiveTypesScope.setEnclosingScope(artifactScope);

    BuiltInTypes.addBuiltInTypes(primitiveTypesScope, BuiltInTypes.PRIMITIVE_TYPES);
  }

  public void addBuiltInObjectTypes(ICD4AnalysisArtifactScope artifactScope) {
    final String scopeName = "java.lang";

    final ICD4AnalysisScope objectTypesScope = CD4AnalysisMill
        .scope();
    objectTypesScope.setName(scopeName);
    objectTypesScope.setEnclosingScope(artifactScope);

    BuiltInTypes.addBuiltInOOTypes(objectTypesScope, BuiltInTypes.OBJECT_TYPES, true);
  }

  public void addBuiltInUtilTypes(ICD4AnalysisArtifactScope artifactScope) {
    final String scopeName = "java.util";

    final ICD4AnalysisScope utilTypesScope = CD4AnalysisMill
        .scope();
    utilTypesScope.setName(scopeName);
    utilTypesScope.setEnclosingScope(artifactScope);

    BuiltInTypes.addBuiltInOOTypes(utilTypesScope, BuiltInTypes.UTIL_TYPES, true);
  }

  @Override
  public Set<String> calculateModelNamesForCDType(String name) {
    return calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForOOType(String name) {
    return calculateModelNamesForCDType(name);
  }

  @Override
  public Set<String> calculateModelNamesForType(String name) {
    return calculateModelNamesForOOType(name);
  }

  @Override
  public Set<String> calculateModelNamesForField(String name) {
    return calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForVariable(String name) {
    return calculateModelNamesForField(name);
  }

  @Override
  public Set<String> calculateModelNamesForMethod(String name) {
    return calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForFunction(String name) {
    return calculateModelNamesForMethod(name);
  }

  @Override
  public Set<String> calculateModelNamesForCDAssociation(String name) {
    return calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForCDRole(String name) {
    return calculateModelNamesSimple(name);
  }
}
