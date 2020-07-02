/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.typesymbols.TypeSymbolsMill;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CD4AnalysisGlobalScope extends CD4AnalysisGlobalScopeTOP {
  public static final String EXTENSION = "cd";

  public CD4AnalysisGlobalScope(ModelPath modelPath) {
    super(modelPath, EXTENSION);
  }

  public CD4AnalysisGlobalScope(ModelPath modelPath, String modelFileExtension) {
    super(modelPath, modelFileExtension);
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
    BuiltInTypes.PRIMITIVE_TYPES
        .forEach(t -> primitiveTypesScope.add(
            TypeSymbolsMill
                .oOTypeSymbolBuilder()
                .setName(t)
                .setEnclosingScope(primitiveTypesScope)
                .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
                .build()));
  }

  public void addBuiltInObjectTypes(CD4AnalysisArtifactScope artifactScope) {
    final String scopeName = "java.lang";

    final CD4AnalysisScope objectTypesScope = CD4AnalysisMill
        .cD4AnalysisScopeBuilder()
        .setName(scopeName)
        .setEnclosingScope(artifactScope)
        .build();
    BuiltInTypes.OBJECT_TYPES
        .forEach(t -> objectTypesScope.add(
            TypeSymbolsMill
                .oOTypeSymbolBuilder()
                .setName(t)
                .setEnclosingScope(objectTypesScope)
                .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
                .setIsClass(true)
                .build()));
  }

  public void addBuiltInUtilTypes(CD4AnalysisArtifactScope artifactScope) {
    final String scopeName = "java.util";

    final CD4AnalysisScope utilTypesScope = CD4AnalysisMill
        .cD4AnalysisScopeBuilder()
        .setName(scopeName)
        .setEnclosingScope(artifactScope)
        .build();
    BuiltInTypes.UTIL_TYPES
        .forEach(t -> utilTypesScope.add(
            TypeSymbolsMill
                .oOTypeSymbolBuilder()
                .setName(t)
                .setEnclosingScope(utilTypesScope)
                .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
                .setIsClass(true)
                .build()));
  }

  public static Set<String> calculateModelNamesSimple(String name) {
    // e.g., if p.CD.Clazz, return p.CD
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }
    return Collections.emptySet();
  }

  public static Set<String> calculateModelNamesParts(String name) {
    // e.g., if p.CD.Clazz.Meth return p.CD
    List<String> nameParts = Splitters.DOT.splitToList(name);

    // at least 3, because of CD.Clazz.meth
    if (nameParts.size() >= 3) {
      // cut the last two name parts (e.g., Clazz.meth)
      return ImmutableSet.of(Joiners.DOT.join(nameParts.subList(0, nameParts.size() - 2)));
    }
    return Collections.emptySet();
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
    return calculateModelNamesParts(name);
  }

  @Override
  public Set<String> calculateModelNamesForVariable(String name) {
    return calculateModelNamesForField(name);
  }

  @Override
  public Set<String> calculateModelNamesForMethod(String name) {
    return calculateModelNamesParts(name);
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
