/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import de.monticore.symboltable.CommonScopeSpanningSymbol;

/**
 * Top level symbol for the class diagram.
 *
 * @author Robert Heim
 */
public class CDSymbol extends CommonScopeSpanningSymbol {
  
  public static final CDSymbolKind KIND = new CDSymbolKind();
  
  // qualified names
  private List<String> importedCdNames = new ArrayList<>();
  
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
  
  /**
   * Add the qualified name of an imported cd.
   * 
   * @param cd the qualified name of the imported cd.
   */
  public void addImport(String cd) {
    importedCdNames.add(cd);
  }
  
  /**
   * Gets the qualified imports in order of appearance in the model.
   * 
   * @return the imports in order of appearance in the model.
   */
  public List<String> getImports() {
    return ImmutableList.copyOf(importedCdNames);
    
  }
}
