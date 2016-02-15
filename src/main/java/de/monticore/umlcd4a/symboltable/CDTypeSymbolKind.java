/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.types.JTypeSymbolKind;

public class CDTypeSymbolKind extends JTypeSymbolKind {

  private static final String NAME = "de.monticore.umlcd4a.symboltable.CDTypeSymbolKind";

  protected CDTypeSymbolKind() {
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || super.isKindOf(kind);
  }
}
