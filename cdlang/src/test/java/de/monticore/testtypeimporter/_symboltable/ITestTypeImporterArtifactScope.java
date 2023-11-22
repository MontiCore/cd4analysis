/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testtypeimporter._symboltable;

import com.google.common.collect.Lists;
import java.util.List;

public interface ITestTypeImporterArtifactScope extends ITestTypeImporterArtifactScopeTOP {

  public default List<String> getRemainingNameForResolveDown(String symbolName) {
    final String packageAS = this.getPackageName();
    final com.google.common.collect.FluentIterable<String> packageASNameParts =
        com.google.common.collect.FluentIterable.from(
            de.se_rwth.commons.Splitters.DOT.omitEmptyStrings().split(packageAS));

    final com.google.common.collect.FluentIterable<String> symbolNameParts =
        com.google.common.collect.FluentIterable.from(
            de.se_rwth.commons.Splitters.DOT.split(symbolName));
    String remainingSymbolName = symbolName;

    if (symbolNameParts.size() > packageASNameParts.size()) {
      remainingSymbolName =
          de.se_rwth.commons.Joiners.DOT.join(symbolNameParts.skip(packageASNameParts.size()));
    }

    return Lists.newArrayList(remainingSymbolName);
  }
}
