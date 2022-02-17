/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcdbasis;

import de.monticore.cd.TestBasis;
import de.monticore.cdbasis._cocos.CDBasisCoCoChecker;
import de.monticore.io.paths.MCPath;
import de.monticore.testcdbasis._cocos.TestCDBasisCoCoChecker;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import de.monticore.testcdbasis._symboltable.ITestCDBasisGlobalScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;

import java.nio.file.Paths;

public class CDBasisTestBasis extends TestBasis {
  protected TestCDBasisParser p;
  protected TestCDBasisCoCoChecker coCoChecker;

  @Before
  public void initObjects() {
    TestCDBasisMill.reset();
    TestCDBasisMill.init();

    // reset the log
    LogStub.init();
    Log.enableFailQuick(false);

    p = new TestCDBasisParser();

    final ITestCDBasisGlobalScope globalScope = TestCDBasisMill.globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    coCoChecker = new TestCDBasisCoCoChecker();
  }
}
