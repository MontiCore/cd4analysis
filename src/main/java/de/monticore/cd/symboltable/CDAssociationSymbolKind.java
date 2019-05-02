/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.symboltable;

import de.monticore.symboltable.SymbolKind;

public class CDAssociationSymbolKind implements SymbolKind {

  private static final String NAME = "de.monticore.umlcd4a.symboltable.CDAssociationSymbolKind";

  protected CDAssociationSymbolKind(){}

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }
}
