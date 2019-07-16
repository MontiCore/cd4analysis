/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._symboltable;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Top level symbol for the class diagram.
 *
 * @author Robert Heim
 */
public class CDDefinitionSymbol extends CDDefinitionSymbolTOP {

  // qualified names
  private List<String> importedCdNames = new ArrayList<>();

  /**
   * Constructor for de.monticore.umlcd4a.symboltable.CDSymbol
   *
   * @param name
   */
  public CDDefinitionSymbol(String name) {
    super(name);
  }

  public Collection<CDTypeSymbol> getTypes() {
    return getSpannedScope().getLocalCDTypeSymbols();
  }

  public Collection<CDAssociationSymbol> getAssociations() {
    return getSpannedScope().getLocalCDAssociationSymbols();
  }

  public Optional<CDTypeSymbol> getType(String name) {
    return getSpannedScope().resolveCDTypeLocally(name);
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

  @Override
  public String toString() {
    return getName();
  }

}
