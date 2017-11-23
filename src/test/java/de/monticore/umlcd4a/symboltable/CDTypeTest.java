/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.umlcd4a.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Collections;

import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import org.junit.Ignore;
import org.junit.Test;

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
    typeScope.setResolvingFilters(Collections.singletonList(CommonResolvingFilter.create(CDFieldSymbol.KIND)));


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
    typeScope.setResolvingFilters(Collections.singletonList(CommonResolvingFilter.create(CDMethodSymbol.KIND)));

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
    typeScope.setResolvingFilters(Collections.singletonList(CommonResolvingFilter.create(CDMethodSymbol.KIND)));

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

  @Test
  public void testOverrideAttribute() {
    // class B overrides attribute "s" of superclass A.
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbol b = (CDTypeSymbol) globalScope.resolve("de.monticore.umlcd4a.symboltable.OverrideAttribute.B", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(b);
    assertEquals("B", b.getName());
    // s is overridden in subclass B
    assertEquals(1, b.getAllVisibleFields().size());
    assertSame(b.getFields().get(0), b.getAllVisibleFields().iterator().next());
  }
  
  @Test
  public void testGetAllVisibleFields() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbol b = (CDTypeSymbol) globalScope.resolve("de.monticore.umlcd4a.symboltable.VisibleFields.B", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(b);
    assertEquals("B", b.getName());
    assertEquals(1, b.getAllVisibleFields().size());
  }

  @Test
  public void testGetAssociationByName() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbol b = (CDTypeSymbol) globalScope.resolve("de.monticore.umlcd4a.symboltable.CD1.Person", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(b);
    assertTrue(b.getAssociation("member").isPresent());
  }

  @Ignore
  @Test
  public void testGetAllKindElements() {
    GlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbol cdTypeSymbol = (CDTypeSymbol) globalScope.resolve("de.monticore.umlcd4a.symboltable.MontiArc.Cmp", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(cdTypeSymbol);
    Scope scope = cdTypeSymbol.getAllKindElements();
    assertEquals(5, scope.getLocalSymbols().size());
    assertEquals(5, scope.getResolvingFilters().size());
    CDAssociationSymbol assoc1 = scope.<CDAssociationSymbol>resolve("def", CDAssociationSymbol.KIND).orElse(null);
    assertNotNull(assoc1);
    CDAssociationSymbol assoc2 = scope.<CDAssociationSymbol>resolve("subs", CDAssociationSymbol.KIND).orElse(null);
    assertNotNull(assoc2);
    CDAssociationSymbol assoc3 = scope.<CDAssociationSymbol>resolve("parent", CDAssociationSymbol.KIND).orElse(null);
    assertNotNull(assoc3);
    CDAssociationSymbol assoc4 = scope.<CDAssociationSymbol>resolve("port", CDAssociationSymbol.KIND).orElse(null);
    assertNotNull(assoc4);

    cdTypeSymbol = (CDTypeSymbol) globalScope.resolve("de.monticore.umlcd4a.symboltable.MontiArc.CTDef", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(cdTypeSymbol);
    scope = cdTypeSymbol.getAllKindElements();
    assertEquals(5, scope.getLocalSymbols().size());
    assertEquals(5, scope.getResolvingFilters().size());
    assoc1 = scope.<CDAssociationSymbol>resolve("instances", CDAssociationSymbol.KIND).orElse(null);
    assertNotNull(assoc1);
    assoc2 = scope.<CDAssociationSymbol>resolve("subDefs", CDAssociationSymbol.KIND).orElse(null);
    assertNotNull(assoc2);
    assoc3 = scope.<CDAssociationSymbol>resolve("portDef", CDAssociationSymbol.KIND).orElse(null);
    assertNotNull(assoc3);
    CDFieldSymbol field1 = scope.<CDFieldSymbol>resolve("cType", CDFieldSymbol.KIND).orElse(null);
    assertNotNull(field1);


    cdTypeSymbol = (CDTypeSymbol) globalScope.resolve("de.monticore.umlcd4a.symboltable.MontiArc.PortDef", CDTypeSymbol.KIND).orElse(null);
    assertNotNull(cdTypeSymbol);
    scope = cdTypeSymbol.getAllKindElements();
    assoc1 = scope.<CDAssociationSymbol>resolve("ctDef", CDAssociationSymbol.KIND).orElse(null);
    assertNotNull(assoc1);

  }
}
