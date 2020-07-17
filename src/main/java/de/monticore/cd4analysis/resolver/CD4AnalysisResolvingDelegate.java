/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis.resolver;

import com.google.common.collect.Lists;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.ICDRoleSymbolResolvingDelegate;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDTypeSymbolResolvingDelegate;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.basictypesymbols._symboltable.ITypeSymbolResolvingDelegate;
import de.monticore.types.basictypesymbols._symboltable.IVariableSymbolResolvingDelegate;
import de.monticore.types.basictypesymbols._symboltable.TypeSymbol;
import de.monticore.types.basictypesymbols._symboltable.VariableSymbol;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.IFieldSymbolResolvingDelegate;
import de.monticore.types.typesymbols._symboltable.IOOTypeSymbolResolvingDelegate;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CD4AnalysisResolvingDelegate
    implements ICDTypeSymbolResolvingDelegate, IOOTypeSymbolResolvingDelegate, ITypeSymbolResolvingDelegate,
    ICDRoleSymbolResolvingDelegate, IFieldSymbolResolvingDelegate, IVariableSymbolResolvingDelegate {
  protected CD4AnalysisGlobalScope cdGlobalScope;

  public CD4AnalysisResolvingDelegate(CD4AnalysisGlobalScope cdGlobalScope) {
    this.cdGlobalScope = cdGlobalScope;
  }

  @Override
  public List<CDTypeSymbol> resolveAdaptedCDTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<CDTypeSymbol> predicate) {
    return cdGlobalScope.resolveCDTypeMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<OOTypeSymbol> resolveAdaptedOOTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<OOTypeSymbol> predicate) {
    List<OOTypeSymbol> result = Lists.newArrayList(resolveAdaptedCDTypeSymbol(foundSymbols, name, modifier, predicate::test));
    result.addAll(cdGlobalScope.resolveOOTypeMany(foundSymbols, name, modifier, predicate));
    return result;
  }

  @Override
  public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    List<TypeSymbol> result = Lists.newArrayList(resolveAdaptedOOTypeSymbol(foundSymbols, name, modifier, predicate::test));
    result.addAll(cdGlobalScope.resolveTypeMany(foundSymbols, name, modifier, predicate));
    return result;
  }

  @Override
  public List<CDRoleSymbol> resolveAdaptedCDRoleSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<CDRoleSymbol> predicate) {
    return cdGlobalScope.resolveCDRoleMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<FieldSymbol> resolveAdaptedFieldSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    final ArrayList<FieldSymbol> result = Lists.newArrayList(resolveAdaptedCDRoleSymbol(foundSymbols, name, modifier, predicate::test));
    result.addAll(cdGlobalScope.resolveFieldMany(foundSymbols, name, modifier, predicate));
    return result;
  }

  @Override
  public List<VariableSymbol> resolveAdaptedVariableSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate) {
    final ArrayList<VariableSymbol> result = Lists.newArrayList(resolveAdaptedFieldSymbol(foundSymbols, name, modifier, predicate::test));
    result.addAll(cdGlobalScope.resolveVariableMany(foundSymbols, name, modifier, predicate));
    return result;
  }
}
