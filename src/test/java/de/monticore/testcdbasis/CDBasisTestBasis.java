/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdbasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._symboltable.ICDBasisGlobalScope;
import de.monticore.io.paths.MCPath;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;

import java.nio.file.Paths;

public class CDBasisTestBasis extends TestBasis {
  protected TestCDBasisParser p;
  protected CD4AnalysisCoCoChecker coCoChecker;

  @Before
  public void initObjects() {
    CD4AnalysisMill.reset();
    CD4AnalysisMill.init();

    // reset the log
    Log.init();
    Log.enableFailQuick(false);

    p = new TestCDBasisParser();

    final ICDBasisGlobalScope globalScope = CDBasisMill
        .globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    coCoChecker = new CD4AnalysisCoCoChecker();
  }
}
