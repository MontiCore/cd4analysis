/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolPredicate;

public class CDMethodSignaturePredicate implements SymbolPredicate {

  private final String expectedMethodName;
  private final List<String> expectedParameterTypes = new ArrayList<>();

  public CDMethodSignaturePredicate(final String methodName, final String parameterType,
      final String ... furtherParameterTypes) {
    this.expectedMethodName = requireNonNull(emptyToNull(methodName));

    expectedParameterTypes.add(requireNonNull(emptyToNull(parameterType)));

    for (final String furtherType : furtherParameterTypes) {
      expectedParameterTypes.add(requireNonNull(emptyToNull(furtherType)));
    }
  }

  @Override
  public boolean test(final Symbol symbol) {
    if ((symbol != null) &&
        symbol.isKindOf(CDMethodSymbol.KIND) &&
        (symbol instanceof CDMethodSymbol)) {
      final CDMethodSymbol methodSymbol = (CDMethodSymbol) symbol;

      if (methodSymbol.getName().equals(expectedMethodName) &&
          (methodSymbol.getParameters().size() == expectedParameterTypes.size())) {
        for (int i=0; i < methodSymbol.getParameters().size(); i++) {
          final String expectedType = expectedParameterTypes.get(i);
          final String actualType = methodSymbol.getParameters().get(i).getType().getFullName();

          if (!actualType.equals(expectedType)) {
            return false;
          }
        }

        return true;
      }
    }

    return false;
  }
}
