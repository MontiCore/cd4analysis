/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cdbasis._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.OOTypeSymbol;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class CDTypeHelper {
  public static Collection<FieldSymbol> getAllFieldsOfSuperTypes(OOTypeSymbol symbol) {
    final Set<FieldSymbol> allSuperTypeFields = new LinkedHashSet<>();
    final Collection<FieldSymbol> fields = symbol.getSpannedScope().getLocalFieldSymbols();

    for (SymTypeExpression superType : symbol.getSuperClassesOnly()) {
      for (FieldSymbol superField : superType.getTypeInfo().getSpannedScope().getLocalFieldSymbols()) {
        if (fields.stream().noneMatch(cdFieldSymbol -> cdFieldSymbol.getName().equals(superField.getName()))) {
          allSuperTypeFields.add(superField);
        }
      }

      allSuperTypeFields.addAll(getAllFieldsOfSuperTypes(superType.getTypeInfo()));
    }

    return ImmutableSet.copyOf(allSuperTypeFields);
  }
}
