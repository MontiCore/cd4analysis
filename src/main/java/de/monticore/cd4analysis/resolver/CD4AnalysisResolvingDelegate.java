/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.resolver;

import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.ICDRoleSymbolResolvingDelegate;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDTypeSymbolResolvingDelegate;
import de.monticore.symbols.basicsymbols._symboltable.ITypeSymbolResolvingDelegate;
import de.monticore.symbols.basicsymbols._symboltable.IVariableSymbolResolvingDelegate;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IFieldSymbolResolvingDelegate;
import de.monticore.symbols.oosymbols._symboltable.IOOTypeSymbolResolvingDelegate;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.function.Predicate;

public class CD4AnalysisResolvingDelegate
    implements ICDTypeSymbolResolvingDelegate, IOOTypeSymbolResolvingDelegate, ITypeSymbolResolvingDelegate,
    ICDRoleSymbolResolvingDelegate, IFieldSymbolResolvingDelegate, IVariableSymbolResolvingDelegate {
  protected final CD4AnalysisGlobalScope cdGlobalScope;

  public CD4AnalysisResolvingDelegate(CD4AnalysisGlobalScope cdGlobalScope) {
    this.cdGlobalScope = cdGlobalScope;
  }

  @Override
  public List<CDTypeSymbol> resolveAdaptedCDTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<CDTypeSymbol> predicate) {
    return cdGlobalScope.resolveCDTypeMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<OOTypeSymbol> resolveAdaptedOOTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<OOTypeSymbol> predicate) {
    return cdGlobalScope.resolveOOTypeMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    return cdGlobalScope.resolveTypeMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<CDRoleSymbol> resolveAdaptedCDRoleSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<CDRoleSymbol> predicate) {
    return cdGlobalScope.resolveCDRoleMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<FieldSymbol> resolveAdaptedFieldSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    return cdGlobalScope.resolveFieldMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<VariableSymbol> resolveAdaptedVariableSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate) {
    return cdGlobalScope.resolveVariableMany(foundSymbols, name, modifier, predicate);
  }
}
