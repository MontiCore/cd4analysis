/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.io.paths.ModelPath;

import java.util.Set;

public class CD4CodeGlobalScope extends CD4CodeGlobalScopeTOP {
  public static final String EXTENSION = "cd";

  public CD4CodeGlobalScope(ModelPath modelPath) {
    super(modelPath, EXTENSION);
  }

  public CD4CodeGlobalScope(ModelPath modelPath, String modelFileExtension) {
    super(modelPath, modelFileExtension);
  }

  @Override
  public CD4CodeGlobalScope getRealThis() {
    return this;
  }

  public void addBuiltInTypes() {
    final CD4CodeArtifactScope artifactScope = CD4CodeMill
        .cD4CodeArtifactScopeBuilder()
        .setPackageName("")
        .setEnclosingScope(this)
        .build();
    artifactScope.setName("BuiltInTypes");

    addBuiltInPrimitiveTypes(artifactScope);
    addBuiltInObjectTypes(artifactScope);
    addBuiltInUtilTypes(artifactScope);
  }

  public void addBuiltInPrimitiveTypes(CD4CodeArtifactScope artifactScope) {
    final CD4CodeScope primitiveTypesScope = CD4CodeMill
        .cD4CodeScopeBuilder()
        .setNameAbsent()
        .setEnclosingScope(artifactScope)
        .build();

    BuiltInTypes.addBuiltInUtilTypes(primitiveTypesScope, BuiltInTypes.PRIMITIVE_TYPES, false);
  }

  public void addBuiltInObjectTypes(CD4CodeArtifactScope artifactScope) {
    final String scopeName = "java.lang";

    final CD4CodeScope objectTypesScope = CD4CodeMill
        .cD4CodeScopeBuilder()
        .setName(scopeName)
        .setEnclosingScope(artifactScope)
        .build();

    BuiltInTypes.addBuiltInUtilTypes(objectTypesScope, BuiltInTypes.OBJECT_TYPES, true);
  }

  public void addBuiltInUtilTypes(CD4CodeArtifactScope artifactScope) {
    final String scopeName = "java.util";

    final CD4CodeScope utilTypesScope = CD4CodeMill
        .cD4CodeScopeBuilder()
        .setName(scopeName)
        .setEnclosingScope(artifactScope)
        .build();

    BuiltInTypes.addBuiltInUtilTypes(utilTypesScope, BuiltInTypes.UTIL_TYPES, true);
  }

  @Override
  public Set<String> calculateModelNamesForCDType(String name) {
    return CD4AnalysisGlobalScope.calculateModelNamesSimple(name);
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
    return CD4AnalysisGlobalScope.calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForVariable(String name) {
    return calculateModelNamesForField(name);
  }

  @Override
  public Set<String> calculateModelNamesForCDMethodSignature(String name) {
    return CD4AnalysisGlobalScope.calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForMethod(String name) {
    return calculateModelNamesForCDMethodSignature(name);
  }

  @Override
  public Set<String> calculateModelNamesForFunction(String name) {
    return calculateModelNamesForMethod(name);
  }

  @Override
  public Set<String> calculateModelNamesForCDAssociation(String name) {
    return CD4AnalysisGlobalScope.calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForCDRole(String name) {
    return CD4AnalysisGlobalScope.calculateModelNamesSimple(name);
  }
}
