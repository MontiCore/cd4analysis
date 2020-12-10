/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class OOTypeHelper {
  public static Collection<VariableSymbol> getAllVariablesOfSuperTypes(TypeSymbol symbol) {
    final Set<VariableSymbol> allSuperTypeFields = new LinkedHashSet<>();
    final Collection<VariableSymbol> fields = symbol.getSpannedScope().getLocalVariableSymbols();

    for (SymTypeExpression superType : symbol.getSuperClassesOnly()) {
      for (VariableSymbol superField : superType.getTypeInfo().getSpannedScope().getLocalVariableSymbols()) {
        if (fields.stream().noneMatch(cdFieldSymbol -> cdFieldSymbol.getName().equals(superField.getName()))) {
          allSuperTypeFields.add(superField);
        }
      }

      allSuperTypeFields.addAll(getAllVariablesOfSuperTypes(superType.getTypeInfo()));
    }

    return ImmutableSet.copyOf(allSuperTypeFields);
  }
}
