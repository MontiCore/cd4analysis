/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._symboltable;

import de.monticore.symboltable.GlobalScope;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class CD4AnalysisResolvingTest {

  @Test
  public void testResolveCD() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    Optional<CDDefinitionSymbol> cd = globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2", CDDefinitionSymbol.KIND);
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
  public void testModelIsLoadedOnlyOnceEvenIfSymbolCannontBeResolved() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2.XXX", CDTypeSymbol.KIND);
    globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2.XXX", CDTypeSymbol.KIND);

    assertEquals(1, globalScope.getSubScopes().size());
  }

  @Test
  public void testCDWithoutPackage() {
    final GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    final CDDefinitionSymbol cdSymbol = globalScope.<CDDefinitionSymbol>resolve("CDWithoutPackage", CDDefinitionSymbol.KIND).orElse(null);
    assertNotNull(cdSymbol);
    assertEquals("CDWithoutPackage", cdSymbol.getName());
    assertEquals("CDWithoutPackage", cdSymbol.getFullName());

    final CDTypeSymbol a = globalScope.<CDTypeSymbol>resolve("CDWithoutPackage.A", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(a);
    assertEquals("A", a.getName());
    assertEquals("CDWithoutPackage.A", a.getFullName());
    assertEquals("CDWithoutPackage.B", a.getSuperClass().get().getFullName());
  }

}
