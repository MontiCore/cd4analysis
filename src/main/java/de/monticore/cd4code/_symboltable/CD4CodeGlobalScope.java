/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4code._symboltable;

import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
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
    return CD4AnalysisGlobalScope.calculateModelNamesParts(name);
  }

  @Override
  public Set<String> calculateModelNamesForVariable(String name) {
    return calculateModelNamesForField(name);
  }

  @Override
  public Set<String> calculateModelNamesForCDMethodSignature(String name) {
    return CD4AnalysisGlobalScope.calculateModelNamesParts(name);
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
