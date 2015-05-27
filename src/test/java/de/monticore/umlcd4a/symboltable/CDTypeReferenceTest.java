/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import de.monticore.symboltable.GlobalScope;

public class CDTypeReferenceTest {

  @Test
  public void test() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    Optional<CDSymbol> cdSymbol= globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2", CDSymbol.KIND);
    assertTrue(cdSymbol.isPresent());
    assertEquals("CD2", cdSymbol.get().getName());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2", cdSymbol.get().getFullName());

    Optional<CDTypeSymbol> cdType = cdSymbol.get().getType("Person");
    assertTrue(cdType.isPresent());

    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", cdType.get().getFullName());

  }

}
