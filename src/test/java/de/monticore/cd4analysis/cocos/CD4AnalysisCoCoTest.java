/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.cocos;

import de.monticore.cd4analysis.CD4AnalysisTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class CD4AnalysisCoCoTest extends CD4AnalysisTestBasis {

  @Test
  public void checkMaCoCO() throws IOException {
    final ASTCDCompilationUnit astcdCompilationUnit = parse("cd4analysis/examples/industrial_strength_models/MaCoCo.cd");
    prepareST(astcdCompilationUnit);

    coCoChecker = new CD4AnalysisCoCosDelegator().getCheckerForAllCoCos();
    coCoChecker.checkAll(astcdCompilationUnit);
  }

  @Test
  public void checkInviDas() throws IOException {
    final ASTCDCompilationUnit astcdCompilationUnit = parse("cd4analysis/examples/industrial_strength_models/InviDas.cd");
    prepareST(astcdCompilationUnit);

    assertNotNull(astcdCompilationUnit.getEnclosingScope().resolveCDType("C"));
    coCoChecker = new CD4AnalysisCoCosDelegator().getCheckerForAllCoCos();
    coCoChecker.checkAll(astcdCompilationUnit);
  }

}
