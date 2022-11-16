/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._symboltable;

import de.se_rwth.commons.Names;
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
  default Set<String> calculateModelNamesForOOType(String name) {
    return calculateModelNamesForType(name);
  }

  @Override
  default Set<String> calculateModelNamesForCDType(String name) {
    return calculateModelNamesForOOType(name);
  }
}
