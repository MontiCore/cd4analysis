/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import com.google.common.collect.Lists;

import java.util.List;

public interface ICDBasisArtifactScope extends ICDBasisArtifactScopeTOP {

  /**
   * This method returns the package name of the current artifact scope.
   * If the package is empty or a language does not support packages,
   * the method implementation returns an empty String.
   *
   * @return
   */
  @Override
  default String getPackageName() {
    return this.isPresentName() ? this.getName() : "";
  }

  default List<String> getRemainingNameForResolveDown(String symbolName) {
    final String packageAS = this.getPackageName();
    final com.google.common.collect.FluentIterable<String> packageASNameParts = com.google.common.collect.FluentIterable
        .from(de.se_rwth.commons.Splitters.DOT.omitEmptyStrings().split(packageAS));
    
    final com.google.common.collect.FluentIterable<String> symbolNameParts = com.google.common.collect.FluentIterable
        .from(de.se_rwth.commons.Splitters.DOT.split(symbolName));
    String remainingSymbolName = symbolName;
    
    if (symbolNameParts.size() > packageASNameParts.size()) {
      remainingSymbolName = de.se_rwth.commons.Joiners.DOT.join(symbolNameParts.skip(packageASNameParts.size()));
    }
    
    return Lists.newArrayList(remainingSymbolName);
  }
}
