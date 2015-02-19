/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CDTypeReferenceTest {

  @Test
  public void test() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbol cdType = (CDTypeSymbol) globalScope.resolve("de.monticore.umlcd4a.symboltable.CD2.Person", CDTypeSymbol
        .KIND).orNull();

    assertNotNull(cdType);
    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", cdType.getFullName());

  }

}
