/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import de.monticore.symboltable.ScopeManipulationApi;
import de.monticore.symboltable.resolving.DefaultResolvingFilter;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

public class CDTypeTest {

  @Test
  public void testGetFields() {
    CDTypeSymbol dummyType = new CDTypeSymbol("Dummy");

    CDAttributeSymbol fieldSymbol1 = new CDAttributeSymbol("field1", dummyType);
    CDAttributeSymbol fieldSymbol2 = new CDAttributeSymbol("field2", dummyType);
    CDAttributeSymbol fieldSymbol3 = new CDAttributeSymbol("field3", dummyType);

    CDTypeSymbol typeSymbol = new CDTypeSymbol("TypeFoo");
    typeSymbol.addField(fieldSymbol1);
    typeSymbol.addField(fieldSymbol2);
    typeSymbol.addField(fieldSymbol3);

    ScopeManipulationApi typeScope = (ScopeManipulationApi) typeSymbol.getSpannedScope();
    typeScope.setResolvingFilters(Arrays.asList(DefaultResolvingFilter.create
        (CDAttributeSymbol.class,
            CDAttributeSymbol.KIND)));


    // Test CDTypeSymbol methods //

    assertEquals(3, typeSymbol.getAttribute().size());
    assertSame(fieldSymbol1, typeSymbol.getAttribute().get(0));
    assertSame(fieldSymbol2, typeSymbol.getAttribute().get(1));
    assertSame(fieldSymbol3, typeSymbol.getAttribute().get(2));

    assertSame(fieldSymbol1, typeSymbol.getField("field1").orNull());
    assertSame(fieldSymbol2, typeSymbol.getField("field2").orNull());
    assertSame(fieldSymbol3, typeSymbol.getField("field3").orNull());


    // Test CDTypeScope methods //

    assertEquals(3, typeScope.getSymbols().size());
    assertSame(fieldSymbol1, typeScope.resolve("field1", CDAttributeSymbol.KIND).orNull());
    assertSame(fieldSymbol2, typeScope.resolve("field2", CDAttributeSymbol.KIND).orNull());
    assertSame(fieldSymbol3, typeScope.resolve("field3", CDAttributeSymbol.KIND).orNull());
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

    ScopeManipulationApi typeScope = (ScopeManipulationApi) typeSymbol.getSpannedScope();
    typeScope.setResolvingFilters(Arrays.asList(DefaultResolvingFilter.create(CDMethodSymbol
            .class,
        CDMethodSymbol.KIND)));

    // Test CDTypeSymbol methods //
    assertEquals(2, typeSymbol.getMethods().size());
    assertSame(methodSymbol1, typeSymbol.getMethods().get(0));
    assertSame(methodSymbol2, typeSymbol.getMethods().get(1));

    assertSame(methodSymbol1, typeSymbol.getMethod("method1").orNull());
    assertSame(methodSymbol2, typeSymbol.getMethod("method2").orNull());
    // Only methods are found
    assertFalse(typeSymbol.getMethod("constructor").isPresent());

    // Test CDTypeScope methods //
    assertEquals(3, typeScope.getSymbols().size());
    assertSame(methodSymbol1, typeScope.resolve("method1", CDMethodSymbol.KIND).orNull());
    assertSame(methodSymbol2, typeScope.resolve("method2", CDMethodSymbol.KIND).orNull());
    assertSame(constructor, typeScope.resolve("constructor", CDMethodSymbol.KIND).orNull());
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

    ScopeManipulationApi typeScope = (ScopeManipulationApi) typeSymbol.getSpannedScope();
    typeScope.setResolvingFilters(Arrays.asList(DefaultResolvingFilter.create(CDMethodSymbol
            .class,
        CDMethodSymbol.KIND)));

    // Test CDTypeSymbol methods //
    assertEquals(2, typeSymbol.getConstructors().size());
    assertSame(constructorSymbol1, typeSymbol.getConstructors().get(0));
    assertSame(constructorSymbol2, typeSymbol.getConstructors().get(1));

    // Test CDTypeScope methods //
    assertEquals(3, typeScope.getSymbols().size());
    assertSame(constructorSymbol1, typeScope.resolve("constructor1", CDMethodSymbol.KIND).orNull());
    assertSame(constructorSymbol2, typeScope.resolve("constructor2", CDMethodSymbol.KIND).orNull());
    assertSame(method, typeScope.resolve("method", CDMethodSymbol.KIND).orNull());

  }

  }
