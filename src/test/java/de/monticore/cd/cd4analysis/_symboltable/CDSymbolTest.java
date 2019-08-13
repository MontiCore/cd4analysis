/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._symboltable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CDSymbolTest {
  
  @Test
  public void testResolveCD() {
    final CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    final CDDefinitionSymbol cd = globalScope.resolveCDDefinition("de.monticore.umlcd4a.symboltable.CD1").orElse(null);
    assertNotNull(cd);
    assertEquals(6, cd.getTypes().size());
    assertEquals(1, cd.getImports().size());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2", cd.getImports().get(0));
  }

  @Test
  public void testResolveAutomaton() {
    final
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    final CDDefinitionSymbol cd = globalScope.resolveCDDefinition("de.monticore.umlcd4a.symboltable.Automaton").orElse(null);
    assertNotNull(cd);
  }

  @Test
  public void testResolveFeatureModel() {
    final CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();
    final CDDefinitionSymbol cd = globalScope.resolveCDDefinition("de.monticore.umlcd4a.symboltable.FeatureModel").orElse(null);
    assertNotNull(cd);
  }
  
}
