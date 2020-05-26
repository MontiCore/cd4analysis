/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4analysis._symboltable;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDTypeReferenceTest {

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testQualifiedTypeReferenceFromWithinGlobalScope() {
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDTypeSymbolLoader ref = new CDTypeSymbolLoader("de.monticore.umlcd4a.symboltable.CD2.Person", globalScope);

    assertTrue(ref.loadSymbol().isPresent());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", ref.getLoadedSymbol().getFullName());
    assertEquals("Person", ref.getLoadedSymbol().getName());
  }

  @Test
  public void testQualifiedTypeReferenceFromWithinAnCD() {
    CD4AnalysisGlobalScope globalScope = CD4AGlobalScopeTestFactory.create();

    CDDefinitionSymbol cd = new CDDefinitionSymbol("CD");
    globalScope.add(cd);

    ICD4AnalysisScope cdScope = CD4AnalysisMill.cD4AnalysisScopeBuilder().build();
    cd.setSpannedScope(cdScope);
    cdScope.setEnclosingScope(globalScope);

    CDTypeSymbol type = new CDTypeSymbol("Person");
    cdScope.add(type);

    CDTypeSymbolLoader ref = new CDTypeSymbolLoader("de.monticore.umlcd4a.symboltable.CD2.Person", cd.getSpannedScope());

    assertTrue(ref.loadSymbol().isPresent());
    assertEquals("de.monticore.umlcd4a.symboltable.CD2.Person", ref.getLoadedSymbol().getFullName());
    assertEquals("Person", ref.getLoadedSymbol().getName());
  }

}
