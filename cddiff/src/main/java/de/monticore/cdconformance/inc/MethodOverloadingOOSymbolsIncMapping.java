/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconformance.inc;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.cdconcretization.util.MethodSignatureString;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols.refmodel.IOOSymbolsLocalIncMapping;
import de.monticore.symbols.oosymbols.refmodel.OOSymbolsIncMapping;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.Names;

import java.util.stream.Collectors;

/**
 * Implementation of the {@link OOSymbolsIncMapping} interface, which supports overloaded
 * methods, i.e., we do not use the method symbol name as key in the bindings, but
 * the full signature of the method, which includes the parameter types.
 */
public class MethodOverloadingOOSymbolsIncMapping extends OOSymbolsIncMapping {
  
  public MethodOverloadingOOSymbolsIncMapping(IOOSymbolsLocalIncMapping globalMapping,
      IOOSymbolsScope referenceScope, IOOSymbolsScope concreteScope) {
    super(globalMapping, referenceScope, concreteScope);
  }
  
  /*
   * FUTURE WORK NOTE: If we would generate parts of the conformance check & concretization
   * infrastructure in the future, OOSymbolsIncMapping could be generated and this
   * is the only handwritten adjustment because method symbol names are not unique.
   */
  
  @Override
  public String computeSymbolKey(ISymbol symbol) {
    if (symbol instanceof CDMethodSignatureSymbol) {
      return MethodSignatureString.printSignature((CDMethodSignatureSymbol) symbol);
    }
    return super.computeSymbolKey(symbol);
  }
  
  /**
   * Computes the unique key for a method symbol but without using the symboltable.
   * This is useful for cases where we do not have a symbol table available (yet), e.g., during
   * concretization.
   *
   * @param method the method for which the key is computed
   * @return the unique key for the method symbol
   */
  public static String computeMethodSymbolKey(String qualifier, ASTCDMethod method) {
    String newMethodFullName = Names.getQualifiedName(qualifier, method.getName());
    return MethodSignatureString.printSignature(newMethodFullName, method.getCDParameterList()
        .stream().map(param -> param.getMCType().printType()).collect(Collectors.toList()));
  }
  
}
