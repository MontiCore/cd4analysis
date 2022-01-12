/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import com.google.common.collect.Lists;
import de.monticore.symboltable.ImportStatement;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static de.se_rwth.commons.Names.getQualifier;
import static de.se_rwth.commons.Names.getSimpleName;
import static de.se_rwth.commons.logging.Log.trace;

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

  default Set<String> calculateQualifiedNames(String name, String packageName,
                                              List<ImportStatement> imports) {
    final Set<String> potentialSymbolNames = new LinkedHashSet<>();

    // the simple name (in default package)
    potentialSymbolNames.add(name);

    // if name is already qualified, no further (potential) names exist.
    if (getQualifier(name).isEmpty()) {
      // maybe the model belongs to the same package
      if (!packageName.isEmpty()) {
        potentialSymbolNames.add(packageName + "." + name);
      }

      for (ImportStatement importStatement : imports) {
        if (importStatement.isStar() || !importStatement.getStatement().endsWith(name)) {
          potentialSymbolNames.add(importStatement.getStatement() + "." + name);
        } else {
          potentialSymbolNames.add(importStatement.getStatement());
        }
      }
    }
    trace("Potential qualified names for \"" + name + "\": " + potentialSymbolNames.toString(),
      "IArtifactScope");

    return potentialSymbolNames;
  }

}
