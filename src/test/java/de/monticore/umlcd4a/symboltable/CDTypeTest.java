/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collections;

import de.monticore.symboltable.CommonScope;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.resolving.CommonResolvingFilter;

public class CDTypeTest {

  @Test
  public void testGetFields() {
    final CDTypeSymbolReference dummyTypeRef = new CDTypeSymbolReference("Dummy", new CommonScope(false));

    CDFieldSymbol fieldSymbol1 = new CDFieldSymbol("field1", dummyTypeRef);
    CDFieldSymbol fieldSymbol2 = new CDFieldSymbol("field2", dummyTypeRef);
    CDFieldSymbol fieldSymbol3 = new CDFieldSymbol("field3", dummyTypeRef);

    CDTypeSymbol typeSymbol = new CDTypeSymbol("TypeFoo");
    typeSymbol.addField(fieldSymbol1);
    typeSymbol.addField(fieldSymbol2);
    typeSymbol.addField(fieldSymbol3);

    MutableScope typeScope = (MutableScope) typeSymbol.getSpannedScope();
    typeScope.setResolvingFilters(Collections.singletonList(CommonResolvingFilter.create
        (CDFieldSymbol.class, CDFieldSymbol.KIND)));


    // Test CDTypeSymbol methods //

    assertEquals(3, typeSymbol.getFields().size());
    assertSame(fieldSymbol1, typeSymbol.getFields().get(0));
    assertSame(fieldSymbol2, typeSymbol.getFields().get(1));
    assertSame(fieldSymbol3, typeSymbol.getFields().get(2));

    assertSame(fieldSymbol1, typeSymbol.getField("field1").orElse(null));
    assertSame(fieldSymbol2, typeSymbol.getField("field2").orElse(null));
    assertSame(fieldSymbol3, typeSymbol.getField("field3").orElse(null));


    // Test CDTypeScope methods //

    assertEquals(3, typeScope.getSymbolsSize());
    assertSame(fieldSymbol1, typeScope.resolve("field1", CDFieldSymbol.KIND).orElse(null));
    assertSame(fieldSymbol2, typeScope.resolve("field2", CDFieldSymbol.KIND).orElse(null));
    assertSame(fieldSymbol3, typeScope.resolve("field3", CDFieldSymbol.KIND).orElse(null));
  }

  @Test
  public void testGetMethods() {
    CDMethodSymbol methodSymbol1 = new CDMethodSymbol("method1");
    CDMethodSymbol constructor = new CDMethodSymbol("constructor");
    constructor.setConstructor(true);
    CDMethodSymbol methodSymbol2 = new CDMethodSymbol("method2");

    CDTypeSymbol typeSymbol = new CDTypeSymbol("TypeFoo");
    typeSymbol.addMethod(methodSymbol1);
    typeSymbol.addMethod(methodSymbol2);
    typeSymbol.addConstructor(constructor);

    MutableScope typeScope = (MutableScope) typeSymbol.getSpannedScope();
    typeScope.setResolvingFilters(Collections.singletonList(CommonResolvingFilter.create(CDMethodSymbol.class,
        CDMethodSymbol.KIND)));

    // Test CDTypeSymbol methods //
    assertEquals(2, typeSymbol.getMethods().size());
    assertSame(methodSymbol1, typeSymbol.getMethods().get(0));
    assertSame(methodSymbol2, typeSymbol.getMethods().get(1));

    assertSame(methodSymbol1, typeSymbol.getMethod("method1").orElse(null));
    assertSame(methodSymbol2, typeSymbol.getMethod("method2").orElse(null));
    // Only methods are found
    assertFalse(typeSymbol.getMethod("constructor").isPresent());

    // Test CDTypeScope methods //
    assertEquals(3, typeScope.getSymbolsSize());
    assertSame(methodSymbol1, typeScope.resolve("method1", CDMethodSymbol.KIND).orElse(null));
    assertSame(methodSymbol2, typeScope.resolve("method2", CDMethodSymbol.KIND).orElse(null));
    assertSame(constructor, typeScope.resolve("constructor", CDMethodSymbol.KIND).orElse(null));
  }

  @Test
  public void testGetConstructors() {
    CDMethodSymbol constructorSymbol1 = new CDMethodSymbol("constructor1");
    constructorSymbol1.setConstructor(true);
    CDMethodSymbol method = new CDMethodSymbol("method");
    CDMethodSymbol constructorSymbol2 = new CDMethodSymbol("constructor2");
    constructorSymbol2.setConstructor(true);

    CDTypeSymbol typeSymbol = new CDTypeSymbol("TypeFoo");
    typeSymbol.addConstructor(constructorSymbol1);
    typeSymbol.addMethod(method);
    typeSymbol.addConstructor(constructorSymbol2);

    MutableScope typeScope = (MutableScope) typeSymbol.getSpannedScope();
    typeScope.setResolvingFilters(Collections.singletonList(CommonResolvingFilter.create(CDMethodSymbol.class,
        CDMethodSymbol.KIND)));

    // Test CDTypeSymbol methods //
    assertEquals(2, typeSymbol.getConstructors().size());
    assertSame(constructorSymbol1, typeSymbol.getConstructors().get(0));
    assertSame(constructorSymbol2, typeSymbol.getConstructors().get(1));

    // Test CDTypeScope methods //
    assertEquals(3, typeScope.getSymbolsSize());
    assertSame(constructorSymbol1, typeScope.resolve("constructor1", CDMethodSymbol.KIND).orElse(null));
    assertSame(constructorSymbol2, typeScope.resolve("constructor2", CDMethodSymbol.KIND).orElse(null));
    assertSame(method, typeScope.resolve("method", CDMethodSymbol.KIND).orElse(null));

  }

  @Ignore("TODO PN<-RH is it expected to not override attributes of the same name, but have both of them in the visible fields? s. #1768")
  @Test
  public void testOverrideAttribute() {
    // class B overrides attribute "s" of superclass A.
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbol b = (CDTypeSymbol) globalScope.resolve("de.monticore.umlcd4a.symboltable.OverrideAttribute.B", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(b);
    assertEquals("B", b.getName());
    // s is overridden in subclass B
    assertEquals(1, b.getAllVisibleFields().size());
  }
}
