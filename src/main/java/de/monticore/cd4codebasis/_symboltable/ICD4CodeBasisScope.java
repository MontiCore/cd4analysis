package de.monticore.cd4codebasis._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public interface ICD4CodeBasisScope extends ICD4CodeBasisScopeTOP {
  @Override
  default List<MethodSymbol> resolveMethodLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<MethodSymbol> predicate) {
    final List<MethodSymbol> methodSymbols = ((IOOSymbolsScope) this)
        .resolveMethodLocallyMany(foundSymbols, name, modifier, predicate);
    methodSymbols.addAll(resolveCDMethodSignatureLocallyMany(foundSymbols, name, modifier, predicate::test));
    return methodSymbols;
  }

  @Override
  default List<FunctionSymbol> resolveFunctionLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<FunctionSymbol> predicate) {
    final List<FunctionSymbol> methodSymbols = ICD4CodeBasisScopeTOP.super
        .resolveFunctionLocallyMany(foundSymbols, name, modifier, predicate);
    methodSymbols.addAll(resolveMethodLocallyMany(foundSymbols, name, modifier, predicate::test));
    return methodSymbols;
  }
}
