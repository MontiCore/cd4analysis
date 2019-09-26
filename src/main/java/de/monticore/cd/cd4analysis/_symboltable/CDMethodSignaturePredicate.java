/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4analysis._symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;

public class CDMethodSignaturePredicate implements Predicate<CDMethOrConstrSymbol> {

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
  public boolean test(final CDMethOrConstrSymbol symbol) {
    if ((symbol != null) &&
        (symbol instanceof CDMethOrConstrSymbol)) {
      final CDMethOrConstrSymbol methodSymbol = (CDMethOrConstrSymbol) symbol;

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
