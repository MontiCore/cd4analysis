/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis._symboltable;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.io.paths.ModelPath;
import de.monticore.utils.Names;

import java.util.HashSet;
import java.util.Set;

public interface ICD4AnalysisGlobalScope extends ICD4AnalysisGlobalScopeTOP {

  @Override
  default Set<String> calculateModelNamesForType(String name) {
    Set<String> result = new HashSet<>();

    while (name.contains(".")) {
      name = Names.getQualifier(name);
      result.add(name);
    }
    return result;
  }

  @Override
  default Set<String> calculateModelNamesForCDType(String name) {
    Set<String> result = new HashSet<>();

    while (name.contains(".")) {
      name = Names.getQualifier(name);
      result.add(name);
    }
    return result;
  }

  @Override
  default Set<String> calculateModelNamesForOOType(String name) {
    Set<String> result = new HashSet<>();

    while (name.contains(".")) {
      name = Names.getQualifier(name);
      result.add(name);
    }
    return result;
  }
}
