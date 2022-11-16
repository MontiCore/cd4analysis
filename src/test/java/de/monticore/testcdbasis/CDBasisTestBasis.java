/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis;

import de.monticore.cd.TestBasis;
import de.monticore.io.paths.MCPath;
import de.monticore.testcdbasis._cocos.TestCDBasisCoCoChecker;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.monticore.testcdbasis._symboltable.ITestCDBasisGlobalScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import java.nio.file.Paths;
import org.junit.Before;

public class CDBasisTestBasis extends TestBasis {
  protected TestCDBasisParser p;
  protected TestCDBasisCoCoChecker coCoChecker;

  @Before
  public void initObjects() {
    // reset the log
    LogStub.init();
    Log.enableFailQuick(false);

    TestCDBasisMill.reset();
    TestCDBasisMill.init();

    p = new TestCDBasisParser();

    final ITestCDBasisGlobalScope globalScope = TestCDBasisMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    coCoChecker = new TestCDBasisCoCoChecker();
  }
}
