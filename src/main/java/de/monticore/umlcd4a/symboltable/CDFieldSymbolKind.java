/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.types.JAttributeSymbolKind;

public class CDFieldSymbolKind extends JAttributeSymbolKind {

  private static final String NAME = CDFieldSymbolKind.class.getName();

  protected CDFieldSymbolKind() {
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
