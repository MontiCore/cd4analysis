/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.symboltable;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.types.JTypeSymbolKind;

public class CDTypeSymbolKind extends JTypeSymbolKind {

  private static final String NAME = CDTypeSymbolKind.class.getName();

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
