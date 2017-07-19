/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import de.monticore.symboltable.SymbolKind;

public class CDSymbolKind implements SymbolKind {

  private static final String NAME = CDSymbol.class.getName();

  protected CDSymbolKind() {
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }
}
