/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import com.google.common.collect.FluentIterable;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ICDBasisScope extends ICDBasisScopeTOP {
  default <T extends ISymbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
    // remove duplicates (same object)
    final Collection<T> deduplicatedResolved = new HashSet<>(resolved);
    if (deduplicatedResolved.size() == 1) {
      return Optional.of(deduplicatedResolved.iterator().next());
    }
    else if (deduplicatedResolved.size() > 1) {
      throw new ResolvedSeveralEntriesForSymbolException("0xA4095 Found " + deduplicatedResolved.size()
          + " symbols: " + deduplicatedResolved.iterator().next().getFullName(),
          resolved);
    }

    return Optional.empty();
  }

  default String getPackageName() {
    return this.isPresentName() ? this.getName() : "";
  }

  default String getRemainingNameForResolveDown(String symbolName) {
    final FluentIterable<String> nameParts = getNameParts(symbolName);
    final FluentIterable<String> packageNameParts = getNameParts(getPackageName());

    if (nameParts.size() >= packageNameParts.size()) {
      final String firstNNameParts = nameParts.stream().limit(packageNameParts.size()).collect(Collectors.joining("."));
      // A scope that exports symbols usually has a name.
      if (firstNNameParts.equals(getPackageName())) {
        return nameParts.stream().skip(packageNameParts.size()).collect(Collectors.joining());
      }
    }

    return symbolName;
  }

  default boolean checkIfContinueAsSubScope(String symbolName) {
    if (this.isExportingSymbols()) {
      final FluentIterable<String> nameParts = getNameParts(symbolName);
      final FluentIterable<String> packageNameParts = getNameParts(getPackageName());

      if (nameParts.size() > 1) {
        // resolve further, as to find the type without package information
        // example "A" should resolve all classes A, no matter the package
        if (!MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameParts.toList()).build().isQualified()) {
          return true;
        }

        if (nameParts.size() >= packageNameParts.size()) {
          final String firstNNameParts = nameParts.stream().limit(packageNameParts.size()).collect(Collectors.joining("."));
          // A scope that exports symbols usually has a name.
          return firstNNameParts.equals(getPackageName());
        }
      }
      else {
        return true;
      }
    }

    return false;
  }
}
