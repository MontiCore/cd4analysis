/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._symboltable;

import de.monticore.cd.cd4analysis._symboltable.references.CDTypeSymbolReference;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDTypeReferenceTest {

  @Test
  public void testQualifiedTypeReferenceFromWithinGlobalScope() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    de.monticore.cd.cd4analysis._symboltable.references.CDTypeSymbolReference ref = new de.monticore.cd.cd4analysis._symboltable.references.CDTypeSymbolReference("de.monticore.umlcd4a.symboltable.CD2.Person", globalScope);

    assertTrue(ref.existsReferencedSymbol());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", ref.getFullName());
    assertEquals("Person", ref.getName());
  }

  @Test
  public void testQualifiedTypeReferenceFromWithinAnCD() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDDefinitionSymbol cd = new CDDefinitionSymbol("CD");
    globalScope.add(cd);

    Scope cdScope = (Scope) cd.getSpannedScope();
    cdScope.setResolvingFilters(globalScope.getResolvingFilters());

    CDTypeSymbol type = new CDTypeSymbol("Person");
    cdScope.add(type);

    de.monticore.cd.cd4analysis._symboltable.references.CDTypeSymbolReference ref = new CDTypeSymbolReference("de.monticore.umlcd4a.symboltable.CD2.Person", cd.getSpannedScope());

    assertTrue(ref.existsReferencedSymbol());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", ref.getFullName());
    assertEquals("Person", ref.getName());
  }

}
