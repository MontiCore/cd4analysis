/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.CD4AnalysisDeSer;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.cdbasis._symboltable.CDBasisGlobalScope;
import de.monticore.io.paths.ModelPath;
import org.junit.Before;

import java.nio.file.Paths;

public class CD4AnalysisTestBasis extends TestBasis {
  protected CD4AnalysisParser p;
  protected CD4AnalysisCoCos cd4AnalyisCoCos;
  protected CD4AnalysisFullPrettyPrinter printer;
  protected CD4AnalysisDeSer deSer;

  @Before
  public void initObjects() {
    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();
    p = new CD4AnalysisParser();
    final ICD4AnalysisGlobalScope globalScope = CD4AnalysisMill
        .globalScope();
    globalScope.clear();
    globalScope.setModelPath(new ModelPath(Paths.get(PATH)));
    if (globalScope instanceof CD4AnalysisGlobalScope) {
      ((CD4AnalysisGlobalScope) globalScope).addBuiltInTypes();
    }
    globalScope.setFileExt(CD4AnalysisGlobalScope.EXTENSION);

    cd4AnalyisCoCos = new CD4AnalysisCoCos();
    printer = new CD4AnalysisFullPrettyPrinter();
    deSer = new CD4AnalysisDeSer();
  }
}
