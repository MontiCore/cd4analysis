/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import com.google.common.collect.FluentIterable;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ICDBasisScope extends ICDBasisScopeTOP {
  @Override
  default <T extends ISymbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
    // remove duplicates (same object)
    final Collection<T> deduplicatedResolved = new HashSet<>(resolved);
    if (deduplicatedResolved.size() == 1) {
      return Optional.of(deduplicatedResolved.iterator().next());
    }
    else if (deduplicatedResolved.size() > 1) {
      throw new ResolvedSeveralEntriesForSymbolException("0xA4095:Found " + deduplicatedResolved.size()
          + " symbols: {" + deduplicatedResolved.stream().map(r -> r.getFullName() + (r.isPresentAstNode() ? " (" + r.getAstNode().get_SourcePositionStart() + ")" : "")).collect(Collectors.joining(", ")) + "}",
          resolved);
    }

    return Optional.empty();
  }

  default String getPackageName() {
    return this.isPresentName() ? this.getName() : "";
  }

  default String getRealPackageName() {
    return this.getPackageName();
  }

  default String getRemainingNameForResolveDown(String symbolName) {
    final FluentIterable<String> nameParts = getNameParts(symbolName);
    final FluentIterable<String> packageNameParts = getNameParts(getPackageName());

    if (nameParts.size() >= packageNameParts.size()) {
      final String firstNNameParts = nameParts.stream().limit(packageNameParts.size()).collect(Collectors.joining("."));
      // A scope that exports symbols usually has a name.
      if (firstNNameParts.equals(getPackageName())) {
        return nameParts.stream().skip(packageNameParts.size()).collect(Collectors.joining("."));
      }
    }

    return symbolName;
  }

  default boolean checkIfContinueAsSubScope(String symbolName) {
    // always try to continue, because the subscope could contain the packages
    // the packageName in the artifact scope is not considered in any way
    return this.isExportingSymbols();
  }

  @Override
  default List<OOTypeSymbol> resolveOOTypeLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<OOTypeSymbol> predicate) {
    final List<OOTypeSymbol> ooTypeSymbols = ICDBasisScopeTOP.super
        .resolveOOTypeLocallyMany(foundSymbols, name, modifier, predicate);
    ooTypeSymbols.addAll(resolveCDTypeLocallyMany(foundSymbols, name, modifier, predicate::test));
    return ooTypeSymbols;
  }

  @Override
  default List<TypeSymbol> resolveTypeLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    final List<TypeSymbol> typeSymbols = ICDBasisScopeTOP.super
        .resolveTypeLocallyMany(foundSymbols, name, modifier, predicate);
    typeSymbols.addAll(resolveOOTypeLocallyMany(foundSymbols, name, modifier, predicate::test));
    return typeSymbols;
  }

  @Override
  default List<VariableSymbol> resolveVariableLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate) {
    final List<VariableSymbol> variableSymbols = ICDBasisScopeTOP.super
        .resolveVariableLocallyMany(foundSymbols, name, modifier, predicate);
    variableSymbols.addAll(resolveFieldLocallyMany(foundSymbols, name, modifier, predicate::test));
    return variableSymbols;
  }
}
