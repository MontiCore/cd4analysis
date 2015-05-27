/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import java.util.Collection;
import java.util.Optional;

import de.monticore.symboltable.CommonScopeSpanningSymbol;

/**
 * Top level symbol for the class diagram.
 *
 * @author Robert Heim
 */
public class CDSymbol extends CommonScopeSpanningSymbol {
  
  public static final CDSymbolKind KIND = new CDSymbolKind();
  
  /**
   * Constructor for de.monticore.umlcd4a.symboltable.CDSymbol
   * 
   * @param name
   * @param kind
   */
  public CDSymbol(String name) {
    super(name, KIND);
  }
  
  /**
   * @return types
   */
  public Collection<CDTypeSymbol> getTypes() {
    return spannedScope.resolveLocally(CDTypeSymbol.KIND);
  }

  public Optional<CDTypeSymbol> getType(String name) {
    return spannedScope.resolveLocally(name, CDTypeSymbol.KIND);
  }
}
