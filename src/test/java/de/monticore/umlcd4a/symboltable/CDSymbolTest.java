/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.monticore.symboltable.GlobalScope;

public class CDSymbolTest {
  
  @Test
  public void testResolveCD() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    final CDSymbol cd = globalScope.<CDSymbol> resolve("de.monticore.umlcd4a.symboltable.CD1",
        CDSymbol.KIND).orElse(null);
    assertNotNull(cd);
    assertEquals(6, cd.getTypes().size());
    assertEquals(1, cd.getImports().size());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2", cd.getImports().get(0));
  }
  
  @Test
  public void testResolveAutomaton() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    final CDSymbol cd = globalScope.<CDSymbol> resolve("de.monticore.umlcd4a.symboltable.Automaton",
        CDSymbol.KIND).orElse(null);
    assertNotNull(cd);
  }
  
  @Test
  public void testResolveFeatureModel() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    final CDSymbol cd = globalScope.<CDSymbol> resolve("de.monticore.umlcd4a.symboltable.FeatureModel",
        CDSymbol.KIND).orElse(null);
    assertNotNull(cd);
  }
  
}
