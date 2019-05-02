/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.symboltable;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.types.JMethodSymbolKind;

public class CDMethodSymbolKind extends JMethodSymbolKind {

  private static final String NAME = CDMethodSymbolKind.class.getName();

  protected CDMethodSymbolKind(){}

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || super.isKindOf(kind);
  }
}
