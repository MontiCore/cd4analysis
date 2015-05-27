/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import de.monticore.symboltable.GlobalScope;

public class CDSymbolTest {
  
  @Ignore("#1647")
  @Test
  public void testResolveCD() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    final CDSymbol cd = globalScope.<CDSymbol> resolve("de.monticore.umlcd4a.symboltable.CD1",
        CDSymbol.KIND).orElse(null);
    assertNotNull(cd);
    assertEquals(6, cd.getTypes().size());
  }
  
}
