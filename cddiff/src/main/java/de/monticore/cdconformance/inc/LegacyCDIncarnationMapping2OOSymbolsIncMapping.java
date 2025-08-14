/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttributeTOP;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdconcretization.util.SymbolUtil;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols.refmodel.IOOSymbolsBindings;
import de.monticore.symbols.oosymbols.refmodel.IOOSymbolsIncMapping;
import de.monticore.symbols.oosymbols.refmodel.IOOSymbolsLocalIncMapping;
import de.monticore.symbols.oosymbols.refmodel.OOSymbolsRestrictedIncMapping;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// TODO CDIncarnationMapping in cd4a should be a supertype of OOSymbolsIncMapping
@Deprecated
public class LegacyCDIncarnationMapping2OOSymbolsIncMapping implements IOOSymbolsIncMapping {
  
  private CDIncarnationMapping cdIncarnationMapping;
  private ASTCDCompilationUnit concreteCD;
  private ASTCDCompilationUnit referenceCD;
  
  public LegacyCDIncarnationMapping2OOSymbolsIncMapping(CDIncarnationMapping cdIncarnationMapping,
      ASTCDCompilationUnit concreteCD, ASTCDCompilationUnit referenceCD) {
    this.cdIncarnationMapping = cdIncarnationMapping;
    this.concreteCD = concreteCD;
    this.referenceCD = referenceCD;
  }
  
  @Override
  public IOOSymbolsScope getReferenceScope() {
    // TODO Once a incarnation mapping can related multiple concrete and reference artifacts,
    // this needs to be adapted.
    return referenceCD.getEnclosingScope();
  }
  
  @Override
  public IOOSymbolsScope getConcreteScope() {
    // TODO Once a incarnation mapping can related multiple concrete and reference artifacts,
    // this needs to be adapted.
    return concreteCD.getEnclosingScope();
  }
  
  @Override
  public String computeSymbolKey(ISymbol symbol) {
    return cdIncarnationMapping.computeSymbolKey(symbol);
  }
  
  @Override
  public IOOSymbolsLocalIncMapping getScopedMapping(ISymbol contextSymbol) {
    return new OOSymbolsRestrictedIncMapping(this, getScopedBindings(contextSymbol));
  }
  
  @Override
  public IOOSymbolsLocalIncMapping getScopedMapping(IScope scope) {
    return new OOSymbolsRestrictedIncMapping(this, getScopedBindings(scope));
  }
  
  @Override
  public IOOSymbolsBindings getScopedBindings(String contextSymbolKey) {
    return new LegacyCDIncarnationBindings2OOSymbolsBindings(cdIncarnationMapping, null, null,
        contextSymbolKey);
  }
  
  @Override
  public IOOSymbolsBindings getScopedBindings(ISymbol contextSymbol) {
    return new LegacyCDIncarnationBindings2OOSymbolsBindings(cdIncarnationMapping, contextSymbol,
        null, contextSymbol.getFullName());
  }
  
  @Override
  public IOOSymbolsBindings getScopedBindings(IScope scope) {
    return new LegacyCDIncarnationBindings2OOSymbolsBindings(cdIncarnationMapping, null, scope,
        null);
  }
  
  @Override
  public Set<TypeSymbol> getIncarnations(TypeSymbol typeSymbol) {
    // TODO solve this in another way
    Optional<TypeSymbol> cd4cSymbol = getReferenceScope().resolveTypeDown(SymbolUtil
        .getFullNameWithoutCD(typeSymbol));
    // TODO move to CDIncarnationMapping
    if (cd4cSymbol.isEmpty() || !cd4cSymbol.get().isPresentAstNode()) {
      return Collections.emptySet();
    }
    return cdIncarnationMapping.getIncarnations(cd4cSymbol.get());
  }
  
  @Override
  public Set<VariableSymbol> getIncarnations(VariableSymbol variableSymbol) {
    if (variableSymbol instanceof FieldSymbol) {
      return new HashSet<>(getIncarnations((FieldSymbol) variableSymbol));
    }
    else {
      return Collections.emptySet();
    }
  }
  
  @Override
  public Set<FunctionSymbol> getIncarnations(FunctionSymbol functionSymbol) {
    if (functionSymbol instanceof MethodSymbol) {
      return new HashSet<>(getIncarnations((MethodSymbol) functionSymbol));
    }
    else {
      return Collections.emptySet();
    }
  }
  
  @Override
  public Set<OOTypeSymbol> getIncarnations(OOTypeSymbol typeSymbol) {
    Set<TypeSymbol> typeSymbols = getIncarnations((TypeSymbol) typeSymbol);
    return typeSymbols.stream().filter(ts -> ts instanceof OOTypeSymbol).map(
        ts -> (OOTypeSymbol) ts).collect(Collectors.toSet());
  }
  
  @Override
  public Set<FieldSymbol> getIncarnations(FieldSymbol fieldSymbol) {
    return cdIncarnationMapping.getIncarnations(SymbolUtil.cdAttributeFromFieldSymbol(fieldSymbol))
        .stream().map(ASTCDAttributeTOP::getSymbol).collect(Collectors.toSet());
  }
  
  @Override
  public Set<MethodSymbol> getIncarnations(MethodSymbol methodSymbol) {
    return cdIncarnationMapping.getIncarnations(SymbolUtil.cdMethodFromMethodSymbol(methodSymbol))
        .stream().map(ASTCDMethod::getSymbol).collect(Collectors.toSet());
  }
  
}
