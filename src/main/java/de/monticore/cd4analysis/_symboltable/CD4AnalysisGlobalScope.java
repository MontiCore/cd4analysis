/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CD4AnalysisGlobalScope extends CD4AnalysisGlobalScopeTOP {
  public static final String EXTENSION = "cd";

  public CD4AnalysisGlobalScope(ModelPath modelPath) {
    super(modelPath, EXTENSION);
  }

  public CD4AnalysisGlobalScope(ModelPath modelPath, String modelFileExtension) {
    super(modelPath, modelFileExtension);
  }

  /**
   * Splits a qualified name in a potential model name part
   * (cutting the potential name of the symbol)
   * <pre>
   * name has to have at least 2 parts,
   * where at least the first one is the model name
   * and at least the last one is the symbol name
   * Example:
   * input {@code "de.monticore.cdbasis.parser.Simple.A"} would return
   * {@code
   *        [
   *          "de",
   *          "de.monticore",
   *          "de.monticore.cdbasis",
   *          "de.monticore.cdbasis.parser",
   *          "de.monticore.cdbasis.parser.Simple"
   *        ]}
   * at least the last element is the name of the symbol
   * </pre>
   *
   * @param qName a qualified name, to be splitted for the model name
   * @return a List of potential model names
   */
  public static Set<String> calculateModelNamesSimple(String qName) {
    if (Names.getQualifier(qName).isEmpty()) {
      Log.error(String.format(
          "0xCD200: to calculate a model name, the name has to have at least two parts. But got \"%s\".",
          qName));
      return null;
    }

    final List<String> nameParts = Splitters.DOT.splitToList(qName);
    return IntStream.range(1, nameParts.size() - 1) // always begin with the first element, and stop at the second to last
        .mapToObj(i -> nameParts.stream().limit(i).collect(Collectors.joining(".")))
        .collect(Collectors.toSet());
  }

  @Override
  public CD4AnalysisGlobalScope getRealThis() {
    return this;
  }

  public void addBuiltInTypes() {
    final CD4AnalysisArtifactScope artifactScope = CD4AnalysisMill
        .cD4AnalysisArtifactScopeBuilder()
        .setPackageName("")
        .setEnclosingScope(this)
        .build();
    artifactScope.setName("BuiltInTypes");

    addBuiltInPrimitiveTypes(artifactScope);
    addBuiltInObjectTypes(artifactScope);
    addBuiltInUtilTypes(artifactScope);
  }

  public void addBuiltInPrimitiveTypes(CD4AnalysisArtifactScope artifactScope) {
    final CD4AnalysisScope primitiveTypesScope = CD4AnalysisMill
        .cD4AnalysisScopeBuilder()
        .setNameAbsent()
        .setEnclosingScope(artifactScope)
        .build();

    BuiltInTypes.addBuiltInUtilTypes(primitiveTypesScope, BuiltInTypes.PRIMITIVE_TYPES, false);
  }

  public void addBuiltInObjectTypes(CD4AnalysisArtifactScope artifactScope) {
    final String scopeName = "java.lang";

    final CD4AnalysisScope objectTypesScope = CD4AnalysisMill
        .cD4AnalysisScopeBuilder()
        .setName(scopeName)
        .setEnclosingScope(artifactScope)
        .build();

    BuiltInTypes.addBuiltInUtilTypes(objectTypesScope, BuiltInTypes.OBJECT_TYPES, true);
  }

  public void addBuiltInUtilTypes(CD4AnalysisArtifactScope artifactScope) {
    final String scopeName = "java.util";

    final CD4AnalysisScope utilTypesScope = CD4AnalysisMill
        .cD4AnalysisScopeBuilder()
        .setName(scopeName)
        .setEnclosingScope(artifactScope)
        .build();

    BuiltInTypes.addBuiltInUtilTypes(utilTypesScope, BuiltInTypes.UTIL_TYPES, true);
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
