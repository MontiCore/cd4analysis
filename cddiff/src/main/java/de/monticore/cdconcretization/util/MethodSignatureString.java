package de.monticore.cdconcretization.util;

import de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MethodSignatureString {

  private MethodSignatureString() {}

  public static Optional<MethodSymbol> resolveMethodSignature(
      IOOSymbolsScope scope, String signatureString) {
    if (signatureString.contains("(")) {
      return resolveMethodSignatureWithArgs(scope, signatureString);
    } else {
      return resolvePlainMethodSymbol(scope, signatureString);
    }
  }

  private static Optional<MethodSymbol> resolveMethodSignatureWithArgs(
      IOOSymbolsScope scope, String signatureString) {
    String methodName = signatureString.substring(0, signatureString.indexOf("("));
    if (signatureString.charAt(signatureString.length() - 1) != ')') {
      Log.error(
          "Method signature: '" + signatureString + "' is not valid. Missing closing parenthesis.");
      return Optional.empty();
    }
    String argsString =
        signatureString.substring(signatureString.indexOf("(") + 1, signatureString.length() - 1);
    List<String> argTypes =
        argsString.isBlank()
            ? List.of()
            : List.of(argsString.split(",")).stream()
                .map(String::trim)
                .collect(Collectors.toList());
    List<Optional<TypeSymbol>> optTypeSymbols =
        argTypes.stream()
            .map(
                typeName -> {
                  Optional<TypeSymbol> symbolOpt = scope.resolveType(typeName);
                  if (symbolOpt.isEmpty()) {
                    Log.error(
                        "Type symbol: '" + typeName + "' is not found in scope " + scope.getName());
                  }
                  return symbolOpt;
                })
            .collect(Collectors.toList());
    if (optTypeSymbols.stream().anyMatch(Optional::isEmpty)) {
      return Optional.empty();
    }
    List<TypeSymbol> typeSymbols =
        optTypeSymbols.stream().map(Optional::get).collect(Collectors.toList());

    List<MethodSymbol> matchingSymbols =
        scope.resolveMethodMany(methodName).stream()
            .filter(method -> isParameterSignatureMatching(method.getParameterList(), typeSymbols))
            .collect(Collectors.toList());
    if (matchingSymbols.size() <= 1) {
      return matchingSymbols.stream().findFirst();
    } else {
      Log.error(
          "Method signature: '" + signatureString + "' is not unique in scope " + scope.getName());
      return Optional.empty();
    }
  }

  private static boolean isParameterSignatureMatching(
      List<VariableSymbol> parameterSymbols, List<TypeSymbol> signatureTypeSymbols) {
    if (parameterSymbols.size() != signatureTypeSymbols.size()) {
      return false;
    }
    for (int i = 0; i < parameterSymbols.size(); i++) {
      if (!parameterSymbols.get(i).getType().getTypeInfo().equals(signatureTypeSymbols.get(i))) {
        return false;
      }
    }
    return true;
  }

  private static Optional<MethodSymbol> resolvePlainMethodSymbol(
      IOOSymbolsScope scope, String symbol) {
    List<MethodSymbol> matchingSymbols = scope.resolveMethodMany(symbol);
    if (matchingSymbols.size() <= 1) {
      return matchingSymbols.stream().findFirst();
    } else {
      Log.error("Method symbol: '" + symbol + "' is not unique in scope " + scope.getName());
      return Optional.empty();
    }
  }

  /**
   * Prints the full signature of a method symbol, if the method is overloaded. Otherwise, it only
   * returns the full name of the method symbol.
   *
   * @param methodSymbol the method symbol to print
   * @return the full signature of the method symbol, or its name if it is not overloaded
   */
  public static String printSignatureIfOverloaded(CDMethodSignatureSymbol methodSymbol) {
    int methodsWithSameName =
        methodSymbol
            .getEnclosingScope()
            .resolveCDMethodSignatureMany(methodSymbol.getFullName())
            .size();
    if (methodsWithSameName > 1) {
      return printSignature(methodSymbol);
    } else {
      return methodSymbol.getFullName();
    }
  }

  public static String printSignature(CDMethodSignatureSymbol methodSymbol) {
    StringBuilder builder = new StringBuilder();
    builder.append(methodSymbol.getFullName());
    builder.append("(");
    for (int i = 0; i < methodSymbol.getParameterList().size(); i++) {
      VariableSymbol param = methodSymbol.getParameterList().get(i);
      builder.append(param.getType().print());
      if (i < methodSymbol.getParameterList().size() - 1) {
        builder.append(",");
      }
    }
    builder.append(")");
    return builder.toString();
  }
}
