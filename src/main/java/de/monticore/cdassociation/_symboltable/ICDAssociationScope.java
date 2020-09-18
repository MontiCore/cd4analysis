package de.monticore.cdassociation._symboltable;

import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public interface ICDAssociationScope extends ICDAssociationScopeTOP {
  @Override
  default List<FieldSymbol> resolveFieldLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    final List<FieldSymbol> variableSymbols = ICDAssociationScopeTOP.super
        .resolveFieldLocallyMany(foundSymbols, name, modifier, predicate);
    variableSymbols.addAll(resolveCDRoleLocallyMany(foundSymbols, name, modifier, predicate::test));
    return variableSymbols;
  }
}
