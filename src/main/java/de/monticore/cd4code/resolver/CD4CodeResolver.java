/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4code.resolver;

import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cd4codebasis._symboltable.ICDMethodSignatureSymbolResolver;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.ICDRoleSymbolResolver;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDTypeSymbolResolver;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.symboltable.modifiers.AccessModifier;
import java.util.List;
import java.util.function.Predicate;

public class CD4CodeResolver
    implements IDiagramSymbolResolver,
        ICDTypeSymbolResolver,
        IOOTypeSymbolResolver,
        ITypeSymbolResolver,
        ICDRoleSymbolResolver,
        IFieldSymbolResolver,
        IVariableSymbolResolver,
        ICDMethodSignatureSymbolResolver,
        IMethodSymbolResolver,
        IFunctionSymbolResolver {
  protected final ICD4CodeGlobalScope cdGlobalScope;

  public CD4CodeResolver(ICD4CodeGlobalScope cdGlobalScope) {
    this.cdGlobalScope = cdGlobalScope;
  }

  @Override
  public List<DiagramSymbol> resolveAdaptedDiagramSymbol(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<DiagramSymbol> predicate) {
    return cdGlobalScope.resolveDiagramMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<CDTypeSymbol> resolveAdaptedCDTypeSymbol(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<CDTypeSymbol> predicate) {
    return cdGlobalScope.resolveCDTypeMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<OOTypeSymbol> resolveAdaptedOOTypeSymbol(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<OOTypeSymbol> predicate) {
    return cdGlobalScope.resolveOOTypeMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<TypeSymbol> resolveAdaptedTypeSymbol(
      boolean foundSymbols, String name, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    return cdGlobalScope.resolveTypeMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<CDRoleSymbol> resolveAdaptedCDRoleSymbol(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<CDRoleSymbol> predicate) {
    return cdGlobalScope.resolveCDRoleMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<FieldSymbol> resolveAdaptedFieldSymbol(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<FieldSymbol> predicate) {
    return cdGlobalScope.resolveFieldMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<VariableSymbol> resolveAdaptedVariableSymbol(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<VariableSymbol> predicate) {
    return cdGlobalScope.resolveVariableMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<CDMethodSignatureSymbol> resolveAdaptedCDMethodSignatureSymbol(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<CDMethodSignatureSymbol> predicate) {
    return cdGlobalScope.resolveCDMethodSignatureMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<MethodSymbol> resolveAdaptedMethodSymbol(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<MethodSymbol> predicate) {
    return cdGlobalScope.resolveMethodMany(foundSymbols, name, modifier, predicate);
  }

  @Override
  public List<FunctionSymbol> resolveAdaptedFunctionSymbol(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<FunctionSymbol> predicate) {
    return cdGlobalScope.resolveFunctionMany(foundSymbols, name, modifier, predicate);
  }
}
