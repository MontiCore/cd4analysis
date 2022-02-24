/* (c) https://github.com/MontiCore/monticore */
package de.monticore.testcd4codebasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisCoCoChecker;
import de.monticore.cd4codebasis._symboltable.ICD4CodeBasisGlobalScope;
import de.monticore.cd4codebasis.cocos.CD4CodeBasisCoCos;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.testcd4codebasis._parser.TestCD4CodeBasisParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;

import java.nio.file.Paths;

public class CD4CodeBasisTestBasis extends TestBasis {
  protected TestCD4CodeBasisParser p;
  protected CD4CodeBasisCoCos cdCD4CodeBasisCoCos;
  protected CD4CodeBasisCoCoChecker coCoChecker;

  @Before
  public void initObjects() {
    Log.init();
    Log.enableFailQuick(false);
    CD4CodeBasisMill.reset();
    CD4CodeBasisMill.init();
    BasicSymbolsMill.initializePrimitives();
    p = new TestCD4CodeBasisParser();

    final ICD4CodeBasisGlobalScope globalScope = CD4CodeBasisMill
        .globalScope();
    globalScope.clear();
    globalScope.setSymbolPath(new MCPath(Paths.get(PATH)));

    cdCD4CodeBasisCoCos = new CD4CodeBasisCoCos();
    coCoChecker = new CD4CodeBasisCoCoChecker();
  }
}
