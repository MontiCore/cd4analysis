/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.cd.cd4analysis._symboltable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDTypeReferenceTest {

  @Test
  public void testQualifiedTypeReferenceFromWithinGlobalScope() {
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbolReference ref = new CDTypeSymbolReference("de.monticore.umlcd4a.symboltable.CD2.Person", globalScope);

    assertTrue(ref.existsReferencedSymbol());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", ref.getFullName());
    assertEquals("Person", ref.getName());
  }

  @Test
  public void testQualifiedTypeReferenceFromWithinAnCD() {
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDDefinitionSymbol cd = new CDDefinitionSymbol("CD");
    globalScope.add(cd);

    ICD4AnalysisScope cdScope = CD4AnalysisSymTabMill.cD4AnalysisScopeBuilder().build();
    cd.setSpannedScope(cdScope);
    cdScope.setEnclosingScope(globalScope);

    CDTypeSymbol type = new CDTypeSymbol("Person");
    cdScope.add(type);

    CDTypeSymbolReference ref = new CDTypeSymbolReference("de.monticore.umlcd4a.symboltable.CD2.Person", cd.getSpannedScope());

    assertTrue(ref.existsReferencedSymbol());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", ref.getFullName());
    assertEquals("Person", ref.getName());
  }

}
