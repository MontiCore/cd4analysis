/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.testcd4codebasis;

import de.monticore.cd.TestBasis;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisGlobalScope;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCreatorDelegator;
import de.monticore.cd4codebasis.cocos.CD4CodeBasisCoCos;
import de.monticore.io.paths.ModelPath;
import de.monticore.testcd4codebasis._parser.TestCD4CodeBasisParser;
import org.junit.Before;

import java.nio.file.Paths;

public class CD4CodeBasisTestBasis extends TestBasis {
  protected TestCD4CodeBasisParser p;
  protected CD4CodeBasisGlobalScope globalScope;
  protected CD4CodeBasisSymbolTableCreatorDelegator symbolTableCreator;
  protected CD4CodeBasisCoCos cdCD4CodeBasisCoCos;

  @Before
  public void initObjects() {
    p = new TestCD4CodeBasisParser();
    globalScope = CD4CodeBasisMill
        .cD4CodeBasisGlobalScopeBuilder()
        .setModelPath(new ModelPath(Paths.get(PATH)))
        .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
        .build();
    symbolTableCreator = CD4CodeBasisMill
        .cD4CodeBasisSymbolTableCreatorDelegatorBuilder()
        .setGlobalScope(globalScope)
        .build();
    cdCD4CodeBasisCoCos = new CD4CodeBasisCoCos();
  }
}
