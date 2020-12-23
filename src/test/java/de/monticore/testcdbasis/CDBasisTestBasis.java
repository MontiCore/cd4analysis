/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdbasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._symboltable.ICDBasisGlobalScope;
import de.monticore.cdbasis.cocos.CDBasisCoCos;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import org.junit.Before;

import java.nio.file.Paths;

public class CDBasisTestBasis extends TestBasis {
  protected TestCDBasisParser p;
  protected CDBasisCoCos cdBasisCoCos;

  @Before
  public void initObjects() {
    CDBasisMill.reset();
    CDBasisMill.init();
    p = new TestCDBasisParser();

    final ICDBasisGlobalScope globalScope = CDBasisMill
        .globalScope();
    globalScope.clear();
    globalScope.setModelPath(new ModelPath(Paths.get(PATH)));
    globalScope.setFileExt(CD4AnalysisGlobalScope.EXTENSION);

    cdBasisCoCos = new CDBasisCoCos();
  }
}
