/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import de.monticore.symboltable.GlobalScope;
import org.junit.Test;

public class CDTypeReferenceTest {

  @Test
  public void test() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    Optional<CDTypeSymbol> cdType = globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2.Person", CDTypeSymbol.KIND);
    assertTrue(cdType.isPresent());

    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", cdType.get().getFullName());

    Optional<CDFieldSymbol> cdField = globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2.Person.name", CDFieldSymbol.KIND);
    assertTrue(cdField.isPresent());

  }

}
