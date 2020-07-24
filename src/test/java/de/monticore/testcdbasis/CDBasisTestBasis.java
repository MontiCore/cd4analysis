/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcdbasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._symboltable.CDBasisGlobalScope;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCreatorDelegator;
import de.monticore.cdbasis.cocos.CDBasisCoCos;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcdbasis._parser.TestCDBasisParser;
import org.junit.Before;

import java.nio.file.Paths;

public class CDBasisTestBasis extends TestBasis {
  protected TestCDBasisParser p;
  protected CDBasisGlobalScope globalScope;
  protected CDBasisSymbolTableCreatorDelegator symbolTableCreator;
  protected CDBasisCoCos cdBasisCoCos;

  @Before
  public void initObjects() {
    p = new TestCDBasisParser();
    globalScope = CDBasisMill
        .cDBasisGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .build();
    symbolTableCreator = CDBasisMill
        .cDBasisSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();
    cdBasisCoCos = new CDBasisCoCos();
  }
}
