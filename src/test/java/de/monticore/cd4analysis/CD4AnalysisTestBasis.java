/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd4analysis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4analysis._symboltable.CD4AnalysisScopeDeSer;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisPrettyPrinter;
import de.monticore.io.paths.ModelPath;
import org.junit.Before;

import java.nio.file.Paths;

public class CD4AnalysisTestBasis extends TestBasis {
  protected CD4AnalysisParser p;
  protected CD4AnalysisCoCos cd4AnalyisCoCos;
  protected CD4AnalysisPrettyPrinter printer;
  protected CD4AnalysisScopeDeSer deSer;

  @Before
  public void initObjects() {
    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();
    p = new CD4AnalysisParser();
    final ICD4AnalysisGlobalScope globalScope = CD4AnalysisMill
        .cD4AnalysisGlobalScope();
    globalScope.clear();
    globalScope.setModelPath(new ModelPath(Paths.get(PATH)));
    globalScope.addBuiltInTypes();
    globalScope.setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION);

    cd4AnalyisCoCos = new CD4AnalysisCoCos();
    printer = CD4AnalysisMill.cD4AnalysisPrettyPrinter();
    deSer = new CD4AnalysisScopeDeSer();
  }
}
