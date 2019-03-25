/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import org.junit.Test;

public class CDTypeReferenceTest {

  @Test
  public void testQualifiedTypeReferenceFromWithinGlobalScope() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbolReference ref = new CDTypeSymbolReference("de.monticore.umlcd4a.symboltable.CD2.Person", globalScope);

    assertTrue(ref.existsReferencedSymbol());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", ref.getFullName());
    assertEquals("Person", ref.getName());
  }

  @Test
  public void testQualifiedTypeReferenceFromWithinAnCD() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDSymbol cd = new CDSymbol("CD");
    globalScope.add(cd);

    Scope cdScope = (Scope) cd.getSpannedScope();
    cdScope.setResolvingFilters(globalScope.getResolvingFilters());

    CDTypeSymbol type = new CDTypeSymbol("Person");
    cdScope.add(type);

    CDTypeSymbolReference ref = new CDTypeSymbolReference("de.monticore.umlcd4a.symboltable.CD2.Person", cd.getSpannedScope());

    assertTrue(ref.existsReferencedSymbol());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", ref.getFullName());
    assertEquals("Person", ref.getName());
  }

}
