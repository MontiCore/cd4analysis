/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolPredicate;

import static com.google.common.base.Strings.nullToEmpty;

public class CDAssociationNameAndTargetNamePredicate implements SymbolPredicate {

  private final String assocName;
  private final String assocTargetName;

  public CDAssociationNameAndTargetNamePredicate(final String assocName, final String
      assocTargetName) {
    this.assocName = nullToEmpty(assocName);
    this.assocTargetName = nullToEmpty(assocTargetName);
  }

  @Override
  public boolean apply(final Symbol symbol) {
    if ((symbol != null) &&
        symbol.isKindOf(CDAssociationSymbol.KIND) &&
        (symbol instanceof CDAssociationSymbol)) {
      CDAssociationSymbol assocSymbol = (CDAssociationSymbol) symbol;

      return assocSymbol.getAssocName().equals(assocName)
          && assocSymbol.getTargetType().getName().equals(assocTargetName);

    }

    return false;
  }
}
