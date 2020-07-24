/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.io.paths.ModelPath;

import java.util.Set;

public class CD4CodeGlobalScope extends CD4CodeGlobalScopeTOP {
  public static final String EXTENSION = "cd";
  protected CDSymbolTableHelper symbolTableHelper;

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

  public void setSymbolTableHelper(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
  }

  public Set<String> calculateModelNamesSimple(String qName) {
    return CDSymbolTableHelper.calculateModelNamesSimple(qName, symbolTableHelper);
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

    BuiltInTypes.addBuiltInTypes(primitiveTypesScope, BuiltInTypes.PRIMITIVE_TYPES, false);
  }

  public void addBuiltInObjectTypes(CD4CodeArtifactScope artifactScope) {
    final String scopeName = "java.lang";

    final CD4CodeScope objectTypesScope = CD4CodeMill
        .cD4CodeScopeBuilder()
        .setName(scopeName)
        .setEnclosingScope(artifactScope)
        .build();

    BuiltInTypes.addBuiltInTypes(objectTypesScope, BuiltInTypes.OBJECT_TYPES, true);
  }

  public void addBuiltInUtilTypes(CD4CodeArtifactScope artifactScope) {
    final String scopeName = "java.util";

    final CD4CodeScope utilTypesScope = CD4CodeMill
        .cD4CodeScopeBuilder()
        .setName(scopeName)
        .setEnclosingScope(artifactScope)
        .build();

    BuiltInTypes.addBuiltInTypes(utilTypesScope, BuiltInTypes.UTIL_TYPES, true);
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
  public Set<String> calculateModelNamesForCDMethodSignature(String name) {
    return calculateModelNamesSimple(name);
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
    return calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForCDRole(String name) {
    return calculateModelNamesSimple(name);
  }
}
