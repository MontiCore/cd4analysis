/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public interface ICDBasisScope extends ICDBasisScopeTOP {
  default String getRealPackageName() {
    return this.isPresentName() ? this.getName() : "";
  }

  default List<String> getRemainingNameForResolveDown(String symbolName) {
    final FluentIterable<String> nameParts = getNameParts(symbolName);
    final FluentIterable<String> packageNameParts = getNameParts(getRealPackageName());

    if (nameParts.size() >= packageNameParts.size()) {
      final String firstNNameParts = nameParts.stream().limit(packageNameParts.size()).collect(Collectors.joining("."));
      // A scope that exports symbols usually has a name.
      if (firstNNameParts.equals(getRealPackageName())) {
        return Lists.newArrayList(nameParts.stream().skip(packageNameParts.size()).collect(Collectors.joining(".")));
      }
    }

    return Lists.newArrayList(symbolName);
  }

  default boolean checkIfContinueAsSubScope(String symbolName) {
    // always try to continue, because the subscope could contain the packages
    // the packageName in the artifact scope is not considered in any way
    return this.isExportingSymbols();
  }
}
