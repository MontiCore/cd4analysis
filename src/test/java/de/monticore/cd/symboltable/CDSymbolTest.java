/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.symboltable;

import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
