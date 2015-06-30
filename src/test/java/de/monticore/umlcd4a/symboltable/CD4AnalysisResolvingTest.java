/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import de.monticore.symboltable.GlobalScope;
import org.junit.Ignore;
import org.junit.Test;

public class CD4AnalysisResolvingTest {

  @Test
  public void testResolveCD() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    Optional<CDSymbol> cd = globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2", CDSymbol.KIND);
    assertTrue(cd.isPresent());

    assertEquals("de.monticore.umlcd4a.symboltable.CD2", cd.get().getFullName());
  }

  @Test
  public void testResolveType() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    Optional<CDTypeSymbol> cdType = globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2.Person", CDTypeSymbol.KIND);
    assertTrue(cdType.isPresent());

    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", cdType.get().getFullName());
  }

  @Test
  public void testResolveField() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    Optional<CDFieldSymbol> cdField = globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2.Person.name", CDFieldSymbol.KIND);
    assertTrue(cdField.isPresent());

    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person.name", cdField.get().getFullName());
  }

  @Test
  @Ignore
  public void test() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2.XXX", CDTypeSymbol.KIND);
    globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2.XXX", CDTypeSymbol.KIND);

    assertEquals(1, globalScope.getSubScopes().size());
  }

}
