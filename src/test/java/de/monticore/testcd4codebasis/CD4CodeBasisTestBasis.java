/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4codebasis.cocos.CD4CodeBasisCoCos;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.testcd4codebasis._cocos.TestCD4CodeBasisCoCoChecker;
import de.monticore.testcd4codebasis._parser.TestCD4CodeBasisParser;
import de.monticore.testcd4codebasis._symboltable.ITestCD4CodeBasisGlobalScope;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;

public class CD4CodeBasisTestBasis extends TestBasis {
  protected TestCD4CodeBasisParser p;
  protected CD4CodeBasisCoCos cdCD4CodeBasisCoCos;
  protected TestCD4CodeBasisCoCoChecker coCoChecker;

  @Before
  public void initObjects() {
    LogStub.init();
    LogStub.enableFailQuick(false);
    TestCD4CodeBasisMill.reset();
    TestCD4CodeBasisMill.init();
    final ITestCD4CodeBasisGlobalScope globalScope = TestCD4CodeBasisMill.globalScope();
    globalScope.clear();
    BasicSymbolsMill.initializePrimitives();
    p = TestCD4CodeBasisMill.parser();

    //    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    cdCD4CodeBasisCoCos = new CD4CodeBasisCoCos(new FullDeriveFromTestCD4CodeBasis());
    coCoChecker = new TestCD4CodeBasisCoCoChecker();
  }
}
