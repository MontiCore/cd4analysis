/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;


import de.monticore.symboltable.ImportStatement;

import java.util.List;
import java.util.Set;

public interface ICDBasisArtifactScope extends ICDBasisArtifactScopeTOP {

  @Override
  default Set<String> calculateQualifiedNames(String name, String packageName,
                                              List<ImportStatement> imports) {
    final Set<String> potentialSymbolNames = ICDBasisArtifactScopeTOP.
      super.calculateQualifiedNames(name, packageName, imports);
    if (!packageName.isEmpty() && potentialSymbolNames.size() == 1) {
      potentialSymbolNames.add(packageName + "." + name);
    }
    return potentialSymbolNames;
  }

}
