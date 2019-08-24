/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4analysis._symboltable;

import org.junit.Test;

import static org.junit.Assert.*;

public class CDTypeTest {

  @Test
  public void testGetFields() {
    final CDTypeSymbolReference dummyTypeRef = new CDTypeSymbolReference("Dummy", new CD4AnalysisScope(false));

    CDFieldSymbol fieldSymbol1 = new CDFieldSymbol("field1", dummyTypeRef);
    CDFieldSymbol fieldSymbol2 = new CDFieldSymbol("field2", dummyTypeRef);
    CDFieldSymbol fieldSymbol3 = new CDFieldSymbol("field3", dummyTypeRef);

    CDTypeSymbol typeSymbol = new CDTypeSymbol("TypeFoo");
    ICD4AnalysisScope typeScope = CD4AnalysisSymTabMill.cD4AnalysisScopeBuilder().build();
    typeSymbol.setSpannedScope(typeScope);

    typeScope.add(fieldSymbol1);
    typeScope.add(fieldSymbol2);
    typeScope.add(fieldSymbol3);

    // Test scope
    assertEquals(3, typeSymbol.getFields().size());
    assertSame(fieldSymbol1, typeSymbol.getFields().get(0));
    assertSame(fieldSymbol2, typeSymbol.getFields().get(1));
    assertSame(fieldSymbol3, typeSymbol.getFields().get(2));
  }

  @Test
  public void testGetMethods() {
    CDMethOrConstrSymbol methodSymbol1 = new CDMethOrConstrSymbol("method1");
    CDMethOrConstrSymbol constructor = new CDMethOrConstrSymbol("constructor");
    constructor.setIsConstructor(true);
    CDMethOrConstrSymbol methodSymbol2 = new CDMethOrConstrSymbol("method2");

    CDTypeSymbol typeSymbol = new CDTypeSymbol("TypeFoo");
    ICD4AnalysisScope typeScope = CD4AnalysisSymTabMill.cD4AnalysisScopeBuilder().build();
    typeSymbol.setSpannedScope(typeScope);

    typeScope.add(methodSymbol1);
    typeScope.add(constructor);
    typeScope.add(methodSymbol2);

    // Test CDTypeSymbol methods //
    assertEquals(3, typeSymbol.getMethods().size());
    assertSame(methodSymbol1, typeSymbol.getMethods().get(0));
    assertSame(constructor, typeSymbol.getMethods().get(1));
    assertSame(methodSymbol2, typeSymbol.getMethods().get(2));


    // Test CDTypeScope methods //
    assertEquals(3, typeScope.getSymbolsSize());
    assertSame(methodSymbol1, typeScope.resolveCDMethOrConstr("method1").orElse(null));
    assertSame(methodSymbol2, typeScope.resolveCDMethOrConstr("method2").orElse(null));
    assertSame(constructor, typeScope.resolveCDMethOrConstr("constructor").orElse(null));
  }

  @Test
  public void testOverrideAttribute() {
    // class B overrides attribute "s" of superclass A.
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbol b =  globalScope.resolveCDType("de.monticore.umlcd4a.symboltable.OverrideAttribute.B").orElse(null);
    assertNotNull(b);
    assertEquals("B", b.getName());
    // s is overridden in subclass B
    assertEquals(1, b.getAllVisibleFields().size());
    assertSame(b.getFields().get(0), b.getAllVisibleFields().iterator().next());
  }

  @Test
  public void testGetAllVisibleFields() {
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbol b =  globalScope.resolveCDType("de.monticore.umlcd4a.symboltable.VisibleFields.B").orElse(null);
    assertNotNull(b);
    assertEquals("B", b.getName());
    assertEquals(1, b.getAllVisibleFields().size());
  }

  @Test
  public void testGetAssociationByName() {
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbol b = globalScope.resolveCDType("de.monticore.umlcd4a.symboltable.CD1.Person").orElse(null);
    assertNotNull(b);
    assertTrue(b.getAssociation("member").isPresent());
  }
}
