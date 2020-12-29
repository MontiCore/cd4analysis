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

  public CD4CodeGlobalScope(){
    super();
    setSymbolTableHelper(new CDSymbolTableHelper(CD4CodeMill.deriveSymTypeOfCD4Code()));
  }

  public CD4CodeGlobalScope(ModelPath modelPath) {
    super(modelPath, EXTENSION);
    setSymbolTableHelper(new CDSymbolTableHelper(CD4CodeMill.deriveSymTypeOfCD4Code()));
  }

  public CD4CodeGlobalScope(ModelPath modelPath, String modelFileExtension) {
    super(modelPath, modelFileExtension);
    setSymbolTableHelper(new CDSymbolTableHelper(CD4CodeMill.deriveSymTypeOfCD4Code()));
  }

  @Override
  public CD4CodeGlobalScope getRealThis() {
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

  @Override
  public void addBuiltInTypes() {
    if (getSubScopes().stream().noneMatch(s -> s.getName().equals(BuiltInTypes.SCOPE_NAME))) {
      final ICD4CodeArtifactScope artifactScope = CD4CodeMill
          .artifactScope();
      artifactScope.setPackageName("");
      artifactScope.setEnclosingScope(this);
      artifactScope.setName(BuiltInTypes.SCOPE_NAME);

      addBuiltInPrimitiveTypes(artifactScope);
      addBuiltInObjectTypes(artifactScope);
      addBuiltInUtilTypes(artifactScope);
    }
  }

  public void addBuiltInPrimitiveTypes(ICD4CodeArtifactScope artifactScope) {
    final ICD4CodeScope primitiveTypesScope = CD4CodeMill
        .scope();
    primitiveTypesScope.setNameAbsent();
    primitiveTypesScope.setEnclosingScope(artifactScope);


    BuiltInTypes.addBuiltInTypes(primitiveTypesScope, BuiltInTypes.PRIMITIVE_TYPES);
  }

  public void addBuiltInObjectTypes(ICD4CodeArtifactScope artifactScope) {
    final String scopeName = "java.lang";

    final ICD4CodeScope objectTypesScope = CD4CodeMill
        .scope();
    objectTypesScope.setName(scopeName);
    objectTypesScope.setEnclosingScope(artifactScope);

    BuiltInTypes.addBuiltInOOTypes(objectTypesScope, BuiltInTypes.OBJECT_TYPES, true);
  }

  public void addBuiltInUtilTypes(ICD4CodeArtifactScope artifactScope) {
    final String scopeName = "java.util";

    final ICD4CodeScope utilTypesScope = CD4CodeMill
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
