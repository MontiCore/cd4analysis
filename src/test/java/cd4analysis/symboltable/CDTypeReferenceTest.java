/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import de.monticore.symboltable.GlobalScope;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CDTypeReferenceTest {

  @Test
  public void test() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbol cdType = (CDTypeSymbol) globalScope.resolve("cd4analysis.symboltable.CD2.Person", CDTypeSymbol
        .KIND).orNull();

    assertNotNull(cdType);
    assertEquals("cd4analysis.symboltable.CD2.Person", cdType.getFullName());

  }

}
