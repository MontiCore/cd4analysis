/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import static com.google.common.base.Strings.nullToEmpty;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolPredicate;

@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class CDAssociationNameAndTargetNamePredicate implements SymbolPredicate {

  private final String assocName;
  private final String assocTargetName;

  public CDAssociationNameAndTargetNamePredicate(final String assocName, final String assocTargetName) {
    this.assocName = nullToEmpty(assocName);
    this.assocTargetName = nullToEmpty(assocTargetName);
  }

  @Override
  public boolean test(final Symbol symbol) {
    if ((symbol != null) &&
        symbol.isKindOf(CDAssociationSymbol.KIND) &&
        (symbol instanceof CDAssociationSymbol)) {
      CDAssociationSymbol assocSymbol = (CDAssociationSymbol) symbol;

      if (!assocSymbol.getAssocName().isPresent()) {
        return false;
      }
      return assocSymbol.getAssocName().get().equals(assocName)
          && assocSymbol.getTargetType().getName().equals(assocTargetName);

    }

    return false;
  }
}
