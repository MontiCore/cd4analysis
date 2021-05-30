/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.List;
import java.util.stream.Collectors;

public class CDBasisDeSer extends CDBasisDeSerTOP {
  @Override
  protected void deserializeSymbols(ICDBasisScope scope, JsonObject scopeJson) {
    super.deserializeSymbols(scope, scopeJson);
    moveCDTypeSymbolsToPackage(scope);
  }

  public static void moveCDTypeSymbolsToPackage(ICDBasisScope scope) {
    final LinkedListMultimap<String, CDPackageSymbol> cdPackageSymbols = scope.getCDPackageSymbols();

    // move CDTypeSymbols in their respective packages
    scope.getCDTypeSymbols().entries().forEach(e -> {
      final String symbolName = e.getKey();
      final ASTMCQualifiedName qn = MCQualifiedNameFacade.createQualifiedName(symbolName);

      if (qn.isQualified()) {
        final String packageName = qn.getPartsList().stream().limit(qn.getPartsList().size() - 1).collect(Collectors.joining("."));
        final List<CDPackageSymbol> cdPackageSymbol = cdPackageSymbols.get(packageName);
        final CDTypeSymbol symbol = e.getValue();

        // set the name of the symbol to the simple name
        final String baseName = qn.getBaseName();
        symbol.setName(baseName);
        if (cdPackageSymbol.size() == 1) {
          final ICDBasisScope previousEnclosingScope = symbol.getEnclosingScope();

          // add the symbol in the scope of the CDPackageSymbol
          cdPackageSymbol.get(0).getSpannedScope().add(symbol);
          symbol.getEnclosingScope().getCDTypeSymbols().put(baseName, symbol);

          // remove the symbol from its current scope
          previousEnclosingScope.getCDTypeSymbols().remove(symbolName, symbol);
        }
      }
    });
  }
}
