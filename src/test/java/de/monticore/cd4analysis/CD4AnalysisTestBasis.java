/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbols2Json;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisFullPrettyPrinter;
import de.monticore.io.paths.MCPath;
import org.junit.Before;

import java.nio.file.Paths;

public class CD4AnalysisTestBasis extends TestBasis {
  protected CD4AnalysisParser p;
  protected CD4AnalysisCoCos cd4AnalyisCoCos;
  protected CD4AnalysisFullPrettyPrinter printer;
  protected CD4AnalysisSymbols2Json symbols2Json;

  @Before
  public void initObjects() {
    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();
    p = new CD4AnalysisParser();
    final ICD4AnalysisGlobalScope globalScope = CD4AnalysisMill
        .globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));
    if (globalScope instanceof CD4AnalysisGlobalScope) {
      ((CD4AnalysisGlobalScope) globalScope).addBuiltInTypes();
    }

    cd4AnalyisCoCos = new CD4AnalysisCoCos();
    printer = new CD4AnalysisFullPrettyPrinter();
    symbols2Json = new CD4AnalysisSymbols2Json();
  }
}
