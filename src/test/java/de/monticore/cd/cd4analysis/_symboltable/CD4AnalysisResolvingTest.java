/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._symboltable;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class CD4AnalysisResolvingTest {

  @Test
  public void testResolveCD() {
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    Optional<CDDefinitionSymbol> cd = globalScope.resolveCDDefinition("de.monticore.umlcd4a.symboltable.CD2");
    assertTrue(cd.isPresent());

    assertEquals("de.monticore.umlcd4a.symboltable.CD2", cd.get().getFullName());
  }

  @Test
  public void testResolveType() {
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    Optional<CDTypeSymbol> cdType = globalScope.resolveCDType("de.monticore.umlcd4a.symboltable.CD2.Person");
    assertTrue(cdType.isPresent());

    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", cdType.get().getFullName());
  }

  @Test
  public void testResolveField() {
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    Optional<CDFieldSymbol> cdField = globalScope.resolveCDField("de.monticore.umlcd4a.symboltable.CD2.Person.name");
    assertTrue(cdField.isPresent());

    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person.name", cdField.get().getFullName());
  }

  @Test
  public void testModelIsLoadedOnlyOnceEvenIfSymbolCannontBeResolved() {
    final CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    globalScope.resolveCDType("de.monticore.umlcd4a.symboltable.CD2.XXX");
    globalScope.resolveCDType("de.monticore.umlcd4a.symboltable.CD2.XXX");

    assertEquals(1, globalScope.getSubScopes().size());
  }

  @Test
  public void testCDWithoutPackage() {
    final CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    final CDDefinitionSymbol cdSymbol = globalScope.<CDDefinitionSymbol>resolveCDDefinition("CDWithoutPackage").orElse(null);
    assertNotNull(cdSymbol);
    assertEquals("CDWithoutPackage", cdSymbol.getName());
    assertEquals("CDWithoutPackage", cdSymbol.getFullName());

    final CDTypeSymbol a = globalScope.<CDTypeSymbol>resolveCDType("CDWithoutPackage.A").orElse(null);
    assertNotNull(a);
    assertEquals("A", a.getName());
    assertEquals("CDWithoutPackage.A", a.getFullName());
    assertEquals("CDWithoutPackage.B", a.getSuperClass().get().getFullName());
  }

}
