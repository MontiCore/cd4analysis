/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.cocos;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class CD4AnalysisCoCoTest extends CD4AnalysisTestBasis {

  @Test
  public void checkMaCoCO() throws IOException {
    final ASTCDCompilationUnit astcdCompilationUnit = parse("cd4analysis/industrial_strength_models/MaCoCo.cd");
    setUpTypes();
    prepareST(astcdCompilationUnit);

    coCoChecker = new CD4AnalysisCoCosDelegator().getCheckerForAllCoCos();
    coCoChecker.checkAll(astcdCompilationUnit);
  }

  @Test
  public void checkInviDas() throws IOException {
    final ASTCDCompilationUnit astcdCompilationUnit = parse("cd4analysis/industrial_strength_models/InviDas.cd");
    setUpTypes();
    prepareST(astcdCompilationUnit);

    assertNotNull(astcdCompilationUnit.getEnclosingScope().resolveCDType("C"));
    coCoChecker = new CD4AnalysisCoCosDelegator().getCheckerForAllCoCos();
    coCoChecker.checkAll(astcdCompilationUnit);
  }

  protected void setUpTypes() {
    ICD4AnalysisScope scope = CD4AnalysisMill.scope();
    TypeVarSymbol t = CD4AnalysisMill.typeVarSymbolBuilder()
      .setName("T")
      .setSpannedScope(CD4AnalysisMill.scope())
      .setEnclosingScope(scope)
      .build();
    scope.add(t);
    OOTypeSymbol list = CD4AnalysisMill.oOTypeSymbolBuilder()
      .setSpannedScope(CD4AnalysisMill.scope())
      .setName("List")
      .setEnclosingScope(CD4AnalysisMill.globalScope())
      .build();
    CD4AnalysisMill.globalScope().add(list);
    ICD4AnalysisScope scope2 = CD4AnalysisMill.scope();
    TypeVarSymbol s = CD4AnalysisMill.typeVarSymbolBuilder()
      .setName("S")
      .setSpannedScope(CD4AnalysisMill.scope())
      .setEnclosingScope(scope2)
      .build();
    scope2.add(s);
    OOTypeSymbol optional = CD4AnalysisMill.oOTypeSymbolBuilder()
      .setSpannedScope(CD4AnalysisMill.scope())
      .setName("Optional")
      .setEnclosingScope(CD4AnalysisMill.globalScope())
      .build();
    CD4AnalysisMill.globalScope().add(optional);
  }
}
